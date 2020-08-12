package com.luisenricke.botonpanico.alert

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.database.AppDatabase
import com.luisenricke.botonpanico.database.dao.AlertDAO
import com.luisenricke.botonpanico.database.entity.Alert
import com.luisenricke.botonpanico.databinding.ItemAlertBinding
import com.luisenricke.kotlinext.formatDateTimeExtended

class AlertAdapter(val context: Context, val clickListener: (Alert) -> Unit, val longClickListener: (Alert) -> Unit) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    private var alerts: List<Alert> = arrayListOf()
    private val dao: AlertDAO = AppDatabase.getInstance(context).alertDAO()

    init {
        update()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int =
            alerts.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) =
            holder.bind(alerts[pos])

    inner class ViewHolder(private val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alert: Alert) {
            with(binding) {
                lblId.text = alert.id.toString()
                lblDate.text = alert.timestamp.time.formatDateTimeExtended()
                // TODO uncomment when knows the state of message sent
                //                lblType.text = alert.type
                lblLocation.text = "${alert.latitude}, ${alert.longitude}"

                cardContact.setOnClickListener {
                    clickListener(alert)
                }

                cardContact.setOnLongClickListener {
                    longClickListener(alert)

                    true
                }
            }
        }
    }

    fun update() {
        alerts = dao.get().sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean =
            dao.count() == 0L
}
