package com.example.myapplication.presentation.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.presentation.Navigation.Routes
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel

@Composable
fun GetAllFav(navController : NavController, viewModel: ShoppingAppViewModel = hiltViewModel()){

    val getAllfavState = viewModel.getAllFavState.collectAsStateWithLifecycle()
    val favdata = getAllfavState.value.UserData ?: emptyList()

    LaunchedEffect(key1 = Unit)
    {
        viewModel.getAllFav()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("WishList" ,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold)}
            )
        }
    )
    {innerpadding->

        Column( modifier = Modifier.fillMaxSize()
            .padding(innerpadding))
        {

            OutlinedTextField(
                value = "",
                onValueChange = {/* search functionality */},
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                placeholder = {Text(text = "Search")},
                leadingIcon = {Icon(Icons.Default.Search , contentDescription = null)}
            )

            when{

                getAllfavState.value.isLoading ->{
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }

                getAllfavState.value.errorMessage != null->{
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center){
                       Text("Error : ${getAllfavState.value.errorMessage}")
                    }
                }

                favdata.isEmpty()->{
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center){
                        Text("Your Wishlist is empty")
                    }
                }

                else ->{
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        items(favdata){product->
                             ProductCard(product, onProductClick = {
                                 navController.navigate(Routes.EachProductDetailsScreen(product.productId))
                             })
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun ProductCard(product : ProductDataModel, onProductClick :() -> Unit){

    Card(
        onClick = {onProductClick},
        modifier = Modifier.fillMaxWidth()
    ){
        Column{
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp))
            {
                Text(text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold)

                Text(text = product.price,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}