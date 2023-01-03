package ru.chistov.converterjpegtopng

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import io.reactivex.rxjava3.disposables.CompositeDisposable


import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

class MainPresenter(
    private val contentResolver: ContentResolver,
    private val repository: PhotoRepository
) : MvpPresenter<MainView>() {

    private val bag = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.checkPermission()
    }

    fun loadPath(uri: Uri) = repository.getPathFromUri(uri,contentResolver)


    fun convertImage(bitmap:Bitmap, path: String) {
        viewState.showSnackBar(path)
         repository.convertJpgToPng(bitmap, path)
             .delay(3, TimeUnit.SECONDS)
            .cache()
            .subscribeByDefault()
            .subscribe(
                {
                    viewState.showToast("${it.first} сконвертирован в png")
                    viewState.hideSnackBar()
                },
                {
                    Log.d("@@@", "${it.message}")
                    viewState.hideSnackBar()
                }
            ).disposeBy(bag)
    }
    fun disposeConvert() {
        bag.dispose()
    }

    override fun onDestroy() {
        bag.dispose()
        super.onDestroy()
    }
}