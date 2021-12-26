package com.jlopez.shoppinglisttestdrivendevelopmentapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// The purpose of this class is to handle the error we get on our test.
// The error happens in the viewmodel test due to the fact that when testing
// our view model, we have to call a suspend function, which is getting
// the shopping item into our database. Since this is a suspend function,
// we need a dispatcher, but we don't have that in the traditional test
// environment.

@ExperimentalCoroutinesApi
class MainCoroutineRule (
    private val dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher){
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}