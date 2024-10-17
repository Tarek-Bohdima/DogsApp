package com.example.android.dogsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.android.dogsapp.DogsApplication
import com.example.android.dogsapp.R
import com.example.android.dogsapp.common.di.activity.ActivityComponent
import com.example.android.dogsapp.common.di.activity.DaggerActivityComponent
import com.example.android.dogsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = DaggerActivityComponent.builder()
            .applicationComponent((application as DogsApplication).appComponent)
            .build()

        activityComponent.inject(this)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    fun getActivityComponent(): ActivityComponent {
        return activityComponent
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, null)
    }
}
