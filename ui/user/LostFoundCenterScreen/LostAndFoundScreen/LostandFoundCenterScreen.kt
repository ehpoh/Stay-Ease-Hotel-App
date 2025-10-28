package com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen


import android.R.attr.action
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

import com.example.stayeasehotel.R
import com.example.stayeasehotel.data.LostItemData.LostItemDataSource

import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.helper.PreviewImageDialog

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.request.CachePolicy
import java.time.format.DateTimeFormatter
import kotlin.collections.isNotEmpty
import kotlin.math.abs




import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DatePickerDefaults.dateFormatter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.stayeasehotel.data.LostItemData.LostItemDataSource.urgentCategoryIds
import com.example.stayeasehotel.helper.ImageCarousel
import com.example.stayeasehotel.shareFilterScreen.AppliedFiltersSummary
import com.example.stayeasehotel.shareFilterScreen.ClearFilterDialog
import com.example.stayeasehotel.shareFilterScreen.FilterDialog
import com.example.stayeasehotel.shareFilterScreen.SharedFilterSection
import com.example.stayeasehotel.shareFilterScreen.SharedFilterViewModel
import com.example.stayeasehotel.ui.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.time.Instant


import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId


import java.util.Calendar
import kotlin.collections.filter
import kotlin.collections.sortedByDescending
import kotlin.text.category


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostandFoundCenterScreen(navController: NavController,lostAndFoundViewModel: LostandFoundCenterViewModel = viewModel(),
                             sharedFilterViewModel: SharedFilterViewModel = viewModel()) {

    val lostUiState  by lostAndFoundViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(lostUiState.lostItems) {
        sharedFilterViewModel.setAllLostItems(lostUiState.lostItems)
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lost and Found", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },


                actions = {
                    IconButton(onClick = { sharedFilterViewModel.toggleFilterDialog(true) }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter")
                    }
                    IconButton(onClick = {
                        navController.navigate("report_lost_item")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Report Lost Item")
                    }


                }
            )


        } ,

        snackbarHost = { SnackbarHost(snackbarHostState) },

        content = { innerPadding ->

            LostandFoundCenterScreenBody(
                uiState = lostUiState,
                lostAndFoundViewModel = lostAndFoundViewModel,
                sharedFilterViewModel = sharedFilterViewModel,
                navController,
                modifier = Modifier.padding(innerPadding)
            )

            FilterDialog(sharedFilterViewModel)
        }
    )





}







