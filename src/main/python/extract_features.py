import os
import argparse
import numpy as np
import torch
from torchvision import models
from torchvision.models import MobileNet_V2_Weights
from PIL import Image

parser = argparse.ArgumentParser()
parser.add_argument("--frames_dir", type=str, default="res/frames/", help="Directory containing frames.")
parser.add_argument("--output_dir", type=str, default="res/features/", help="Directory to save extracted features.")


def initialize_model():
    global model, preprocessing_pipeline
    weights = MobileNet_V2_Weights.IMAGENET1K_V1
    model = models.mobilenet_v2(weights=weights)
    model.eval()
    preprocessing_pipeline = weights.transforms()

def extract_features_from_img(img_path):
    if 'model' not in globals() or 'preprocessing_pipeline' not in globals():
        raise ValueError("Model is not initialized. Call initialize_model() first.")
    
    img = Image.open(img_path).convert("RGB")
    img_tensor = preprocessing_pipeline(img)
    img_tensor = img_tensor.unsqueeze(0)
    
    with torch.no_grad():
        features = model(img_tensor)
        
    return features.numpy()

def save_features(frames_dir, output_dir):
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        
    for folder_name in os.listdir(frames_dir):
        folder_path = os.path.join(frames_dir, folder_name)
        object_features = []
        for frame_name in sorted(os.listdir(folder_path)):
            frame_path = os.path.join(folder_path, frame_name)
            features = extract_features_from_img(frame_path)
            object_features.append(features)
            
        object_features = np.vstack(object_features)
        feature_file_path = os.path.join(output_dir, f"{folder_name}.npy")
        np.save(feature_file_path, object_features)
        print(f"Saved features for {folder_name} to {feature_file_path}")


def main(args):
    initialize_model()
    save_features(args.frames_dir, args.output_dir)


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
