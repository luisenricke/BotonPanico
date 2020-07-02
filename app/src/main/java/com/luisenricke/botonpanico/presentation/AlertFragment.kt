package com.luisenricke.botonpanico.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luisenricke.botonpanico.databinding.FragmentAlertBinding

class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)

        binding.apply {

        }

        return binding.root
    }


}
