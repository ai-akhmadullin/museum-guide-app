import csv
import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument("--csv_file", type=str, default="MuseumGuideApp/app/src/main/assets/upm_exhibits_dataset.csv", help="Path to the input CSV file with exhibit information.")
parser.add_argument("--output_file", type=str, default="MuseumGuideApp/app/src/main/assets/labels.txt", help="Path to the output file with extracted titles.")


def extract_titles(csv_file, output_file):
    with open(csv_file, "r", encoding="utf-8") as input_file:
        reader = csv.DictReader(input_file)
        titles = [row["title"] for row in reader]

    with open(output_file, "w", encoding="utf-8") as output_file:
        for title in titles:
            output_file.write(title + "\n")

    print(f"Titles have been written to {output_file.name}")

def main(args):
    extract_titles(args.csv_file, args.output_file)


if __name__ == "__main__":
    args = parser.parse_args()
    main(args)
