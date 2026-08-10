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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.presentation.ViewModel.AdminViewModel
import com.example.myapplication.presentation.utils.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryDataModel?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<CategoryDataModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", fontWeight = FontWeight.Bold, color = Color.White) },
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
        ) {
            if (categoriesState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = R.color.orange)
                )
            } else if (categoriesState.errorMessage != null) {
                Text(
                    text = categoriesState.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (categoriesState.categories.isEmpty()) {
                Text(
                    text = "No categories found. Click + to add.",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoriesState.categories) { category ->
                        CategoryRow(
                            category = category,
                            onEdit = {
                                categoryToEdit = category
                                showEditDialog = true
                            },
                            onDelete = {
                                showDeleteConfirm = category
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddDialog) {
        CategoryDialog(
            title = "Add New Category",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, imageUri ->
                val newCategory = CategoryDataModel(
                    name = name,
                    date = System.currentTimeMillis(),
                    createBy = "Admin",
                    categoryImage = ""
                )
                viewModel.addCategory(newCategory, imageUri) {
                    Toast.makeText(context, "Category added!", Toast.LENGTH_SHORT).show()
                    viewModel.clearActionState()
                    showAddDialog = false
                }
            },
            actionLoading = actionState.isLoading
        )
    }

    // Edit Category Dialog
    if (showEditDialog && categoryToEdit != null) {
        CategoryDialog(
            title = "Edit Category",
            initialName = categoryToEdit!!.name,
            initialImage = categoryToEdit!!.categoryImage,
            onDismiss = {
                showEditDialog = false
                categoryToEdit = null
            },
            onConfirm = { name, imageUri ->
                val updatedCategory = categoryToEdit!!.copy(
                    name = name
                )
                viewModel.updateCategory(categoryToEdit!!.name, updatedCategory, imageUri) {
                    Toast.makeText(context, "Category updated!", Toast.LENGTH_SHORT).show()
                    viewModel.clearActionState()
                    showEditDialog = false
                    categoryToEdit = null
                }
            },
            actionLoading = actionState.isLoading
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Category?") },
            text = { Text("Are you sure you want to delete category '${showDeleteConfirm!!.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        val categoryName = showDeleteConfirm!!.name
                        viewModel.deleteCategory(categoryName) {
                            Toast.makeText(context, "Category deleted!", Toast.LENGTH_SHORT).show()
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
fun CategoryRow(
    category: CategoryDataModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
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
                model = category.categoryImage,
                contentDescription = category.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = category.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }
            
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun CategoryDialog(
    title: String,
    initialName: String = "",
    initialImage: String = "",
    onDismiss: () -> Unit,
    onConfirm: (name: String, imageUri: Uri?) -> Unit,
    actionLoading: Boolean
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!actionLoading) onDismiss() },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Category Name",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.4f))
                        .border(1.dp, Color.Gray, CircleShape)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (initialImage.isNotEmpty()) {
                        AsyncImage(
                            model = initialImage,
                            contentDescription = "Existing Image",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Choose Image",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
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
                        if (name.isNotBlank()) {
                            onConfirm(name.trim(), selectedImageUri)
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
