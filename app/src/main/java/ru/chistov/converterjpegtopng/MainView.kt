package ru.chistov.converterjpegtopng

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun showImage(image: Uri, path: String)
    fun checkPermission()
    fun showToast(message: String)
    fun showSnackBar(path: String)
    fun hideSnackBar()
    fun isClickable(boolean: Boolean)
}