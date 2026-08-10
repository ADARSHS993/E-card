package com.example.myapplication.presentation.Navigation

import kotlinx.serialization.Serializable

sealed class SubNavigation{

   @Serializable
   object LoginSignUpScreen : SubNavigation()

    @Serializable
    object MainHomeScreen : SubNavigation()
}

sealed class Routes{

    @Serializable
    object LoginScreen

    @Serializable
    object SignUpScreen

    @Serializable
    object HomeScreen

    @Serializable
    object ProfileScreen

    @Serializable
    object WishListScreen

    @Serializable
    object CartScreen

    @Serializable
    data class CheckoutScreen(val ProductId : String)

    @Serializable
    object SeeAllProductScreen

    @Serializable
    object PayScreen

    @Serializable
    data class  EachProductDetailsScreen(val productId : String)

    @Serializable
    object AllCategoryScreen

    @Serializable
    data class EachCategoryItemsScreen(val categoryname : String)

    @Serializable
    object AdminLoginScreen

    @Serializable
    object AdminDashboardScreen

    @Serializable
    object AdminCategoriesScreen

    @Serializable
    object AdminProductsScreen

    @Serializable
    object AdminOrdersScreen

    @Serializable
    object AdminUsersScreen

    @Serializable
    object MyOrdersScreen
}