@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LostandFoundCenterScreenBody(
    uiState: LostandFoundCenterUiState,
    lostAndFoundViewModel: LostandFoundCenterViewModel,
    sharedFilterViewModel: SharedFilterViewModel,
    navController: NavController,
    modifier: Modifier
) {


    var selectedItem by remember { mutableStateOf<LostItemEntity?>(null) }



    val filteredItems by sharedFilterViewModel.filteredLostItems.collectAsState()
    val sharedFilterUiState by sharedFilterViewModel.uiState.collectAsState()
    val urgentItems = filteredItems.filter { item ->
        urgentCategoryIds.any { urgentId ->
            item.category.equals(
                stringResource(id = urgentId),
                ignoreCase = true
            )
        }
    }

    val recentItems = filteredItems.sortedByDescending { it.dateFound }.take(5)



    Column( modifier = modifier
        .fillMaxSize()
        .padding(dimensionResource(R.dimen.dp_4))
        .verticalScroll(rememberScrollState()))  {
        // Filters Section
        SharedFilterSection(
            viewModel = sharedFilterViewModel,
            title = "Filters",
            onFilterApplied = { sharedFilterViewModel.performSearch() }

        )

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")


        val filters = mapOf(
            "Category" to sharedFilterUiState.selectedCategories,
            "Location" to sharedFilterUiState.selectedLocations,
            "Start Date" to listOf(sharedFilterUiState.startDate?.format(dateFormatter) ?: "All"),
            "End Date" to listOf(sharedFilterUiState.endDate?.format(dateFormatter) ?: "All"),
            "Start Time" to listOf(sharedFilterUiState.startTime?.format(timeFormatter) ?: "All"),
            "End Time" to listOf(sharedFilterUiState.endTime?.format(timeFormatter) ?: "All"),
        )



        AppliedFiltersSummary(
            filters = filters,
            onRemoveFilter = { type, value -> sharedFilterViewModel.removeFilter(type, value) },
            onClearAll = { sharedFilterViewModel.showClearFiltersConfirmationDialog(true) }
        )




        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))

        Text("Recently Reported", style = MaterialTheme.typography.titleMedium)
        if (uiState.inserting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                ,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }else{
            if (recentItems.isNotEmpty()) {

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(375.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(recentItems.size) { index ->
                        LostItemCardCompact(
                            item = recentItems[index],
                            viewModel = lostAndFoundViewModel,
                            onImageClick = { selectedItem = recentItems[index] }
                        )
                    }
                }

            }else {
                // Display "No items found" message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No lost items found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            Text(
                "🚨 Urgent Items",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (urgentItems.isNotEmpty()) {


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(375.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(urgentItems.size) { index ->
                        LostItemCardCompact(
                            item = urgentItems[index],
                            viewModel = lostAndFoundViewModel,
                            onImageClick = { selectedItem = urgentItems[index] }
                        )
                    }
                }

            }
            else {
                // Display "No items found" message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No lost items found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            Text(
                "All Items",
                style = MaterialTheme.typography.titleMedium,

                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (filteredItems.isNotEmpty()) {


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(375.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(filteredItems.size) { index ->
                        LostItemCardCompact(
                            item = filteredItems[index],
                            viewModel = lostAndFoundViewModel,
                            onImageClick = { selectedItem = filteredItems[index] }
                        )
                    }
                }

            }else {
                // Display "No items found" message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No lost items found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }


        }



        if (selectedItem != null) {
            LaunchedEffect(selectedItem) {
                navController.navigate("lost_item_detail/${selectedItem!!.id}")
                selectedItem = null
            }

        }






    }



}


@Composable
fun LostItemCardCompact(
    item: LostItemEntity,
    viewModel: LostandFoundCenterViewModel,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .padding(vertical = 6.dp)
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .width(250.dp)
                .padding(12.dp)

        ) {

            //  Image Carousel (if available)
            if (item.imageUrls.isNotEmpty()) {

                Image(
                    painter = rememberAsyncImagePainter(item.imageUrls[0]),
                    contentDescription = "Url",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop
                )

            }else{
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .clickable { onImageClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image not available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }

            }





            InfoRow("\uD83D\uDCE6 ${stringResource(R.string.item_title)}:", item.itemTitle)




            InfoRow("🆔 ${stringResource(R.string.lost_id)}:", item.id)



            InfoRow("📍 Found ${stringResource(R.string.found_location)}:", item.foundLocation)


            InfoRow("📅 Found ${stringResource(R.string.date_found)}:", item.dateFound.toString())


        }
    }
}

suspend fun Context.shareTextAndImages(
    imageUrls: List<String>,
    shareText: String
) {
    try {
        val uris = mutableListOf<Uri>()

        for (url in imageUrls) {
            val uri = Uri.parse(url)
            val contentUri = when (uri.scheme) {
                "http", "https" -> {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val body = response.body ?: continue

                    val cacheFile = File(cacheDir, "shared_image_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(cacheFile).use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    }

                    FileProvider.getUriForFile(this, "$packageName.provider", cacheFile)
                }
                "content", "file" -> uri
                else -> continue
            }

            uris.add(contentUri)
        }

        if (uris.isNotEmpty()) {
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share Item via"))
        } else {
            throw Exception("No valid images to share.")
        }

    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(this@shareTextAndImages, "Failed to share images", Toast.LENGTH_SHORT).show()
        }
    }
}


@Composable
fun InfoRow(iconLabel: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Text(
            text = iconLabel,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 13.5.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 13.5.sp

        )
    }
}




@Composable
fun safeImagePainter(uriString: String, imageLoader: ImageLoader): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = uriString.toUri(),
        imageLoader = imageLoader,
        contentScale = ContentScale.Crop
    )
}
