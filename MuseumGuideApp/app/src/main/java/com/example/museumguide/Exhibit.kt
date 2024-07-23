package com.example.museumguide

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader

data class Exhibit(
    val id: Int,
    val title: String,
    val author: String,
    val datingFrom: String,
    val datingTo: String
)

fun parseExhibits(context: Context): List<Exhibit> {
    val inputStream = context.assets.open("upm_exhibits_dataset.csv")
    val reader = InputStreamReader(inputStream)
    val csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT)
        .setHeader()
        .setSkipHeaderRecord(true)
        .build()

    val csvParser = CSVParser(reader, csvFormat)
    return csvParser.records.map { record ->
        Exhibit(
            id = record.get("id").toInt(),
            title = record.get("title"),
            author = record.get("author"),
            datingFrom = record.get("dating from"),
            datingTo = record.get("dating to")
        )
    }
}

fun getExhibitById(exhibits: List<Exhibit>, id: Int): Exhibit? {
    return exhibits.find { it.id == id }
}

@Composable
fun ExhibitDetailScreen(
    exhibitId: Int,
    otherClassifications: String,
    exhibits: List<Exhibit>,
    navController: NavController
) {
    val context = LocalContext.current
    val exhibit = remember { getExhibitById(exhibits, exhibitId) }
    val imagePath = "gallery/${exhibitId}.jpg"
    val imageBitmap = getBitmapFromAssets(context, imagePath)

    val alternativeIds = remember {
        otherClassifications.split(",").mapNotNull { it.toIntOrNull() }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        imageBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
        }

        Text("Title: ${exhibit?.title ?: "Unknown"}")
        Text("Author: ${exhibit?.author ?: "Unknown"}")
        Text("Dating From: ${exhibit?.datingFrom ?: "Unknown"}")
        Text("Dating To: ${exhibit?.datingTo ?: "Unknown"}")

        Button(onClick = { navController.navigate(Screens.PhotoScanner.screen) }) {
            Text("Back to Camera")
        }

        if (alternativeIds.isNotEmpty()) {
            Text(
                text = "The object recognized is not correct? You might want to check these:",
                modifier = Modifier.padding(top = 16.dp)
            )

            LazyRow {
                items(alternativeIds) { altExhibitId ->
                    val altImagePath = "gallery/${altExhibitId}.jpg"
                    val altImageBitmap = getBitmapFromAssets(context, altImagePath)

                    Box(modifier = Modifier.padding(8.dp)) {
                        altImageBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        navController.navigate("exhibit_detail/$altExhibitId")
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

