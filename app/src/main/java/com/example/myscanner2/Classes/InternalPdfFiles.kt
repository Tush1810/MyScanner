package com.example.myscanner2.Classes

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.File

data class InternalPdfFiles(var bitmaps:MutableList<Bitmap>, var name:String,var file: File)