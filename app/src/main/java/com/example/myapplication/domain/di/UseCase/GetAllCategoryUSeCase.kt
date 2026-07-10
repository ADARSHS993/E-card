package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.CategoryDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoryUSeCase @Inject constructor(private val repo : Repo) {

    fun getAllCategory(): Flow<ResultState<List<CategoryDataModel>>> {
        return repo.getAllCategories()
    }
}