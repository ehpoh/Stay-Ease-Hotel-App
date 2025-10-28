package com.example.stayeasehotel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionScreen(
    checkInDate: Long?,
    checkOutDate: Long?,
    roomCount: Int,
    isDateSelectable: (Long) -> Boolean,
    setDates: (start: Long?, end: Long?) -> Unit,
    increaseRoomCount: () -> Unit,
    decreaseRoomCount: () -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val datePattern = stringResource(R.string.date_pattern_1)
    val noDateLabel = stringResource(R.string.no_date_selected)
    val formatter = DateTimeFormatter.ofPattern(datePattern)

    val customSelectableDates = object : SelectableDates {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return isDateSelectable(utcTimeMillis)
        }


        override fun isSelectableYear(year: Int): Boolean {
            val today = LocalDate.now()
            val yearLater = today.plusYears(1)
            return year in today.year..yearLater.year
        }
    }

    val currentMonthMillis = YearMonth.now()
        .atDay(1) // first day of current month
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    // Date range picker state
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = customSelectableDates,
        initialDisplayedMonthMillis = currentMonthMillis,
        initialSelectedStartDateMillis = checkInDate,
        initialSelectedEndDateMillis = checkOutDate
    )

    // Sync picker selection to ViewModel
    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        setDates(
            dateRangePickerState.selectedStartDateMillis,
            dateRangePickerState.selectedEndDateMillis
        )
    }

    // Display selected dates
    val checkInText = dateRangePickerState.selectedStartDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            .format(formatter)
    } ?: noDateLabel

    val checkOutText = dateRangePickerState.selectedEndDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            .format(formatter)
    } ?: noDateLabel

    // Search enabled condition
    val isSearchEnabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.dp_16) )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_16)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Calendar date range picker in container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.dp_400))
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {},
                headline = {},
                showModeToggle = false, // always show calendar mode
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_32))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.check_in_date),
                        fontSize = dimensionResource(R.dimen.sp_20).value.sp
                    )
                    OutlinedButton(onClick = { }) {
                        Text(
                            checkInText,
                            fontSize = dimensionResource(R.dimen.sp_20).value.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.check_out_date),
                        fontSize = dimensionResource(R.dimen.sp_20).value.sp
                    )
                    OutlinedButton(onClick = { }) {
                        Text(
                            checkOutText,
                            fontSize = dimensionResource(R.dimen.sp_20).value.sp
                        )
                    }
                }
            }
        }


        // Room count input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.dp_8))
        ) {
            Text(
                stringResource(R.string.room_count),
                fontSize = dimensionResource(R.dimen.sp_20).value.sp,
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = decreaseRoomCount,
                enabled = roomCount > 1,
                shape = CircleShape,
                contentPadding = PaddingValues(dimensionResource(R.dimen.dp_0)),
                modifier = Modifier.size(dimensionResource(R.dimen.dp_40)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (roomCount > 1) Color.Black else Color.Gray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.decrease),
                )
            }

            Text(
                text = roomCount.toString(),
                fontSize = dimensionResource(R.dimen.sp_20).value.sp,
                modifier = Modifier.width(dimensionResource(R.dimen.dp_40)),
                textAlign = TextAlign.Center
            )

            OutlinedButton(
                onClick = increaseRoomCount,
                enabled = roomCount < 10,
                shape = CircleShape,
                contentPadding = PaddingValues(dimensionResource(R.dimen.dp_0)),
                modifier = Modifier.size(dimensionResource(R.dimen.dp_40)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (roomCount < 10) Color.Black else Color.Gray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.increase),
                )
            }
        }

        // Search button
        Button(
            onClick = {
                onNextClicked()
            },
            enabled = isSearchEnabled,
            modifier = Modifier
                .width(dimensionResource(R.dimen.dp_180))
        ) {
            Text(
                stringResource(R.string.search),
                fontSize = dimensionResource(R.dimen.sp_20).value.sp
            )
        }
    }

}

@Preview
@Composable
fun DateSelectionPreview() {
    DateSelectionScreen(
        checkInDate = null,
        checkOutDate = null,
        roomCount = 1,
        isDateSelectable = { true },
        setDates = { _, _ -> },
        increaseRoomCount = {},
        decreaseRoomCount = {},
        onNextClicked = {}
    )
}
