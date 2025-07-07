package com.example.museumguide

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.museumguide.data.TfLiteClassifier
import com.example.museumguide.domain.Classification
import com.example.museumguide.domain.ImageAnalyzer

@Composable
fun PhotoScanner(navigationController: NavHostController) {
    val context = LocalContext.current as Activity

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        shouldShowRationale = !isGranted && !context.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraScreen(navigationController)
    } else {
        if (shouldShowRationale) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera permission is required to use this feature. Please enable it in settings.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }) {
                        Text("Open Settings")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera permission is required to use this feature.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        launcher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("Request Permission")
                    }
                }
            }
        }
    }
}

@Composable
fun CameraScreen(navigationController: NavHostController, debugMode: Boolean = false) {
    val context = LocalContext.current

    var classifications by remember {
        mutableStateOf(emptyList<Classification>())
    }

    val analyzer = remember {
        ImageAnalyzer(
            classifier = TfLiteClassifier(
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
        CameraPreview(controller = controller, Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        ) {
            if (classifications.isNotEmpty()) {
                if (debugMode) {
                    // Debug Mode: Show all classifications with their scores
                    classifications.forEach { classification ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = classification.name,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Score: %.2f".format(classification.score),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                } else {
                    // Regular Mode: Show only the top classification
                    val topClassification = classifications[0]
                    val otherClassifications = classifications.drop(1).take(4).joinToString(",") { it.id.toString() }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = topClassification.name,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TopClassificationImage(
                        classification = topClassification,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            val exhibitId = topClassification.id
                            navigationController.navigate("exhibit_detail/$exhibitId?otherClassifications=$otherClassifications")
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    ) {
                        Text("See Details")
                    }
                }
            }
        }
    }
}

@Composable
fun TopClassificationImage(classification: Classification, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imagePath = "gallery/${classification.id}.jpg"
    val imageBitmap = getBitmapFromAssets(context, imagePath)

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .width(150.dp)
            .height(200.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "No Image", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}
