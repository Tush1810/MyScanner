package com.example.myscanner2.Fragments

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myscanner2.Adapters.PdfAdapter
import com.example.myscanner2.Files.sdk29andup
import com.example.myscanner2.Objects.ListOfFiles
import com.example.myscanner2.Objects.PdfConverter
import com.example.myscanner2.Objects.PdfDocument
import com.example.myscanner2.R
import com.example.myscanner2.databinding.FragmentDisplayBinding
import com.example.myscanner2.databinding.PdfViewBinding
import kotlin.properties.Delegates


class DisplayFragment : Fragment(R.layout.fragment_display) {

    private lateinit var binding: FragmentDisplayBinding
    private var position by Delegates.notNull<Int>()


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentDisplayBinding.bind(view)

        position= this.arguments?.getInt("Position") ?: -1
        var pdfAdapter=PdfAdapter(requireContext(),PdfDocument.getPdf())

        Toast.makeText(requireContext(),"Click on any page to opetain OCR !!!",Toast.LENGTH_SHORT).show()

        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=pdfAdapter

        pdfAdapter.setOnItemClickListener(object:PdfAdapter.onItemClickListener{
            override fun onItemClick(position:Int,binding:PdfViewBinding) {
                var bundle=Bundle()
                bundle.putInt("Position",position)
                var fragment=OcrFragment()
                fragment.arguments=bundle

                requireActivity().supportFragmentManager.beginTransaction().apply {
                    this.replace(R.id.mainFl,fragment)
                    this.addToBackStack("ocr")
                    this.commit()
                }
            }
        })


        binding.btn.setOnClickListener{
            if(PdfConverter.createpdfAndSaveToExternalStorage(requireContext(),ListOfFiles.getName(position),ListOfFiles.getBitmaps(position))){
                Toast.makeText(requireContext(),"Successfully Saved!!!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}