package com.example.myapplication.domain.di.repo

import android.net.Uri
import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.OrderDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.model.UserData
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun checkIsAdmin(uid: String): Flow<ResultState<Boolean>>
    
    // Categories
    fun getCategories(): Flow<ResultState<List<CategoryDataModel>>>
    fun addCategory(category: CategoryDataModel, imageUri: Uri?): Flow<ResultState<String>>
    fun updateCategory(oldName: String, category: CategoryDataModel, imageUri: Uri?): Flow<ResultState<String>>
    fun deleteCategory(categoryName: String): Flow<ResultState<String>>
    
    // Products
    fun getProducts(): Flow<ResultState<List<ProductDataModel>>>
    fun addProduct(product: ProductDataModel, imageUri: Uri?): Flow<ResultState<String>>
    fun updateProduct(product: ProductDataModel, imageUri: Uri?): Flow<ResultState<String>>
    fun deleteProduct(productId: String): Flow<ResultState<String>>
    
    // Orders
    fun getOrders(): Flow<ResultState<List<OrderDataModel>>>
    fun updateOrderStatus(orderId: String, status: String): Flow<ResultState<String>>
    
    // Users
    fun getUsers(): Flow<ResultState<List<com.example.myapplication.domain.di.model.USerDataParent>>>
}
