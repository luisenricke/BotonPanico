package com.luisenricke.botonpanico.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.databinding.FragmentInfoBinding

class InfoFragment : BaseFragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        getActivityContext().setBottomNavigationViewVisibility(false)

        // Toolbar
        getActivityContext().setSupportActionBar(binding.toolbar)
        setupActionBar(getActivityContext().supportActionBar, getString(R.string.settings_info))

        binding.apply {
            lblVersion.text = Constraint.VERSION
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack()
                true
            }
            else              -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getActivityContext().setBottomNavigationViewVisibility(true)
        _binding = null
    }
}
