package com.jlopez.shoppinglisttestdrivendevelopmentapp.repositories

import androidx.lifecycle.LiveData
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Resource
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local.ShoppingItem
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.remote.responses.ImageResponse
import retrofit2.Response

interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String) : Resource<ImageResponse>
}