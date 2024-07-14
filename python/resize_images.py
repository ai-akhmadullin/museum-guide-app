from PIL import Image, ExifTags
import os
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--input_gallery_dir", type=str, default="res/gallery", help="Path to the input directory containing images.")
parser.add_argument("--output_gallery_dir", type=str, default="MuseumGuideApp/app/src/main/assets/gallery", help="Path to the output directory containing images.")
parser.add_argument("--target_width", type=int, default=600, help="Target width for resized images.")
parser.add_argument("--target_height", type=int, default=800, help="Target height for resized images.")
parser.add_argument("--preserve_aspect_ratio", type=bool, default=True, help="Whether to preserve the aspect ratio of the image.")

def resize_image(input_path, output_path, target_width, target_height, preserve_aspect_ratio):
    with Image.open(input_path) as img:
        # Correct for EXIF orientation
        try:
            for orientation in ExifTags.TAGS.keys():
                if ExifTags.TAGS[orientation] == 'Orientation':
                    break

            exif = img._getexif()

            if exif is not None:
                orientation = exif.get(orientation, None)

                if orientation == 3:
                    img = img.rotate(180, expand=True)
                elif orientation == 6:
                    img = img.rotate(270, expand=True)
                elif orientation == 8:
                    img = img.rotate(90, expand=True)
        except Exception as e:
            print(f"Could not process EXIF orientation for {input_path}: {e}")

        # Resize the image
        if preserve_aspect_ratio:
            img.thumbnail((target_width, target_height))
        else:
            img = img.resize((target_width, target_height))
        
        img.save(output_path)


def main(args):
    if not os.path.exists(args.output_gallery_dir):
        os.makedirs(args.output_gallery_dir)

    for filename in os.listdir(args.input_gallery_dir):
        if filename.endswith(".jpg"):
            input_path = os.path.join(args.input_gallery_dir, filename)
            output_path = os.path.join(args.output_gallery_dir, filename)
            resize_image(input_path, output_path, args.target_width, args.target_height, args.preserve_aspect_ratio)
            print(f"Resized {filename}")


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
