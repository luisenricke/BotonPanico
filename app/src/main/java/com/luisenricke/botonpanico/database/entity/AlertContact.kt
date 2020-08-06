package com.luisenricke.botonpanico.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.luisenricke.botonpanico.database.entity.AlertContact.SCHEMA.ALERT_ID
import com.luisenricke.botonpanico.database.entity.AlertContact.SCHEMA.CONTACT_ID
import com.luisenricke.botonpanico.database.entity.AlertContact.SCHEMA.ID
import com.luisenricke.botonpanico.database.entity.AlertContact.SCHEMA.MESSAGE_SENT

@Entity(
        tableName = AlertContact.SCHEMA.TABLE, foreignKeys = [
            ForeignKey(
                entity = Alert::class,
                parentColumns = arrayOf(Alert.SCHEMA.ID),
                childColumns = arrayOf(ALERT_ID),
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE),
            ForeignKey(
                entity = Contact::class,
                parentColumns = arrayOf(Contact.SCHEMA.ID),
                childColumns = arrayOf(CONTACT_ID),
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)
        ]
)
data class AlertContact(
    @ColumnInfo(name = ALERT_ID, index = true) val alertId: Long,
    @ColumnInfo(name = CONTACT_ID, index = true) val contactId: Long,
    @ColumnInfo(name = MESSAGE_SENT) val messageSent: String = "",
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = 0)
{

    object SCHEMA {
        const val TABLE = "AlertContact"
        const val ID = "id"
        const val ALERT_ID = "alert_id"
        const val CONTACT_ID = "contact_id"
        const val MESSAGE_SENT = "message_sent"
    }

    override fun toString(): String {
        return """/* ${System.lineSeparator()}
            |id:            $id
            |alertId:       $alertId
            |contactId:     $contactId
            |messageSent:   $messageSent
        ${System.lineSeparator()} */""".trimMargin()
    }
}
