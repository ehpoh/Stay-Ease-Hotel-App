package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.stayeasehotel.R
import com.example.stayeasehotel.data.StaffPositions
import com.example.stayeasehotel.ui.viewmodel.StaffSigninViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSignInScreen(navController: NavHostController) {
    val viewModel: StaffSigninViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Registration") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("staff_list")}) {
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
                    .height(800.dp)
                    .background(Color.Blue)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        "Staff Registration",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier.width(350.dp)
                            .height(650.dp)
                            .background(Color.White)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = uiState.name,
                                onValueChange = { viewModel.updateField("name", it) },
                                label = { Text("Full Name") },
                                modifier = Modifier.width(260.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = { viewModel.updateField("email", it) },
                                label = { Text("Email") },
                                placeholder = { Text("example@gmail.com") },
                                modifier = Modifier.width(260.dp),
                                isError = uiState.email.isNotBlank() && !viewModel.isValidEmail(uiState.email),
                                supportingText = {
                                    if (uiState.email.isNotBlank() && !viewModel.isValidEmail(uiState.email)) {
                                        Text("Must be a valid Gmail address")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.areaCode,
                                    onValueChange = {
                                        viewModel.updateField("areaCode", it.filter { it.isDigit() }.take(3))
                                    },
                                    label = { Text("Code") },
                                    placeholder = { Text("012") },
                                    modifier = Modifier.weight(1f),
                                    isError = uiState.areaCode.isNotBlank() && uiState.areaCode.length != 3,
                                    supportingText = {
                                        if (uiState.areaCode.isNotBlank() && uiState.areaCode.length != 3) {
                                            Text("Must be 3 digits")
                                        }
                                    }
                                )

                                Text(
                                    "-",
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .align(Alignment.CenterVertically),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                OutlinedTextField(
                                    value = uiState.phoneNumber,
                                    onValueChange = {
                                        viewModel.updateField("phoneNumber", it.filter { it.isDigit() }.take(8))
                                    },
                                    label = { Text("Number") },
                                    placeholder = { Text("3456789") },
                                    modifier = Modifier.weight(2f),
                                    isError = uiState.phoneNumber.isNotBlank() && (uiState.phoneNumber.length < 7 || uiState.phoneNumber.length > 8),
                                    supportingText = {
                                        if (uiState.phoneNumber.isNotBlank() && (uiState.phoneNumber.length < 7 || uiState.phoneNumber.length > 8)) {
                                            Text("7-8 digits required")
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            var isGenderExpanded by remember { mutableStateOf(false) }
                            val genders = listOf("Male", "Female")

                            ExposedDropdownMenuBox(
                                expanded = isGenderExpanded,
                                onExpandedChange = { isGenderExpanded = it },
                                modifier = Modifier.width(260.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.gender,
                                    onValueChange = {},
                                    label = { Text("Gender") },
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = isGenderExpanded
                                        )
                                    },
                                    modifier = Modifier.menuAnchor().width(260.dp),
                                    isError = uiState.gender.isBlank()
                                )

                                ExposedDropdownMenu(
                                    expanded = isGenderExpanded,
                                    onDismissRequest = { isGenderExpanded = false }
                                ) {
                                    genders.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                viewModel.updateField("gender", selectionOption)
                                                isGenderExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            var isPositionExpanded by remember { mutableStateOf(false) }
                            val positions = listOf(StaffPositions.ADMIN, StaffPositions.STAFF)

                            ExposedDropdownMenuBox(
                                expanded = isPositionExpanded,
                                onExpandedChange = { isPositionExpanded = it },
                                modifier = Modifier.width(260.dp)
                            ) {
                                OutlinedTextField(
                                    value = when (uiState.position) {
                                        StaffPositions.ADMIN -> "Admin"
                                        StaffPositions.STAFF -> "Staff"
                                        else -> ""
                                    },
                                    onValueChange = {},
                                    label = { Text("Position") },
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = isPositionExpanded
                                        )
                                    },
                                    modifier = Modifier.menuAnchor().width(260.dp),
                                    isError = uiState.position.isBlank()
                                )

                                ExposedDropdownMenu(
                                    expanded = isPositionExpanded,
                                    onDismissRequest = { isPositionExpanded = false }
                                ) {
                                    positions.forEach { position ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    when (position) {
                                                        StaffPositions.ADMIN -> "Admin"
                                                        StaffPositions.STAFF -> "Staff"
                                                        else -> position
                                                    }
                                                )
                                            },
                                            onClick = {
                                                viewModel.updateField("position", position)
                                                isPositionExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.password,
                                onValueChange = { viewModel.updateField("password", it) },
                                label = { Text("Password") },
                                modifier = Modifier.width(260.dp),
                                isError = uiState.password.isNotBlank() && uiState.password.length < 6,
                                supportingText = {
                                    if (uiState.password.isNotBlank() && uiState.password.length < 6) {
                                        Text("Min 6 characters")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(60.dp)
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

                            Spacer(modifier = Modifier.height(8.dp))

                            if (uiState.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.signUp(
                                            onSuccess = {
                                                navController.navigate("staff_list")
                                            },
                                            onError = { /* Error handled in state */ }
                                        )
                                    },
                                    modifier = Modifier.width(250.dp)
                                ) {
                                    Text(
                                        "Register Staff",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}