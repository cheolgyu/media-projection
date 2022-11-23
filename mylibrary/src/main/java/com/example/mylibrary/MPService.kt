package com.example.mylibrary

import android.R.attr.bitmap
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.media.Image
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.example.myapplication.Noti
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer


class MPService : MPbase() {
    companion object {
        //var kakao_send_notify = false
    }

    var STORE_DIRECTORY: String? = null
    private val FOREGROUND_SERVICE_ID = 1000


    //    lateinit var bsView: BackgroundServiceView
    lateinit var bsThread: BackgroundThread
    val TAG: String = this.javaClass.simpleName

    override fun onCreate() {
        Log.d(TAG, "=============start===================")
        // utils = Utils(this.applicationContext)

        bsThread = BackgroundThread()
        bsThread!!.start()

        run_notify()
        ready_media()
        // bsView = BackgroundServiceView(applicationContext)
        val filter = IntentFilter()
        filter.addAction(BCAST_CONFIGCHANGED)
        this.applicationContext.registerReceiver(mBroadcastReceiver, filter);
        BS_THREAD = true
    }

    @Throws(java.lang.Exception::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {

            my_resultCode = intent.getIntExtra("resultCode", 1000)
            my_data = intent.getParcelableExtra("data")

            createVirtualDisplay()
            createModel()
            //  detect_run!!.build(mWidth, mHeight)
            // bsView.start()

        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    fun image_available_bitmap(): Bitmap? {
        var image = imageReader?.acquireNextImage()
        if (image != null) {
            val planes: Array<Image.Plane> = image.planes


            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride: Int = planes[0].pixelStride
            val rowStride: Int = planes[0].rowStride
            val rowPadding: Int = rowStride - pixelStride * mWidth

            val w: Int = mWidth + rowPadding / pixelStride

            var bitmap: Bitmap? = null
            bitmap = Bitmap.createBitmap(
                w,//+ rowPadding / pixelStride,
                mHeight,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            return bitmap

        }

        return null
    }

    class ActionInfo {
        var x: Float = 0.0f
        var y: Float = 0.0f
        lateinit var action_type: String

        constructor(x: Float, y: Float, action_type: String) : this() {
            this.x = x
            this.y = y
            this.action_type = action_type
        }

        constructor()
    }

    inner class BackgroundThread : Thread() {

        fun exit() {
            sleep(1000)
            interrupt()
            Log.d("종료", "=====================쓰레드.exit======================")
        }

        override fun run() {
            while (true && !isInterrupted) {
                if (BS_THREAD && !isInterrupted ) {
                    //if (BS_THREAD && !RUN_DETECT && !isInterrupted && detect_run?.detector?.run_state == true) {


                    val startTime = SystemClock.uptimeMillis()
                    val bitmap = image_available_bitmap()

                    if (bitmap != null){
                        Log.d("이미지", STORE_DIRECTORY+bitmap.toString()+startTime)
                        save(bitmap, "$STORE_DIRECTORY$startTime.png")
                    }


                }


            }


        }

    }

    fun save( bitmap : Bitmap,  filepath: String){
        val file = File(filepath)
        var os: OutputStream? = null

        try {
            file.createNewFile()
            os = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 100, os)
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        BS_THREAD = false
        bsThread.exit()
        close()

        Toast.makeText(
            this,
            "app_service_stop",
            Toast.LENGTH_SHORT
        ).show()



        Log.d("종료", "=====================끝=====================")
    }


    fun run_notify() {
        var noti = Noti(this)
        noti.createNotificationChannel()
        var notify = noti.build(11232131)
        startForeground(FOREGROUND_SERVICE_ID, notify)

    }

    fun ready_media() {
        var u = Utils(this)
        STORE_DIRECTORY = u.mkdir()
    }


}
