package com.example.myapplication.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessAlert(
    onClick : () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = {},
        modifier = Modifier.background(shape = RoundedCornerShape(16.dp), color = Color.White),
        content = {
            Column(modifier = Modifier.fillMaxWidth()
                .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Box (modifier = Modifier
                    .size(64.dp)
                    .background(color = colorResource(id = R.color.orange), shape = CircleShape)){

                    Icon(imageVector = Icons.Default.Check
                        , contentDescription = "Success"
                        , modifier = Modifier
                            .align(Alignment.Center)
                            .size(52.dp)
                        , tint = Color.Gray)

                    Spacer(modifier = Modifier.size(16.dp))

                    Text(text = "Success" ,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.orange))

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Congratulation, you have \n completed your registration",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(modifier = Modifier.fillMaxWidth()
                        .height(48.dp),
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange)),
                        shape = RoundedCornerShape(8.dp)) {

                        Text(text = "Go to Home",
                            color = Color.Gray)
                    }
                }
            }
        }
    )

}