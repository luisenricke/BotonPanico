package com.luisenricke.botonpanico.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.PHONE
import com.luisenricke.botonpanico.database.entity.Contact.SCHEMA.ID
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
    }

    override fun toString(): String {
        return """
            id:             ${id}
            name:           ${name}
            phone:          ${phone}
            relationship:   ${relationship}
        """.trimIndent()
    }

    data class Minimal(var name: String = "", var phone: String = "")
}
