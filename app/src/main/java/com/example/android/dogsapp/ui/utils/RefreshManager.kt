package com.example.android.dogsapp.ui.utils

interface RefreshManager {
    fun isRefreshing(): Boolean
    fun setRefreshing(refreshing: Boolean)
    fun setOnRefreshListener(listener: () -> Unit)
    fun onRefreshTriggered()
}
