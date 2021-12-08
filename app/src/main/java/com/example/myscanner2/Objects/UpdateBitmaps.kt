package com.example.myscanner2.Objects

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.myscanner2.Adapters.FileImagesAdapter
import com.example.myscanner2.Classes.FileImages

object UpdateBitmaps {
    var bitmaps: MutableList<FileImages> = mutableListOf()

    @SuppressLint("StaticFieldLeak")
    var fileImagesAdapter: FileImagesAdapter? =null


    fun initialise(activity: FragmentActivity) {
        bitmaps = mutableListOf()
        fileImagesAdapter = FileImagesAdapter(activity, bitmaps)
    }

    fun addBitmap(bitmap:Bitmap, uri: Uri?){
        bitmaps.add(FileImages(bitmap,uri))
        Log.i("MainActivity","Notifying")
        fileImagesAdapter?.notifyDataSetChanged()
    }


}