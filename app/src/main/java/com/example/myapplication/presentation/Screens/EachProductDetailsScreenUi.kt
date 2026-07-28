package com.example.myapplication.presentation.Screens

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.myapplication.R
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.presentation.Navigation.Routes
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EachProductDetailScreens(
    navController: NavController,
    productId: String,
    viewModel: ShoppingAppViewModel = hiltViewModel()
) {
    val getProductById = viewModel.getProductByIdState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var selectedSize by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) } // Changed to var so it can be toggled

    LaunchedEffect(key1 = productId) {
        if (!productId.isNullOrBlank()) {
            viewModel.getProductByID(productId)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerpadding ->
        when {
            getProductById.value.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(id = R.color.orange))
                }
            }

            getProductById.value.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = getProductById.value.errorMessage!!, color = Color.Red)
                }
            }

            getProductById.value.UserData != null -> {
                val product = getProductById.value.UserData!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerpadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Image Header
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        // 2. Name & Price
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )

                        Text(
                            text = "Rs ${product.price}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colorResource(id = R.color.orange),
                            modifier = Modifier.padding(vertical = 8.dp),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )

                        // 3. Size Selection
                        Text(
                            text = "Select Size",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("S", "M", "L", "XL").forEach { size ->
                                OutlinedButton(
                                    onClick = { selectedSize = size },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (selectedSize == size) colorResource(id = R.color.orange) else Color.Transparent,
                                        contentColor = if (selectedSize == size) Color.White else colorResource(
                                            id = R.color.orange
                                        )
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        colorResource(id = R.color.orange)
                                    )
                                ) {
                                    Text(text = size) // Fixed: Now shows S, M, L, XL
                                }
                            }
                        }

                        // 4. Quantity Selection
                        Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }) {
                                Text("-", style = MaterialTheme.typography.headlineMedium)
                            }
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(onClick = { quantity++ }) {
                                Text("+", style = MaterialTheme.typography.headlineMedium)
                            }
                        }

                        // 5. Description
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                       Spacer(modifier = Modifier.height(32.dp))

                        // 6. Action Buttons
                        Button(
                            onClick = {
                                val cartDataModels = CartDataModel(
                                    productId = productId,
                                    name = product.name,
                                    image = product.image,
                                    price = product.price,
                                    quantity = quantity,
                                    size = selectedSize
                                )
                                viewModel.addToCart(cartDataModels = cartDataModels)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange)),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text("Add to Cart", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { navController.navigate(Routes.CheckoutScreen(productId)) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        ) {
                            Text("Buy Now", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                isFavorite = !isFavorite
                                viewModel.addToFav(product)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                colorResource(id = R.color.orange)
                            )
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = colorResource(id = R.color.orange)
                            )
                          Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Wishlist", color = colorResource(id = R.color.orange))
                        }

                       Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
