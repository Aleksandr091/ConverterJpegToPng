package ru.chistov.converterjpegtopng

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class GetImageContract : ActivityResultContract<Intent,Map<String,Uri>?>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        val input = Intent(Intent.ACTION_PICK)
        input.type = "image/*"
        input.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg"))
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Map<String, Uri>? {
        TODO("Not yet implemented")
    }

}