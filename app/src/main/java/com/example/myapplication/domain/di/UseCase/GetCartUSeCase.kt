package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUSeCase @Inject constructor(private val repo : Repo){

    fun getCart() : Flow<ResultState<List<CartDataModel>>> {
        return repo.getCart()
    }
}