package com.example.stayeasehotel.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.stayeasehotel.R
import com.example.stayeasehotel.data.remoteSource.BookingRemoteDataSource
import com.example.stayeasehotel.data.repository.BookingRepository
import com.example.stayeasehotel.data.remoteSource.StaffBookingRemoteDataSource
import com.example.stayeasehotel.domain.BookingUseCase
import com.example.stayeasehotel.ui.staff.reservation.StaffReservationScreen
import com.example.stayeasehotel.ui.viewmodel.StaffBookingViewModel
import com.example.stayeasehotel.ui.staff.reserveDetails.StaffReservationDetailsScreen
 import com.google.firebase.firestore.FirebaseFirestore

enum class ReservationNavigation(@StringRes val title: Int) {
    ReservationRecord(R.string.title_staff_reservation_record),
    ReservationDetails(R.string.title_staff_reservation_details)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationAppBar(
    currentScreen: ReservationNavigation,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    showFilter: Boolean = false,
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    filterLabel: String
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
        },
        actions = {
            if (showFilter) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.dp_16))
                ) {
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter_button)
                        )
                    }

                    Text(
                        text = filterLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable(onClick = onFilterClick)
                    )
                }

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffReservationNavigation(
    navController: NavHostController,
    modifier: Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = when{
        backStackEntry?.destination?.route?.startsWith(ReservationNavigation.ReservationDetails.name) == true -> ReservationNavigation.ReservationDetails
        else -> ReservationNavigation.ReservationRecord
    }
    var showFilterSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val bookingRepository = remember { BookingRepository(context) }   // Room DB
    val remoteGuestDataSource = remember { BookingRemoteDataSource(FirebaseFirestore.getInstance()) }
    val remoteStaffDataSource = remember { StaffBookingRemoteDataSource(FirebaseFirestore.getInstance()) }
    val bookingUseCase = remember { BookingUseCase(bookingRepository, remoteGuestDataSource, remoteStaffDataSource) }
    val staffBookingViewModel = remember { StaffBookingViewModel(bookingUseCase) }
    val filterLabel by staffBookingViewModel.filterLabel.collectAsState()


    Scaffold(
        topBar = {
            ReservationAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp =  { navController.navigateUp() },
                showFilter = currentScreen == ReservationNavigation.ReservationRecord,
                onFilterClick = { showFilterSheet = true },
                filterLabel = filterLabel
            )
        }
    ) { innerPadding ->
        /*val paymentRepository = remember { StaffPaymentRepository(FirebaseFirestore.getInstance()) }
        val paymentViewModel = remember { StaffPaymentViewModel(paymentRepository) }

        val reservationRepository = remember { StaffReservationRepository(FirebaseFirestore.getInstance()) }
        val reservationViewModel = remember { StaffReservationViewModel(reservationRepository) }*/

        /*val context = LocalContext.current
        val bookingRepository = remember { BookingRepository(context) }   // Room DB
        val remoteGuestDataSource = remember { BookingRemoteDataSource(FirebaseFirestore.getInstance()) }
        val remoteStaffDataSource = remember { StaffBookingRemoteDataSource(FirebaseFirestore.getInstance()) }
        val bookingUseCase = remember { BookingUseCase(bookingRepository, remoteGuestDataSource, remoteStaffDataSource) }

        val staffBookingViewModel = remember { StaffBookingViewModel(bookingUseCase) }*/

        NavHost(
            navController = navController,
            startDestination = ReservationNavigation.ReservationRecord.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ReservationNavigation.ReservationRecord.name) {
                StaffReservationScreen(
                    viewModel = staffBookingViewModel,
                    onReservationClick = { reservationId ->
                        navController.navigate("${ReservationNavigation.ReservationDetails.name}/$reservationId")
                    }
                )
            }

            composable(
                route = "${ReservationNavigation.ReservationDetails.name}/{reservationId}",
                arguments = listOf(navArgument("reservationId") { type = NavType.StringType })
            ) { entry ->
                val reservationId = entry.arguments?.getString("reservationId") ?: ""
                StaffReservationDetailsScreen(
                    reservationId = reservationId,
                    reservationViewModel = staffBookingViewModel
                )
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(onDismissRequest = { showFilterSheet = false}) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.dp_16)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_12))
                ) {
                    Text(
                        text = "Filter by Status",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val clearFilterText = stringResource(R.string.clear_filter)
                    val confirmedText = stringResource(R.string.status_confirmed)
                    val completedText = stringResource(R.string.status_completed)
                    val pendingText = stringResource(R.string.status_pending)
                    val cancelledText = stringResource(R.string.status_cancelled)


                    val statuses = listOf(
                        clearFilterText,
                        confirmedText,
                        completedText,
                        pendingText,
                        cancelledText
                    )

                    statuses.forEach { status ->
                        Text(
                            text = status,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (status == clearFilterText) {
                                        staffBookingViewModel.clearFilter()
                                    } else {
                                        staffBookingViewModel.setFilter(status)
                                    }
                                    showFilterSheet = false
                                }
                                .padding(vertical = dimensionResource(R.dimen.dp_12))
                        )
                    }
                }
            }
        }
    }

}