package com.example.museumguide

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Home() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val images = loadImagesFromAssets(context, "gallery").take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.museum_name),
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "ABOUT US",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.about_museum),
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            images.forEach { image ->
                val bitmap = getBitmapFromAssets(context, "gallery/$image")

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(end = 8.dp)
                    )
                }
            }
        }

        Text(
            text = "NEWS",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.news_1),
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = stringResource(id = R.string.news_2),
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color.Gray
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Contact information:",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Address: \n" + stringResource(id = R.string.museum_address),
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Phone: " + stringResource(id = R.string.museum_phone),
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Follow us:",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconWithLink(
                    context = context,
                    iconPath = "facebook-icon.png",
                    link = stringResource(id = R.string.facebook_link)
                )
                Spacer(modifier = Modifier.size(32.dp))
                IconWithLink(
                    context = context,
                    iconPath = "instagram-icon.png",
                    link = stringResource(id = R.string.instagram_link)
                )
                Spacer(modifier = Modifier.size(32.dp))
                IconWithLink(
                    context = context,
                    iconPath = "youtube-icon.png",
                    link = stringResource(id = R.string.youtube_link)
                )
            }
        }
    }
}

@Composable
fun IconWithLink(context: Context, iconPath: String, link: String) {
    val bitmap = getBitmapFromAssets(context, iconPath)
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                }
        )
    }
}
