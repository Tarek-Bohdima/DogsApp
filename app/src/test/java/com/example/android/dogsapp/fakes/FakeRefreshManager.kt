package com.example.android.dogsapp.fakes

import com.example.android.dogsapp.ui.utils.RefreshManager

class FakeRefreshManager : RefreshManager {
    var refreshing: Boolean = false
        private set

    override fun isRefreshing(): Boolean = refreshing
    override fun setRefreshing(refreshing: Boolean) { this.refreshing = refreshing }
    override fun setOnRefreshListener(listener: () -> Unit) = Unit
    override fun onRefreshTriggered() = Unit
}
