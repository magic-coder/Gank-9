package com.wazing.gank.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.webkit.WebView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun <T> T.logd(tag: String = "zz") {
    when (this) {
        is List<*> -> {
            for (i in this) {
                i.logd()
            }
        }
        else -> Log.d(tag, this.toString())
    }
}

/** 日期转换 START **/
private const val ONE_SECOND: Long = 1000
private const val ONE_MINUTE = ONE_SECOND * 60
private const val ONE_HOUR = ONE_MINUTE * 60
private const val ONE_DAY = ONE_HOUR * 24

private const val PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

fun Date.timestampString(): String {
    val splitTime = Date().time - this.time
    return if (splitTime < 30 * ONE_DAY) {
        when {
            splitTime < ONE_MINUTE -> "刚刚".format()
            splitTime < ONE_HOUR -> "%d分钟前".format(splitTime / ONE_MINUTE)
            splitTime < ONE_DAY -> "%d小时前".format(splitTime / ONE_HOUR)
            else -> "%d天前".format(splitTime / ONE_DAY)
        }
    } else SimpleDateFormat("M月d日 HH:mm", Locale.getDefault()).format(this)
}

fun getTimestampDate(date: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS"): Date {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.parse(date)
}

fun getTimestampString(date: String): String {
    return try {
        getTimestampDate(date).timestampString()
    } catch (e: Exception) {
        getTimestampDate(date, PATTERN).timestampString()
    }
}

/** 日期转换 END **/


/**
 * webView 截取屏幕
 */
fun WebView.interceptBitmap(): Bitmap? {
    val scale = this.scale
    val width = this.width
    val height = (this.contentHeight * scale + 0.5).toInt()
    if (width <= 0 || height <= 0) {
        this.context.toast("width and height must be > 0")
        return null
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

/**
 * 将bitmap保存到内存卡
 */
fun Bitmap.saveImage(context: Context, fileName: String, nextThing: (File) -> Unit) {
    val bitmap = this
    doAsync {
        val file: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absoluteFile
        val dirName = "GankPictures"
        val dirFile = File(file, dirName)
        if (!dirFile.exists()) dirFile.mkdirs()
        val currentFile = File(dirFile, fileName)
        if (currentFile.exists()) currentFile.delete()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(currentFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos?.let {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        uiThread {
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(File(currentFile.path))))
            nextThing(currentFile)
        }
    }
}

fun Activity.checkSelfPermission(nextThing: () -> Unit, permissionName: String, permissionCode: Int) {
    if (ContextCompat.checkSelfPermission(this, permissionName)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionName), permissionCode)
    } else {
        nextThing()
    }
}