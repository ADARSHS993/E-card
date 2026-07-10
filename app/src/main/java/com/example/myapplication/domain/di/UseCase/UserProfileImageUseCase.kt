package com.example.myapplication.domain.di.UseCase

import android.net.Uri
import com.example.myapplication.common.ResultState
import com.example.myapplication.domain.di.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileImageUseCase @Inject constructor(private val repo : Repo) {

    fun UserProfileImage (uri : Uri) : Flow<ResultState<String>>{
        return repo.userProfileImage(uri)
    }
}
