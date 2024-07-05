import subprocess
import os
import argparse

parser = argparse.ArgumentParser()

parser.add_argument("--videos_dir", type=str, default="res/listed exhibits/", help="Directory containing video files.")
parser.add_argument("--frames_dir", type=str, default="res/frames/", help="Directory to save extracted frames.")
parser.add_argument("--fps", type=int, default=1, help="Frames per second to extract.")


def extract_frames(video_path, output_folder, fps=1):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
        
    output_pattern = os.path.join(output_folder, "frame_%02d.jpg")
    command = [
        "ffmpeg",
        "-i", video_path,
        "-vf", f"fps={fps}",
        output_pattern
    ]
    
    subprocess.run(command, check=True)


def main(videos_dir, frames_dir, fps):
    for file_name in os.listdir(videos_dir):
        if file_name.endswith(".mov"):
            video_id = os.path.splitext(file_name)[0]
            video_path = os.path.join(videos_dir, file_name)
            
            output_folder = os.path.join(frames_dir, f"{video_id}")
            
            extract_frames(video_path, output_folder, fps)


if __name__ == "__main__":
    args = parser.parse_args()

    main(args.videos_dir, args.frames_dir, args.fps)
