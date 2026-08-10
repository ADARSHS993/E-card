package com.example.myapplication.presentation.Screens.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.presentation.ViewModel.AdminViewModel
import com.example.myapplication.presentation.utils.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val productsState by viewModel.productsState.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductDataModel?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<ProductDataModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getProducts()
        viewModel.getCategories()
    }

    val filteredProducts = productsState.products.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Products", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.orange)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = colorResource(id = R.color.orange),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or category...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.orange),
                    cursorColor = colorResource(id = R.color.orange)
                )
            )

            Box(modifier = Modifier.weight(1f)) {
                if (productsState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorResource(id = R.color.orange)
                    )
                } else if (productsState.errorMessage != null) {
                    Text(
                        text = productsState.errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (filteredProducts.isEmpty()) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No products found. Click + to add." else "No matching products found.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredProducts) { product ->
                            ProductRow(
                                product = product,
                                onEdit = {
                                    productToEdit = product
                                    showEditDialog = true
                                },
                                onDelete = {
                                    showDeleteConfirm = product
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddDialog) {
        ProductDialog(
            title = "Add Product",
            categories = categoriesState.categories.map { it.name },
            onDismiss = { showAddDialog = false },
            onConfirm = { product, imageUri ->
                viewModel.addProduct(product, imageUri) {
                    Toast.makeText(context, "Product added!", Toast.LENGTH_SHORT).show()
                    viewModel.clearActionState()
                    showAddDialog = false
                }
            },
            actionLoading = actionState.isLoading
        )
    }

    // Edit Product Dialog
    if (showEditDialog && productToEdit != null) {
        ProductDialog(
            title = "Edit Product",
            categories = categoriesState.categories.map { it.name },
            initialProduct = productToEdit,
            onDismiss = {
                showEditDialog = false
                productToEdit = null
            },
            onConfirm = { product, imageUri ->
                viewModel.updateProduct(product, imageUri) {
                    Toast.makeText(context, "Product updated!", Toast.LENGTH_SHORT).show()
                    viewModel.clearActionState()
                    showEditDialog = false
                    productToEdit = null
                }
            },
            actionLoading = actionState.isLoading
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Product?") },
            text = { Text("Are you sure you want to delete product '${showDeleteConfirm!!.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        val productId = showDeleteConfirm!!.productId
                        viewModel.deleteProduct(productId) {
                            Toast.makeText(context, "Product deleted!", Toast.LENGTH_SHORT).show()
                            viewModel.clearActionState()
                            showDeleteConfirm = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProductRow(
    product: ProductDataModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    maxLines = 1
                )
                Text(
                    text = "Category: ${product.category}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Stock: ${product.availableUnits}",
                    fontSize = 13.sp,
                    color = if (product.availableUnits <= 5) Color.Red else Color.Green.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Rs. ${product.finalPrice}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.orange)
                    )
                    if (product.price != product.finalPrice) {
                        Text(
                            text = "Rs. ${product.price}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    title: String,
    categories: List<String>,
    initialProduct: ProductDataModel? = null,
    onDismiss: () -> Unit,
    onConfirm: (product: ProductDataModel, imageUri: Uri?) -> Unit,
    actionLoading: Boolean
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var price by remember { mutableStateOf(initialProduct?.price ?: "") }
    var finalPrice by remember { mutableStateOf(initialProduct?.finalPrice ?: "") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(initialProduct?.category ?: (categories.firstOrNull() ?: "")) }
    var availableUnits by remember { mutableStateOf(initialProduct?.availableUnits?.toString() ?: "0") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    var dropdownExpanded by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { if (!actionLoading) onDismiss() },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Product Image Picker
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.4f))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (initialProduct?.image?.isNotEmpty() == true) {
                        AsyncImage(
                            model = initialProduct.image,
                            contentDescription = "Existing Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Add Image",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Product Name",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.orange),
                            cursorColor = colorResource(id = R.color.orange)
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        categories.forEach { categoryName ->
                            DropdownMenuItem(
                                text = { Text(categoryName) },
                                onClick = {
                                    selectedCategory = categoryName
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = "Original Price",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        value = finalPrice,
                        onValueChange = { finalPrice = it },
                        label = "Final Price",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = availableUnits,
                    onValueChange = { availableUnits = it },
                    label = "Available Stock",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    modifier = Modifier.fillMaxWidth()
                )
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
                        if (name.isNotBlank() && price.isNotBlank() && finalPrice.isNotBlank() && selectedCategory.isNotBlank()) {
                            val newProduct = ProductDataModel(
                                name = name.trim(),
                                price = price.trim(),
                                finalPrice = finalPrice.trim(),
                                description = description.trim(),
                                category = selectedCategory,
                                availableUnits = availableUnits.toIntOrNull() ?: 0,
                                date = initialProduct?.date ?: System.currentTimeMillis(),
                                createBy = initialProduct?.createBy ?: "Admin",
                                image = initialProduct?.image ?: "",
                                productId = initialProduct?.productId ?: ""
                            )
                            onConfirm(newProduct, selectedImageUri)
                        } else {
                            // Validation failure
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.orange))
                ) {
                    Text("Save", color = Color.White)
                }
            }
        },
        dismissButton = {
            if (!actionLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
