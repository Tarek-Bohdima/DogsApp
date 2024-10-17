package com.example.android.dogsapp.ui.utils

import javax.inject.Inject

class SwipeToRefreshManagerImpl @Inject constructor() : RefreshManager {

    private var isRefreshing = false
    private var refreshListener: (() -> Unit)? = null

    override fun isRefreshing() = isRefreshing

    override fun setRefreshing(refreshing: Boolean) {
        isRefreshing = refreshing
    }

    override fun setOnRefreshListener(listener: () -> Unit) {
        refreshListener = listener
    }

    override fun onRefreshTriggered() {
        if (!isRefreshing) {
            isRefreshing = true
            refreshListener?.invoke()
        }
    }
}
