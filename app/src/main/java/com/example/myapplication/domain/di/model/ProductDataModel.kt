package com.example.myapplication.domain.di.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDataModel(
    var name : String = "",
    var description : String = "",
    var price : String = "",
    var finalPrice : String = "",
    var image : String = "",
    var date : Long = System.currentTimeMillis() ,
    var createBy : String = "",
    var category : String = "",
    var availableUnits : Int = 0,
    var productId : String = ""

)
