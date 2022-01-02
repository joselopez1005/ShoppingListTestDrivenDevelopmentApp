package com.jlopez.shoppinglisttestdrivendevelopmentapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {

    // In here classname refers to the name found in the manifest
    // and what is annotated with android hilt app
    // We don't want that since that environment is not suitable for us.
    // Rather we change it to HiltTestApplication
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}