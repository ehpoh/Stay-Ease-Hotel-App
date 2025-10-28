package com.example.stayeasehotel.shareFilterScreen



import com.example.stayeasehotel.R

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.semantics.SemanticsPropertyKey


import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.stayeasehotel.data.LostItemData.LostItemDataSource
import com.example.stayeasehotel.shareFilterScreen.SharedFilterViewModel

import com.example.stayeasehotel.shareFilterScreen.FilterUiState

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar



import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SharedFilterSection(

    viewModel: SharedFilterViewModel,
//    categories: List<String>,
//    locations: List<String>,
    title: String,
    onFilterApplied: () -> Unit,
    modifier: Modifier = Modifier
) {


    val uiState by viewModel.uiState.collectAsState()


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.dp_8))
    ) {
        var localSearchQuery by rememberSaveable  { mutableStateOf(uiState.searchQuery) }

        // Search Bar
        OutlinedTextField(
            value = localSearchQuery,
            onValueChange = { localSearchQuery=it },
            placeholder = { Text(stringResource(R.string.search_items)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.updateSearchQuery(localSearchQuery)
                    viewModel.performSearch()
                }
            )
        )


        Spacer(Modifier.height(dimensionResource(R.dimen.dp_8)))




        Spacer(Modifier.height(dimensionResource(R.dimen.dp_2)))
        ClearFilterDialog(uiState,viewModel)

    }
}


