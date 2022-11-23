package com.example.mylibrary

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService


var BS_THREAD = false


open class MPbase  : Service(){
    val BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED"
    var my_data: Intent? = null
    var my_resultCode: Int? = null
    lateinit var orientationChangeCallback: OrientationChangeCallback

    //display
    var mWidth = 0
    var mHeight = 0
    var mRotation = -1
    var mDensity = 0
    var imageReader: ImageReader? = null

    var mProjectionStopped = true
    val SCREENCAP_NAME = "screencap"
    val VIRTUAL_DISPLAY_FLAGS =
        DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    var mHandler: Handler? = null
    var virtualDisplay: VirtualDisplay? = null
    var mediaProjection: MediaProjection? = null
    val mediaProjectionManager: MediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    var mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, myIntent: Intent) {
            if (myIntent.action == BCAST_CONFIGCHANGED) {
                Log.d("회전-브로드캐스트", "received->$BCAST_CONFIGCHANGED")
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // it's Landscape
                    Log.d("회전-브로드캐스트", "LANDSCAPE")
                    orientationEventListener()
                } else {
                    Log.d("회전-브로드캐스트", "PORTRAIT")
                    orientationEventListener()
                }
            }
        }
    }
    fun orientationEventListener() {
        Log.d("회전", "onOrientationChanged")
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val rotation: Int = display.rotation

        if (mRotation != rotation) {
            Toast.makeText(
                applicationContext,
                //applicationContext.getString(R.string.app_orientation_changed_start),
                "app_orientation_changed_start",
                Toast.LENGTH_SHORT
            ).show()

            if (virtualDisplay != null) {
                virtualDisplay!!.release()
            }
            if (imageReader != null) {
                imageReader!!.setOnImageAvailableListener(null, null)
            }
            if (!mProjectionStopped) {
                mProjectionStopped = false
                virtualDisplay = get_virtualDisplay()!!
            }
            //Thread.sleep(100)
            Toast.makeText(
                applicationContext,
                //applicationContext.getString(R.string.app_orientation_changed_stop),
                "app_orientation_changed_stop",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun get_virtualDisplay(): VirtualDisplay? {
        set_display()
        make_image_reader()

        var vd = mediaProjection!!.createVirtualDisplay(
            SCREENCAP_NAME,
            mWidth,
            mHeight,
            mDensity,
            VIRTUAL_DISPLAY_FLAGS,
            imageReader!!.surface,
            null,
            mHandler
        )

        return vd
    }

    fun set_display() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var metrics = DisplayMetrics()
        var display = wm.defaultDisplay
        wm.defaultDisplay.getMetrics(metrics)
        mDensity = metrics.densityDpi

        display!!.getMetrics(metrics)
        mRotation = wm.defaultDisplay.rotation
        // get width and height
        var size = Point()
        display.getRealSize(size)
        mWidth = size.x
        mHeight = size.y
//        if (detect_run != null) {
//            detect_run!!.build(mWidth, mHeight)
//        }

    }

    @SuppressLint("WrongConstant")
    fun make_image_reader() {
        // start capture reader
        imageReader = ImageReader.newInstance(
            mWidth,
            mHeight,
            PixelFormat.RGBA_8888,
            1
        )
    }

    fun createModel() {
        val so = getScreenOrientation()
        //detect_run = Run(applicationContext)

    }  fun getScreenOrientation(): Int {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return when (wm.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }
    }

    fun createVirtualDisplay() {
        mediaProjection =
            mediaProjectionManager.getMediaProjection(
                my_resultCode!!,
                my_data!!
            )

        // start capture handling thread
        object : Thread() {
            override fun run() {
                Looper.prepare()
                mHandler = Handler()
                Looper.loop()
            }
        }.start()

        virtualDisplay = get_virtualDisplay()!!
        mProjectionStopped = false

        orientationChangeCallback = OrientationChangeCallback()
        if (orientationChangeCallback.canDetectOrientation()) {
            orientationChangeCallback.enable()
        }

    }

    inner class OrientationChangeCallback internal constructor(

    ) :
        OrientationEventListener(applicationContext) {
        override fun onOrientationChanged(orientation: Int) {
            orientationEventListener()
        }


    }

    fun close() {
        // detect_run!!.close()
        orientationChangeCallback.disable()
        imageReader!!.close()
        virtualDisplay!!.release()
        mediaProjection!!.stop()
        this.applicationContext.unregisterReceiver(mBroadcastReceiver);

    }
}