package com.jlopez.shoppinglisttestdrivendevelopmentapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.jlopez.shoppinglisttestdrivendevelopmentapp.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named


//APPLYING DAGGER-HILT TESTING:
// When we have a big application with several tests, rather than initiating a database
// or a dao several times over and over, it is better to let dagger-hilt do it
//BIG NOTE:
// When working with LiveData, issues come up with Junit and livedata's condition of
// being asynchronous. To fix that, we have to tell JUnit that we want all of this
// to be run in the main thread, basically no asynchronous functions allowed

// RunWith - Makes sure all the tests in the class will run in the emulator (instrumented)
// SmallTest - Telling Junit that we are making unit tests
//@RunWith(AndroidJUnit4::class)
@SmallTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class ShoppingDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // This takes care of the problem. It will make sure every function is run in order
    // asynchronously or not
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("Test_DB")
    lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        // inMemory means that it won't change the actual values on the real database
        // Another note is that we want to allow MainThreadQueries for testing purposes
        // Since if several threads are executing, we won't know which thread is causing
        // The issue
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            ShoppingItemDatabase::class.java
//        ).allowMainThreadQueries().build()
        hiltRule.inject()
        dao = database.shoppingDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    // Run Blocking will allow suspend functions to be called in the main thread
    @Test
    fun insertShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)

        // Cool extension function that basically converts our livedata to a list
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems.contains(shoppingItem))
    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)

        dao.deleteShoppingItem(shoppingItem)
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(allShoppingItems.contains(shoppingItem)).isFalse()
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {
        val shoppingItem1 = ShoppingItem("name1", 2, 5f, "url", id = 1)
        val shoppingItem2 = ShoppingItem("name2", 3, 12f, "url", id = 2)
        val shoppingItem3 = ShoppingItem("name3", 6, 2f, "url", id = 3)

        dao.insertShoppingItem(shoppingItem1)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        val totalPriceSum = dao.observeTotalPrice().getOrAwaitValue()
        assertThat(totalPriceSum).isEqualTo(2*5f + 3 * 12f + 6*2f)
    }
}