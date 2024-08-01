import subprocess
import os
import argparse
import math

parser = argparse.ArgumentParser()

parser.add_argument("--videos_dir", type=str, default="res/upm_dataset/listed exhibits/", help="Directory containing video files.")
parser.add_argument("--frames_dir", type=str, default="res/frames/", help="Directory to save extracted frames.")
parser.add_argument("--fps", type=int, default=2, help="Frames per second to extract.")


def get_video_duration(video_path):
    command = [
        "ffprobe",
        "-v", "error",
        "-select_streams", "v:0",
        "-show_entries", "stream=duration",
        "-of", "default=noprint_wrappers=1:nokey=1",
        video_path
    ]
    result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    duration = float(result.stdout.strip())
    return duration


def extract_frames(video_path, output_folder, fps=1, max_frames=100):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    duration = get_video_duration(video_path)
    frame_interval = duration / max_frames
        
    output_pattern = os.path.join(output_folder, "frame_%02d.jpg")
    command = [
        "ffmpeg",
        "-i", video_path,
        "-vf", f"fps={fps},select='not(mod(n,{math.ceil(frame_interval * fps)}))'",
        "-vsync", "vfr",
        output_pattern
    ]
    
    subprocess.run(command, check=True)


def main(videos_dir, frames_dir, fps):
    # Determine the duration of the shortest video
    min_duration = float("inf")
    for file_name in os.listdir(videos_dir):
        if file_name.lower().endswith(".mov"):
            video_path = os.path.join(videos_dir, file_name)
            duration = get_video_duration(video_path)
            if duration < min_duration:
                min_duration = duration

    print(f"The shortest video is {min_duration} seconds long.")
    
    max_frames = int(min_duration * fps)

    # Extract frames from each video
    for file_name in os.listdir(videos_dir):
        if file_name.lower().endswith(".mov"):
            video_id = os.path.splitext(file_name)[0]
            video_path = os.path.join(videos_dir, file_name)
            
            output_folder = os.path.join(frames_dir, f"{video_id}")
            
            extract_frames(video_path, output_folder, fps, max_frames)


if __name__ == "__main__":
    args = parser.parse_args()
    main(args.videos_dir, args.frames_dir, args.fps)
