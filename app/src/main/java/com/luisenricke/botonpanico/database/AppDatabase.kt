package com.luisenricke.botonpanico.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.luisenricke.androidext.room.converter.Date
import com.luisenricke.androidext.room.ioThread
import com.luisenricke.botonpanico.database.dao.ContactDAO
import com.luisenricke.botonpanico.database.entity.Contact

@Database(
    entities = [
        Contact::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(Date::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDAO(): ContactDAO

    companion object {
        private const val NAME = "Database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context)
                    .also { INSTANCE = it }
            }
        }

        private fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ioThread {

                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)

                    }
                })
                .allowMainThreadQueries()
                .build()
        }


    }
}
