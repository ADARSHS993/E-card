package com.example.myapplication.domain.di.model

data class CategoryDataModel(
    var name : String = "",
    var date : Long = System.currentTimeMillis(),
    var createBy : String = "",
    var categoryImage : String = "",

)
