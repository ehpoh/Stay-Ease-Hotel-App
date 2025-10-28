package com.example.stayeasehotel.ui.navigation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.stayeasehotel.DateSelectionScreen
import com.example.stayeasehotel.R
import com.example.stayeasehotel.RoomSelectionScreen
import com.example.stayeasehotel.ui.user.bookingSuccess.BookingSuccessScreen
import com.example.stayeasehotel.ui.viewmodel.BookingViewModel
import com.example.stayeasehotel.ui.user.userDetails.GuestDetailsScreen
import com.example.stayeasehotel.ui.user.payment.PaymentScreen
import com.example.stayeasehotel.ui.user.roomInfo.RoomInfoScreen
import com.example.stayeasehotel.data.repository.RoomRepository
import com.example.stayeasehotel.ui.viewmodel.RoomViewModel
import com.google.firebase.firestore.FirebaseFirestore

enum class BookingNavigation(@StringRes val title: Int) {
    DateSelection(title = R.string.title_select_dates),
    RoomSelection(title = R.string.title_room_selection),
    RoomInfo(title = R.string.title_room_details),
    GuestDetails(title = R.string.title_guest_details),
    Payment(title = R.string.title_payment),
    Success(title = R.string.title_success),
}
enum class PaymentOption {
    CREDIT_CARD,
    CASH,
    NONE
}

enum class CardFieldError {
    EMPTY,
    INVALID_LENGTH,
    INVALID_MONTH,
    NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingAppBar(
    currentScreen: BookingNavigation,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingNavigation(
    bookingViewModel: BookingViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = BookingNavigation.valueOf(
        backStackEntry?.destination?.route ?: BookingNavigation.DateSelection.name
    )
    Scaffold(
        topBar = {
            if (currentScreen != BookingNavigation.Success) {
                BookingAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    ) { innerPadding ->
        // Collect UI state from StateFlow
        val roomRepository = remember { RoomRepository(FirebaseFirestore.getInstance()) }
        val roomViewModel = remember { RoomViewModel(roomRepository) }
        val rooms by roomViewModel.rooms.collectAsState()

        val context = LocalContext.current


        val booking by bookingViewModel.uiState.collectAsState()

        val isLoading by bookingViewModel.isLoading.collectAsState()

        NavHost(
            navController = navController,
            startDestination = BookingNavigation.DateSelection.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BookingNavigation.DateSelection.name) {
                DateSelectionScreen(
                    checkInDate = booking.checkInDate,
                    checkOutDate = booking.checkOutDate,
                    roomCount = booking.roomCount,
                    isDateSelectable = { utcMillis -> bookingViewModel.isDateSelectable(utcMillis) },
                    setDates = { start, end -> bookingViewModel.setDates(start, end) },
                    increaseRoomCount = { bookingViewModel.increaseRoomCount() },
                    decreaseRoomCount = { bookingViewModel.decreaseRoomCount() },
                    onNextClicked = {
                        // Navigate to next screen, e.g., RoomSelection
                        navController.navigate(BookingNavigation.RoomSelection.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(BookingNavigation.RoomSelection.name) {
                RoomSelectionScreen(
                    isLoading = isLoading,
                    rooms = rooms,
                    onBookNowClick = { selectedRoom ->
                        bookingViewModel.checkRoomAvailability(
                            selectedRoom
                        ) { isAvailable, availableCount ->
                            if (isAvailable) {
                                bookingViewModel.setSelectedRoom(selectedRoom)
                                navController.navigate(BookingNavigation.GuestDetails.name)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.msg_no_rooms_available, availableCount),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    },
                    onRoomDetailsClicked = { selectedRoom ->
                        bookingViewModel.setSelectedRoom(selectedRoom)
                        navController.navigate(BookingNavigation.RoomInfo.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(BookingNavigation.RoomInfo.name) {
                RoomInfoScreen(
                    isLoading = isLoading,
                    room = booking.selectedRoom!!,
                    checkInMillis = booking.checkInDate,
                    nights = booking.nights,
                    roomCount = booking.roomCount,
                    totalPrice = booking.totalPrice ?: 0.0,
                    onBookNowClick = { selectedRoom ->
                        bookingViewModel.checkRoomAvailability(
                            selectedRoom
                        ) { isAvailable, availableCount ->
                            if (isAvailable) {
                                bookingViewModel.setSelectedRoom(selectedRoom)
                                navController.navigate(BookingNavigation.GuestDetails.name)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.msg_no_rooms_available, availableCount),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }

            composable(BookingNavigation.GuestDetails.name) {
                GuestDetailsScreen(
                    uiState = booking,
                    onToggleRequest = { bookingViewModel.toggleRequest(it) },
                    onNextClicked = {
                        navController.navigate(BookingNavigation.Payment.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(BookingNavigation.Payment.name) {
                PaymentScreen(
                    uiState = booking,
                    viewModel = bookingViewModel,
                    onSelectCreditCard = { bookingViewModel.selectCreditCard() },
                    onSelectTouchNGo = { bookingViewModel.selectCash() },
                    onPayNowClicked = {
                        navController.navigate(BookingNavigation.Success.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(BookingNavigation.Success.name) {
                BookingSuccessScreen(
                    modifier = Modifier.fillMaxHeight(),
                    onBackToHome = {
                        navController.navigate(BookingNavigation.DateSelection.name) {
                            popUpTo(BookingNavigation.DateSelection.name) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )

            }
        }
    }
}
