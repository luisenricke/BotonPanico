package com.luisenricke.botonpanico.alert

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.database.AppDatabase
import com.luisenricke.botonpanico.database.dao.AlertDAO
import com.luisenricke.botonpanico.database.entity.Alert
import com.luisenricke.botonpanico.databinding.ItemAlertBinding

class AlertAdapter(val context: Context, val clickListener: (Alert) -> Unit, val longClickListener: (Alert) -> Unit) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    private var alerts: List<Alert> = arrayListOf()
    private val dao: AlertDAO = AppDatabase.getInstance(context).alertDAO()

    init {
        alerts = arrayListOf(
                Alert(latitude = 10.0, longitude = 10.0, type = "Test1", id = 1),
                Alert(latitude = 11.0, longitude = 11.0, type = "Test2", id = 2),
                Alert(latitude = 12.0, longitude = 12.0, type = "Test3", id = 3),
                Alert(latitude = 13.0, longitude = 13.0, type = "Test4", id = 4),
                Alert(latitude = 14.0, longitude = 14.0, type = "Test5", id = 5),
                Alert(latitude = 15.0, longitude = 15.0, type = "Test6", id = 6),
                Alert(latitude = 16.0, longitude = 16.0, type = "Test7", id = 7),
                Alert(latitude = 17.0, longitude = 17.0, type = "Test8", id = 8),
                Alert(latitude = 18.0, longitude = 18.0, type = "Test9", id = 9)
        )
        //        updateList()
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
                lblDate.text = alert.timestamp.toString()
                lblType.text = alert.type
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

    fun updateList() {
        //        contacts = dao.get().sortedBy { it.name }.sortedByDescending { it.isHighlighted }
        notifyDataSetChanged()
    }
}
