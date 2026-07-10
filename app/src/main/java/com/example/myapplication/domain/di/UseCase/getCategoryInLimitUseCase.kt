package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class getCategoryInLimitUseCase @Inject constructor(private val repo : Repo) {

    fun getCategoryInLimited(): Flow<ResultState<List<CategoryDataModel>>> {
        return repo.getCategoriesInLimited()
    }
}