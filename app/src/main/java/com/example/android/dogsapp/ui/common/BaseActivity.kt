package com.example.android.dogsapp.ui.common

import androidx.appcompat.app.AppCompatActivity
import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.common.ActivityCompositionRoot

open class BaseActivity : AppCompatActivity() {
    private val appCompositionRoot get() = (application as DogsApplication).appCompositionRoot

    val compositionRoot by lazy {
        ActivityCompositionRoot( appCompositionRoot)
    }
}
