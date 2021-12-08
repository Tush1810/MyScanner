package com.example.myscanner2.Objects

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myscanner2.Classes.FileImages
import com.example.myscanner2.Classes.InternalPdfFiles
import com.example.myscanner2.Files.sdk29andup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


object PdfConverter {

    fun createpdfAndSaveToInternalStorage(context: Context, images: MutableList<FileImages>, fileName: String):Boolean{
        var i=0
        var pdfDocument=PdfDocument()
        if(findPdfFromInternalStorageWithSameName(context,"$fileName.pdf")) return false
        images.forEach {
            i++
            var bitmap=it.bitmap

            var myPageInfo:PdfDocument.PageInfo=PdfDocument.PageInfo
                    .Builder(bitmap.width, bitmap.height, i).create()

            var page=pdfDocument.startPage(myPageInfo)
            page.canvas.drawBitmap(bitmap, 0.toFloat(), 2.toFloat(), null)
            pdfDocument.finishPage(page)
        }
        Log.i("MainActivity","Pdf created")
        savePdfToInternalStorage(context, fileName, pdfDocument)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createpdfAndSaveToExternalStorage(context:Context, name:String, images:List<Bitmap>):Boolean{
        var i=0
        var pdfDocument=PdfDocument()
        images.forEach {
            i++
            var bitmap=it

            var myPageInfo:PdfDocument.PageInfo=PdfDocument.PageInfo
                .Builder(bitmap.width, bitmap.height, i).create()

            var page=pdfDocument.startPage(myPageInfo)
            page.canvas.drawBitmap(bitmap, 0.toFloat(), 2.toFloat(), null)
            pdfDocument.finishPage(page)
        }

        var downloads: Uri = sdk29andup {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }?:MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val contentValues=ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME,name)
            put(MediaStore.Downloads.MIME_TYPE,"pdf")
        }

        return try{
            context.contentResolver.insert(downloads,contentValues)?.also{
                context.contentResolver.openOutputStream(it).use { openOutputStream->
                    pdfDocument.writeTo(openOutputStream)
                }
            }?:throw java.lang.Exception("Could not open mediastore.downloads")
            true
        }catch(e:Exception){
            false
        }
    }

    private fun savePdfToInternalStorage(context: Context, fileName: String, pdfDocument: PdfDocument):Boolean{
        return try{
            Log.i("MainActivity","Inside try")
            context.openFileOutput("$fileName.pdf", MODE_PRIVATE).use {
                pdfDocument.writeTo(it)
                Log.i("MainActivity","Pdf Created with name $fileName.pdf")
                true
            }
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun loadPdfFromInternalStorage(context: Context):List<InternalPdfFiles>{
        try {
            return withContext(Dispatchers.IO) {
                val files = context.filesDir.listFiles()
                files?.filter { it.canRead() && it.isFile }?.map { file ->
                    Log.i("MainActivity", "${file.name}")
                    val renderer: PdfRenderer = PdfRenderer(ParcelFileDescriptor
                            .open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                    var bm = mutableListOf<Bitmap>()
                    var bitmap: Bitmap? = null

                    Log.i("Page count", renderer.pageCount.toString())

                    for (i in 0..renderer.pageCount - 1) {
                        val page: PdfRenderer.Page = renderer.openPage(i)
                        val width: Int = page.width
                        val height: Int = page.height
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bm.add(bitmap)
                        page.close()
                    }
                    renderer.close()
                    InternalPdfFiles(bm, file.name, file)
                } ?: listOf()
            }
        }catch (e:Exception){
            Log.i("MainActivity","Error")
            e.printStackTrace()
            return listOf()
        }
    }

    private fun findPdfFromInternalStorageWithSameName(context: Context, name:String):Boolean{
        val files= context.filesDir.listFiles()
        files.forEach {
            if(it.name.equals("$name")) {
                return true
            }
        }
        Log.i("MainActivity","returning false")
        return false
    }


    fun deletePdfFromInternalStorage(context:Context,fileName: String):Boolean{
        return try{
            context.deleteFile(fileName)
        }catch(e:Exception){
            e.printStackTrace()
            false
        }
    }

    @SuppressLint("Range")
    fun addPdfToInternalStorage(context:Context, uri:Uri): InternalPdfFiles? {
        try {
            var pdf:InternalPdfFiles?=null
            val cursor = context.contentResolver.query(uri, arrayOf<String>(OpenableColumns.DISPLAY_NAME,
                    OpenableColumns.SIZE), null, null, null)?.apply {
                Log.i("Inside","Cursor optained")
                this.moveToFirst()
                var displayName=this.getString(this.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i("Name",displayName)
                var file=File(context.filesDir.toString()+"/"+displayName)
                FileOutputStream(file)?.use{outputStream ->
                    context.contentResolver.openInputStream(uri)?.use{
                        var buffer=ByteArray(1024)
                        var read=it.read(buffer)
                        while(read!=-1){
                            outputStream.write(buffer,0,read)
                            read=it.read(buffer)
                        }
                        Log.i("MainActivity","File created")
                    }
                }

                val renderer: PdfRenderer = PdfRenderer(ParcelFileDescriptor
                        .open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                var bm = mutableListOf<Bitmap>()
                var bitmap: Bitmap? = null

                Log.i("Page count", renderer.pageCount.toString())

                for (i in 0..renderer.pageCount - 1) {
                    val page: PdfRenderer.Page = renderer.openPage(i)
                    val width: Int = page.width
                    val height: Int = page.height
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bm.add(bitmap)
                    page.close()
                }
                renderer.close()
                return InternalPdfFiles(bm, file.name, file)
            }
            return null
        }catch(e:Exception){
            Log.i("MainActivity","Error.")
            e.printStackTrace()
            return null
        }
        return null
    }
}