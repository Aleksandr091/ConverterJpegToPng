package ru.chistov.converterjpegtopng


import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import io.reactivex.rxjava3.core.Single
import java.io.FileOutputStream



class PhotoRepositoryImpl(private val activityResultRegistry: ActivityResultRegistry,
                          private val callback:(imageUri:Uri?)->Unit)
    : PhotoRepository {

    private val getContent: ActivityResultLauncher<String> =
        activityResultRegistry.register(REGISTRY_KEY,
            ActivityResultContracts.GetContent()){ imageUri:Uri?->
            if (imageUri != null) {
                callback(imageUri)
            }

        }
    override fun pickImage() {
        getContent.launch("image/*")
    }
    override fun getPathFromUri(contentUri: Uri, contentResolver: ContentResolver): String? {
        var res: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(columnIndex)

            cursor.close()
        }
        return res

    }

    override fun convertJpgToPng(bitmap: Bitmap, pathToBitmap: String): Single<Pair<String, Bitmap>> {
        val (pathImagePickedDir, nameImagePicked) = splitPathToBitmap(pathToBitmap)
        return Single.fromCallable {
            val pathImageOutput = "$pathImagePickedDir/$nameImagePicked.png"
            val imageOutputStream = FileOutputStream(pathImageOutput)

            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutputStream)) {
                return@fromCallable (pathImageOutput to BitmapFactory.decodeFile(pathImageOutput))
            } else {
                throw Exception("Conversion problem")
            }
        }
    }

    private fun splitPathToBitmap(pathToBitmap: String): Pair<String, String> {
        val pathImagePickedParts = pathToBitmap.split("/")
        val pathImagePickedDir = pathImagePickedParts
            .subList(1, pathImagePickedParts.size - 1)
            .joinToString(prefix = "/", separator = "/")
        val nameImagePicked = pathImagePickedParts[pathImagePickedParts.size - 1]
            .split(".")[0]

        return pathImagePickedDir to nameImagePicked
    }


}