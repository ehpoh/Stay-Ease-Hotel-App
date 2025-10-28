package com.example.stayeasehotel.helper

import com.example.stayeasehotel.R

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.safeImagePainter
import kotlin.math.abs


@Composable
fun PreviewImageDialog(
    selectedImages: List<Uri>,
    previewIndex: Int,
    onDismiss: () -> Unit
) {
    if (previewIndex in selectedImages.indices) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImages[previewIndex]),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                )
            }
        }
    }
}


@Composable
fun ImageCarousel(imageUrls: List<String>) {
    var localPreviewIndex by remember { mutableStateOf(-1) }
    var localPreviewImages by remember { mutableStateOf<List<String>>(emptyList()) }

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val visibleIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2

            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                // Get the center of each item
                val itemCenter = item.offset + item.size / 2
                abs(itemCenter - viewportCenter)
            }?.index ?: 0
        }
    }

    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)) {


        // Show images carousel when not loading
        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(dimensionResource(R.dimen.dp_8))
        ) {
            items(imageUrls.size) { index ->

                val uri = imageUrls[index].toUri()

                val painter = safeImagePainter(imageUrls[index], imageLoader)


                val state = painter.state

                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.dp_325))
                        .fillMaxHeight()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            localPreviewImages = imageUrls
                            localPreviewIndex = index
                        }

                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Image $index",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (state is AsyncImagePainter.State.Error) {
                        Text(
                            text = stringResource(R.string.failed_to_load_image),
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }




            }




        }

    }

    if (localPreviewIndex != -1 && localPreviewImages.isNotEmpty()) {
        PreviewImageDialog(
            selectedImages = localPreviewImages.map { it.toUri() },
            previewIndex = localPreviewIndex,
            onDismiss = { localPreviewIndex = -1 }
        )
    }


    if (imageUrls.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.dp_4)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${visibleIndex + 1} / ${imageUrls.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