@Composable
fun AppliedFiltersSummary(
    filters: Map<String, List<String>>,
    onRemoveFilter: (filterType: String, value: String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.dp_8))
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = dimensionResource(R.dimen.dp_8)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_12))
        ) {
            filters.forEach { (filterType, values) ->
                Text("$filterType:", modifier = Modifier.alignByBaseline())

                if (values.isEmpty() || (values.size == 1 && values.contains("All"))) {
                    FilterChip(
                        label = "All",
                        showRemoveIcon = false,
                        onRemove = { onRemoveFilter(filterType, "All") }
                    )
                } else {
                    values.filter { it != "All" }.forEach { value ->
                        FilterChip(
                            label = value,
                            showRemoveIcon = true,
                            onRemove = { onRemoveFilter(filterType, value) }
                        )
                    }
                }
            }
        }

        // Show "Clear All" button if any filters are applied
        val isAnyFilterApplied = filters.any { (key, values) ->
            values.any { it != "All" }
        }

        if (isAnyFilterApplied) {
            TextButton(onClick = onClearAll) {
                Text("Clear All Filters")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    viewModel: SharedFilterViewModel,
    options: FilterOptions = FilterOptions()

) {
    val uiState by viewModel.uiState.collectAsState()


    val categoryLabels =
        listOf("All") + LostItemDataSource.categories.map { id -> stringResource(id) }
    val locationLabels =
        listOf("All") + LostItemDataSource.locations.map { id -> stringResource(id) }


    val submitterLabels =
        listOf("All") + LostItemDataSource.submitter.map { id -> stringResource(id) }
    if (uiState.isFilterDialogVisible) {
        Dialog(onDismissRequest = { viewModel.toggleFilterDialog(false) }) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.dp_12)),
                tonalElevation = dimensionResource(R.dimen.dp_4)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(dimensionResource(R.dimen.dp_16))
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(R.dimen.dp_8)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //  Close icon
                        IconButton(
                            onClick = {
                                viewModel.toggleFilterDialog(false)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close_filter_dialog)
                            )
                        }

                        //   Title expands between the icons
                        Text(
                            stringResource(R.string.filter_lost_items),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        //   Filter button
                        TextButton(onClick = {
                            val isValid = viewModel.validateDateTimeRange()
                            if (isValid) {
                                viewModel.performSearch()
                                viewModel.toggleFilterDialog(false)
                            }
                        }) {
                            Text(stringResource(R.string.filter))
                        }
                    }


                    if (options.showDateTime) {
                        val context = LocalContext.current
                        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                        val todayInMillis = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }.timeInMillis

                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_16)))

                        Text(
                            text = "📅 Date Range",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val picker = DatePickerDialog(context)
                                    picker.datePicker.maxDate = System.currentTimeMillis()

                                    uiState.endDate?.let { endDate ->
                                        val cal = Calendar.getInstance().apply {
                                            set(
                                                endDate.year,
                                                endDate.monthValue - 1,
                                                endDate.dayOfMonth
                                            )

                                        }
                                        picker.datePicker.maxDate =
                                            cal.timeInMillis  //restricted the date after end date
                                    }

                                    picker.setOnDateSetListener { _, year, month, day ->
                                        viewModel.setStartDate(LocalDate.of(year, month + 1, day))
                                    }
                                    picker.show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = uiState.startDate?.format(dateFormatter) ?: stringResource(
                                            R.string.start
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = stringResource(R.string.select_start_date),
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_8))) // optional spacing

                            OutlinedButton(
                                onClick = {
                                    val picker = DatePickerDialog(context)
                                    picker.datePicker.maxDate = todayInMillis
                                    uiState.startDate?.let { startDate ->
                                        val cal = Calendar.getInstance().apply {
                                            set(
                                                startDate.year,
                                                startDate.monthValue - 1,
                                                startDate.dayOfMonth
                                            )
                                        }
                                        picker.datePicker.minDate = cal.timeInMillis
                                    }
                                    picker.setOnDateSetListener { _, year, month, day ->
                                        viewModel.setEndDate(LocalDate.of(year, month + 1, day))
                                    }
                                    picker.show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = uiState.endDate?.format(dateFormatter) ?: "End",
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select end date",
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )
                                }
                            }
                        }




                        val calendar = Calendar.getInstance()

                        val defaultHour = uiState.startTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
                        val defaultMinute = uiState.startTime?.minute ?: calendar.get(Calendar.MINUTE)

                        // 🔹 Time Range Section
                        Text(
                            text = "⏰ Time Range",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {

                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val selectedStartTime = LocalTime.of(hour, minute)


                                            viewModel.setStartTime(selectedStartTime)


                                        },
                                        defaultHour,
                                        defaultMinute,
                                        false
                                    ).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = uiState.startTime?.format(timeFormatter) ?: stringResource(R.string.start),
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = stringResource(R.string.select_start_time),
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )
                                }

                            }

                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_8)))


                            val defaultHour1 =
                                uiState.endTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
                            val defaultMinute1 =
                                uiState.endTime?.minute ?: calendar.get(Calendar.MINUTE)


                            val context = LocalContext.current
                            LaunchedEffect(Unit) {
                                viewModel.errorEvent.collect { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }


                            OutlinedButton(
                                onClick = {

                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->

                                            val selectedEndTime = LocalTime.of(hour, minute)
                                            viewModel.setEndTime(selectedEndTime)


                                        },
                                        defaultHour1,
                                        defaultMinute1,
                                        false
                                    ).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = uiState.endTime?.format(timeFormatter) ?: stringResource(
                                            R.string.end
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    )
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(
                                        R.string.select_end_time
                                    ), modifier = Modifier.align(Alignment.CenterEnd))
                                }

                            }
                        }

                    }




                    if (options.showCategory) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setIsCategoryExpanded(!uiState.isCategoryExpanded) }
                                .padding(vertical = dimensionResource(R.dimen.dp_8)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.category),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (uiState.isCategoryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (uiState.isCategoryExpanded) stringResource(
                                    R.string.collapse
                                ) else stringResource(R.string.expand)
                            )
                        }

                        AnimatedVisibility(visible = uiState.isCategoryExpanded) {
                            Column {
                                categoryLabels.forEach { category ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = dimensionResource(R.dimen.dp_8)),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = category in uiState.selectedCategories,
                                            onCheckedChange = { checked ->
                                                viewModel.toggleCategory(category, checked)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_8)))
                                        Text(
                                            text = category,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }



                    if (options.showLocation) {
                        // LOCATION SECTION
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setIsLocationExpanded(!uiState.isLocationExpanded) }
                                .padding(vertical = dimensionResource(R.dimen.dp_8)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.location),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (uiState.isLocationExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (uiState.isLocationExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                            )
                        }

                        AnimatedVisibility(visible = uiState.isLocationExpanded) {
                            Column {
                                locationLabels.forEach { location ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = dimensionResource(R.dimen.dp_8)),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = location in uiState.selectedLocations,
                                            onCheckedChange = { checked ->
                                                viewModel.toggleLocation(location, checked)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_8)))
                                        Text(
                                            text = location,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (options.showSubmitter) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setIsSubmitterExpanded(!uiState.isSubmitterExpanded) }
                                .padding(vertical = dimensionResource(R.dimen.dp_8)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.submitter),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (uiState.isSubmitterExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (uiState.isSubmitterExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                            )
                        }

                        // Expandable checkbox list
                        AnimatedVisibility(visible = uiState.isSubmitterExpanded) {
                            Column {
                                submitterLabels.forEach { submitter ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = dimensionResource(R.dimen.dp_8)),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = submitter in uiState.selectedSubmitters,
                                            onCheckedChange = { checked ->
                                                viewModel.toggleSubmitter(submitter, checked)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_8)))
                                        Text(
                                            text = submitter,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }



                    }

                    if (options.showStatus) {  // Assuming you renamed or adjusted options property for status
                        val statusLabels = listOf("All", "Pending", "Approved", "Rejected", "Deleted","In Progress")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setIsStatusExpanded(!uiState.isStatusExpanded) }
                                .padding(vertical = dimensionResource(R.dimen.dp_8)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.status),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (uiState.isStatusExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (uiState.isStatusExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                            )
                        }

                        AnimatedVisibility(visible = uiState.isStatusExpanded) {
                            Column {
                                statusLabels.forEach { status ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = dimensionResource(R.dimen.dp_8)),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = status in uiState.selectedStatuses,
                                            onCheckedChange = { checked ->
                                                viewModel.toggleStatus(status, checked)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_8)))
                                        Text(
                                            text = status,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }


                }
            }
        }
    }
}





@Composable
fun FilterChip(label: String,showRemoveIcon:Boolean=true, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        tonalElevation = dimensionResource(R.dimen.dp_2)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.dp_12), vertical = dimensionResource(R.dimen.dp_6))
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_4)))


            if (showRemoveIcon) { // Show the close icon only if this flag is true
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.remove_filter),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.dp_16))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onRemove() }
                )
            }

        }
    }
}





@Composable
fun ClearFilterDialog(uiState: FilterUiState, viewModel: SharedFilterViewModel) {
    if (uiState.showClearFiltersConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearFiltersConfirmationDialog(false) },
            title = { Text("Confirm Clear Filters") },
            text = { Text("Do you really want to clear all filters?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllFilters()
                    viewModel.showClearFiltersConfirmationDialog(false)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.showClearFiltersConfirmationDialog(false)
                }) {
                    Text("Cancel")
                }
            }
        )
    }

}