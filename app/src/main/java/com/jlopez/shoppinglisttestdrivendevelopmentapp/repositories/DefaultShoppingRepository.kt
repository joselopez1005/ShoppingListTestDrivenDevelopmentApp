package com.jlopez.shoppinglisttestdrivendevelopmentapp.repositories

import androidx.lifecycle.LiveData
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Resource
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local.ShoppingDao
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local.ShoppingItem
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.remote.PixabayAPI
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.remote.responses.ImageResponse
import java.lang.Exception
import javax.inject.Inject

class DefaultShoppingRepository @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val pixabayAPI: PixabayAPI
) : ShoppingRepository{
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return shoppingDao.observeAllShoppingItems()
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return shoppingDao.observeTotalPrice()
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return try{
            val response = pixabayAPI.searchForImage(imageQuery)
            if(response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Unkown Error Occured", null)
            }
            else {
                Resource.error("Unkown error occured", null)
            }
        } catch (e: Exception) {
            Resource.error("Network Error", null)
        }
    }
}