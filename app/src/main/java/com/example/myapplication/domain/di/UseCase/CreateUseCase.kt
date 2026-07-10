package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.UserData
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUseCase @Inject constructor(private val repo : Repo){

    fun createUser(userData : UserData): Flow<ResultState<String>>{
        return repo.registerUserwithEmailAndPassword(userData)
    }
}