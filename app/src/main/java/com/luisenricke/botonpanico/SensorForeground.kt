package com.luisenricke.botonpanico

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.luisenricke.androidext.preferenceGet
import com.luisenricke.androidext.toastShort
import com.luisenricke.kotlinext.roundDecimals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Math.abs

class SensorForeground : Service(), SensorEventListener {

    private val CHANNEL_ID = SensorForeground.javaClass.name + "ChannelID"
    private val CHANNEL_NAME = "SensorForeground Notification Channel"
    private val NOTIFICATION_TITLE = "SensorForeground"
    private val NOTIFICATION_ICON = android.R.drawable.ic_menu_compass

    companion object {
        val TAG = SensorForeground::class.simpleName

        fun startService(context: Context) {
            val startIntent = Intent(context, SensorForeground::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, SensorForeground::class.java)
            context.stopService(stopIntent)
        }
    }

    private var manager: SensorManager? = null
    private var accelerometer: Sensor? = null

    var gravity = floatArrayOf(0.0f, 0.0f, 0.0f)
    var acceleration = floatArrayOf(0.0f, 0.0f, 0.0f)

    var xAxis = 0.0f
    var count = 0

    var isTriggered = false

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        manager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        initValues()

        // startForeground(1010, null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //return super.onStartCommand(intent, flags, startId)
//        manager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//
        initValues()

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
//            .setContentText(input)
            .setSmallIcon(NOTIFICATION_ICON)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1010, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        manager?.unregisterListener(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.8f
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                acceleration[0] = event.values[0] - gravity[0]

                Log.i(TAG, "x: ${acceleration[1]}")

                xAxis = abs(acceleration[0])

                if (xAxis >= 15 && !isTriggered) {
                    Log.i(TAG, "Triggered")
                    motionCheck()
                }
            }
        }
    }

    /// https://stackoverflow.com/a/55607504/12923478
    private fun motionCheck() {
        val duration = 5_000
        var startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Default).launch {
            isTriggered = true
            var totalElapsed = 0L
            while (totalElapsed < duration) {
                totalElapsed = System.currentTimeMillis() - startTime
                count = if (xAxis >= 15) count + 1 else count
                Log.i(TAG, "total: $totalElapsed, xAxis: $xAxis, count: $count")
                delay(200)
            }

            if(count>8){
                // Get phone
                val phone = applicationContext.preferenceGet("phone", String::class)
                // Location
                val locationTrack = LocationTrack(applicationContext)
                locationTrack.process()
                val longitude = locationTrack.longitude
                val latitude = locationTrack.latitude
                Timber.i("$latitude, $longitude")

                //SMS
                val intent = Intent()
                val pi = PendingIntent.getActivity(applicationContext, 0, intent, 0)
                val sms: SmsManager = SmsManager.getDefault()
                sms.sendTextMessage(phone,null,"Marcame estoy en peligro", pi,null)
                sms.sendTextMessage(
                    phone,
                    null,
                    "Ubicación: http://maps.google.com/?q=${latitude.roundDecimals(6)},${longitude.roundDecimals(
                        6
                    )}",
                    pi,
                    null
                )

            }

            initValues()
        }
    }

    private fun initValues() {
        //gravity = floatArrayOf(0.0f, 0.0f, 0.0f)
        //acceleration = floatArrayOf(0.0f, 0.0f, 0.0f)
        //xAxis = 0.0f
        count = 0
        isTriggered = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onBind(intent: Intent): IBinder? = null
}
