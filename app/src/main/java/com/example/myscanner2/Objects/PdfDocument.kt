package com.example.myscanner2.Objects

import android.graphics.Bitmap
import com.example.myscanner2.Classes.Pdf

object PdfDocument {
    private lateinit var pdf: MutableList<Pdf>

    fun initialize(){
        pdf= mutableListOf()
    }

    fun addBitmap(bitmap: Bitmap){
        pdf.add(Pdf(bitmap))
    }

    fun getPdf()=pdf
}