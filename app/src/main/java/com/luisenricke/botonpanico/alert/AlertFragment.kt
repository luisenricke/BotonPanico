package com.luisenricke.botonpanico.alert

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.database.entity.Alert
import com.luisenricke.botonpanico.databinding.FragmentAlertBinding
import timber.log.Timber

class AlertFragment : BaseFragment() {

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertAdapter: AlertAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)

        alertAdapter = setAlertAdapter(binding.root.context)

        binding.apply {
            recyclerAlerts.apply {
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 0) {
                            getActivityContext().setBottomNavigationViewVisibility(false)
                        } else if (dy < 0) {
                            getActivityContext().setBottomNavigationViewVisibility(true)
                        }
                    }
                })

                adapter = alertAdapter
            }

            return binding.root
        }

    }

    override fun onStart() {
        super.onStart()
        alertAdapter.updateList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAlertAdapter(context: Context): AlertAdapter {
        val clickListener: (Alert) -> Unit = { item ->
            Timber.i("clickListener")
            val action = AlertFragmentDirections.actionAlertToAlertDetail()
            action.idAlert = item.id
            navController.navigate(action)
        }

        val longClickListener: (Alert) -> Unit = { item ->
            Timber.i("longClickListener")
        }

        return AlertAdapter(context, clickListener, longClickListener)
    }
}
