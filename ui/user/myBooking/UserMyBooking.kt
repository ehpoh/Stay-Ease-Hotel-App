package com.example.stayeasehotel.ui.user.myBooking

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.stayeasehotel.R
import com.example.stayeasehotel.data.roomDatabase.PaymentEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationEntity
import com.example.stayeasehotel.data.repository.RoomRepository
import com.example.stayeasehotel.ui.theme.Blue
import com.example.stayeasehotel.ui.theme.LightGreen
import com.example.stayeasehotel.ui.theme.Red
import com.example.stayeasehotel.ui.theme.Yellow

import com.example.stayeasehotel.ui.user.UserBottomAppBar
import com.example.stayeasehotel.ui.viewmodel.BookingViewModel
import com.example.stayeasehotel.ui.viewmodel.RoomViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserMyBooking(
    navController: NavHostController,
    bookingViewModel: BookingViewModel
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Active, 1 = History
    val bookings by bookingViewModel.userBookings.collectAsState() // bookings for current user
    val uiState by bookingViewModel.uiState.collectAsState()
    val userEmail = uiState.userEmail
    val roomViewModel = remember { RoomViewModel(RoomRepository(FirebaseFirestore.getInstance())) }

    // Load the data when the screen appears
    LaunchedEffect(userEmail) {
        if (userEmail != null) {
            bookingViewModel.loadUserBookings(userEmail)
        }
    }

    Scaffold(
        bottomBar = {
            UserBottomAppBar(navController) // Your bottom navigation bar
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Tabs for Active & History
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.tab_active)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.tab_history)) }
                )
            }

            // Filter bookings
            val activeBookings = bookings.filter {
                it.first.bookingStatus == stringResource(R.string.status_pending) || it.first.bookingStatus == stringResource(R.string.status_confirmed)
            }
            val historyBookings = bookings.filter {
                it.first.bookingStatus == stringResource(R.string.status_complete) || it.first.bookingStatus == stringResource(R.string.status_cancelled)
            }

            // Display list based on selected tab
            val bookingsShown = if (selectedTab == 0) activeBookings else historyBookings

            if (bookingsShown.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_bookings), style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.dp_8))
                ) {
                    items(bookingsShown) { bookingPair  ->
                        val (reservation, payment) = bookingPair
                        BookingCard(bookingViewModel, reservation, payment, roomViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    bookingViewModel: BookingViewModel,
    reservation: ReservationEntity,
    payment: PaymentEntity,
    roomViewModel: RoomViewModel
) {
    val rooms by roomViewModel.rooms.collectAsState()
    val roomImageUrl = rooms.firstOrNull { it.roomId == reservation.roomId }?.image

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.dp_8)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.dp_16)),
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.dp_4))
    ) {
        Row(modifier = Modifier.padding(dimensionResource(R.dimen.dp_4))) {
            // Room image
            if (!roomImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = roomImageUrl,
                    contentDescription = stringResource(R.string.content_room_image, reservation.roomType ?: "N/A"),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.dp_145))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.dp_12))),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.dp_120))
                        .background(Color.Gray, RoundedCornerShape(dimensionResource(R.dimen.dp_12))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_image), color = Color.White, fontSize = dimensionResource(R.dimen.sp_12).value.sp)
                }
            }

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.dp_16)))

            // Booking details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.room_label, reservation.roomType ?: stringResource(R.string.no_roomType_label)),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    stringResource(R.string.check_in_label, bookingViewModel.formatDate(reservation.checkInDate)),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    stringResource(R.string.check_out_label, bookingViewModel.formatDate(reservation.checkOutDate)),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dp_8)))

                Text(
                    stringResource(R.string.payment_label, payment.paymentStatus, payment.paymentOption),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    stringResource(R.string.payment_amount, payment.amount),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Status badge
                val statusColor = when (reservation.bookingStatus) {
                    stringResource(R.string.status_confirmed) -> LightGreen
                    stringResource(R.string.status_pending) -> Yellow
                    stringResource(R.string.status_completed) -> Blue
                    stringResource(R.string.status_cancelled) -> Red
                    else -> Color.Gray
                }

                Box(
                    modifier = Modifier
                        .padding(top = dimensionResource(R.dimen.dp_8))
                        .background(statusColor, RoundedCornerShape(dimensionResource(R.dimen.dp_12)))
                        .padding(horizontal = dimensionResource(R.dimen.dp_12), vertical = dimensionResource(R.dimen.dp_8))
                ) {
                    Text(
                        text = reservation.bookingStatus,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

