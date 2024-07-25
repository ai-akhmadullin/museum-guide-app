package com.example.museumguide

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.museumguide.data.TfLiteIClassifier
import com.example.museumguide.domain.Classification
import com.example.museumguide.domain.ImageAnalyzer

@Composable
fun PhotoScanner(navigationController: NavHostController) {
    val context = LocalContext.current

    var classifications by remember {
        mutableStateOf(emptyList<Classification>())
    }

    val analyzer = remember {
        ImageAnalyzer(
            classifier = TfLiteIClassifier(
                context = context
            ),
            onResults = {
                classifications = it
            }
        )
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewWithOverlay(controller = controller, Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        ) {
            if (classifications.isNotEmpty()) {
                val topClassification = classifications[0]
                val otherClassifications = classifications.drop(1).take(4).joinToString(",") { it.id.toString() }

                Text(
                    text = topClassification.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(onClick = {
                    val exhibitId = topClassification.id
                    navigationController.navigate("exhibit_detail/$exhibitId?otherClassifications=$otherClassifications")
                }) {
                    Text("See Details")
                }

            }
        }
    }
}
