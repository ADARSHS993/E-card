package com.example.myapplication.domain.di.UseCase

import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.model.BannerDataModel
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBannersUSeCase @Inject constructor(private val repo : Repo) {

    fun getAllBanners(): Flow<ResultState<List<BannerDataModel>>> {
        return repo.getBanner()
    }
}