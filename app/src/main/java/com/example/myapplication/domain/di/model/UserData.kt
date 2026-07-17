package com.example.myapplication.domain.di.model

import androidx.compose.runtime.mutableStateMapOf

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val profileImage: String = "",
    val confirmPassword: String,
){
    fun toMap() : Map<String, Any>{
        val map = mutableStateMapOf<String,Any>()
        map["firstName"] = firstName
        map["lastName"] = lastName
        map["email"] = email
        map["password"] = password
        map["phoneNumber"] = phoneNumber
        map["address"] = address
        map["profileImage"] = profileImage
        return map
    }
}

data class USerDataParent(val nodeID : String = "",
                          val userData : UserData = UserData(
                              firstName = "",
                              lastName = "",
                              email = "",
                              password = "",
                              phoneNumber = "",
                              address = "",
                              profileImage = "",
                              confirmPassword = ""
                          )
)
