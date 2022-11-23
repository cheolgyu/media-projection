package com.example.mylibrary


import android.content.Context
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.view.WindowManager
import java.io.File

class Utils(var context: Context) {


    fun mkdir(): String? {
        val externalFilesDir = context.applicationContext.getExternalFilesDir(null)
        var path = ""
        if (externalFilesDir != null) {
            path =
                    externalFilesDir.absolutePath + "/screenshots/"
            val storeDirectory =
                    File(path)
            storeDirectory.deleteRecursively()
            if (!storeDirectory.exists()) {

                val success: Boolean = storeDirectory.mkdirs()
                if (!success) {
                    Log.e(
                            "eeee",
                            "failed to create file storage directory."
                    )
                    return null
                }
            }
            return path
        } else {
            Log.e(
                    "eeee",
                    "failed to create file storage directory, getExternalFilesDir is null."
            )
            return null
        }
    }
}