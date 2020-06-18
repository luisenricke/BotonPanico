package com.luisenricke.androidext

import android.content.Context
import android.widget.Toast

object Toast {
    fun short(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun long(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
