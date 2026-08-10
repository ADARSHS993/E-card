package com.example.myapplication.data.di.repo

import android.net.Uri
import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.OrderDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.model.UserData
import com.example.myapplication.domain.di.repo.AdminRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : AdminRepository {

    override fun checkIsAdmin(uid: String): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("admins").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getString("role") == "admin") {
                    trySend(ResultState.Success(true))
                } else {
                    trySend(ResultState.Success(false))
                }
                close()
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Failed to check admin access"))
                close()
            }
        awaitClose { }
    }

    // Categories
    override fun getCategories(): Flow<ResultState<List<CategoryDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = firebaseFirestore.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Failed to load categories"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val categories = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CategoryDataModel::class.java)
                    }.sortedBy { it.name }
                    trySend(ResultState.Success(categories))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun addCategory(category: CategoryDataModel, imageUri: Uri?): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("categoryImages/${category.name}_${System.currentTimeMillis()}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        category.categoryImage = downloadUrl.toString()
                        firebaseFirestore.collection("categories").document(category.name).set(category)
                            .addOnSuccessListener {
                                trySend(ResultState.Success("Category added successfully"))
                                close()
                            }
                            .addOnFailureListener {
                                trySend(ResultState.Error(it.message ?: "Failed to save category"))
                                close()
                            }
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to upload image"))
                    close()
                }
        } else {
            firebaseFirestore.collection("categories").document(category.name).set(category)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Category added successfully"))
                    close()
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to save category"))
                    close()
                }
        }
        awaitClose { }
    }

    override fun updateCategory(oldName: String, category: CategoryDataModel, imageUri: Uri?): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        
        val performSave = { finalCategory: CategoryDataModel ->
            val batch = firebaseFirestore.batch()
            
            if (oldName != finalCategory.name) {
                val oldDocRef = firebaseFirestore.collection("categories").document(oldName)
                val newDocRef = firebaseFirestore.collection("categories").document(finalCategory.name)
                batch.delete(oldDocRef)
                batch.set(newDocRef, finalCategory)
            } else {
                val docRef = firebaseFirestore.collection("categories").document(finalCategory.name)
                batch.set(docRef, finalCategory)
            }
            
            batch.commit().addOnSuccessListener {
                if (oldName != finalCategory.name) {
                    firebaseFirestore.collection("products").whereEqualTo("category", oldName).get()
                        .addOnSuccessListener { productSnapshot ->
                            val productBatch = firebaseFirestore.batch()
                            for (doc in productSnapshot.documents) {
                                productBatch.update(doc.reference, "category", finalCategory.name)
                            }
                            productBatch.commit().addOnCompleteListener {
                                trySend(ResultState.Success("Category updated successfully"))
                                close()
                            }
                        }
                        .addOnFailureListener {
                            trySend(ResultState.Success("Category updated (product sync failed)"))
                            close()
                        }
                } else {
                    trySend(ResultState.Success("Category updated successfully"))
                    close()
                }
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Failed to update category"))
                close()
            }
        }
        
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("categoryImages/${category.name}_${System.currentTimeMillis()}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        category.categoryImage = downloadUrl.toString()
                        performSave(category)
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to upload image"))
                    close()
                }
        } else {
            performSave(category)
        }
        awaitClose { }
    }

    override fun deleteCategory(categoryName: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("categories").document(categoryName).delete()
            .addOnSuccessListener {
                trySend(ResultState.Success("Category deleted successfully"))
                close()
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Failed to delete category"))
                close()
            }
        awaitClose { }
    }

    // Products
    override fun getProducts(): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = firebaseFirestore.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Failed to load products"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ProductDataModel::class.java)?.apply {
                            productId = doc.id
                        }
                    }.sortedByDescending { it.date }
                    trySend(ResultState.Success(products))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun addProduct(product: ProductDataModel, imageUri: Uri?): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val docRef = firebaseFirestore.collection("products").document()
        product.productId = docRef.id
        
        val performSave = { finalProduct: ProductDataModel ->
            docRef.set(finalProduct)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product added successfully"))
                    close()
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to save product"))
                    close()
                }
        }
        
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("productImages/${product.productId}_${System.currentTimeMillis()}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        product.image = downloadUrl.toString()
                        performSave(product)
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to upload image"))
                    close()
                }
        } else {
            performSave(product)
        }
        awaitClose { }
    }

    override fun updateProduct(product: ProductDataModel, imageUri: Uri?): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        
        val performSave = { finalProduct: ProductDataModel ->
            firebaseFirestore.collection("products").document(finalProduct.productId).set(finalProduct)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product updated successfully"))
                    close()
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to update product"))
                    close()
                }
        }
        
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("productImages/${product.productId}_${System.currentTimeMillis()}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        product.image = downloadUrl.toString()
                        performSave(product)
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Failed to upload image"))
                    close()
                }
        } else {
            performSave(product)
        }
        awaitClose { }
    }

    override fun deleteProduct(productId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("products").document(productId).delete()
            .addOnSuccessListener {
                trySend(ResultState.Success("Product deleted successfully"))
                close()
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Failed to delete product"))
                close()
            }
        awaitClose { }
    }

    // Orders
    override fun getOrders(): Flow<ResultState<List<OrderDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = firebaseFirestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Failed to load orders"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(OrderDataModel::class.java)
                    }.sortedByDescending { it.date }
                    trySend(ResultState.Success(orders))
                }
            }
        awaitClose { listener.remove() }
    }

    override fun updateOrderStatus(orderId: String, status: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("orders").document(orderId)
            .update("orderStatus", status)
            .addOnSuccessListener {
                trySend(ResultState.Success("Order status updated successfully"))
                close()
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Failed to update order status"))
                close()
            }
        awaitClose { }
    }

    // Users
    override fun getUsers(): Flow<ResultState<List<com.example.myapplication.domain.di.model.USerDataParent>>> = callbackFlow {
        trySend(ResultState.Loading)
        val listener = firebaseFirestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Error(error.message ?: "Failed to load users"))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val users = snapshot.documents.mapNotNull { doc ->
                        val data = doc.toObject(UserData::class.java)
                        if (data != null) {
                            com.example.myapplication.domain.di.model.USerDataParent(doc.id, data)
                        } else {
                            null
                        }
                    }
                    trySend(ResultState.Success(users))
                }
            }
        awaitClose { listener.remove() }
    }
}
