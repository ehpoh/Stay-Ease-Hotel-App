
package com.example.stayeasehotel

import com.example.stayeasehotel.data.remoteSource.BookingRemoteDataSource
import com.example.stayeasehotel.data.remoteSource.StaffBookingRemoteDataSource
import com.example.stayeasehotel.data.repository.BookingRepository
import com.example.stayeasehotel.domain.BookingUseCase
import com.example.stayeasehotel.ui.LostItemUi.LostFoundCenterScreen.MyLostClaimItemScreen.MyLostClaimItemScreen
import com.example.stayeasehotel.ui.ReportLostItemScreen.ReportLostItemScreen
import com.example.stayeasehotel.ui.management.ManagementScreen
import com.example.stayeasehotel.ui.staff.LostAndFoundManagementScreen
import com.example.stayeasehotel.ui.staff.StaffEditPage
import com.example.stayeasehotel.ui.staff.StaffLogOut
import com.example.stayeasehotel.ui.staff.StaffLogin

import com.example.stayeasehotel.ui.staff.StaffProfilePage
import com.example.stayeasehotel.ui.staff.StaffSignInScreen
import com.example.stayeasehotel.ui.staff.StaffStaffList
import com.example.stayeasehotel.ui.staff.StaffUserList
import com.example.stayeasehotel.ui.user.LostAndFoundCenterScreenMainScreen
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.LostItemDetailScreen
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.LostandFoundCenterScreen
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.LostandFoundCenterViewModel
import com.example.stayeasehotel.ui.user.ThisIsMine.ThisIsMineScreen
import com.example.stayeasehotel.ui.user.UserAboutUs
import com.example.stayeasehotel.ui.user.UserChoice
import com.example.stayeasehotel.ui.user.UserDeleteAccount
import com.example.stayeasehotel.ui.user.UserHomePage
import com.example.stayeasehotel.ui.user.UserLogOut
import com.example.stayeasehotel.ui.user.UserLogin

import com.example.stayeasehotel.ui.user.UserProfilePage
 import com.example.stayeasehotel.ui.user.UserSuccessfulPage
import com.example.stayeasehotel.ui.viewmodel.BookingViewModel
import com.example.stayeasehotel.ui.viewmodel.StaffViewModel
import com.example.stayeasehotel.ui.viewmodel.UserViewModel



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.stayeasehotel.ui.staff.StaffHomePage

import com.example.stayeasehotel.ui.staff.StaffProfilePage
import com.example.stayeasehotel.ui.staff.StaffReservationManagement
import com.example.stayeasehotel.ui.user.UserHomePage
import com.example.stayeasehotel.ui.user.UserLogin

import com.example.stayeasehotel.ui.user.myBooking.UserMyBooking
import com.example.stayeasehotel.ui.user.UserProfilePage


import com.example.stayeasehotel.ui.user.UserEditPage
import com.example.stayeasehotel.ui.user.UserRoomManagement
import com.example.stayeasehotel.ui.user.UserSignin

