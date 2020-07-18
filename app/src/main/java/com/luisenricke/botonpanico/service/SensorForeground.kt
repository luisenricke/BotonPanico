package com.luisenricke.botonpanico.service

import android.Manifest
import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
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
import kotlin.math.abs

// https://expertise.jetruby.com/how-to-implement-motion-sensor-in-a-kotlin-app-b70db1b5b8e5
/// https://stackoverflow.com/a/55607504/12923478
@Suppress("unused")
class SensorForeground : Service(), SensorEventListener {

    companion object {
        private const val SERVICE_ID = 1010

        private const val CHANNEL_ID = "SensorForegroundChannelID"
        private const val CHANNEL_NAME = "SensorForeground Notification Channel"
        private const val NOTIFICATION_TITLE = "SensorForeground"
        private const val NOTIFICATION_ICON = android.R.drawable.ic_menu_compass

        private var isCycleTriggered = false
        private const val CYCLE_TIME_DURATION = 1000 * 5 // milliseconds * seconds
        private const val CYCLE_TIME_PAUSE = 200L // milliseconds
        private const val CYCLE_ACTIVATION_LIMIT = 8
        private const val X_AXIS_TRIGGER = 15f

        @JvmStatic
        fun startService(context: Context) {
            val startIntent = Intent(context, SensorForeground::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        @JvmStatic
        fun stopService(context: Context) {
            val stopIntent = Intent(context, SensorForeground::class.java)
            context.stopService(stopIntent)
        }
    }

    private var manager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private val isPermissionLocationEnable: Boolean
        get() = permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)

    private val isPermissionSendSMSEnable: Boolean
        get() = permissionCheck(Manifest.permission.SEND_SMS)

    // region service
    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        manager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(SERVICE_ID, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        manager?.unregisterListener(this)
        // SendSMS.getInstance(this).unregisterReceivers()
    }

    override fun onBind(intent: Intent): IBinder? = null

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
//            .setContentText(input)
            .setSmallIcon(NOTIFICATION_ICON)
            .setContentIntent(pendingIntent)
            .build()
    }
    // endregion

    // region sensor
    override fun onSensorChanged(event: SensorEvent?) {
        val gravity = floatArrayOf(0.0f, 0.0f, 0.0f)
        val acceleration = floatArrayOf(0.0f, 0.0f, 0.0f)
        val x: Float

        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val alpha = 0.8f
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                acceleration[0] = event.values[0] - gravity[0]
                // Timber.d("x: ${acceleration[1]}")
                x = abs(acceleration[0])

                if (x >= X_AXIS_TRIGGER && !isCycleTriggered) sensorCycleCheck(x)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun sensorCycleCheck(xAxis: Float) {
        val startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Default).launch {
            isCycleTriggered = true

            var count = 0
            var totalElapsed = 0L
            while (totalElapsed < CYCLE_TIME_DURATION) {
                totalElapsed = System.currentTimeMillis() - startTime
                count = if (xAxis >= X_AXIS_TRIGGER) count + 1 else count
//                Timber.d("total: $totalElapsed, xAxis: $xAxis, count: $count")
                delay(CYCLE_TIME_PAUSE)
            }

            if (count > CYCLE_ACTIVATION_LIMIT) {
                // Phone
                val phone = applicationContext.preferenceGet("phone", String::class)

                // Vibration
                Vibration.getInstance(this@SensorForeground).vibrate()

                // Location
                val location = LastLocation.getInstance(this@SensorForeground).getLocation()
//                Timber.i("${location?.latitude}, ${location?.longitude}")

                // SMS
                if (phone != null && location != null)
                    SendSMS.getInstance(this@SensorForeground).locationMessage(phone, location)
            }

            isCycleTriggered = false
        }
    }
    // endregion

    // region permission
    fun checkLocationPermission() {
        if (!isPermissionLocationEnable) {
            permissionApply(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Constraint.PERMISSION_ACCESS_FINE_LOCATION_CODE,
                getString(R.string.permission_access_fine_location_apply_message),
                getString(R.string.permission_access_fine_location_apply_denied)
            )
        }
    }

    fun checkSendSMSPermission() {
        if (!isPermissionSendSMSEnable) {
            permissionApply(
                Manifest.permission.SEND_SMS,
                Constraint.PERMISSION_SEND_SMS,
                getString(R.string.permission_send_sms_apply_message),
                getString(R.string.permission_send_sms_apply_denied)
            )
        }
    }
    // endregion
}
