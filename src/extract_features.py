import os
import argparse
import numpy as np
import torch
from torchvision import models
from torchvision.models import MobileNet_V2_Weights
from PIL import Image

parser = argparse.ArgumentParser()
parser.add_argument("--frames_dir", type=str, default="res/frames/", help="Directory containing frames.")


weights = MobileNet_V2_Weights.IMAGENET1K_V1
model = models.mobilenet_v2(weights=weights)
model.eval()

preprocessing_pipeline = weights.transforms()

def extract_features_from_img(img_path):
    img = Image.open(img_path).convert("RGB")
    img_tensor = preprocessing_pipeline(img)
    img_tensor = img_tensor.unsqueeze(0)
    
    with torch.no_grad():
        features = model(img_tensor)
        
    return features.numpy()

def save_features(frames_dir):
    for folder_name in os.listdir(frames_dir):
        folder_path = os.path.join(frames_dir, folder_name)
        if os.path.isdir(folder_path):
            object_features = []
            for frame_name in sorted(os.listdir(folder_path)):
                if frame_name.endswith(".jpg"):
                    frame_path = os.path.join(folder_path, frame_name)
                    features = extract_features_from_img(frame_path)
                    object_features.append(features)
            
            object_features = np.vstack(object_features)  
            feature_file_path = os.path.join(folder_path, "features.npy")
            np.save(feature_file_path, object_features)


def main(args):
    save_features(args.frames_dir)


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
