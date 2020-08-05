package com.luisenricke.botonpanico.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.PHONE
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.ID
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.IMAGE
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.IS_HIGHLIGHTED
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.MESSAGE
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.NAME
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.RELATIONSHIP
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.TABLE

@Entity(tableName = TABLE)
data class Contact(
    @ColumnInfo(name = NAME)
    var name: String = "",
    @ColumnInfo(name = PHONE)
    var phone: String = "",
    @ColumnInfo(name = RELATIONSHIP)
    var relationship: String = "",
    @ColumnInfo(name = MESSAGE)
    var message: String = "",
    @ColumnInfo(name = IMAGE)
    var image: String = "",
    @ColumnInfo(name = IS_HIGHLIGHTED)
    var isHighlighted: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0
) {
    object SCHEMA {
        const val TABLE = "Contact"
        const val ID = "id"
        const val NAME = "name"
        const val PHONE = "phone"
        const val RELATIONSHIP = "relationship"
        const val MESSAGE = "message"
        const val IMAGE = "image"
        const val IS_HIGHLIGHTED = "isHighlighted"
    }

    override fun toString(): String {
        return """/* ${System.lineSeparator()}
            |id:             $id
            |name:           $name
            |phone:          $phone
            |relationship:   $relationship
            |message:        $message
            |image:          $image
            |isHighlighted   $isHighlighted
        ${System.lineSeparator()} */""".trimMargin()
    }

    data class Minimal(var name: String = "", var phone: String = "")
}
