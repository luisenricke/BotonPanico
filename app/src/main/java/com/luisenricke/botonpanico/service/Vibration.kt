package com.luisenricke.botonpanico.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.SingletonHolder
import timber.log.Timber

@Suppress("unused")
class Vibration private constructor(private val context: Context) {

    companion object : SingletonHolder<Vibration, Context>(::Vibration) {
        const val DEFAULT_TIME: Long = 1000L * 1L // milliseconds * seconds * minutes
    }

    private val manager: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    @Suppress("deprecation")
    fun vibrate(milliseconds: Long = DEFAULT_TIME) {
        if (!manager.hasVibrator()) Timber.e(context.getString(R.string.vibrator_doesnt_available))
            .also { return }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.vibrate(VibrationEffect.createOneShot(milliseconds, DEFAULT_AMPLITUDE))
        } else {
            manager.vibrate(milliseconds)
        }
    }
}
