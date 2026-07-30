package com.example.myapplication.presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.HomeScreenState
import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.UseCase.AddToFavUseCase
import com.example.myapplication.domain.di.UseCase.AddtoCardUseCase
import com.example.myapplication.domain.di.UseCase.CreateUseCase
import com.example.myapplication.domain.di.UseCase.GetAllBannersUSeCase
import com.example.myapplication.domain.di.UseCase.GetAllCategoryUSeCase
import com.example.myapplication.domain.di.UseCase.GetAllFavUseCase
import com.example.myapplication.domain.di.UseCase.GetAllProductUseCase
import com.example.myapplication.domain.di.UseCase.GetAllSuggestedProductUseCase
import com.example.myapplication.domain.di.UseCase.GetCartUSeCase
import com.example.myapplication.domain.di.UseCase.GetCheckOutUSeCase
import com.example.myapplication.domain.di.UseCase.GetPRoductInLimitsUSeCase
import com.example.myapplication.domain.di.UseCase.GetProductById
import com.example.myapplication.domain.di.UseCase.GetSpecificCategoryUSeCase
import com.example.myapplication.domain.di.UseCase.GetUserUSeCase
import com.example.myapplication.domain.di.UseCase.LoginUserUseCase
import com.example.myapplication.domain.di.UseCase.RemoveFromCartUseCase
import com.example.myapplication.domain.di.UseCase.UpdateUserDataUseCase
import com.example.myapplication.domain.di.UseCase.UserProfileImageUseCase
import com.example.myapplication.domain.di.UseCase.getCategoryInLimitUseCase
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.model.USerDataParent
import com.example.myapplication.domain.di.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingAppViewModel @Inject constructor(
    private val createUseCase: CreateUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserUseCase: GetUserUSeCase,
    private val updateUserDataUseCase : UpdateUserDataUseCase,
    private val userProfileImageUseCase: UserProfileImageUseCase,
    private val getProductsInLimitsUSeCase: GetPRoductInLimitsUSeCase,
    private val getCategoriesInLimitsUSeCase: getCategoryInLimitUseCase,
    private val addtoCardUSeCase: AddtoCardUseCase,
    private val getProductById: GetProductById,
    private val addToFavUSeCase: AddToFavUseCase,
    private val getAllFavUseCase: GetAllFavUseCase,
    private val getAllCategoryUSeCase: GetAllCategoryUSeCase,
    private val getCheckOutUSeCase: GetCheckOutUSeCase,
    private val getAllBannersUSeCase: GetAllBannersUSeCase,
    private val getSpecificCategoryUSeCase: GetSpecificCategoryUSeCase,
    private val getAllSuggestedProductUseCase: GetAllSuggestedProductUseCase,
    private val getAllProductUseCase: GetAllProductUseCase,
    private val getCartUSeCase : GetCartUSeCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
    ): ViewModel()
{

        private val _signUpState = MutableStateFlow(SignUpScreenState())
        val signUpScreenState = _signUpState.asStateFlow()

        private val _loginState = MutableStateFlow(LoginScreenState())
        val loginScreenState = _loginState.asStateFlow()

        private val _prodfileState = MutableStateFlow(ProdfileScreenState())
        val prodfileScreenState = _prodfileState.asStateFlow()

        private val _removeFromCartState = MutableStateFlow(removeFromCartState())

        private val _upDateState = MutableStateFlow(UpDateScreenState())
        val upDateScreenState = _upDateState.asStateFlow()

        private val _uploadUserProfileImageState = MutableStateFlow(UploadUserProfileImageState())
        val uploadUserProfileImageState = _uploadUserProfileImageState.asStateFlow()

        private val _addtoCardState = MutableStateFlow(AddtoCardState())
        val addtoCardState = _addtoCardState.asStateFlow()

        private val _getProductByIdState = MutableStateFlow(GetProductByIDState())
        val getProductByIdState = _getProductByIdState.asStateFlow()

        private val _addToFavState = MutableStateFlow(AddToFav())
        val addToFavState = _addToFavState.asStateFlow()

        private val _getAllFavState = MutableStateFlow(GetAllFavState())
        val getAllFavState = _getAllFavState.asStateFlow()

        private val _getAllProductState = MutableStateFlow(GetAllProductState())
        val getAllProductState = _getAllProductState.asStateFlow()

        private val _getAllCategoriesState = MutableStateFlow(GetAllCategoriesState())
        val getAllCategoriesState = _getAllCategoriesState.asStateFlow()

        private val _getCartState = MutableStateFlow(GetCartState())
        val getCartState = _getCartState.asStateFlow()

        private val _getCheckoutState = MutableStateFlow(GetCheckoutState())
        val getCheckoutState = _getCheckoutState.asStateFlow()

        private val _getSpecificCategoryItemsState = MutableStateFlow(GetSpecificCategoryItemsState())
        val getSpecificCategoryItemsState = _getSpecificCategoryItemsState.asStateFlow()

        private val _getAllSuggestedProductsState = MutableStateFlow(GetAllSuggestedProductsState())
        val getAllSuggestedProductsState = _getAllSuggestedProductsState.asStateFlow()

        private val homeScreenState = MutableStateFlow(HomeScreenState())
        val homeState = homeScreenState.asStateFlow()


    fun getSpecificCategoryItems(categoryName: String) {

        viewModelScope.launch {
            getSpecificCategoryUSeCase.getSpecificCategory(categoryName).collect {

                when (it) {
                    is ResultState.Error -> {
                        _getSpecificCategoryItemsState.value = _getSpecificCategoryItemsState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )

                    }

                    is ResultState.Loading -> {
                        _getSpecificCategoryItemsState.value = _getSpecificCategoryItemsState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getSpecificCategoryItemsState.value = _getSpecificCategoryItemsState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }

                  }

                }
            }
        }




    fun getCheckOut(productId: String) {
        // 1. Check if the string is actually a number (Total Price from Cart)
        val isPrice = productId.toDoubleOrNull() != null || productId.contains(".")

        if (isPrice) {
            // If it's a price, update state manually without calling Firestore
            _getCheckoutState.value = _getCheckoutState.value.copy(
                isLoading = false,
                errorMessage = null,
                UserData = ProductDataModel(
                    productId = "CART_CHECKOUT",
                    name = "Cart Items Total",
                    price = productId, // This is the total price passed from Cart
                    image = "" // You can put a placeholder cart icon URL here
                )
            )
        } else {
            // 2. Normal logic: Fetch single product details by ID
            viewModelScope.launch {
                getCheckOutUSeCase.getCheckOut(productId).collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _getCheckoutState.value = _getCheckoutState.value.copy(isLoading = true)
                        }
                        is ResultState.Error -> {
                            _getCheckoutState.value = _getCheckoutState.value.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        is ResultState.Success -> {
                            _getCheckoutState.value = _getCheckoutState.value.copy(
                                isLoading = false,
                                UserData = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    fun getAllCategories(){

        viewModelScope.launch {
            getAllCategoryUSeCase.getAllCategory().collect {

                when(it){
                    is ResultState.Error -> {
                        _getAllCategoriesState.value = _getAllCategoriesState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _getAllCategoriesState.value = _getAllCategoriesState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _getAllCategoriesState.value = _getAllCategoriesState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }

            }
            }
    }

    fun getCart(){

        viewModelScope.launch {

            getCartUSeCase.getCart().collect {

                when(it){
                    is ResultState.Error -> {
                        _getCartState.value = _getCartState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )

                    }

                    is ResultState.Loading -> {
                        _getCartState.value = _getCartState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getCartState.value = _getCartState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }
            }
        }
    }


    fun removeFromCart(cartId: String) {

        viewModelScope.launch {

            removeFromCartUseCase
                .removeFromCart(cartId)
                .collect { result ->

                    when (result) {

                        is ResultState.Loading -> {

                            _removeFromCartState.value =
                                _removeFromCartState.value.copy(
                                    isLoading = true,
                                    errorMessage = null
                                )
                        }

                        is ResultState.Error -> {

                            _removeFromCartState.value =
                                _removeFromCartState.value.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                        }

                        is ResultState.Success -> {

                            _removeFromCartState.value =
                                _removeFromCartState.value.copy(
                                    isLoading = false,
                                    errorMessage = null
                                )

                            // No getCart() needed.
                            // SnapshotListener automatically updates cart.
                        }
                    }
                }
        }
    }

    fun getAllProducts(){
        viewModelScope.launch {

            getAllProductUseCase.getAllProduct().collect {

                when(it){

                    is ResultState.Error<*> -> {
                        _getAllProductState.value = _getAllProductState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getAllProductState.value = _getAllProductState.value.copy(
                            isLoading = true
                        )
                    }

                   is ResultState.Success -> {
                       _getAllProductState.value = _getAllProductState.value.copy(
                           isLoading = false,
                           UserData = it.data
                       )
                   }
                }
            }
        }
    }

    fun getAllFav() {

        viewModelScope.launch {

            getAllFavUseCase.getAllFav().collect {

                when (it) {

                    is ResultState.Error -> {
                        _getAllFavState.value = _getAllFavState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getAllFavState.value = _getAllFavState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getAllFavState.value = _getAllFavState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }
            }
        }
    }

        fun addToFav(productDataModels: ProductDataModel) {

        viewModelScope.launch {
            addToFavUSeCase.addToFav(productDataModels).collect {
            when(it){

            is ResultState.Error -> {

            _addToFavState.value = _addToFavState.value.copy(
                isLoading = false,
                errorMessage = it.message
            )

            }

            is ResultState.Loading -> {

            _addToFavState.value = _addToFavState.value.copy(
                isLoading = true
            )

            }

            is ResultState.Success -> {
                _addToFavState.value = _addToFavState.value.copy(
                    isLoading = false,
                    UserData = it.data
                )
            }

            }
            }

            }
        }


        fun getProductByID(productId : String){

        viewModelScope.launch {
            getProductById.getProductById(productId).collect {

            when(it){

            is ResultState.Error -> {
                _getProductByIdState.value = _getProductByIdState.value.copy(
                    isLoading = false,
                    errorMessage = it.message
                )
            }

                is ResultState.Loading -> {

                    _getProductByIdState.value = _getProductByIdState.value.copy(
                      isLoading = true
                    )
            }

                is ResultState.Success -> {

                _getProductByIdState.value = _getProductByIdState.value.copy(

                    isLoading = false,
                    UserData = it.data
                )
              }
             }
            }
        }
    }

        fun addToCart(cartDataModels: CartDataModel) {

        viewModelScope.launch {

        addtoCardUSeCase.addtoCard(cartDataModels).collect {
            when(it){

                is ResultState.Loading -> {
                    _addtoCardState.value = _addtoCardState.value.copy(
                        isLoading = true
                    )
                }

                is ResultState.Error -> {
                    _addtoCardState.value = _addtoCardState.value.copy(
                        isLoading = false,
                        errorMessage = it.message

                    )
                }

                is ResultState.Success -> {
                    _addtoCardState.value = _addtoCardState.value.copy(
                        isLoading = false,
                        UserData = it.data
                    )
            }
            }
        }
        }
    }

    init {
        LoadHomeScreenData()
    }

    fun LoadHomeScreenData(){

        viewModelScope.launch {

            combine(

                getCategoriesInLimitsUSeCase.getCategoryInLimited(),
                getProductsInLimitsUSeCase.getProductInLimits(),
                getAllBannersUSeCase.getAllBanners()
            ){categoriesResult, productsResult, bannersResult ->
                when{
                    categoriesResult is ResultState.Error -> {
                        HomeScreenState(isLoading = false, errorMessage = categoriesResult.message)
                    }

                    productsResult is ResultState.Error -> {
                        HomeScreenState(isLoading = false, errorMessage = productsResult.message)
                    }

                    bannersResult is ResultState.Error -> {
                        HomeScreenState(isLoading = false, errorMessage = bannersResult.message)
                    }

                    categoriesResult is ResultState.Success && productsResult is ResultState.Success && bannersResult is ResultState.Success -> {

                        HomeScreenState(
                            isLoading = false,
                            categories = categoriesResult.data,
                            products = productsResult.data,
                            banner = bannersResult.data
                        )

                    }

                    else -> {
                        HomeScreenState(isLoading = true)
                    }
                }
            }.collect {
                state -> homeScreenState.value = state
            }
        }
    }


    fun uploadUSerProfileImage(uri : Uri){

        viewModelScope.launch {
            userProfileImageUseCase.UserProfileImage(uri).collect {

                when(it){

                    is ResultState.Error ->{
                        _uploadUserProfileImageState.value = _uploadUserProfileImageState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )

                    }

                    is ResultState.Loading -> {
                        _uploadUserProfileImageState.value = _uploadUserProfileImageState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _uploadUserProfileImageState.value = _uploadUserProfileImageState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }
            }
        }
    }

    fun upDateUserData(userDataParent: USerDataParent){

        viewModelScope.launch {

            updateUserDataUseCase.UpdateUseData(userDataParent).collect {

                when(it){

                    is ResultState.Error -> {
                        _upDateState.value = _upDateState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _upDateState.value = _upDateState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _upDateState.value = _upDateState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }
            }
        }
    }

    fun createUser(userData : UserData){

        viewModelScope.launch {

            createUseCase.createUser(userData).collect {

                when(it){

                    is ResultState.Error -> {
                        _signUpState.value = _signUpState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _signUpState.value = _signUpState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _signUpState.value = _signUpState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }
            }
        }
    }


    fun loginUser(userData: UserData){
        viewModelScope.launch {
            loginUserUseCase.login(userData).collect {

                when(it){

                    is ResultState.Error -> {
                        _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _loginState.value = _loginState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _loginState.value = _loginState.value.copy(
                            isLoading = false,
                            UserData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getUserById(uid : String) {

        viewModelScope.launch {

            getUserUseCase.getUSerById(uid).collect {

                when (it) {

                    is ResultState.Error -> {
                        _prodfileState.value = _prodfileState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {

                        _prodfileState.value = _prodfileState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {

                        _prodfileState.value = _prodfileState.value.copy(
                            isLoading = false,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getAllsuggestedProducts() {
        viewModelScope.launch {

          getAllSuggestedProductUseCase.getAllSuggestedProducts().collect {

              when(it){

                  is ResultState.Error -> {
                      _getAllSuggestedProductsState.value = _getAllSuggestedProductsState.value.copy(
                          isLoading = false,
                          errorMessage = it.message
                      )
                  }

                  is ResultState.Loading -> {
                      _getAllSuggestedProductsState.value = _getAllSuggestedProductsState.value.copy(
                          isLoading = true
                      )
                  }

                  is ResultState.Success -> {
                      _getAllSuggestedProductsState.value = _getAllSuggestedProductsState.value.copy(
                          isLoading = false,
                          UserData = it.data
                      )
                  }
              }

          }

        }
    }


}





data class ProdfileScreenState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val userData : USerDataParent? = null
)

data class SignUpScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : String? = null
)

data class LoginScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : String? = null
)

data class UpDateScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : String? = null
)

data class UploadUserProfileImageState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : String? = null
)

data class AddtoCardState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : String? = null
)

data class GetProductByIDState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : ProductDataModel? = null
)

data class AddToFav(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : String? = null
)

data class GetAllFavState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<ProductDataModel>? = emptyList()
)

data class GetAllProductState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<ProductDataModel>? = emptyList()
)

data class GetCartState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<CartDataModel>? = emptyList()
)

data class GetAllCategoriesState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<CategoryDataModel>? = emptyList()
)

data class GetCheckoutState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : ProductDataModel? = null
)

data class GetSpecificCategoryItemsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<ProductDataModel>? = emptyList()
)

data class GetAllSuggestedProductsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<ProductDataModel>? = emptyList()
)

data class removeFromCartState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val UserData : List<CartDataModel>? = emptyList()
)