import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val staffViewModel: StaffViewModel = viewModel()

    // Observe navigation triggers from both ViewModels
    val shouldNavigateToStateChoiceUser by userViewModel.navigateToStateChoice.collectAsState()
    val shouldNavigateToStateChoiceStaff by staffViewModel.navigateToStateChoice.collectAsState()

    val context = LocalContext.current
    val bookingViewModel = remember {
        val localRepository = BookingRepository(context)
        val remoteGuestSource = BookingRemoteDataSource(FirebaseFirestore.getInstance())
        val remoteStaffSource = StaffBookingRemoteDataSource(FirebaseFirestore.getInstance())
        val useCase = BookingUseCase(localRepository, remoteGuestSource, remoteStaffSource)

        BookingViewModel(useCase)
    }

    LaunchedEffect(shouldNavigateToStateChoiceUser) {
        if (shouldNavigateToStateChoiceUser) {
            navController.navigate("state_choice") {
                popUpTo(0) { inclusive = true }
            }
            userViewModel.resetNavigation()
        }
    }

    LaunchedEffect(shouldNavigateToStateChoiceStaff) {
        if (shouldNavigateToStateChoiceStaff) {
            navController.navigate("state_choice") {
                popUpTo(0) { inclusive = true }
            }
            staffViewModel.resetNavigation()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "state_choice"
    ) {


        composable("state_choice") {
            StateChoice(navController)
        }
        composable("staff_login") {
            StaffLogin(navController)
        }
        composable("staff_app") {
            StaffApp(navController, staffViewModel)
        }
        composable("user_choice") {
            UserChoice(navController)
        }
        composable("user_login") {
            UserLogin(navController, bookingViewModel)
        }
        composable("user_signin") {
            UserSignin(navController)
        }
        composable("user_success") {
            UserSuccessfulPage(navController)
        }
        composable("user_app") {
            UserApp(navController, userViewModel, bookingViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserApp(
    mainNavController: NavHostController,
    userViewModel: UserViewModel,
    bookingViewModel: BookingViewModel
) {
    val userNavController = rememberNavController()

    NavHost(
        navController = userNavController,
        startDestination = "user_home"
    ) {
        composable("user_home") {
            UserHomePage(userNavController)
        }

        composable("user_room") {
            UserRoomManagement(userNavController, bookingViewModel)
        }
        composable("user_booking") {
            UserMyBooking(userNavController, bookingViewModel)
        }

        composable("user_profile") {
            UserProfilePage(userNavController, userViewModel)
        }
        composable("user_edit") {
            UserEditPage(userNavController, userViewModel)
        }
        composable("user_about_us") {
            UserAboutUs(userNavController)
        }
        composable("user_logout") {
            UserLogOut(mainNavController, userViewModel)
        }
        composable("user_delete_account") {
            UserDeleteAccount(userNavController, userViewModel)
        }



        composable("user_lostAndFound") {
            LostAndFoundCenterScreenMainScreen(userNavController)
        }



        composable("report_lost_item") {
            ReportLostItemScreen(userNavController)
        }
        composable("user_profile") {
            UserProfilePage(userNavController, userViewModel)
        }

        composable("lost_and_found_center") {
            LostandFoundCenterScreen(userNavController)
        }
        composable("my_lost_items") {
            MyLostClaimItemScreen(userNavController)
        }



        composable("user_about_us") {
            UserAboutUs(userNavController)
        }
        composable("user_logout") {
            UserLogOut(mainNavController, userViewModel)
        }
        composable("user_delete_account") {
            UserDeleteAccount(userNavController, userViewModel)
        }


        composable("claim_Items/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            if (itemId != null) {
                ThisIsMineScreen(itemId = itemId,userNavController)
            }

        }

        composable("lost_item_detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            if (itemId != null) {

                val lostAndFoundViewModel: LostandFoundCenterViewModel = viewModel()

                // Get the item from ViewModel (LiveData/StateFlow)
                val uiState by lostAndFoundViewModel.uiState.collectAsState()
                val item = uiState.lostItems.find { it.id == itemId }

                if (item != null) {
                    LostItemDetailScreen(
                        item = item,
                        onNavigateBack = { userNavController.popBackStack() },
                        onMarkAsMine = { markedItem ->
                            // Navigate to claim screen for this item
                            userNavController.navigate("claim_Items/${markedItem.id}")
                        },
                        lostAndFoundViewModel = lostAndFoundViewModel
                    )
                }
            }

        }
    }
}

@Composable
fun StaffApp(
    mainNavController: NavHostController,
    staffViewModel: StaffViewModel
) {
    val staffNavController = rememberNavController()

    NavHost(
        navController = staffNavController,
        startDestination = "staff_home"
    ) {

        composable("staff_reservation") {
            StaffReservationManagement(staffNavController)
        }
        composable("staff_home") {
            StaffHomePage(staffNavController)
        }
        composable("staff_profile") {
            StaffProfilePage(staffNavController, staffViewModel)
        }
        composable("staff_edit") {
            StaffEditPage(staffNavController, staffViewModel)
        }
        composable("staff_list") {
            StaffStaffList(staffNavController)
        }
        composable("staff_signin") {
            StaffSignInScreen(staffNavController)
        }
        composable("user_list") {
            StaffUserList(staffNavController)
        }
        composable("staff_logout") {
            StaffLogOut(mainNavController, staffViewModel)
        }

        composable("staff_lostAndFound") {
            LostAndFoundManagementScreen(staffNavController)
        }

        composable("ManagementScreen") {
            ManagementScreen(staffNavController)
        }
        composable("report_lost_item") {
            ReportLostItemScreen(staffNavController)
        }
    }
}


