package com.jlopez.shoppinglisttestdrivendevelopmentapp.di

import android.content.Context
import androidx.room.Room
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local.ShoppingItemDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    // No singleton because we want to use different instances of dependencies
    // every test case
    @Provides
    @Named("Test_DB")
    fun provideInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, ShoppingItemDatabase::class.java)
            .allowMainThreadQueries()
            .build()



}