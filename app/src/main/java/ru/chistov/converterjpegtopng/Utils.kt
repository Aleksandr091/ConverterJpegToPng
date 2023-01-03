package ru.chistov.converterjpegtopng

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
const val MY_READ_PERMISSION_CODE = 564
const val REGISTRY_KEY = "imagePicked"
const val REQUEST_CODE_GET_CONTENT = 123

fun <T : Any> Single<T>.subscribeByDefault(): Single<T> {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun Disposable.disposeBy(bag: CompositeDisposable) {
    bag.add(this)
}

fun ImageView.load(url: Uri) {
    Glide.with(context)
        .load(url)
        .into(this)
}