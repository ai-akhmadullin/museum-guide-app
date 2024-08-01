package com.example.museumguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoLibrary
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
        installSplashScreen()
        enableEdgeToEdge()

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
}

@Composable
fun MuseumGuideApp(navigationController: NavHostController, exhibits: List<Exhibit>) {
    val selectedIcon = remember { mutableStateOf(Icons.Default.Home) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.DarkGray
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
                    val homeIcon = if (selectedIcon.value == Icons.Default.Home) Icons.Filled.Home else Icons.Outlined.Home
                    Icon(
                        imageVector = homeIcon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(onClick = {
                        selectedIcon.value = Icons.Default.CameraAlt
                        navigationController.navigate(Screens.PhotoScanner.screen) {
                            popUpTo(0)
                        }
                    }) {
                        val cameraIcon = if (selectedIcon.value == Icons.Default.CameraAlt) Icons.Filled.CameraAlt else Icons.Outlined.CameraAlt
                        Icon(
                            imageVector = cameraIcon,
                            contentDescription = null,
                            tint = Color.Black
                        )
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
                    val photoLibraryIcon = if (selectedIcon.value == Icons.Default.PhotoLibrary) Icons.Filled.PhotoLibrary else Icons.Outlined.PhotoLibrary
                    Icon(
                        imageVector = photoLibraryIcon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navigationController,
            startDestination = Screens.Home.screen,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screens.Home.screen) { Home() }
            composable(Screens.PhotoScanner.screen) { PhotoScanner(navigationController) }
            composable(Screens.Gallery.screen) { Gallery(navigationController, exhibits) }
            composable(
                route = "exhibit_detail/{exhibitId}?otherClassifications={otherClassifications}",
                arguments = listOf(
                    navArgument("exhibitId") { type = NavType.IntType },
                    navArgument("otherClassifications") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val exhibitId = backStackEntry.arguments?.getInt("exhibitId") ?: 0
                val otherClassifications = backStackEntry.arguments?.getString("otherClassifications").orEmpty()
                ExhibitDetailScreen(
                    exhibitId = exhibitId,
                    otherClassifications = otherClassifications,
                    exhibits = exhibits,
                    navController = navigationController
                )
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
