package com.example.myapplication.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.domain.di.model.OrderDataModel
import com.example.myapplication.presentation.Screens.admin.OrderStatusChip
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreenUi(
    navController: NavController,
    viewModel: ShoppingAppViewModel = hiltViewModel()
) {
    val ordersState by viewModel.getMyOrdersState.collectAsStateWithLifecycle()
    var selectedOrderForDetail by remember { mutableStateOf<OrderDataModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getMyOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.orange)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
                .padding(16.dp)
        ) {
            if (ordersState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = R.color.orange)
                )
            } else if (ordersState.errorMessage != null) {
                Text(
                    text = ordersState.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (ordersState.orders.isEmpty()) {
                Text(
                    text = "You have not placed any orders yet.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ordersState.orders) { order ->
                        UserOrderCard(
                            order = order,
                            onClick = { selectedOrderForDetail = order }
                        )
                    }
                }
            }
        }
    }

    if (selectedOrderForDetail != null) {
        UserOrderDetailDialog(
            order = selectedOrderForDetail!!,
            onDismiss = { selectedOrderForDetail = null }
        )
    }
}

@Composable
fun UserOrderCard(
    order: OrderDataModel,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(order.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order ID: ${order.orderId.takeLast(8).uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
                OrderStatusChip(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Placed on: $formattedDate",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.items.sumOf { it.quantity }} items",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Total: Rs. ${order.totalAmount}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colorResource(id = R.color.orange)
                )
            }
        }
    }
}

@Composable
fun UserOrderDetailDialog(
    order: OrderDataModel,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(order.date))
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Order Status Details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Order ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Date: $formattedDate", fontSize = 13.sp, color = Color.Gray)
                    Text(text = "Delivery Method: ${order.selectedDeliveryMethod}", fontSize = 13.sp, color = Color.Gray)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Status:", fontSize = 13.sp, color = Color.Gray)
                        OrderStatusChip(status = order.orderStatus)
                    }
                }

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Shipping Details
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Shipping Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "${order.firstName} ${order.lastName}", fontSize = 13.sp)
                    Text(text = "${order.address}, ${order.city}, ${order.postalCode}, ${order.country}", fontSize = 13.sp)
                }

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Items list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Items", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text(text = "Qty: ${item.quantity} | Size: ${item.size}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(
                                text = "Rs. ${item.price}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Paid:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "Rs. ${order.totalAmount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.orange)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.orange))
            ) {
                Text("Dismiss", color = Color.White)
            }
        }
    )
}
