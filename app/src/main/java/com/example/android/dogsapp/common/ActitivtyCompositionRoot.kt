package com.example.android.dogsapp.common


class ActivityCompositionRoot(
    private val appCompositionRoot: AppCompositionRoot
) {
    private val dogsApi get() = appCompositionRoot.dogsApi

//    val fetchDogsUseCase get() = FetchDogsUseCase(dogsApi)
}