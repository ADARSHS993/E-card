package com.example.myapplication.presentation.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.presentation.Navigation.Routes
import com.example.myapplication.presentation.ViewModel.ShoppingAppViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCategoriesScreen(
    navController: NavController,
    viewModel: ShoppingAppViewModel = hiltViewModel(),
) {

    val state = viewModel.getAllCategoriesState.collectAsStateWithLifecycle()
    val categories = state.value.UserData ?: emptyList()
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(key1 = Unit) {

        viewModel.getAllCategories()
    }



    Scaffold(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {

            TopAppBar(
                title = { Text("All Categories") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        }
    ) { innerpadding ->

        when {
            state.value.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.value.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.value.errorMessage}")
                }
            }

            categories.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Categories Found")
                }
            }

            else -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = innerpadding,
                    modifier = Modifier.fillMaxSize()

                ) {

                    items(categories) { category ->

                        CategoryCard(category = category!! , onCategoryclick = {
                            navController.navigate(Routes.EachCategoryItemsScreen(category.name))
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryDataModel, onCategoryclick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{onCategoryclick()}
    ) {

        Column {
            AsyncImage(
                model = category.categoryImage,
                contentDescription = category.name,
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = category.name,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
