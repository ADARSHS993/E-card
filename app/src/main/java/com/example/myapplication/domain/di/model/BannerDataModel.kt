package com.example.myapplication.domain.di.model

data class BannerDataModel(
    val name : String = "",
    val image: String = "",
    val date : Long = System.currentTimeMillis()

)
