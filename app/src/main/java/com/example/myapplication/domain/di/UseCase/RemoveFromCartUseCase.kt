package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(private val repo: Repo){
    fun removeFromCart(cartId: String): Flow<ResultState<String>> {
        return repo.removeFromCart(cartId)
    }
}