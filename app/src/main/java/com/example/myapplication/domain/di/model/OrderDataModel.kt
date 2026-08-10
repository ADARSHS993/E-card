package com.example.myapplication.domain.di.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderDataModel(
    var orderId: String = "",
    var userId: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var city: String = "",
    var postalCode: String = "",
    var country: String = "",
    var selectedDeliveryMethod: String = "",
    var items: List<CartDataModel> = emptyList(),
    var totalAmount: Double = 0.0,
    var date: Long = System.currentTimeMillis(),
    var paymentStatus: String = "Pending", // "Pending", "Success", "Failed"
    var orderStatus: String = "Pending" // "Pending", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled"
)
