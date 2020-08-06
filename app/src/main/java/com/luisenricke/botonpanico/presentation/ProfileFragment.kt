package com.luisenricke.botonpanico.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luisenricke.androidext.loadImageInternalStorage
import com.luisenricke.androidext.saveImageInternalStorage
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.databinding.FragmentProfileBinding
import timber.log.Timber

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var imageStorage: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                val isSaved = binding.root.context.saveImageInternalStorage(imageStorage, Constraint.PROFILE_PHOTO)
                Timber.i("Photo saved: $isSaved")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
