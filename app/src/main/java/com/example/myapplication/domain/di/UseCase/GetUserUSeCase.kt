package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.USerDataParent
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUSeCase @Inject constructor(private val repo : Repo) {

    fun getUSerById(userId : String): Flow<ResultState<USerDataParent>> {
        return repo.getUserById(userId)
    }
}