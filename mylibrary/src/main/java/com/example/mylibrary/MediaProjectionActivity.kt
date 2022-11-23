package com.example.myapplication


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.mylibrary.MPService
import com.example.mylibrary.R


open class MediaProjectionActivity : AppCompatActivity() {
    var mIntent: Intent? = null
    var REQ_CODE_OVERLAY_PERMISSION = 1

    fun new_bg(): Intent {
        if (mIntent == null) {
           mIntent = Intent(applicationContext, MPService::class.java)
            return mIntent!!
        } else {
            return mIntent!!
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaprojection)



        val action = intent.extras?.getString("action")
       mIntent = new_bg()
        if (action != null && action == "stop") {
            stopService(mIntent!!)
        }


        //CheckTouch(applicationContext).checkAccessibilityPermissions()

    }

    fun service_stop_btn(view: View?) {
        stopService(mIntent)
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun service_start_btn(view: View?) {
        var mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        var captureIntent: Intent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, 1000)

       // CheckTouch(this).checkFirstRun()
//        Log.d(
//                "탑뷰",
//                Settings.canDrawOverlays(applicationContext).toString()
//        )
//        if (Settings.canDrawOverlays(applicationContext)) {
//            var mediaProjectionManager =
//                    getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
//            var captureIntent: Intent = mediaProjectionManager.createScreenCaptureIntent()
//            startActivityForResult(captureIntent, 1000)
//        } else {
//            onObtainingPermissionOverlayWindow()
//        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            mIntent?.putExtra("resultCode", resultCode)
            mIntent?.putExtra("data", data)
            startService(mIntent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    open fun onObtainingPermissionOverlayWindow() {
        Log.d(
                "탑뷰", "권한"

        )
        val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION)
    }


}