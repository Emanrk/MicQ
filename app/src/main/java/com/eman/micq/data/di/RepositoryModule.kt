package com.eman.micq.data.di

import com.eman.micq.data.repository.AuthRepository
import com.eman.micq.data.repository.AuthRepositoryImpl
import com.eman.micq.data.repository.QueueRepository
import com.eman.micq.data.repository.QueueRepositoryImpl
import com.eman.micq.data.repository.ShiftRepository
import com.eman.micq.data.repository.ShiftRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindQueueRepository(
        queueRepositoryImpl: QueueRepositoryImpl
    ): QueueRepository

    @Binds
    @Singleton
    abstract fun bindShiftRepository(
        shiftRepositoryImpl: ShiftRepositoryImpl
    ): ShiftRepository
}
