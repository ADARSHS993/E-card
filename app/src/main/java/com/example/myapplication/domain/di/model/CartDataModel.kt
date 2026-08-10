package com.example.myapplication.domain.di.model

import kotlinx.serialization.Serializable

@Serializable
data class CartDataModel(

    var productId: String = "",
    var name: String = "",
    var image: String = "",
    var price: String = "",
    var quantity: Int = 0,
    var cartId: String = "",
    var size: String = "",
    var description: String = "",
    var category: String = "",
)

