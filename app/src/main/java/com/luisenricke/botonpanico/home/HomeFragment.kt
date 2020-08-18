package com.luisenricke.botonpanico.home

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luisenricke.androidext.PermissionMessage
import com.luisenricke.androidext.checkPermissions
import com.luisenricke.androidext.requestCriticalPermissions
import com.luisenricke.androidext.toastLong
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.databinding.FragmentHomeBinding
import com.luisenricke.botonpanico.service.LastLocation
import com.luisenricke.botonpanico.service.SensorForeground

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {

            btnStart.setOnClickListener {
                requestAlertService(binding.btnStop.context)

                //                val alertFacade = AlertFacade()
                //                alertFacade.alertTriggeredMainThreat(getActivityContext())
            }

            btnStop.setOnClickListener {
                SensorForeground.stopService(root.context)
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestAlertService(context: Context) {
        val permissions = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)

        val message = PermissionMessage(
                context = getActivityContext(),
                title = getString(R.string.permission_message_alert_title),
                message = getString(R.string.permission_message_alert_message),
                positiveButton = getString(R.string.permission_message_alert_positive_button),
                negativeButton = getString(R.string.permission_message_alert_negative_button),
                denied = getString(R.string.permission_message_alert_denied)
        )

        if (checkPermissions(permissions)) {
            SensorForeground.startService(getActivityContext())

            if (!LastLocation.getInstance(context).isGpsEnable) {
                toastLong(getString(R.string.home_gps_request))
            }

        } else {
            requestCriticalPermissions(permissions, Constraint.PERMISSIONS_ALERT_SERVICE, message)
        }
    }
}
