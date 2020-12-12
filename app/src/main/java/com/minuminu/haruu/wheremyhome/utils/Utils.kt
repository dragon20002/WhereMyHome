package com.minuminu.haruu.wheremyhome.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object Utils {
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.d(javaClass.name, "storageDir = ${storageDir?.absolutePath!!}")

        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun loadImageFile(context: Context, name: String): Bitmap {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return BitmapFactory.decodeFile("$storageDir/$name").let {
            // 이미지가 회전되어 출력되는 이슈 처리
            val matrix = Matrix().apply {
                val exif = ExifInterface("$storageDir/$name")
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                Log.d(javaClass.name, "exif orientation: $orientation")

                when (orientation) {
                    6 -> postRotate(90f)
                    3 -> postRotate(180f)
                    8 -> postRotate(270f)
                }
            }

            Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
        }
    }

    fun resizeBitmap(bitmap: Bitmap, toWidth: Float, toHeight: Float): Bitmap {
        val scaleFactor = min(bitmap.width / toWidth, bitmap.height / toHeight)
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width / scaleFactor).toInt(),
            (bitmap.height / scaleFactor).toInt(),
            true
        )
    }

    fun createSnapshotFile(context: Context, name: String, bitmap: Bitmap): File {
        val storageDir: File = context.filesDir
        val imageFile =
            File("$storageDir/resize_$name")

        val fout = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fout)
        fout.close()

        return imageFile
    }

    fun loadSnapshotFile(context: Context, name: String): Bitmap? {
        val storageDir: File = context.filesDir

        try {
            return BitmapFactory.decodeFile("$storageDir/resize_$name").let {
                // 이미지가 회전되어 출력되는 이슈 처리
                val matrix = Matrix().apply {
                    val exif = ExifInterface("$storageDir/resize_$name")
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                    Log.d(javaClass.name, "exif orientation: $orientation")

                    when (orientation) {
                        6 -> postRotate(90f)
                        3 -> postRotate(180f)
                        8 -> postRotate(270f)
                    }
                }

                Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun dp2px(context: Context, dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    fun px2dp(context: Context, px: Float): Float {
        val metrics = context.resources.displayMetrics
        return px * DisplayMetrics.DENSITY_DEFAULT / metrics.densityDpi
    }
}