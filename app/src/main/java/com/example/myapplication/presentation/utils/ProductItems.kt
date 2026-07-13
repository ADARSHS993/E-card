package com.example.myapplication.presentation.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.domain.di.model.ProductDataModel

@Composable
fun ProductItems(
    product : ProductDataModel,
    onProductClick : () -> Unit
){

    Card(modifier = Modifier.fillMaxWidth()
        .clickable(
            onClick = onProductClick
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ){

        Column {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Fit)

            Column (modifier = Modifier.padding(8.dp)){

                Text(text = product.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body1)

                Text(text = "$${product.finalPrice}"
                , style = MaterialTheme.typography.body2
                , color = MaterialTheme.colors.primary)
            }
        }
    }
}