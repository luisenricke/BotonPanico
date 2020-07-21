package com.luisenricke.botonpanico.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luisenricke.androidext.preferenceGet
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.databinding.FragmentHomeBinding
import com.luisenricke.botonpanico.service.SensorForeground
import timber.log.Timber

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val checkPhone = this.preferenceGet("phone", String::class)
        Timber.i("phone: $checkPhone")

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {

            btnStart.setOnClickListener { checkAlertPermissions() }

            btnStop.setOnClickListener { SensorForeground.stopService(root.context) }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
