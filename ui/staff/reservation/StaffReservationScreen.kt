package com.example.stayeasehotel.ui.staff.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.stayeasehotel.R
import com.example.stayeasehotel.ui.theme.Blue
import com.example.stayeasehotel.ui.theme.LightGreen
import com.example.stayeasehotel.ui.theme.Red
import com.example.stayeasehotel.ui.theme.Yellow

import com.example.stayeasehotel.ui.viewmodel.StaffBookingViewModel

@Composable
fun StaffReservationScreen(
    viewModel: StaffBookingViewModel,
    onReservationClick: (String) -> Unit
) {
    val reservations by viewModel.filteredReservations.collectAsState()
    var reservationToDelete by remember { mutableStateOf<String?>(null) }

    Box {
        LazyColumn {
            items(reservations) { reservation ->
                ManagementCard(
                    title = stringResource(R.string.booking_no, reservation.bookingNo),
                    subtitle = reservation.userName,
                    description = reservation.roomType,
                    status = reservation.bookingStatus,
                    onClick = { onReservationClick(reservation.id) },
                    onRemoveClick = { reservationToDelete = reservation.id }
                )
            }
        }

        if (reservationToDelete != null) {
            AlertDialog(
                onDismissRequest = { reservationToDelete = null },
                title = { Text(stringResource(R.string.remove_reservation_title)) },
                text = { Text(stringResource(R.string.remove_reservation_confirmation)) },
                confirmButton = {
                    TextButton(onClick = {
                        reservationToDelete?.let { viewModel.removeReservation(it) }
                        reservationToDelete = null
                    }) {
                        Text(stringResource(R.string.remove_button))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reservationToDelete = null }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }

}

@Composable
fun ManagementCard(
    title: String,
    subtitle: String,
    description: String,
    status: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val statusColor = when (status) {
        stringResource(R.string.status_confirmed) -> LightGreen
        stringResource(R.string.status_completed) -> Blue
        stringResource(R.string.status_pending) -> Yellow
        stringResource(R.string.status_cancelled) -> Red
        else -> Color.Gray
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.dp_8))
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.dp_12)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8))
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.dp_4))
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_12))
                ) {
                    Text(subtitle)
                    Text(description)
                }

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .background(statusColor, RoundedCornerShape(50))
                        .padding(
                            horizontal = dimensionResource(R.dimen.dp_12),
                            vertical = dimensionResource(R.dimen.dp_4)
                        )
                ) {
                    Text(
                        text = status,
                        color = Color.White
                    )
                }
                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_button))
                }
            }

        }
    }
}
