package com.example.stayeasehotel.ui.ReportLostItemScreen

import android.Manifest

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.res.stringResource

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.stayeasehotel.R
import androidx.lifecycle.viewmodel.compose.viewModel


import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.stayeasehotel.data.LostItemData.LostItemDataSource
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

import com.example.stayeasehotel.helper.PreviewImageDialog
import com.example.stayeasehotel.model.UserEntity
import com.example.stayeasehotel.shareFilterScreen.FilterDialog
import com.example.stayeasehotel.shareFilterScreen.SharedFilterViewModel
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.LostandFoundCenterScreenBody
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.LostandFoundCenterViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostItemScreen(navController: NavController , viewModel: ReportLostItemViewModel = viewModel()) {


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.report_lost_and_found), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }


            )


        } ,


        content = { innerPadding ->

            ReportLostItemScreenBody( viewModel,modifier = Modifier.padding(innerPadding))

        }
    )
}




@Composable
fun ReportLostItemScreenBody( viewModel: ReportLostItemViewModel,modifier:Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()

    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        viewModel.setSelectedImages(uris)





    }
    if (uiState.isSubmitting) {
        Dialog(onDismissRequest = { /* block back press too */ }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }



    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.addCapturedImage(uiState.cameraImageUri)
            Toast.makeText(context, context.getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context,
                context.getString(R.string.failed_to_take_photo), Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = viewModel.createImageUri(context)
            viewModel.setCameraImageUri(uri)

            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context,
                context.getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }






    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(dimensionResource(R.dimen.dp_16))
    ) {





        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))

        // IMAGE GRID
        ImagePickerGrid(
            selectedImages = uiState.selectedImages,
            onAddImage = { viewModel.setShowDialog(true)
            },
            onRemoveImage = { index ->
                viewModel.setRemoveImage(index)
            },
            onPreviewImage = { index ->
                viewModel.setPreviewIndexChange(index)
            }
        )
        if (uiState.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowDialog(false)
                },
                title = { Text(stringResource(R.string.choose_image_source)) },
                text = { Text(stringResource(R.string.select_an_image_from_gallery_or_capture_using_camera)) },
                confirmButton = {
                    TextButton(onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        viewModel.setShowDialog(false)

                    }) {
                        Text(stringResource(R.string.camera))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setShowDialog(false)
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = {
                        imagePickerLauncher.launch(arrayOf("image/*"))
                        viewModel.setShowDialog(false)

                    }) {
                        Text(stringResource(R.string.gallery))
                    }
                }
            )
        }



        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))

        // FORM
        AddLostItemForm( viewModel,uiState)

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))





    }



    PreviewImageDialog(
        selectedImages = uiState.selectedImages,
        previewIndex = uiState.previewIndex,
        onDismiss = { viewModel.setPreviewIndexChange(-1) }
    )



}



