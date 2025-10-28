package com.example.stayeasehotel.ui.user

import com.example.stayeasehotel.ui.viewmodel.UserLoginViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stayeasehotel.R
import com.example.stayeasehotel.ui.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLogin(
    navController: NavHostController,
    bookingViewModel: BookingViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val loginViewModel: UserLoginViewModel = viewModel()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Log In") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(500.dp)
                    .background(Color.Blue)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "User Login",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Box(
                        modifier = Modifier.width(300.dp)
                            .height(370.dp)
                            .background(Color.White)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))

                            TextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    loginViewModel.updateEmail(it) // Update ViewModel
                                },
                                label = { Text("Email") },
                                placeholder = { Text("Enter your email") },
                                modifier = Modifier.width(260.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            TextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    loginViewModel.updatePassword(it) // Update ViewModel
                                },
                                label = { Text("Password") },
                                placeholder = { Text("Enter your password") },
                                modifier = Modifier.width(260.dp)
                            )

                            // Error message space
                            Box(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(60.dp)
                            ) {
                                errorMessage?.let {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.Center),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        softWrap = true
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Button(
                                    onClick = {
                                        if (email.isBlank() || password.isBlank()) {
                                            errorMessage = "Please fill in all fields"
                                        } else {
                                            isLoading = true
                                            errorMessage = null

                                            loginViewModel.login(
                                                onSuccess = { userData ->
                                                    isLoading = false
                                                    // Pass user data to booking viewModel
                                                    bookingViewModel.setUserData(userData)
                                                    navController.navigate("user_app")
                                                },
                                                onError = { error ->
                                                    isLoading = false
                                                    errorMessage = error
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                ) {
                                    Text(
                                        "User Login",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
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