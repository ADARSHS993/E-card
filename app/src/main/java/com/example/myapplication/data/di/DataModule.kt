package com.example.myapplication.data.di

import com.example.myapplication.data.di.repo.RepoImpl
import com.example.myapplication.domain.di.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // 1. THIS FIXES THE ERROR: Binds the Interface to the Implementation
    @Binds
    @Singleton
    abstract fun bindRepo(
        repoImpl: RepoImpl
    ): Repo

    // 2. These remain as Provides because they come from an external library (Firebase)
    companion object {
        @Singleton
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        @Singleton
        @Provides
        fun provideFirebaseFirestore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
    }
}