package com.luisenricke.botonpanico.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luisenricke.androidext.intentSelectImageFromGallery
import com.luisenricke.androidext.loadImageInternalStorage
import com.luisenricke.androidext.saveImageInternalStorage
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.databinding.FragmentProfileBinding
import timber.log.Timber

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var imageStorage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.apply {

            imageStorage = root.context.loadImageInternalStorage(Constraint.PROFILE_PHOTO)
            if (imageStorage != null) imgProfile.setImageBitmap(imageStorage)

            imgProfile.setOnClickListener {
                imageOptionsWipe(root.context, imgProfile, imageStorage, Constraint.PROFILE_PHOTO)
            }

        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            Constraint.INTENT_IMAGE_FROM_GALLERY -> {
                imageStorage = getImage(binding.root.context, data) ?: return
                binding.imgProfile.setImageBitmap(imageStorage)
                val isSaved = binding.root.context
                    .saveImageInternalStorage(imageStorage, Constraint.PROFILE_PHOTO)
                Timber.i("Photo saved: $isSaved")
            }
        }
    }

    // TODO test this
    fun getImage(context: Context, data: Intent?): Bitmap? {
        val contentURI: Uri = data?.data!!
        var bitmap: Bitmap? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, contentURI)
            bitmap = ImageDecoder.decodeBitmap(source)

        } else {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentURI)
        }

        return bitmap!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
