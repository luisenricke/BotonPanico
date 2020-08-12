package com.luisenricke.botonpanico.alert

import android.content.Context
import android.os.Bundle
import android.view.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luisenricke.androidext.toastLong
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.AlertContact
import com.luisenricke.botonpanico.databinding.FragmentAlertDetailBinding
import com.luisenricke.kotlinext.formatDateTimeExtended
import timber.log.Timber

class AlertDetailFragment : BaseFragment() {

    private var _binding: FragmentAlertDetailBinding? = null
    private val binding get() = _binding!!

    private var idAlert: Long = 0L

    private lateinit var alertDetailAdapter: AlertDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val args = AlertDetailFragmentArgs.fromBundle(it)
            idAlert = args.idAlert
        }

        getActivityContext().setBottomNavigationViewVisibility(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlertDetailBinding.inflate(inflater, container, false)

        alertDetailAdapter = setAlertDetailAdapter(binding.root.context)

        binding.apply {
            val context = root.context

            // Toolbar
            getActivityContext().setSupportActionBar(toolbar)
            setupActionBar(getActivityContext().supportActionBar, getString(R.string.alert_detail))

            // Alert
            val alert = database.alertDAO().get(idAlert)

            if (idAlert == 0L || alert == null) {
                toastLong(context.getString(R.string.alert_empty))
                navController.popBackStack()
            }

            // region BindingViews
            lblDate.text = alert!!.timestamp.time.formatDateTimeExtended()
            lblLocation.text = if (alert.latitude != 0.0 && alert.longitude != 0.0) {
                "${alert.latitude}, ${alert.longitude}"
            } else {
                context.getString(R.string.alert_detail_location_not_available)
            }

            // RecyclerView
            recyclerAlertContact.apply {
                setHasFixedSize(true)

                adapter = alertDetailAdapter
            }

            //  endregion BindingViews

            return binding.root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_alert_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home      -> {
                navController.popBackStack()
                true
            }
            R.id.menu_alert_delete -> {
                deleteAlert(this.getActivityContext())
                true
            }
            else                   -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        displayViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        getActivityContext().setBottomNavigationViewVisibility(true)
    }

    private fun deleteAlert(context: Context) {
        val alert = database.alertDAO().get(idAlert)
        Timber.v("Alert: ${alert.toString()}")

        MaterialAlertDialogBuilder(context).setTitle(context.getString(R.string.alert_delete_title))
                .setIcon(R.drawable.ic_baseline_report_problem_24)
                .setMessage(context.getString(R.string.alert_delete_message))
                .setPositiveButton(context.getString(R.string.alert_delete_positive)) { _, _ ->
                    alert?.let {
                        val isContactDeleted = database.alertDAO().delete(idAlert)
                        Timber.v("Alert with $isContactDeleted has been deleted with our child rows")
                        toastLong(context.getString(R.string.alert_deleted_successfully))
                        navController.popBackStack()
                    }
                }
                .setNegativeButton(context.getString(R.string.alert_delete_negative)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun setAlertDetailAdapter(context: Context): AlertDetailAdapter {
        val clickListener: (AlertContact) -> Unit = { item ->
            Timber.v("clickListener")
        }

        val longClickListener: (AlertContact) -> Unit = { item ->
            Timber.v("longClickListener")
        }

        return AlertDetailAdapter(context, idAlert, clickListener, longClickListener)
    }

    private fun displayViews() {
        val isAlertsEmpty = alertDetailAdapter.isEmpty()

        if (isAlertsEmpty) {
            binding.apply {
                recyclerAlertContact.visibility = View.GONE
                lblAlertContactsTitle.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                recyclerAlertContact.visibility = View.VISIBLE
                lblAlertContactsTitle.visibility = View.VISIBLE
                layoutEmpty.visibility = View.GONE
            }

            alertDetailAdapter.update()
        }
    }
}

