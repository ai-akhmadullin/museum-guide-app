## Museum Guide App

An Android app that recognizes museum exhibits on-device using a TensorFlow Lite model and provides details about the recognized artwork. The UI is built with Jetpack Compose, the camera is powered by CameraX, and inference runs locally via the TensorFlow Lite Task Library. The repository also contains Python utilities used to prepare data and train the MobileNetV2-based classifier, as well as a thesis documenting the work.

### Features
- **On‑device exhibit recognition**: Live camera scanning and classification with a custom `mobilenet_v2_custom.tflite` model.
- **Exhibit details**: Navigate to a detail screen for the top match and quickly explore alternative matches.
- **Gallery**: Browse all available exhibits from bundled assets.
- **Modern Android UI**: Jetpack Compose + Material 3, Navigation, and splash screen.
- **Offline‑first**: Model, labels, images, and metadata are bundled in the app assets.

### Repository structure
```text
.
├── MuseumGuideApp/                 # Android application (Kotlin, Jetpack Compose)
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── assets/
│   │   │   │   ├── mobilenet_v2_custom.tflite   # On‑device classifier model
│   │   │   │   ├── mobilenet_v2_custom.json     # Model metadata
│   │   │   │   ├── labels.txt                    # Class names
│   │   │   │   ├── upm_exhibits_dataset.csv      # Exhibit metadata
│   │   │   │   └── gallery/                      # Exhibit images (id.jpg)
│   │   │   └── java/com/example/museumguide/
│   │   │       ├── MainActivity.kt               # Nav host, bottom bar, screen routing
│   │   │       ├── Home.kt                       # About/news and social links
│   │   │       ├── Photo.kt                      # PhotoScanner + CameraScreen (CameraX + inference)
│   │   │       ├── CameraPreview.kt              # Camera preview and overlay
│   │   │       ├── Gallery.kt                    # Grid of assets/gallery images
│   │   │       ├── Exhibit.kt                    # Exhibit model, CSV parser, detail screen
│   │   │       ├── Screens.kt                    # Screen routes
│   │   │       └── domain/ & data/
│   │   │           ├── domain/IClassifier.kt, Classification.kt, BitmapExt.kt
│   │   │           └── data/TfLiteClassifier.kt  # TensorFlow Lite Task Vision
│   │   └── build.gradle.kts                      # Compose, CameraX, TFLite deps
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── python/                         # Data prep, training, and export utilities
│   ├── extract_frames.py, extend_frames.py
│   ├── extract_features.py, train_model.py
│   ├── resize_images.py, extract_labels.py
│   ├── metadata_writer_for_image_classifier.py
│   └── mobilenet_v2_custom.(h5|tflite)
├── thesis/                         # LaTeX sources and figures for the thesis
│   ├── thesis.tex, chapXX.tex, bibliography.*
│   └── img/ (architecture, screenshots, etc.)
├── doc/                            # Supplemental docs (e.g., Specification.docx)
└── museum-guide.apk                # Built APK artifact
```

### Android tech stack
- **Language/Framework**: Kotlin, Jetpack Compose (Material 3, Navigation)
- **Camera**: CameraX (`camera-core`, `camera-camera2`, `camera-view`, `camera-lifecycle`)
- **ML**: TensorFlow Lite Task Vision, optional GPU delegate
- **Utilities**: Apache Commons CSV, AndroidX SplashScreen

### Licensing
- Code (Android app and Python utilities): MIT License — see `MuseumGuideApp/LICENSE`.
- Thesis text and figures: Creative Commons Attribution 4.0 — see `thesis/LICENSE`.
- Third‑party libraries are under their respective licenses.
- Some images/logos may be subject to separate rights; verify before reuse.
