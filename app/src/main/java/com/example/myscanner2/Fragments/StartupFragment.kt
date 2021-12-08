package com.example.myscanner2.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myscanner2.Adapters.DocumentAdapter
import com.example.myscanner2.Classes.InternalPdfFiles
import com.example.myscanner2.Objects.ListOfFiles
import com.example.myscanner2.Objects.PdfConverter
import com.example.myscanner2.Objects.PdfDocument
import com.example.myscanner2.Objects.UpdateBitmaps
import com.example.myscanner2.R
import com.example.myscanner2.databinding.DocumentViewBinding
import com.example.myscanner2.databinding.FragmentStartupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StartupFragment : Fragment(R.layout.fragment_startup) {

    private lateinit var binding: FragmentStartupBinding
    lateinit var list:MutableList<InternalPdfFiles>
    lateinit var createDocumentFragment: CreateDocumentFragment
    private lateinit var c: Context
    private lateinit var documentAdapter:DocumentAdapter

    private lateinit var getpdfLauncher: ActivityResultLauncher<Array<String>>;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentStartupBinding.bind(view)
        c=requireContext()

        val x=ActivityResultContracts.OpenDocument();

        getpdfLauncher=registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if(it==null){
                Log.i("Uri","= null")
                return@registerForActivityResult
            }
            var pdf=PdfConverter.addPdfToInternalStorage(c,it)
            if(pdf!=null){
                list.add(pdf)
                documentAdapter.notifyDataSetChanged()
                Toast.makeText(c,"Document added to internal files",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(c,"Error occured",Toast.LENGTH_SHORT).show()
            }
        }

        initialise()

        Log.i("MainActivity","Continued")




        binding.addBtn.setOnClickListener {
            Log.i("MainActivity","Clicked")
            createDocumentFragment= CreateDocumentFragment()
            parentFragmentManager.beginTransaction().apply{
                binding.progressBar2.visibility=View.GONE
                this.replace(R.id.mainFl,createDocumentFragment)
                this.addToBackStack("abc")
                UpdateBitmaps.initialise(requireActivity())
                this.commit()
            }
        }

        binding.addBtn2.setOnClickListener{
            Log.i("MainActivity","Clicked")
            getpdfLauncher.launch(arrayOf("application/pdf"))
        }
    }

    fun initialise():Unit{
        GlobalScope.launch {
            binding.progressBar2.visibility=View.VISIBLE
            list= PdfConverter.loadPdfFromInternalStorage(requireContext()).toMutableList()
            ListOfFiles.initialise(list)
            Log.i("MainActivity","Fine till here size=${list.size}")
            documentAdapter= DocumentAdapter(requireContext(),list)
            Log.i("MainActivity","Fine till here1 size=${list.size}")
            launch(Dispatchers.Main){
                try {
                    binding.recyclerView.layoutManager = LinearLayoutManager(c)
                    documentAdapter.setOnClickListener(object: DocumentAdapter.OnItemClickListener{
                        override fun onItemClick(position: Int, binding: DocumentViewBinding) {
                            PdfDocument.initialize()

                            list[position].bitmaps.forEach {
                                PdfDocument.addBitmap(it)
                            }
                            var displayFragment=DisplayFragment()
                            var bundle=Bundle()
                            bundle.putInt("Position",position)
                            bundle.putString("Name",list[position].name)
                            displayFragment.arguments = bundle
                            requireActivity().supportFragmentManager.beginTransaction().apply {
                                this.replace(R.id.mainFl,displayFragment)
                                this.addToBackStack("qwerty")
                                this.commit()
                            }
                        }
                    })
                    binding.recyclerView.adapter=documentAdapter
                    binding.progressBar2.visibility=View.GONE
                }catch (e:Exception){
                    Log.i("MainActivity error",e.toString())
                }
            }
        }
    }
}