import argparse
import subprocess
import numpy as np
import os
import tensorflow as tf
from keras.applications import MobileNetV2
from keras.preprocessing.image import ImageDataGenerator

parser = argparse.ArgumentParser()
parser.add_argument("--frames_dir", type=str, default="res/frames", help="Directory containing frames.")
parser.add_argument("--output_tf_path", type=str, default="python/mobilenet_v2_custom.h5", help="Path to save the TF model.")
parser.add_argument("--output_tflite_path", type=str, default="python/mobilenet_v2_custom.tflite", help="Path to save the TFLite model.")
parser.add_argument("--epochs", type=int, default=20, help="Number of epochs for initial training.")
parser.add_argument("--finetune_epochs", type=int, default=20, help="Number of epochs for fine-tuning.")
parser.add_argument("--validation_split", type=float, default=0.0, help="Fraction of data to use for validation (0 to disable validation).")

parser.add_argument("--generate_metadata", type=bool, default=True, help="Flag to generate metadata for the TFLite model.")
parser.add_argument("--metadata_script", type=str, default="python/metadata_writer_for_image_classifier.py", help="Path to the metadata script.")
parser.add_argument("--label_file", type=str, default="MuseumGuideApp/app/src/main/assets/labels.txt", help="Path to the label file.")
parser.add_argument("--export_directory", type=str, default="MuseumGuideApp/app/src/main/assets", help="Directory to save the model with metadata.")


def main(args):
    np.random.seed(42)
    tf.random.set_seed(42)

    classes = len([d for d in os.listdir(args.frames_dir) if not d.startswith('.')])
    subdirectories = [str(i) for i in range(1, classes + 1)]

    datagen = ImageDataGenerator(
        rescale=1./255,
        validation_split=args.validation_split if args.validation_split > 0 else 0
    )

    train_generator = datagen.flow_from_directory(
        args.frames_dir,
        subset="training" if args.validation_split > 0 else None,
        target_size=(224, 224),
        batch_size=16,
        class_mode="categorical",
        classes=subdirectories
    )

    validation_generator = None
    if args.validation_split > 0:
        validation_generator = datagen.flow_from_directory(
            args.frames_dir,
            subset="validation",
            target_size=(224, 224),
            batch_size=16,
            class_mode="categorical",
            classes=subdirectories
        )

    base_model = MobileNetV2(input_shape=(224, 224, 3),
                                                   include_top=False,
                                                   weights="imagenet")

    base_model.trainable = False

    model = tf.keras.Sequential([
        base_model,
        tf.keras.layers.GlobalAveragePooling2D(),
        tf.keras.layers.Dense(1024, activation="relu"),
        tf.keras.layers.Dropout(0.1),
        tf.keras.layers.Dense(train_generator.num_classes, activation="softmax")
    ])

    model.compile(optimizer="adam",
                  loss="categorical_crossentropy",
                  metrics=["accuracy"])

    model.fit(
        train_generator,
        epochs=args.epochs,
        validation_data=validation_generator
    )

    base_model.trainable = True
    fine_tune_after = 120
    for layer in base_model.layers[:fine_tune_after]:
        layer.trainable = False

    model.compile(optimizer=tf.keras.optimizers.Adam(1e-5),
                  loss="categorical_crossentropy",
                  metrics=["accuracy"])

    model.fit(
        train_generator,
        epochs=args.epochs + args.finetune_epochs,
        initial_epoch=args.epochs,
        validation_data=validation_generator
    )

    model.save(args.output_tf_path)

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()

    with open(args.output_tflite_path, "wb") as f:
        f.write(tflite_model)

    if args.generate_metadata:
        subprocess.run(["/usr/bin/python3", args.metadata_script,
                        "--model_file", args.output_tflite_path,
                        "--label_file", args.label_file,
                        "--export_directory", args.export_directory])


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
