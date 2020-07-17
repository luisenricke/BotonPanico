package com.luisenricke.botonpanico.service

import android.Manifest
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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.luisenricke.androidext.permissionApply
import com.luisenricke.androidext.permissionCheck
import com.luisenricke.androidext.preferenceGet
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.MainActivity
import com.luisenricke.botonpanico.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs

// https://expertise.jetruby.com/how-to-implement-motion-sensor-in-a-kotlin-app-b70db1b5b8e5
/// https://stackoverflow.com/a/55607504/12923478
class SensorForeground : Service(), SensorEventListener {

    private val CHANNEL_ID = SensorForeground.javaClass.name + "ChannelID"
    private val CHANNEL_NAME = "SensorForeground Notification Channel"
    private val NOTIFICATION_TITLE = "SensorForeground"
    private val NOTIFICATION_ICON = android.R.drawable.ic_menu_compass

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, SensorForeground::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, SensorForeground::class.java)
            context.stopService(stopIntent)
        }
    }

    private val isPermissionEnable: Boolean
        get() = permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)

    private var manager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private var gravity = floatArrayOf(0.0f, 0.0f, 0.0f)
    private var acceleration = floatArrayOf(0.0f, 0.0f, 0.0f)

    private var xAxis = 0.0f
    private var count = 0

    private var isTriggered = false

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

    private fun motionCheck() {
        val duration = 5_000
        val startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Default).launch {
            isTriggered = true
            var totalElapsed = 0L
            while (totalElapsed < duration) {
                totalElapsed = System.currentTimeMillis() - startTime
                count = if (xAxis >= 15) count + 1 else count
                Timber.d("total: $totalElapsed, xAxis: $xAxis, count: $count")
                delay(200)
            }

            if (count > 8) {
                // Phone
                val phone = applicationContext.preferenceGet("phone", String::class)

                // Location
                val location = LastLocation.getInstance(this@SensorForeground).getLocation()
                val latitude = location?.latitude
                val longitude = location?.longitude
                Timber.i("$latitude, $longitude")


/*
                //SMS
                val intent = Intent()
                val pi = PendingIntent.getActivity(applicationContext, 0, intent, 0)
                val sms: SmsManager = SmsManager.getDefault()
                sms.sendTextMessage(phone,null,"Marcame estoy en peligro", pi,null)
                sms.sendTextMessage(
                    phone,
                    null,
                    "Ubicación: http://maps.google.com/?q=${latitude?.roundDecimals(6)},${longitude?.roundDecimals(
                        6
                    )}",
                    pi,
                    null
                )
*/
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

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.8f
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                acceleration[0] = event.values[0] - gravity[0]

                Timber.d("x: ${acceleration[1]}")

                xAxis = abs(acceleration[0])

                if (xAxis >= 15 && !isTriggered) {
                    Timber.i("Triggered")
                    motionCheck()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onBind(intent: Intent): IBinder? = null

    fun checkLocationPermission() {
        if (!isPermissionEnable) {
            permissionApply(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Constraint.PERMISSION_ACCESS_FINE_LOCATION_CODE,
                getString(R.string.permission_access_fine_location_apply_message),
                getString(R.string.permission_access_fine_location_apply_denied)
            )
        }
    }
}
