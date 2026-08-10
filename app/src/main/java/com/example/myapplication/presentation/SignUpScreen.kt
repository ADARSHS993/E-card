package com.example.myapplication.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.domain.di.model.UserData
import com.example.myapplication.presentation.Navigation.Routes
import com.example.myapplication.presentation.Navigation.SubNavigation
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel
import com.example.myapplication.presentation.utils.CustomTextField
import com.example.myapplication.presentation.utils.SuccessAlert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: ShoppingAppViewModel = hiltViewModel(),
) {
    val state = viewModel.signUpScreenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    if (state.value.UserData != null) {
        SuccessAlert(onClick = {
            navController.navigate(SubNavigation.MainHomeScreen)
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.orange),
                        Color(0xFF2C3E50)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                style = TextStyle(fontWeight = FontWeight.ExtraBold),
                color = Color.White,
                modifier = Modifier.padding(top = 40.dp, bottom = 8.dp)
            )

            Text(
                text = "Sign up to start shopping on E-card",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First Name",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    CustomTextField(
                        value = lastname,
                        onValueChange = { lastname = it },
                        label = "Last Name",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    CustomTextField(
                        value = phoneNo,
                        onValueChange = { phoneNo = it },
                        label = "Phone Number",
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (state.value.isLoading) {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.orange),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Button(
                            onClick = {
                                if (firstName.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() &&
                                    phoneNo.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                                ) {
                                    if (password == confirmPassword) {
                                        val userData = UserData(
                                            firstName = firstName,
                                            lastName = lastname,
                                            email = email,
                                            password = password,
                                            confirmPassword = confirmPassword,
                                            phoneNumber = phoneNo
                                        )
                                        viewModel.createUser(userData)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Passwords do not match",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please fill all the fields",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.orange),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (state.value.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.value.errorMessage!!,
                            color = Color.Red,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                Text(
                    text = " OR ",
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(24.dp))

            // Footer Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "Login",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.LoginScreen)
                    }
                )
            }
        }
    }
}

