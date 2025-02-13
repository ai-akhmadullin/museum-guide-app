package com.example.museumguide.domain

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class ImageAnalyzer(
    private val classifier: IClassifier,
    private val onResults: (List<Classification>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 30 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .normalizePixelValues()

            val results = classifier.classify(bitmap, rotationDegrees)
            onResults(results)
        }
        frameSkipCounter++

        image.close()
    }
}