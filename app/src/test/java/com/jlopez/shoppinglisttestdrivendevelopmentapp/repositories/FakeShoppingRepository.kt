package com.jlopez.shoppinglisttestdrivendevelopmentapp.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Resource
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local.ShoppingItem
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.remote.responses.ImageResponse

// We want to make our test independent and fast.
// In this case we will simulate a database rather than using an actual database
// We can then also set a boolean representing if we want the API to give us an error
// The purpose of this Repository is to test our view model later on

class FakeShoppingRepository: ShoppingRepository {

    private val shoppingItems = mutableListOf<ShoppingItem>()

    private val observableShoppingItems = MutableLiveData<List<ShoppingItem>>(shoppingItems)
    private val observableTotalPrice = MutableLiveData<Float>()

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshLiveData() {
        // We are using postValue because the change in ShoppingItems is done asynchronously
        // setValue is done in the main thread, but post value is done in another thread
        observableShoppingItems.postValue(shoppingItems)
        observableTotalPrice.postValue(getTotalPrice())
    }

    private fun getTotalPrice(): Float {
        return shoppingItems.sumOf{ it.price.toDouble() }.toFloat()
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        refreshLiveData()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
        refreshLiveData()
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return observableShoppingItems
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return observableTotalPrice
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return if(shouldReturnNetworkError) {
            Resource.error("Error ", null)
        }
        else {
            Resource.success(ImageResponse(listOf(), 0, 0))
        }
    }
}