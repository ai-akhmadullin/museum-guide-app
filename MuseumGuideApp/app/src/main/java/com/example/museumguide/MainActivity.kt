package com.example.museumguide

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.museumguide.ui.theme.MuseumGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        }

        setContent {
            MuseumGuideTheme {
                val context = LocalContext.current
                val exhibits = remember { parseExhibits(context) }
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    MuseumGuideApp(navController, exhibits)
                }
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun MuseumGuideApp(navigationController: NavHostController, exhibits: List<Exhibit>) {
    val selectedIcon = remember {
        mutableStateOf(Icons.Default.Home)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Cyan
            ) {
                IconButton(
                    onClick = {
                        selectedIcon.value = Icons.Default.Home
                        navigationController.navigate(Screens.Home.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = if (selectedIcon.value == Icons.Default.Home) Color.White else Color.DarkGray
                    )
                }

                Box(modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(onClick = {
                        navigationController.navigate(Screens.PhotoScanner.screen) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Green)
                    }
                }

                IconButton(
                    onClick = {
                        selectedIcon.value = Icons.Default.PhotoLibrary
                        navigationController.navigate(Screens.Gallery.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = if (selectedIcon.value == Icons.Default.PhotoLibrary) Color.White else Color.DarkGray
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController = navigationController, startDestination = Screens.Home.screen,
            modifier = Modifier.padding(paddingValues)) {
            composable(Screens.Home.screen) { Home() }
            composable(Screens.PhotoScanner.screen) { PhotoScanner(navigationController) }
            composable(Screens.Gallery.screen) { Gallery(navigationController, exhibits) }
            composable(
                route = "exhibit_detail/{exhibitId}",
                arguments = listOf(navArgument("exhibitId") { type = NavType.IntType })
            ) { backStackEntry ->
                val exhibitId = backStackEntry.arguments?.getInt("exhibitId") ?: 0
                ExhibitDetailScreen(exhibitId, exhibits, navigationController)
            }
        }
    }
}


@Preview
@Composable
fun MuseumGuideAppPreview() {
    MuseumGuideTheme {
        val context = LocalContext.current
        val exhibits = remember { parseExhibits(context) }
        val navController = rememberNavController()
        MuseumGuideApp(navController, exhibits)
    }
}
