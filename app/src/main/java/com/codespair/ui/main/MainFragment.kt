package com.codespair.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.codespair.R
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val REQUEST_PHOTO = 777
private const val UCROP_REQUEST = 778

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var cameraButton: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        cameraButton = view.findViewById(R.id.cameraButton)
        imageView = view.findViewById(R.id.imageView)
        return view
    }

    override fun onStart() {
        super.onStart()
        cameraButton.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = randomFile()
        photoUri = FileProvider.getUriForFile(requireContext(), "com.codespair", photoFile)
        Timber.i("photoUri before opening camera: ${photoUri.path}")
        captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(captureImageIntent, REQUEST_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_PHOTO -> {
                if(resultCode == RESULT_OK) {
                    Timber.i("photoUri ${photoUri.path}")
                    startUCropActivity(photoUri, photoUri)
                } else {
                    Timber.e("Something went wrong when taking picture with camera, resultCode: $resultCode")
                }
            }
            UCROP_REQUEST -> {
                updateImageView()
            }
        }
    }

    private fun startUCropActivity(sourceUri: Uri, destinationUri: Uri) {
        val options = UCrop.Options()
        options.setToolbarTitle("Crop your image")
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(5f, 5f)
            .start(requireContext(), this, UCROP_REQUEST)
    }

    private fun updateImageView() {
        if (photoFile.exists()) {
            Glide.with(this)
                .load(photoUri)
                .into(imageView)
        }
    }

    private fun randomFile() : File {
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val now = LocalDate.now()
        return File(context?.applicationContext?.filesDir, "pic-${UUID.randomUUID()}-${now.format(formatter)}.jpg")
    }

}