package com.example.myapplication.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.presentation.Navigation.Routes
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: ShoppingAppViewModel = hiltViewModel(),
) {

    val cartState = viewModel.getCartState.collectAsStateWithLifecycle()
    val cartItems = cartState.value.UserData ?: emptyList()

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

    val totalPrice = remember(cartItems) {

        cartItems.sumOf { item ->

            val price = item.price
                .replace(Regex("[^0-9.]"), "")
                .toDoubleOrNull()
                ?: 0.0

            price * item.quantity
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getCart()
    }

    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),

        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                scrollBehavior = scrollBehavior
            )
        },

        bottomBar = {

            if (cartItems.isNotEmpty()) {

                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .navigationBarsPadding()
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Total:",
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Rs %.2f".format(totalPrice),
                                style =
                                    MaterialTheme.typography.titleLarge,
                                color =
                                    colorResource(id = R.color.orange),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Button(

                            onClick = {

                                navController.navigate(
                                    Routes.CheckoutScreen(
                                        totalPrice.toString()
                                    )
                                )
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),

                            shape =
                                RoundedCornerShape(12.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        colorResource(
                                            id = R.color.orange
                                        )
                                )
                        ) {

                            Text(
                                text = "Check Out",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when {

                cartState.value.isLoading &&
                        cartItems.isEmpty() -> {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.align(Alignment.Center),
                        color =
                            colorResource(id = R.color.orange)
                    )
                }

                cartState.value.errorMessage != null -> {

                    Text(
                        text =
                            "Error: ${cartState.value.errorMessage}",
                        modifier =
                            Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }

                cartItems.isEmpty() -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.Center,
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Your cart is empty",
                            style =
                                MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = cartItems,

                            // Firestore document ID
                            key = { item ->
                                item.cartId
                            }

                        ) { item ->

                            CartItemCard(

                                item = item,

                                onDelete = {

                                    // IMPORTANT
                                    viewModel.removeFromCart(
                                        item.cartId
                                    )
                                },

                                onClick = {

                                    navController.navigate(
                                        Routes
                                            .EachProductDetailsScreen(
                                                item.productId
                                            )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartDataModel, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // FIXED: Added the clickable modifier so clicking the card actually works
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ... AsyncImage and Details Column (Keep your existing code here) ...

            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )

            // Details Column
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Size: ${item.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rs ${item.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(id = R.color.orange),
                    fontWeight = FontWeight.ExtraBold
                )
            }


            // Quantity and Delete Column
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { onDelete() }) { // Ensure onDelete is called
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = "x${item.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}