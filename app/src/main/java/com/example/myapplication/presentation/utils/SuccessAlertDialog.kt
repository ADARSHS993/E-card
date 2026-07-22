package com.example.myapplication.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessAlert(
    onClick: () -> Unit
) {
    // BasicAlertDialog is used for custom layouts in Material 3
    BasicAlertDialog(
        onDismissRequest = { /* Handle dismiss if needed */ },
        properties = DialogProperties(usePlatformDefaultWidth = false) // Allows custom width
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f) // Take up 85% of screen width
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. The Success Icon Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = colorResource(id = R.color.orange).copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(color = colorResource(id = R.color.orange), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            modifier = Modifier.size(36.dp),
                            tint = Color.White // White checkmark looks better on Orange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. The Success Title
                Text(
                    text = "Success",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. The Description
                Text(
                    text = "Congratulations, you have\ncompleted your registration!",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Action Button
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.orange)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Go to Home",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Use White text for readability
                    )
                }
            }
        }
    }
}