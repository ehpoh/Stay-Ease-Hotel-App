package com.example.stayeasehotel.ui.staff.reserveDetails

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.stayeasehotel.R
import com.example.stayeasehotel.ui.ConfirmDialog
import com.example.stayeasehotel.ui.theme.Blue
import com.example.stayeasehotel.ui.theme.LightGreen
import com.example.stayeasehotel.ui.theme.Red
import com.example.stayeasehotel.ui.theme.Yellow

import com.example.stayeasehotel.ui.viewmodel.StaffBookingViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffReservationDetailsScreen(
    reservationId: String,
    reservationViewModel: StaffBookingViewModel,
) {
    val reservation by reservationViewModel.selectedReservation.collectAsState()
    val payment by reservationViewModel.selectedPayment.collectAsState()

    var tempReserveStatus by remember { mutableStateOf<String?>(null) }
    var tempPaymentStatus by remember { mutableStateOf<String?>(null)}
    var showReserveBottomSheet by remember { mutableStateOf(false) }
    var showPaymentBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load reservation on screen open
    LaunchedEffect(reservationId) {
        reservationViewModel.getReservationById(reservationId)
        reservationViewModel.getPaymentForReservation(reservationId)
    }

    LaunchedEffect(reservation) {
        reservation?.let { res ->
            tempReserveStatus = res.bookingStatus
        }
    }

    LaunchedEffect(payment) {
        payment?.let { pay ->
            tempPaymentStatus = pay.paymentStatus
        }
    }

    LaunchedEffect(reservationViewModel.toastMessage) {
        reservationViewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        reservation?.let { res ->
            LazyColumn(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.dp_16)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_16))
            ) {
                // Booking Info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.dp_16)),
                        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_4))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.dp_12)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.padding(dimensionResource(R.dimen.dp_16))) {
                                Text(stringResource(R.string.booking_number, res.bookingNo), style = MaterialTheme.typography.titleMedium)

                                Spacer(Modifier.height(dimensionResource(R.dimen.dp_4)))

                                Text(stringResource(R.string.created_date, reservationViewModel.formatTimestamp(res.createdAt)), style = MaterialTheme.typography.bodyMedium)
                            }
                            StatusChip(
                                status = tempReserveStatus ?: res.bookingStatus,
                                onClick = { showReserveBottomSheet = true }
                            )
                        }
                    }
                }

                // Dates
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.dp_16)),
                        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_4))
                    ) {
                        Row(
                            Modifier.padding(dimensionResource(R.dimen.dp_16)),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8))) {
                                Text(stringResource(R.string.check_in_no_arg_label), style = MaterialTheme.typography.labelLarge)
                                Text(reservationViewModel.formatLongTimestamp(res.checkInDate))
                            }

                            Spacer(Modifier.width(dimensionResource(R.dimen.dp_40)))

                            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8))) {
                                Text(stringResource(R.string.check_out_no_arg_label), style = MaterialTheme.typography.labelLarge)
                                Text(reservationViewModel.formatLongTimestamp(res.checkOutDate))
                            }
                        }
                    }
                }

                // Customer Info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.dp_16)),
                        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_4))
                    ) {
                        Column(
                            Modifier.padding(dimensionResource(R.dimen.dp_16))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(R.string.guest_title),
                                    modifier = Modifier.size(dimensionResource(R.dimen.dp_20))
                                )
                                Spacer(Modifier.width(dimensionResource(R.dimen.dp_8)))
                                Text(
                                    res.userName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(dimensionResource(R.dimen.dp_8)))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(dimensionResource(R.dimen.dp_20))
                                )
                                Spacer(Modifier.width(dimensionResource(R.dimen.dp_8)))
                                Text(
                                    res.userEmail,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .border(
                                            dimensionResource(R.dimen.dp_1),
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(dimensionResource(R.dimen.dp_4))
                                        )
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(dimensionResource(R.dimen.dp_4))
                                        )
                                ) {
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse(context.getString(R.string.mailto_email, res.userEmail))
                                        }
                                        context.startActivity(intent)
                                    }) {
                                        Icon(Icons.Default.Email, contentDescription = null)
                                    }
                                }
                            }

                            Spacer(Modifier.height(dimensionResource(R.dimen.dp_8)))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(dimensionResource(R.dimen.dp_20))
                                )
                                Spacer(Modifier.width(dimensionResource(R.dimen.dp_8)))
                                Text(
                                    res.userPhone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .border(
                                            dimensionResource(R.dimen.dp_1),
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(dimensionResource(R.dimen.dp_4))
                                        )
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(dimensionResource(R.dimen.dp_4))
                                        )
                                ) {
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse(context.getString(R.string.tel_to_contact, res.userPhone))
                                        }
                                        context.startActivity(intent)
                                    }) {
                                        Icon(Icons.Default.Call, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                }

                // Reservation Info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.dp_16)),
                        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_4))
                    ) {
                        Column(
                            Modifier.padding(dimensionResource(R.dimen.dp_16)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_8))
                        ) {
                            Text(stringResource(R.string.room_type_count, res.roomType, res.roomCount), style = MaterialTheme.typography.titleSmall)
                            Text(stringResource(R.string.booking_nights, res.nights), style = MaterialTheme.typography.bodyMedium)
                            Text(stringResource(R.string.total_price_label, res.totalPrice ?: 0.0), style = MaterialTheme.typography.bodyMedium)
                            if (res.requests.isNotEmpty()) {
                                Text(
                                    stringResource(R.string.requests_list, res.requests.joinToString()),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Payment Info
                item {
                    payment?.let { pay ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.dp_16)),
                            elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_4))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(R.dimen.dp_12)),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.padding(dimensionResource(R.dimen.dp_16))
                                ) {
                                    Text(stringResource(R.string.title_payment), style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.payment_amount, pay.amount), style = MaterialTheme.typography.bodyMedium)
                                    Text(stringResource(R.string.payment_option, pay.paymentOption), style = MaterialTheme.typography.bodyMedium)
                                    pay.cardLast4?.let {
                                        Text(stringResource(R.string.card_ending, it), style = MaterialTheme.typography.bodyMedium)
                                    }
                                }

                                StatusChip(
                                    status = tempPaymentStatus ?: pay.paymentStatus,
                                    onClick = { showPaymentBottomSheet = true }
                                )
                            }

                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    if (tempReserveStatus != res.bookingStatus || tempPaymentStatus != payment?.paymentStatus) {
                        showDialog = true
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_changes_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.dp_12))
            ) {
                Text(stringResource(R.string.save_changes))
            }

            if (showDialog) {
                ConfirmDialog(
                    onConfirm = {
                        tempReserveStatus?.let { status ->
                            if (status != res.bookingStatus) {
                                reservationViewModel.updateReservationStatus(res.id, status)
                            }

                        }

                        payment?.let { pay ->
                            tempPaymentStatus?.let { status ->
                                if (status != pay.paymentStatus) {
                                    reservationViewModel.updatePaymentStatus(pay.id, status)
                                }
                            }
                        }

                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.dp_32))
                )
            }
        }


        // Bottom Sheet for status selection
        if (showReserveBottomSheet || showPaymentBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showReserveBottomSheet = false
                    showPaymentBottomSheet = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.dp_16)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_12))
                ) {
                    Text(
                        stringResource(R.string.change_status),
                        style = MaterialTheme.typography.titleMedium
                    )

                    when {
                        showReserveBottomSheet -> {
                            listOf(
                                stringResource(R.string.status_pending),
                                stringResource(R.string.status_confirmed),
                                stringResource(R.string.status_completed),
                                stringResource(R.string.status_cancelled)).forEach { statusOption ->
                                Text(
                                    text = stringResource(R.string.set_to_status, statusOption),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            tempReserveStatus = statusOption
                                            showReserveBottomSheet = false
                                        }
                                        .padding(vertical = dimensionResource(R.dimen.dp_12)),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        showPaymentBottomSheet -> {
                            listOf(
                                stringResource(R.string.status_completed),
                                stringResource(R.string.status_on_hold),
                                stringResource(R.string.status_refunded)).forEach { statusOption ->
                                Text(
                                    text = stringResource(R.string.set_to_status, statusOption),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            tempPaymentStatus = statusOption
                                            showPaymentBottomSheet = false
                                        }
                                        .padding(vertical = dimensionResource(R.dimen.dp_12)),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    status: String,
    onClick: () -> Unit = {}
) {
    val color = when (status) {
        // Reservation Status
        stringResource(R.string.status_confirmed) -> LightGreen
        stringResource(R.string.status_completed) -> Blue
        stringResource(R.string.status_pending) -> Yellow
        stringResource(R.string.status_cancelled) -> Red

        // Payment Status
        stringResource(R.string.status_on_hold) -> Yellow
        stringResource(R.string.status_refunded) -> Red

        else -> Color.Gray
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = dimensionResource(R.dimen.dp_12), vertical = dimensionResource(R.dimen.dp_8))
    ) {
        Text(status, color = Color.White)
    }
}