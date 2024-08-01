package com.example.museumguide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.io.IOException

@Composable
fun Gallery(navController: NavHostController, exhibits: List<Exhibit>) {
    val context = LocalContext.current
    val images = loadImagesFromAssets(context, "gallery")

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(images) { image ->
            val bitmap = getBitmapFromAssets(context, "gallery/$image", 400, 500)
            val exhibitId = image.substringBefore(".").toIntOrNull()

            if (bitmap != null && exhibitId != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            navController.navigate("exhibit_detail/$exhibitId")
                        }
                )
            }
        }
    }
}

fun loadImagesFromAssets(context: Context, path: String): List<String> {
    return try {
        context.assets.list(path)?.toList() ?: emptyList()
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}

fun getBitmapFromAssets(context: Context, filePath: String, width: Int? = null, height: Int? = null): Bitmap? {
    return try {
        val inputStream = context.assets.open(filePath)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        if (width != null && height != null) {
            Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        } else {
            originalBitmap
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}