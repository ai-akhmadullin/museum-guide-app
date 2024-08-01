package com.example.museumguide.domain

import android.graphics.Bitmap

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}

fun Bitmap.normalizePixelValues(): Bitmap {
    val width = this.width
    val height = this.height
    val pixels = IntArray(width * height)
    this.getPixels(pixels, 0, width, 0, 0, width, height)

    val normalizedBitmap = Bitmap.createBitmap(width, height, this.config)

    for (i in pixels.indices) {
        val color = pixels[i]
        val alpha = (color shr 24 and 0xff) / 255.0
        val red = (color shr 16 and 0xff) / 255.0
        val green = (color shr 8 and 0xff) / 255.0
        val blue = (color and 0xff) / 255.0

        val normalizedColor = (alpha * 255).toInt() shl 24 or
                (red * 255).toInt() shl 16 or
                (green * 255).toInt() shl 8 or
                (blue * 255).toInt()

        normalizedBitmap.setPixel(i % width, i / width, normalizedColor)
    }

    return normalizedBitmap
}