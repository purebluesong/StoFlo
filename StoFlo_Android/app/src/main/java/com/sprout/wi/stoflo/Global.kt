package com.sprout.wi.stoflo

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

/**
 * Created by purebluesong on 2016/6/21.
 */

object Global {
    val REQUSET_CODE_GALLERY = 1

    @JvmOverloads fun Bitmap2Bytes(bm: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(format, 100, baos)
        return baos.toByteArray()
    }

    fun Bytes2Bimap(b: ByteArray): Bitmap? {
        if (b.size != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            return null
        }
    }

    fun bitmap2Drawable(pic: Bitmap, res: Resources): Drawable {
        return BitmapDrawable(res, pic)
    }

    interface standardAvtivityInterface {
        fun onStart()
        fun onCreate()
        fun onStop()
        fun onDestory()

    }



}
