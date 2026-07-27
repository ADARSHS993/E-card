package com.example.myapplication.domain.di.repo

import android.net.Uri
import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.BannerDataModel
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.model.USerDataParent
import com.example.myapplication.domain.di.model.UserData
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun registerUserwithEmailAndPassword(userData: UserData) : Flow<ResultState<String>>
    fun loginUserwithEmailAndPassword(userData : UserData) : Flow<ResultState<String>>
    fun getUserById(uid: String) : Flow<ResultState<USerDataParent>>
    fun updateUserData(userDataParent: USerDataParent): Flow<ResultState<String>>
    fun userProfileImage(uri : Uri) : Flow<ResultState<String>>
    fun getCategoriesInLimited() : Flow<ResultState<List<CategoryDataModel>>>
    fun getProductInLimited() : Flow<ResultState<List<ProductDataModel>>>
    fun getAllProducts() : Flow<ResultState<List<ProductDataModel>>>
    fun getProductById(productId : String) : Flow<ResultState<ProductDataModel>>
    fun addToCart(cartDataModels : CartDataModel) : Flow<ResultState<String>>
    fun addToFav(productDataModels : ProductDataModel) : Flow<ResultState<String>>
    fun getallFav() : Flow<ResultState<List<ProductDataModel>>>
    fun getCart() : Flow<ResultState<List<CartDataModel>>>
    fun getAllCategories() : Flow<ResultState<List<CategoryDataModel>>>
    fun getCheckout(productId : String) : Flow<ResultState<ProductDataModel>>
    fun getBanner() : Flow<ResultState<List<BannerDataModel>>>
    fun getSpecificCategories(categoryName : String) : Flow<ResultState<List<ProductDataModel>>>
    fun getAllSuggestedProducts() : Flow<ResultState<List<ProductDataModel>>>
}