@Composable
fun ImagePickerGrid(
    selectedImages: List<Uri>,
    onAddImage: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onPreviewImage: (Int) -> Unit
) {
    val columns = 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8)),
        content = {
            items(selectedImages.size) { index ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                        .clickable { onPreviewImage(index) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImages[index]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { onRemoveImage(index) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.remove), tint = Color.Red)
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.LightGray)
                        .clickable { onAddImage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_image))
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLostItemForm(

    viewModel: ReportLostItemViewModel,
    uiState: ReportLostItemUiState
) {



    val context = LocalContext.current




    val fieldKeys = LostItemDataSource.fieldKeys
    val categories = LostItemDataSource.categories
    val locations = LostItemDataSource.locations





    Column {



        fieldKeys.forEach { resId ->
            val keyName = stringResource(resId)
            val showAsterisk = resId != R.string.description && resId != R.string.lost_id



            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_6)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.dp_8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.dp_8))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFF9FAFF), Color(0xFFEFF3FF))
                            )
                        ),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8))
                ) {

                    Row {

                        Text(
                            text = keyName+":",
                            fontSize = 16.sp
                        )
                        if (showAsterisk) {
                            Text(
                                text = " *",
                                color = Color.Red,
                                fontSize = 16.sp
                            )
                        }
                    }


                    when (resId) {


                        R.string.date_found -> {
                            OutlinedTextField(
                                value = uiState.dateFound,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setShowDatePicker(true) },
                                enabled = false,
                                placeholder = { Text(stringResource(R.string.select_date)) }
                            )
                        }

                        R.string.time_found -> {
                            OutlinedTextField(
                                value = uiState.timeFound,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setShowTimePicker(true) },
                                enabled = false,
                                placeholder = { Text(stringResource(R.string.select_time)) }
                            )

                            if (uiState.showTimePicker) {


                                val calendar = Calendar.getInstance()
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)

                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        val calendar = Calendar.getInstance().apply {
                                            set(Calendar.HOUR_OF_DAY, selectedHour)
                                            set(Calendar.MINUTE, selectedMinute)
                                        }
                                        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)

                                        viewModel.setTimeFoundChange(time)

                                        viewModel.setShowTimePicker(false)
                                    },
                                    hour,
                                    minute,
                                    false
                                ).show()
                                viewModel.setShowTimePicker(false)

                            }
                        }




                        R.string.found_location -> {
                            ExposedDropdownMenuBox(
                                expanded = uiState.expandedLocation,
                                onExpandedChange = { viewModel.setExpandedLocationChange(!uiState.expandedLocation) }
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedLocation,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.select_found_location)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedLocation)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = uiState.expandedLocation,
                                    onDismissRequest = { viewModel.setExpandedLocationChange(false) }
                                ) {
                                    locations.forEach { location ->
                                        DropdownMenuItem(
                                            text = { Text(stringResource(location)) },
                                            onClick = {

                                                viewModel.setFoundLocationChange(
                                                    context.getString(
                                                        location
                                                    )
                                                )
                                                viewModel.setExpandedLocationChange(false)
                                            }
                                        )
                                    }
                                }
                            }

                            if (uiState.selectedLocation == stringResource(R.string.others)) {
                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_8)))
                                OutlinedTextField(
                                    value = uiState.otherLocation,
                                    onValueChange = {

                                        viewModel.setOtherFoundLocationChange(it)


                                    },
                                    label = { Text(stringResource(R.string.please_specify)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (uiState.selectedLocation == stringResource(R.string.room)) {
                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_8)))

                                ExposedDropdownMenuBox(
                                    expanded = uiState.expandedRoomID,
                                    onExpandedChange = { viewModel.setExpandedRoomIDChange(!uiState.expandedRoomID) }
                                ) {
                                    OutlinedTextField(
                                        value = uiState.selectedRoomID,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.select_room)) },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedRoomID)
                                        },
                                        modifier = Modifier
                                            .menuAnchor() // ⬅ important for positioning
                                            .fillMaxWidth()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = uiState.expandedRoomID,
                                        onDismissRequest = {
                                            viewModel.setExpandedRoomIDChange(
                                                false
                                            )
                                        }
                                    ) {

                                        viewModel.roomIdList.forEach { roomId ->
                                            DropdownMenuItem(
                                                text = { Text(roomId) },
                                                onClick = {
                                                    viewModel.updateSelectedRoomID(roomId)  // update ViewModel state
                                                    viewModel.setExpandedRoomIDChange(false)  // close menu
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        R.string.category -> {


                            ExposedDropdownMenuBox(
                                expanded = uiState.expandedCategory,
                                onExpandedChange = {
                                    viewModel.setExpandedCategoryChange(!uiState.expandedCategory)

                                }
                            ) {

                                OutlinedTextField(
                                    value = uiState.selectedCategory,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.select_category)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            uiState.expandedCategory
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = uiState.expandedCategory,
                                    onDismissRequest = { viewModel.setExpandedCategoryChange(false) }
                                ) {
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(context.getString(category)) },
                                            onClick = {
                                                viewModel.setCategoryChange(
                                                    context.getString(
                                                        category
                                                    )
                                                )
                                                viewModel.setExpandedCategoryChange(false)

                                            }
                                        )
                                    }
                                }
                            }

                            if (uiState.selectedCategory == stringResource(R.string.others)) {
                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_8)))
                                OutlinedTextField(
                                    value = uiState.otherCategory,
                                    onValueChange = {
                                        viewModel.setOtherCategoryChange(it)


                                    },
                                    label = { Text(stringResource(R.string.please_specify)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        else -> {
                            val value = when (resId) {
                                R.string.item_title -> uiState.itemTitle
                                R.string.description -> uiState.description
                                else -> ""
                            }

                            val onValueChange: (String) -> Unit = when (resId) {
                                R.string.item_title -> viewModel::setTitleChange
                                R.string.description -> viewModel::setDescriptionChange
                                else -> { _ -> /* no-op */ }
                            }


                            OutlinedTextField(
                                value = value,
                                onValueChange = onValueChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Please Enter ${context.getString(resId)}") },

                                )
                        }


                    }

                }



            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))
        }



        Button(
            onClick = {
                viewModel.ReportNewLostItem()

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.dp_50)),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.submit_lost_item),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        showDialog(viewModel, uiState)

    }


    if (uiState.showDatePicker) {
        val todayInMillis = remember {
            viewModel.getTodayInMillis()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = todayInMillis,
            initialDisplayMode = DisplayMode.Picker,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return viewModel.isDateSelectable(utcTimeMillis)
                }
            }
        )


        DatePickerDialog(
            onDismissRequest = { viewModel.setShowDatePicker(false) },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formattedDate = viewModel.formatDate(millis)




                        // If valid
                        viewModel.setDateFoundError(null)
                        viewModel.setDateFoundChange(formattedDate)
                        viewModel.setShowDatePicker(false)




                    }

                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.setDateFoundError(null)
                    viewModel.setShowDatePicker(false)
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            Column {
                DatePicker(state = datePickerState)

                uiState.dateFoundError?.let { error ->

                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.dp_8))
                    )
                }
            }
        }
    }










}







@Composable
fun showDialog(viewModel: ReportLostItemViewModel,
               uiState: ReportLostItemUiState) {
    if (uiState.submissionSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            confirmButton = {
                Button(onClick = { viewModel.clearMessages() }) {
                    Text(stringResource(R.string.ok))
                }
            },
            title = { Text(stringResource(R.string.success)) },
            text = {
                if (viewModel.isStaff) {
                    Text(
                        "Lost item successfully recorded.\n\n" +
                                "Lost Item ID: ${uiState.submittedLostId}\n\n" +
                                "This record has been saved and is now visible to staff for further actions."
                    )
                } else {
                    Text(
                        "Your lost item has been submitted successfully!\n" +
                                "Lost Item ID: ${uiState.submittedLostId}\n\n" +
                                "Please note: Your submission will be reviewed by our staff. " +
                                "It will only appear in the Lost & Found center once it has been approved."
                    )
                }
            }
        )
    }

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            confirmButton = {
                Button(onClick = { viewModel.clearMessages() }) {
                    Text("OK")
                }
            },
            title = { Text("Error") },
            text = { Text(uiState.errorMessage ?: "Unknown error") }
        )
    }


}



