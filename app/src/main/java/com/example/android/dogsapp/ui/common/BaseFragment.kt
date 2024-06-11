package com.example.android.dogsapp.ui.common

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {
    protected val compositionRoot get() = (requireActivity() as BaseActivity).compositionRoot
}
