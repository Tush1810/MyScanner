package com.example.myscanner2.Objects

import android.graphics.Bitmap
import com.example.myscanner2.Classes.InternalPdfFiles
import java.io.File

object ListOfFiles {

    private lateinit var list:List<InternalPdfFiles>

    fun initialise(list:List<InternalPdfFiles>){
        this.list=list
    }

    fun getFile(position:Int): File {
        return list[position].file
    }

    fun getName(position:Int): String {
        return list[position].name
    }

    fun getBitmaps(position:Int): MutableList<Bitmap> {
        return list[position].bitmaps
    }
}