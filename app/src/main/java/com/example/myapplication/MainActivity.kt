package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.Navigation.App
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.logging.Handler
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultWithDataListener {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                      mainScreen(firebaseAuth,{payTest()})
                }
            }
        }


    @Composable
    fun mainScreen(firebaseAuth: FirebaseAuth, onPayTest: () -> Unit){

        val showSplash = remember { mutableStateOf(true) }
        val startDestination = remember { mutableStateOf<Any?>(null) }

        LaunchedEffect(key1 = Unit){
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                startDestination.value = com.example.myapplication.presentation.Navigation.SubNavigation.LoginSignUpScreen
            } else {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("admins")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists() && doc.getString("role") == "admin") {
                            startDestination.value = com.example.myapplication.presentation.Navigation.Routes.AdminDashboardScreen
                        } else {
                            startDestination.value = com.example.myapplication.presentation.Navigation.SubNavigation.MainHomeScreen
                        }
                    }
                    .addOnFailureListener {
                        startDestination.value = com.example.myapplication.presentation.Navigation.SubNavigation.MainHomeScreen
                    }
            }

            android.os.Handler(Looper.getMainLooper()).postDelayed({
                showSplash.value = false
            }, 3000)
        }

        if(showSplash.value || startDestination.value == null){
            SplashScreen()
        }else{
            App(
                firebasAuth = firebaseAuth,
                payTest = onPayTest,
                startDestination = startDestination.value!!
            )
        }
    }

    @Composable
    fun SplashScreen() {
        // 1. Animation States
        val scale = remember { Animatable(0f) }
        val alpha = remember { Animatable(0f) }

        // 2. Start Animation on Launch
        LaunchedEffect(key1 = true) {
            // Logo pops up with an overshoot effect
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
            // Text fades in
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
        }

        // 3. UI Layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.orange),
                            Color(0xFFFFB347) // A lighter shade of orange
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Container
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.splash),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Title
                Text(
                    text = "E-card",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.alpha(alpha.value),
                    letterSpacing = 4.sp
                )

                // Optional Tagline
                Text(
                    text = "Smart Shopping, Easy Cards",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.alpha(alpha.value)
                )
            }

            // Optional: Bottom text
            Text(
                text = "Powered by Razorpay",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
                    .alpha(alpha.value),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }



    fun payTest(


    ) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Razorpay Corp")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","<currency>");
            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","50000")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","<email>")
            prefill.put("contact","<phone>")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        TODO("Not yet implemented")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        TODO("Not yet implemented")
    }
}


