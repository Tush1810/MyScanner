package com.example.myscanner2.Fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myscanner2.Objects.PdfDocument
import com.example.myscanner2.R
import com.example.myscanner2.databinding.FragmentOcrBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrFragment : Fragment(R.layout.fragment_ocr) {

    private lateinit var binding: FragmentOcrBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentOcrBinding.bind(view)
        var bundle=this.arguments
        var position:Int=bundle?.get("Position") as Int

        var bitmap=PdfDocument.getPdf()[position].bitmap
        getTextFromImage(bitmap)

        binding.btnCopy.setOnClickListener{
            var clipboard:ClipboardManager= requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("OCR",binding.text.text.toString()))
            Toast.makeText(requireContext(),"Copied to clipboard !!!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTextFromImage(bitmap: Bitmap) {
        var recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        var image= InputImage.fromBitmap(bitmap,0)

        recognizer.process(image).addOnSuccessListener {result->
            Log.i("MainActivity","Inside the success listener")
            var sb=java.lang.StringBuilder()
            for (block in result.textBlocks) {
                val blockText = block.text
                val blockCornerPoints = block.cornerPoints
                val blockFrame = block.boundingBox
                for (line in block.lines) {
                    if(line.text.isNotEmpty()){
                        sb.append(line.text+"\n")
                    }
                }
            }
            Log.i("MainActivity",sb.toString())
            binding.text.text = sb.toString()
        }.addOnFailureListener {
            Log.i("MainActivity","Inside the failure listener")
        }
    }
}