import os
from glob import glob
from PIL import Image
import pillow_heif
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--upm_dir", type=str, default="res/upm_dataset_kveten/UPM_source", help="Path to the new UPM dataset directory.")
parser.add_argument("--frames_dir", type=str, default="res/frames", help="Path to the frames directory.")
parser.add_argument("--existing_objects", type=int, default=101, help="Number of existing objects in the frames directory.")

def convert_heic_to_jpg(heic_path, jpg_path):
    heif_file = pillow_heif.read_heif(heic_path)
    image = Image.frombytes(
        heif_file.mode,
        heif_file.size,
        heif_file.data,
        "raw",
    )
    image.save(jpg_path, format="JPEG")


def main(args):
    # Get all HEIC files in the UPM_source directory
    heic_files = glob(os.path.join(args.upm_dir, "*.HEIC"))

    # Dictionary to keep track of file counts per ID
    id_counts = {}

    for heic_file in heic_files:
        filename = os.path.basename(heic_file)
        
        # Extract original ID from filename
        try:
            original_id_str = filename.split("_")[0]
            original_id = int(original_id_str)
        except ValueError:
            print(f"Filename {filename} does not comply with the expected naming pattern. Skipping this file.")
            continue

        # Calculate new folder name
        new_folder_name = str(original_id + args.existing_objects)

        # Create new folder inside frames directory
        new_folder_path = os.path.join(args.frames_dir, new_folder_name)
        os.makedirs(new_folder_path, exist_ok=True)

        # Update count for this ID
        count = id_counts.get(original_id, 0) + 1
        id_counts[original_id] = count

        # Define new filename
        new_filename = f"frame_{count}.jpg"
        new_file_path = os.path.join(new_folder_path, new_filename)

        # Convert HEIC to JPG and save
        try:
            convert_heic_to_jpg(heic_file, new_file_path)
            print(f"Converted {heic_file} to {new_file_path}")
        except Exception as e:
            print(f"Failed to convert {heic_file}: {e}")


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
