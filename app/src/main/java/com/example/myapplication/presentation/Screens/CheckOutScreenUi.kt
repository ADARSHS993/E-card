package com.example.myapplication.presentation.Screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.myapplication.R
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(
    navController: NavController,
    productId: String,
    viewModel: ShoppingAppViewModel = hiltViewModel(),
    pay: () -> Unit,
) {

    val context = LocalContext.current
    val cartState =
        viewModel.getCartState.collectAsStateWithLifecycle()

    val cartItems =
        cartState.value.UserData ?: emptyList()

    val placeOrderState by viewModel.placeOrderState.collectAsStateWithLifecycle()

    val email = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("") }
    val firstname = remember { mutableStateOf("") }
    val lastname = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val postalCode = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }

    val selectedMethod =
        remember {
            mutableStateOf("Standard FREE delivery over Rs. 4500")
        }

    LaunchedEffect(Unit) {
        viewModel.getCart()
    }

    val totalPrice = remember(cartItems) {

        cartItems.sumOf { item ->

            val price = item.price
                .replace(Regex("[^0-9.]"), "")
                .toDoubleOrNull()
                ?: 0.0

            price * item.quantity
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Shipping")
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        when {

            cartState.value.isLoading &&
                    cartItems.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            colorResource(id = R.color.orange)
                    )
                }
            }

            cartState.value.errorMessage != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text =
                            "Error: ${cartState.value.errorMessage}",
                        color = Color.Red
                    )
                }
            }

            cartItems.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {

                    Text("Your cart is empty")
                }
            }

            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Order Summary",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // DISPLAY ALL CART PRODUCTS

                    cartItems.forEach { item ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            AsyncImage(
                                model = item.image,
                                contentDescription = item.name,

                                modifier = Modifier
                                    .size(90.dp)
                                    .border(
                                        1.dp,
                                        Color.LightGray
                                    )
                            )

                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text = item.name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text = "Size: ${item.size}"
                                )

                                Text(
                                    text =
                                        "Quantity: ${item.quantity}"
                                )

                                Text(
                                    text = "Rs ${item.price}",
                                    color =
                                        colorResource(
                                            id = R.color.orange
                                        ),
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider()
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // TOTAL

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Total",
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "Rs %.2f".format(totalPrice),

                            style =
                                MaterialTheme.typography.titleLarge,

                            color =
                                colorResource(id = R.color.orange),

                            fontWeight =
                                FontWeight.Bold
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )


                    // CONTACT

                    Text(
                        text = "Contact Information",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    OutlinedTextField(

                        value = email.value,

                        onValueChange = {
                            email.value = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Email")
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email
                            )
                    )


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )


                    // ADDRESS

                    Text(
                        text = "Shipping Address",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    OutlinedTextField(

                        value = country.value,

                        onValueChange = {
                            country.value = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Country / Region")
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(

                            value = firstname.value,

                            onValueChange = {
                                firstname.value = it
                            },

                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),

                            label = {
                                Text("First Name")
                            }
                        )


                        OutlinedTextField(

                            value = lastname.value,

                            onValueChange = {
                                lastname.value = it
                            },

                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),

                            label = {
                                Text("Last Name")
                            }
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    OutlinedTextField(

                        value = address.value,

                        onValueChange = {
                            address.value = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Address")
                        }
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(

                            value = city.value,

                            onValueChange = {
                                city.value = it
                            },

                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),

                            label = {
                                Text("City")
                            }
                        )


                        OutlinedTextField(

                            value = postalCode.value,

                            onValueChange = {
                                postalCode.value = it
                            },

                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),

                            label = {
                                Text("Postal Code")
                            },

                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Number
                                )
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )


                    // SHIPPING METHOD

                    Text(
                        text = "Shipping Method",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        RadioButton(

                            selected =
                                selectedMethod.value ==
                                        "Standard FREE delivery over Rs. 4500",

                            onClick = {

                                selectedMethod.value =
                                    "Standard FREE delivery over Rs. 4500"
                            }
                        )

                        Text(
                            "Standard FREE delivery over Rs. 4500"
                        )
                    }


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        RadioButton(

                            selected =
                                selectedMethod.value ==
                                        "Cash on delivery Rs. 50",

                            onClick = {

                                selectedMethod.value =
                                    "Cash on delivery Rs. 50"
                            }
                        )

                        Text(
                            "Cash on delivery Rs. 50"
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )


                    if (placeOrderState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = colorResource(id = R.color.orange))
                        }
                    } else {
                        Button(
                            onClick = {
                                if (
                                    email.value.isNotBlank() &&
                                    country.value.isNotBlank() &&
                                    firstname.value.isNotBlank() &&
                                    address.value.isNotBlank() &&
                                    city.value.isNotBlank() &&
                                    postalCode.value.isNotBlank()
                                ) {
                                    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    val order = com.example.myapplication.domain.di.model.OrderDataModel(
                                        userId = currentUserId,
                                        email = email.value,
                                        firstName = firstname.value,
                                        lastName = lastname.value,
                                        address = address.value,
                                        city = city.value,
                                        postalCode = postalCode.value,
                                        country = country.value,
                                        selectedDeliveryMethod = selectedMethod.value,
                                        items = cartItems,
                                        totalAmount = totalPrice,
                                        date = System.currentTimeMillis(),
                                        paymentStatus = if (selectedMethod.value.contains("Cash")) "COD" else "Pending",
                                        orderStatus = "Pending"
                                    )
                                    viewModel.placeOrder(order) {
                                        // Toast or handling is done via state dialog below
                                    }
                                } else {
                                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.orange)
                            )
                        ) {
                            Text(
                                text = if (selectedMethod.value.contains("Cash")) "Place Order (COD)" else "Place Order & Pay",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }

    if (placeOrderState.orderId != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearPlaceOrderState()
                navController.navigate(com.example.myapplication.presentation.Navigation.SubNavigation.MainHomeScreen) {
                    popUpTo(com.example.myapplication.presentation.Navigation.Routes.HomeScreen::class.qualifiedName!!) { inclusive = false }
                }
            },
            title = { Text("Order Placed!") },
            text = { Text("Your order was placed successfully.\nOrder ID: ${placeOrderState.orderId}") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearPlaceOrderState()
                        navController.navigate(com.example.myapplication.presentation.Navigation.SubNavigation.MainHomeScreen) {
                            popUpTo(com.example.myapplication.presentation.Navigation.Routes.HomeScreen::class.qualifiedName!!) { inclusive = false }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
