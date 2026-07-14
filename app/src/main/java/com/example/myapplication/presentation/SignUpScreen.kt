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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
fun SignUpScreen(navController: NavHostController) {
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.splacee),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Dark Overlay (This makes white text/fields pop)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)) // 40% dark tint
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                style = TextStyle(fontWeight = FontWeight.ExtraBold),
                color = Color.White, // Changed to White for contrast
                modifier = Modifier.padding(top = 60.dp, bottom = 32.dp)
            )

            // Input Fields
            // Note: If fields are still hard to see, ensure CustomTextField has:
            // colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White.copy(alpha = 0.1f))
            CustomTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            CustomTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = "Last Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            CustomTextField(
                value = phoneNo,
                onValueChange = { phoneNo = it },
                label = "Phone Number",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Main Sign Up Button
            Button(
                onClick = {
                    if(firstName.isEmpty() || lastname.isEmpty() || email.isEmpty() || phoneNo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                           if(password == confirmPassword){
                               Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                           }else{
                               Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
                           }
                    }else{
                        Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.White.copy(alpha = 0.5f))
                Text(
                    text = " Or ",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White.copy(alpha = 0.7f)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.White.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Button (White background makes it look modern on dark image)
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
                        text = "Sign up with Google",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Already have an account? ", color = Color.White.copy(alpha = 0.8f))
                Text(
                    text = "Login",
                    color = Color.Cyan, // Cyan or Bright Primary stands out well on dark
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { /* Navigate */ }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
        SignUpScreen()
}