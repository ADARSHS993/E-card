package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.model.ProductDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPRoductInLimitsUSeCase @Inject constructor(private val repo : Repo) {

    fun getProductInLimits(): Flow<ResultState<List<ProductDataModel>>> {
        return repo.getProductInLimited()
    }
}