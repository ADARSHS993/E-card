package com.example.myapplication.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.OrderDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.model.UserData
import com.example.myapplication.domain.di.repo.AdminRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepo: AdminRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow(AdminLoginState())
    val loginState = _loginState.asStateFlow()

    private val _categoriesState = MutableStateFlow(AdminCategoriesState())
    val categoriesState = _categoriesState.asStateFlow()

    private val _productsState = MutableStateFlow(AdminProductsState())
    val productsState = _productsState.asStateFlow()

    private val _ordersState = MutableStateFlow(AdminOrdersState())
    val ordersState = _ordersState.asStateFlow()

    private val _usersState = MutableStateFlow(AdminUsersState())
    val usersState = _usersState.asStateFlow()

    private val _actionState = MutableStateFlow(AdminActionState())
    val actionState = _actionState.asStateFlow()

    fun loginAdmin(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginState.value = AdminLoginState(isLoading = true)
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result.user?.uid ?: ""
                        checkIsAdmin(uid, onSuccess)
                    } else {
                        _loginState.value = AdminLoginState(
                            isLoading = false,
                            errorMessage = task.exception?.localizedMessage ?: "Login failed"
                        )
                    }
                }
        }
    }

    private fun checkIsAdmin(uid: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.checkIsAdmin(uid).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _loginState.value = AdminLoginState(isLoading = true)
                    }
                    is ResultState.Error -> {
                        firebaseAuth.signOut()
                        _loginState.value = AdminLoginState(isLoading = false, errorMessage = result.message)
                    }
                    is ResultState.Success -> {
                        if (result.data) {
                            _loginState.value = AdminLoginState(isLoading = false, isSuccess = true)
                            onSuccess()
                        } else {
                            firebaseAuth.signOut()
                            _loginState.value = AdminLoginState(isLoading = false, errorMessage = "Access denied. Not an Admin.")
                        }
                    }
                }
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _loginState.value = AdminLoginState()
    }

    // Categories
    fun getCategories() {
        viewModelScope.launch {
            adminRepo.getCategories().collect { result ->
                when (result) {
                    is ResultState.Loading -> _categoriesState.value = _categoriesState.value.copy(isLoading = true)
                    is ResultState.Error -> _categoriesState.value = _categoriesState.value.copy(isLoading = false, errorMessage = result.message)
                    is ResultState.Success -> _categoriesState.value = _categoriesState.value.copy(isLoading = false, categories = result.data, errorMessage = null)
                }
            }
        }
    }

    fun addCategory(category: CategoryDataModel, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.addCategory(category, imageUri).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    fun updateCategory(oldName: String, category: CategoryDataModel, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.updateCategory(oldName, category, imageUri).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    fun deleteCategory(categoryName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.deleteCategory(categoryName).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    // Products
    fun getProducts() {
        viewModelScope.launch {
            adminRepo.getProducts().collect { result ->
                when (result) {
                    is ResultState.Loading -> _productsState.value = _productsState.value.copy(isLoading = true)
                    is ResultState.Error -> _productsState.value = _productsState.value.copy(isLoading = false, errorMessage = result.message)
                    is ResultState.Success -> _productsState.value = _productsState.value.copy(isLoading = false, products = result.data, errorMessage = null)
                }
            }
        }
    }

    fun addProduct(product: ProductDataModel, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.addProduct(product, imageUri).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    fun updateProduct(product: ProductDataModel, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.updateProduct(product, imageUri).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    fun deleteProduct(productId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.deleteProduct(productId).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    // Orders
    fun getOrders() {
        viewModelScope.launch {
            adminRepo.getOrders().collect { result ->
                when (result) {
                    is ResultState.Loading -> _ordersState.value = _ordersState.value.copy(isLoading = true)
                    is ResultState.Error -> _ordersState.value = _ordersState.value.copy(isLoading = false, errorMessage = result.message)
                    is ResultState.Success -> _ordersState.value = _ordersState.value.copy(isLoading = false, orders = result.data, errorMessage = null)
                }
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            adminRepo.updateOrderStatus(orderId, status).collect { result ->
                handleActionResult(result, onSuccess)
            }
        }
    }

    // Users
    fun getUsers() {
        viewModelScope.launch {
            adminRepo.getUsers().collect { result ->
                when (result) {
                    is ResultState.Loading -> _usersState.value = _usersState.value.copy(isLoading = true)
                    is ResultState.Error -> _usersState.value = _usersState.value.copy(isLoading = false, errorMessage = result.message)
                    is ResultState.Success -> _usersState.value = _usersState.value.copy(isLoading = false, users = result.data, errorMessage = null)
                }
            }
        }
    }

    private fun handleActionResult(result: ResultState<String>, onSuccess: () -> Unit) {
        when (result) {
            is ResultState.Loading -> _actionState.value = AdminActionState(isLoading = true)
            is ResultState.Error -> _actionState.value = AdminActionState(isLoading = false, errorMessage = result.message)
            is ResultState.Success -> {
                _actionState.value = AdminActionState(isLoading = false, isSuccess = true, message = result.data)
                onSuccess()
            }
        }
    }

    fun clearActionState() {
        _actionState.value = AdminActionState()
    }
    
    fun clearLoginState() {
        _loginState.value = AdminLoginState()
    }
}

data class AdminLoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class AdminCategoriesState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDataModel> = emptyList(),
    val errorMessage: String? = null
)

data class AdminProductsState(
    val isLoading: Boolean = false,
    val products: List<ProductDataModel> = emptyList(),
    val errorMessage: String? = null
)

data class AdminOrdersState(
    val isLoading: Boolean = false,
    val orders: List<OrderDataModel> = emptyList(),
    val errorMessage: String? = null
)

data class AdminUsersState(
    val isLoading: Boolean = false,
    val users: List<com.example.myapplication.domain.di.model.USerDataParent> = emptyList(),
    val errorMessage: String? = null
)

data class AdminActionState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val message: String? = null
)
