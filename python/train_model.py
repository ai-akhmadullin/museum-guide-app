import argparse
import numpy as np
import tensorflow as tf
from keras.applications import MobileNetV2
from keras.preprocessing.image import ImageDataGenerator

parser = argparse.ArgumentParser()
parser.add_argument("--frames_dir", type=str, default="res/frames", help="Directory containing frames.")
parser.add_argument("--output_model_path", type=str, default="python/model.tflite", help="Path to save the TFLite model.")
parser.add_argument("--epochs", type=int, default=10, help="Number of epochs for initial training.")
parser.add_argument("--finetune_epochs", type=int, default=10, help="Number of epochs for fine-tuning.")
parser.add_argument("--validation_split", type=float, default=0.2, help="Fraction of data to use for validation (0 to disable validation).")


def main(args):
    np.random.seed(42)
    tf.random.set_seed(42)

    datagen = ImageDataGenerator(
        rescale=1./255,
        rotation_range=40,
        width_shift_range=0.2,
        height_shift_range=0.2,
        shear_range=0.2,
        zoom_range=0.2,
        horizontal_flip=True,
        fill_mode="nearest",
        validation_split=args.validation_split if args.validation_split > 0 else 0
    )

    train_generator = datagen.flow_from_directory(
        args.frames_dir,
        subset="training" if args.validation_split > 0 else None,
        target_size=(224, 224),
        batch_size=32,
        class_mode="categorical"
    )

    validation_generator = None
    if args.validation_split > 0:
        validation_generator = datagen.flow_from_directory(
            args.frames_dir,
            subset="validation",
            target_size=(224, 224),
            batch_size=32,
            class_mode="categorical"
        )

    base_model = MobileNetV2(input_shape=(224, 224, 3),
                                                   include_top=False,
                                                   weights="imagenet")

    base_model.trainable = False

    model = tf.keras.Sequential([
        base_model,
        tf.keras.layers.GlobalAveragePooling2D(),
        tf.keras.layers.Dense(1024, activation="relu"),
        tf.keras.layers.Dropout(0.5),
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

    model.compile(optimizer=tf.keras.optimizers.Adam(1e-5),
                  loss="categorical_crossentropy",
                  metrics=["accuracy"])

    model.fit(
        train_generator,
        epochs=args.epochs + args.finetune_epochs,
        initial_epoch=args.epochs,
        validation_data=validation_generator
    )

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()

    with open(args.output_model_path, "wb") as f:
        f.write(tflite_model)


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
