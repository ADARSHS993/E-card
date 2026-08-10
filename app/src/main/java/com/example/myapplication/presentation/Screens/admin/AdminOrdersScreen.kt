package com.example.myapplication.presentation.Screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.domain.di.model.OrderDataModel
import com.example.myapplication.presentation.ViewModel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    var selectedStatusFilter by remember { mutableStateOf("All") }
    var selectedOrderForDetail by remember { mutableStateOf<OrderDataModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getOrders()
    }

    val statuses = listOf("All", "Pending", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled")

    val filteredOrders = ordersState.orders.filter {
        selectedStatusFilter == "All" || it.orderStatus.equals(selectedStatusFilter, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Orders", fontWeight = FontWeight.Bold, color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
        ) {
            // Horizontal Filter Chips
            ScrollableTabRow(
                selectedTabIndex = statuses.indexOf(selectedStatusFilter),
                edgePadding = 16.dp,
                containerColor = Color.White,
                contentColor = colorResource(id = R.color.orange)
            ) {
                statuses.forEach { status ->
                    Tab(
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status },
                        text = {
                            Text(
                                text = status,
                                fontWeight = if (selectedStatusFilter == status) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
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
                } else if (filteredOrders.isEmpty()) {
                    Text(
                        text = "No orders found in this status.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredOrders) { order ->
                            OrderCard(
                                order = order,
                                onClick = { selectedOrderForDetail = order }
                            )
                        }
                    }
                }
            }
        }
    }

    // Order Detail & Update Dialog
    if (selectedOrderForDetail != null) {
        OrderDetailDialog(
            order = selectedOrderForDetail!!,
            onDismiss = { selectedOrderForDetail = null },
            onUpdateStatus = { status ->
                viewModel.updateOrderStatus(selectedOrderForDetail!!.orderId, status) {
                    Toast.makeText(context, "Order Status Updated to $status!", Toast.LENGTH_SHORT).show()
                    viewModel.clearActionState()
                    selectedOrderForDetail = null
                }
            },
            actionLoading = actionState.isLoading
        )
    }
}

@Composable
fun OrderCard(
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
                    text = "ID: ${order.orderId.takeLast(8).uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                OrderStatusChip(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Customer: ${order.firstName} ${order.lastName}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = "Date: $formattedDate",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.items.sumOf { it.quantity }} item(s)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Total: Rs. ${order.totalAmount}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.orange)
                )
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        "confirmed" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "processing" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        "shipped" -> Pair(Color(0xFFEDE7F6), Color(0xFF673AB7))
        "delivered" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "cancelled" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color.LightGray, Color.DarkGray)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailDialog(
    order: OrderDataModel,
    onDismiss: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    actionLoading: Boolean
) {
    var selectedStatus by remember { mutableStateOf(order.orderStatus) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("Pending", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled")

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(order.date))

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { if (!actionLoading) onDismiss() },
        title = {
            Text(
                text = "Order Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Summary
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Order ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Date: $formattedDate", fontSize = 13.sp, color = Color.Gray)
                    Text(text = "Payment Status: ${order.paymentStatus}", fontSize = 13.sp, color = Color.Gray)
                    Text(text = "Delivery Method: ${order.selectedDeliveryMethod}", fontSize = 13.sp, color = Color.Gray)
                }

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Customer Info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Customer Info", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "Name: ${order.firstName} ${order.lastName}", fontSize = 14.sp)
                    Text(text = "Email: ${order.email}", fontSize = 14.sp)
                    Text(
                        text = "Address: ${order.address}, ${order.city}, ${order.postalCode}, ${order.country}",
                        fontSize = 14.sp
                    )
                }

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Items list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Items Ordered", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(text = "Qty: ${item.quantity} | Size: ${item.size}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                text = "Rs. ${item.price}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Grand Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = "Rs. ${order.totalAmount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.orange)
                        )
                    }
                }

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // Status Update dropdown
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Update Order Status", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedStatus, fontWeight = FontWeight.Medium)
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Dropdown")
                        }
                        
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.75f)
                        ) {
                            statuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        selectedStatus = status
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (actionLoading) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.orange),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Button(
                    onClick = {
                        onUpdateStatus(selectedStatus)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.orange))
                ) {
                    Text("Update Status", color = Color.White)
                }
            }
        },
        dismissButton = {
            if (!actionLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}
