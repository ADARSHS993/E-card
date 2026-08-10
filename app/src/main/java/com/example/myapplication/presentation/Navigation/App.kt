package com.example.myapplication.presentation.Navigation

import ProfileScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.material.CircularProgressIndicator
import com.example.myapplication.R

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.myapplication.domain.di.model.ProductDataModel

import com.google.firebase.auth.FirebaseAuth

import com.example.myapplication.presentation.LoginScreen
import com.example.myapplication.presentation.SignUpScreen
import com.example.myapplication.presentation.Screens.AllCategoriesScreen
import com.example.myapplication.presentation.Screens.CartScreen
import com.example.myapplication.presentation.Screens.CheckOutScreen
import com.example.myapplication.presentation.Screens.EachCategorieProductScreenUi
import com.example.myapplication.presentation.Screens.EachProductDetailScreens
import com.example.myapplication.presentation.Screens.GetAllFav
import com.example.myapplication.presentation.Screens.HomeScreenUi


data class BottomNavigationItem(
    val name: String ,
    val Icon : ImageVector,
    val unselectedIcon : ImageVector
)

@Composable
fun App(
    firebasAuth: FirebaseAuth,
    payTest: () -> Unit,
    startDestination: Any
) {
    val navController = rememberNavController()// The NavController is the "GPS" that moves you between rooms.

    var selectedItem by remember {
        mutableStateOf(0)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()// Tracks where you are right now
    val currentDestination = navBackStackEntry?.destination?.route // The name of the current screen

    val shouldShowBottomBar = remember { mutableStateOf(false) }

    LaunchedEffect(currentDestination) {
        shouldShowBottomBar.value = when (currentDestination) {

            Routes.LoginScreen::class.qualifiedName,
            Routes.SignUpScreen::class.qualifiedName,
            Routes.AdminLoginScreen::class.qualifiedName,
            Routes.AdminDashboardScreen::class.qualifiedName,
            Routes.AdminCategoriesScreen::class.qualifiedName,
            Routes.AdminProductsScreen::class.qualifiedName,
            Routes.AdminOrdersScreen::class.qualifiedName,
            Routes.AdminUsersScreen::class.qualifiedName,
            Routes.MyOrdersScreen::class.qualifiedName
                -> false

            else -> true
        }
    }


    val BottomNavItem = listOf(

        BottomNavigationItem("Home", Icons.Default.Home, unselectedIcon = Icons.Outlined.Home),
        BottomNavigationItem(
            "WishList",
            Icons.Default.Favorite,
            unselectedIcon = Icons.Outlined.Favorite
        ),
        BottomNavigationItem(
            "Cart",
            Icons.Default.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart
        ),
        BottomNavigationItem(
            "Profile",
            Icons.Default.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    val starScreen = startDestination

    Scaffold(
            bottomBar = {

                if (shouldShowBottomBar.value) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        )
                ) {

                    BottomNavigation {

                        BottomNavItem.forEachIndexed { index, item ->

                            BottomNavigationItem(
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index

                                    when (index) {
                                        0 -> navController.navigate(Routes.HomeScreen::class.qualifiedName!!)
                                        1 -> navController.navigate(Routes.WishListScreen::class.qualifiedName!!)
                                        2 -> navController.navigate(Routes.CartScreen::class.qualifiedName!!)
                                        3 -> navController.navigate(Routes.ProfileScreen::class.qualifiedName!!)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedItem == index)
                                            item.Icon
                                        else
                                            item.unselectedIcon,
                                        contentDescription = item.name
                                    )
                                },
                                label = {
                                    Text(item.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            NavHost(
                navController = navController,
                startDestination = starScreen
            ) {

                navigation<SubNavigation.LoginSignUpScreen>(
                    startDestination = Routes.LoginScreen
                ) {

                    composable<Routes.LoginScreen> {
                        LoginScreen(navController = navController)
                    }

                    composable<Routes.SignUpScreen> {
                        SignUpScreen(navController = navController)
                    }
                }

                navigation<SubNavigation.MainHomeScreen>(
                    startDestination = Routes.HomeScreen
                ) {

                    composable<Routes.HomeScreen> {
                        HomeScreenUi(navController = navController)
                    }

                    composable<Routes.WishListScreen> {
                        GetAllFav(navController = navController)
                    }

                    composable<Routes.CartScreen> {
                        CartScreen(navController = navController)
                    }

                    composable<Routes.ProfileScreen> {
                        ProfileScreen(
                            navController = navController,
                            firebaseAuth = firebasAuth
                        )
                    }

                    composable<Routes.SeeAllProductScreen> {
                        GetAllFav(navController = navController)
                    }

                    composable<Routes.AllCategoryScreen> {
                        AllCategoriesScreen(navController = navController)
                    }

                    composable<Routes.EachCategoryItemsScreen> {

                        val category: Routes.EachCategoryItemsScreen = it.toRoute()
                        EachCategorieProductScreenUi(
                            navController = navController,
                            categoryName = category.categoryname
                        )
                    }
                }

                composable<Routes.EachProductDetailsScreen> {

                    val product: Routes.EachProductDetailsScreen = it.toRoute()

                    EachProductDetailScreens(
                        navController = navController,
                        productId = product.productId
                    )
                }

                composable<Routes.CheckoutScreen> {

                    val product: Routes.CheckoutScreen = it.toRoute()

                    CheckOutScreen(
                        navController = navController,
                        productId = product.ProductId,
                        pay = payTest
                    )
                }

                composable<Routes.MyOrdersScreen> {
                    com.example.myapplication.presentation.Screens.MyOrdersScreenUi(navController = navController)
                }

                // Admin Screens
                composable<Routes.AdminLoginScreen> {
                    com.example.myapplication.presentation.Screens.admin.AdminLoginScreen(navController = navController)
                }

                composable<Routes.AdminDashboardScreen> {
                    com.example.myapplication.presentation.Screens.admin.AdminDashboardScreen(navController = navController)
                }

                composable<Routes.AdminCategoriesScreen> {
                    com.example.myapplication.presentation.Screens.admin.AdminCategoriesScreen(navController = navController)
                }

                composable<Routes.AdminProductsScreen> {
                    com.example.myapplication.presentation.Screens.admin.AdminProductsScreen(navController = navController)
                }

                composable<Routes.AdminOrdersScreen> {
                    com.example.myapplication.presentation.Screens.admin.AdminOrdersScreen(navController = navController)
                }

                composable<Routes.AdminUsersScreen> {
                    com.example.myapplication.presentation.Screens.admin.AdminUsersScreen(navController = navController)
                }
            }
        }
    }
}