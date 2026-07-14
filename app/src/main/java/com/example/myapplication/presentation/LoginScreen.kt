package com.example.myapplication.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.presentation.utils.CustomTextField

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    val context = LocalContext.current
    // State management - initialized to empty string instead of " "
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.splacee),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Dark Scrim Overlay (Makes white text and fields pop)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // 3. Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                fontSize = 32.sp,
                style = TextStyle(fontWeight = FontWeight.ExtraBold),
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Login to your account",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Email Field
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            // Password Field
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            // Forgot Password Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot Password?",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { /* Handle Forgot Password */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f))
                Text(
                    text = " OR ",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.3f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Login Button
            Button(
                onClick = { /* Handle Google Sign In */ },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Don't have an account? ", color = Color.White.copy(alpha = 0.8f))
                Text(
                    text = "Sign Up",
                    color = Color.Cyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        // context.startActivity(...) or Navigation call
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}