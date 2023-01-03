package ru.chistov.converterjpegtopng

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.chistov.converterjpegtopng.databinding.ActivityMainBinding


class MainActivity : MvpAppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding
    private val presenter: MainPresenter by moxyPresenter {
        MainPresenter(contentResolver,
            PhotoRepositoryImpl(activityResultRegistry){imageUri ->

                if (imageUri != null) {
                    presenter.loadPath(imageUri)?.let { showImage(imageUri, it) }
                }

            }
        )
    }

    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imagePicked.setOnClickListener {
            binding.imagePicked.background = null
            //pickImage()
            presenter.loadImage()
        }


        snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)

            .setAction(R.string.cancel) {
                presenter.disposeConvert()
                isClickable(true)
            }
    }
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg"))
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CODE_GET_CONTENT
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_CODE_GET_CONTENT &&
            data != null
        ) {
            val imagePickedUri = data.data
            if (imagePickedUri != null) {
                val path: String? =presenter.loadPath(imagePickedUri)
                if (path != null) {
                   showImage(imagePickedUri,path)
                    binding.buttonConvert.setOnClickListener {
                        isClickable(false)
                        presenter.convertImage((binding.imagePicked.drawable as BitmapDrawable).bitmap,
                            path)
                    }
                }

            }
        }
    }


    override fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(),
                MY_READ_PERMISSION_CODE
            )
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSnackBar(path: String) {
        snackbar.setText("${resources.getString(R.string.converting)}\n$path")
        snackbar.show()
    }

    override fun hideSnackBar() {
        snackbar.dismiss()
        isClickable(true)

    }

    override fun isClickable(boolean: Boolean) {
        binding.root.isClickable = boolean
    }

    override fun showImage(image: Uri,path: String){
        binding.imagePicked.background = null
        binding.imagePicked.load(image)
        binding.textPathImagePicked.text = path

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешен доступ к галерее", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Нет доступа к галерее", Toast.LENGTH_SHORT).show()
            }
        }
    }

}