package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.example.stayeasehotel.R
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stayeasehotel.ui.viewmodel.StaffLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffLogin(navController: NavHostController) {
    val viewModel: StaffLoginViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Login") },
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
            painter = painterResource(R.drawable.background),
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
                    .background(Color.Red)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        "Staff Login",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Box(
                        modifier = Modifier.width(300.dp)
                            .height(370.dp) // Increased height to accommodate error message
                            .background(Color.White)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))

                            TextField(
                                value = uiState.email,
                                onValueChange = { viewModel.updateEmail(it) },
                                label = { Text("Email") },
                                placeholder = { Text("Enter staff email") },
                                modifier = Modifier.width(260.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            TextField(
                                value = uiState.password,
                                onValueChange = { viewModel.updatePassword(it) },
                                label = { Text("Password") },
                                placeholder = { Text("Enter staff password") },
                                modifier = Modifier.width(260.dp)
                            )

                            // Error message space
                            Box(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(60.dp) // Fixed height to prevent layout jumps
                            ) {
                                uiState.errorMessage?.let {
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

                            if (uiState.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.login(
                                            onSuccess = { navController.navigate("staff_app") },
                                            onError = { /* Error handled in state */ }
                                        )
                                    },
                                    modifier = Modifier
                                ) {
                                    Text(
                                        "Staff Login",
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