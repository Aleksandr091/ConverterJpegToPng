package ru.chistov.converterjpegtopng

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.rxjava3.core.Single

interface PhotoRepository {
    fun getPathFromUri(contentUri: Uri, contentResolver: ContentResolver): String?
    fun convertJpgToPng(bitmap: Bitmap, pathToBitmap: String): Single<Pair<String, Bitmap>>
}