package com.eman.micq.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * [FirebaseModule] is a Hilt module that provides Firebase-related dependencies.
 *
 * @Module: Tells Hilt that this class is a source of dependency bindings.
 */
@Module
/**
 * @InstallIn: Specifies that the bindings provided here should be available
 * globally in the [SingletonComponent]. This means the provided instances
 * will persist throughout the application's lifecycle.
 */
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * @Provides: Tells Hilt how to construct and provide an instance of [FirebaseAuth].
     * @Singleton: Ensures that only one instance of [FirebaseAuth] is created and shared
     * across the entire application.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * @Provides: Tells Hilt how to provide the [FirebaseDatabase] instance.
     * @Singleton: Ensures a single instance of the Realtime Database is used.
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()
}
