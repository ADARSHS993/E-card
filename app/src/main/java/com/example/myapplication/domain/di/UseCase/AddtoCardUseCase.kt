package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CartDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddtoCardUseCase @Inject constructor(private val repo : Repo){

    fun addtoCard(cartDataModels : CartDataModel): Flow<ResultState<String>> {
        return repo.addToCart(cartDataModels)
    }
}