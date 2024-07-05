package com.example.museumguide

sealed class Screens (val screen: String) {
    data object Home: Screens("home")
    data object PhotoScanner: Screens("photo_scanner")
    data object Gallery: Screens("gallery")
}