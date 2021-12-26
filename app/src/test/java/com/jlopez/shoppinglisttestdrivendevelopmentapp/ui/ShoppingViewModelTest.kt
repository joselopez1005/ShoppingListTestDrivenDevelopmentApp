package com.jlopez.shoppinglisttestdrivendevelopmentapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Constants
import com.jlopez.shoppinglisttestdrivendevelopmentapp.MainCoroutineRule
import com.jlopez.shoppinglisttestdrivendevelopmentapp.Status
import com.jlopez.shoppinglisttestdrivendevelopmentapp.getOrAwaitValueTest
import com.jlopez.shoppinglisttestdrivendevelopmentapp.repositories.FakeShoppingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShoppingViewModelTest {

    private lateinit var viewModel: ShoppingViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()



    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun  `insert shopping item with empty field returns error`() {
        viewModel.insertShoppingItem("name", "", "1.25")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        // So here we are basically checking that an empty field returns an error
        // What we do here is we get the content if its our first time accessing it
        // meaning that we get a resource type, specifically its status and making
        // sure its an error
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun  `insert shopping item with too long name field, returns error`() {
        val string = buildString {
            for(i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem(string, "5", "1.25")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun  `insert shopping item with too long price field, returns error`() {
        val string = buildString {
            for(i in 1..Constants.MAX_PRICE_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem("name", "5", string)

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun  `insert shopping item with too high amount field, returns error`() {
        viewModel.insertShoppingItem("name", "99999999999999", "1.25")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun  `insert valid shopping item, returns true`() {

        viewModel.insertShoppingItem("name", "5", "1.25")

        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}