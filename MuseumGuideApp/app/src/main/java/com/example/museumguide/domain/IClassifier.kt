package com.example.museumguide.domain

import android.graphics.Bitmap

interface IClassifier {
    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
}