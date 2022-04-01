package com.ybouidjeri.newsapi

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.ybouidjeri.newsapi.models.Source
import java.io.*
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utils {

    const val TAG = "NEWSAPI_UTILS"

    const val HEADLINE_KEY = "HEADLINE"
    const val PREF_SOURCE_KEY = "PREF_SOURCE"
    const val API_KEY = "03d38c813bd44671af7b5ef80106992d"

    val defaultSource = Source("bbc-news", "BBC News")

    fun setPreferedSource(context: Context, source: Source) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = pref.edit()
        val gson = Gson()
        val json: String = gson.toJson(source)
        editor.putString(PREF_SOURCE_KEY, json)
        editor.apply()
    }

    fun getPreferedSource(context: Context): Source {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json: String? = pref.getString(PREF_SOURCE_KEY, null)
        if (json != null) {
            val source: Source = gson.fromJson(json, Source::class.java)
            return source
        }
        return defaultSource
    }


    fun saveInputStreamInFile(inputStream: InputStream, filePath: String): Boolean {
        val file = File(filePath)
        var outputStream: OutputStream? = null
        try {
            val buffer = ByteArray(1024)
            outputStream = FileOutputStream(file)
            while (true) {
                val read: Int = inputStream.read(buffer)
                if (read == -1) {
                    break
                }
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Errror saving inputstream in local: ${e.message}")
            e.printStackTrace()
            return false
        } finally {
            inputStream.close()
            outputStream?.close()
        }
    }

    fun md5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest: MessageDigest = MessageDigest.getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)  {
                    h = "0$h"
                }
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}