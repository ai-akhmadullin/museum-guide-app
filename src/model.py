import os
import numpy as np
import faiss
import re
import argparse
from extract_features import initialize_model, extract_features_from_img

parser = argparse.ArgumentParser()
parser.add_argument("--features_dir", type=str, default="res/features/", help="Directory containing features.")
parser.add_argument("--exhibits_dir", type=str, default="res/listed exhibits/", help="Directory containing exhibits.")


def load_features(features_dir):
    all_features = []
    video_ids = []

    for file_name in os.listdir(features_dir):
        video_id = os.path.splitext(file_name)[0]
        feature_file_path = os.path.join(features_dir, file_name)
        object_features = np.load(feature_file_path)
        all_features.append(object_features)
        video_ids.extend([video_id] * len(object_features))
    
    return np.vstack(all_features), video_ids


def find_best_match(photo_features, index, video_ids):
    D, I = index.search(photo_features, 1)
    best_match_idx = I[0][0]
    best_match_video_id = video_ids[best_match_idx]
    return best_match_video_id, D[0][0]


def evaluate_model(exhibits_dir, index, video_ids):
    correct = 0
    total = 0
    
    for file_name in os.listdir(exhibits_dir):
        if re.match(r"\d+[a-z]\.jpe?g", file_name, re.IGNORECASE):
            actual_id = re.match(r"(\d+)[a-z]\.jpe?g", file_name, re.IGNORECASE).group(1)
            photo_path = os.path.join(exhibits_dir, file_name)
            photo_features = extract_features_from_img(photo_path)
            best_match_id, _ = find_best_match(photo_features, index, video_ids)
            
            if int(best_match_id) == int(actual_id):
                correct += 1
            total += 1
    
    accuracy = correct / total
    return accuracy, correct, total


def main(args):
    all_features, video_ids = load_features(args.features_dir)
    d = all_features.shape[1]

    index = faiss.IndexFlatL2(d)
    index.add(all_features)

    initialize_model()

    accuracy, correct, total = evaluate_model(args.exhibits_dir, index, video_ids)
    print(f"Accuracy: {accuracy:.2f} ({correct}/{total})")


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)