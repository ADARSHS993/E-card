package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCheckOutUSeCase @Inject constructor(private val repo : Repo) {

    fun getCheckOut(productID : String): Flow<ResultState<ProductDataModel>> {
        return repo.getCheckout(productID)
    }
}