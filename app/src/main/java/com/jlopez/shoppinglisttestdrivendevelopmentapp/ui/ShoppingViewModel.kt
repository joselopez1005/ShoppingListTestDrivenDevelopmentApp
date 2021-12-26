package com.jlopez.shoppinglisttestdrivendevelopmentapp.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Constants
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Event
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Resource
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local.ShoppingItem
import com.jlopez.shoppinglisttestdrivendevelopmentapp.data.remote.responses.ImageResponse
import com.jlopez.shoppinglisttestdrivendevelopmentapp.repositories.DefaultShoppingRepository
import com.jlopez.shoppinglisttestdrivendevelopmentapp.repositories.ShoppingRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    // The reason we are passing in the interface for the repository is
    // so we can pass in either the test repository or the real repository
    // to this view model
    private val repository: ShoppingRepository
) : ViewModel() {

    val shoppingItems = repository.observeAllShoppingItems()

    val totalPrice = repository.observeTotalPrice()


    // The reason we are using the Event class is because whenever the screen is rotated
    // we don't want the live data to be re emmited. So what the Event class will do is that
    // as soon as the data is emitted a flag will be set and thus not repeat the same process
    private val _images = MutableLiveData<Event<Resource<ImageResponse>>>()
    val images: LiveData<Event<Resource<ImageResponse>>> = _images

    private val _curImageUrl = MutableLiveData<String>()
    val curImageUrl: LiveData<String> = _curImageUrl

    // This pair is responsible for making sure the added Shopping Item is actually added in
    // correctly
    private val _insertShoppingItemStatus = MutableLiveData<Event<Resource<ShoppingItem>>>()
    val insertShoppingItemStatus: LiveData<Event<Resource<ShoppingItem>>> = _insertShoppingItemStatus

    /*
     * For the next three functions, we do not do unit testing because we have already done so
     * in the repository testing.
     */
    fun setCurImageUrl(url: String) {
        _curImageUrl.postValue(url)
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun insertShoppingItem(name: String, amountString: String, priceString: String) {
        if(name.isEmpty() || amountString.isEmpty() || priceString.isEmpty()) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("The fields must not be empty", null)))
            return
        }
        if(name.length > Constants.MAX_NAME_LENGTH) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Name cannot be that long", null)))
            return
        }
        if(priceString.length > Constants.MAX_PRICE_LENGTH) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Price cannot be that long", null)))
            return
        }
        val amount = try {
            amountString.toInt()
        } catch(e: Exception) {
            _insertShoppingItemStatus.postValue(Event(Resource.error("Please Enter a valid amount", null)))
            return
        }
        val shoppingItem = ShoppingItem(name, amount, priceString.toFloat(), _curImageUrl.value ?: "")
        insertShoppingItemIntoDb(shoppingItem)
        setCurImageUrl("")
        _insertShoppingItemStatus.postValue(Event(Resource.success(shoppingItem)))
    }

    fun searchFOrImage(imageQuery: String) {
        if(imageQuery.isEmpty()) {
            return
        }
        // Value will notify all observer instantly of a change in the data
        // PostValue will only notify all observers about the last change, good if the data is constanly changing
        _images.value = Event(Resource.loading(null))
        viewModelScope.launch {
            val response = repository.searchForImage(imageQuery)
            _images.value = Event(response)
        }
    }
}