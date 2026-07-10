package com.example.myapplication.common

import com.example.myapplication.domain.di.model.BannerDataModel
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.ProductDataModel

data class HomeScreenState(
    val isLoading : Boolean = true,
    val errorMessage : String? = null,
    val categories : List<CategoryDataModel>? = null,
    val products : List<ProductDataModel>? = null,
    val banner : List<BannerDataModel>? = null
)