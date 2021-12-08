package com.example.myscanner2.Fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myscanner2.Objects.PdfConverter
import com.example.myscanner2.Objects.UpdateBitmaps
import com.example.myscanner2.R
import com.example.myscanner2.databinding.FragmentCreateDocumentBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateDocumentFragment : Fragment(R.layout.fragment_create_document) {

    private lateinit var binding: FragmentCreateDocumentBinding

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var captureImageLauncher: ActivityResultLauncher<Void>
    private lateinit var gallerImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var contract:ActivityResultContract<Any?,Any?>
    private var TAG="MainActivity"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentCreateDocumentBinding.bind(view)

        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter= UpdateBitmaps.fileImagesAdapter


        binding.createPdf.setOnClickListener {
            binding.pb.visibility=View.VISIBLE
            if(binding.fileName.text.toString().length==0){
                Toast.makeText(requireContext(),"Please enter file name before saving",Toast.LENGTH_SHORT).show()
            }else if(UpdateBitmaps.bitmaps.size==0){
                Toast.makeText(requireContext(),"Please capture an image before saving",Toast.LENGTH_SHORT).show()
            }else if(PdfConverter.createpdfAndSaveToInternalStorage(requireContext(), UpdateBitmaps.bitmaps,binding.fileName.text.toString())){
                requireActivity().supportFragmentManager.popBackStack()
                Toast.makeText(requireContext(),
                    "Pdf Saved to internal Storage !!!!!!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),
                    "Pdf with the same name already exists", Toast.LENGTH_SHORT).show()
            }
            binding.pb.visibility=View.GONE
        }

        captureImageLauncher=registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            UpdateBitmaps.addBitmap(it,null)
        }

        gallerImageLauncher=registerForActivityResult(ActivityResultContracts
                .StartActivityForResult()) {

            var data = it.data
            if (it.resultCode == RESULT_OK && data != null) {
                if (data.data != null) {
                    Log.i("Image", "Only 1st")
                    cropActivityResultLauncher.launch(CropImage.activity(data.data).getIntent(requireContext()))
                } else {
                    Log.i("Image", "Only 2nd")
                    data.clipData?.let {
                        for (i in 0 until it.itemCount) {
                            cropActivityResultLauncher.launch(CropImage.activity(it.getItemAt(i).uri).getIntent(requireContext()))
                        }
                    }
                }
            }
        }

        cropActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            Log.i(TAG,"inside the launcher")
            try {
                var data = it.data!!
                if (it.resultCode == Activity.RESULT_OK && data != null) {
                    var result = CropImage.getActivityResult(data)
                    var uri = result.uri
                    var bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    UpdateBitmaps.addBitmap(bitmap, uri)
                    Log.i(TAG, "Fine till here")
                } else {
                    Log.i(TAG, "Can't get inside to return bitmap")
                }
            }catch(e:Exception){
                Log.i("MainActivity","Error")
                e.printStackTrace()
            }
        })


        binding.capturebtn.setOnClickListener {
            var intent = CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON).getIntent(requireContext())
            cropActivityResultLauncher.launch((intent))
        }

        binding.galleryBtn.setOnClickListener {
            var intent=Intent()
            intent.type="images/jpg"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.action=Intent.ACTION_GET_CONTENT
            Intent.createChooser(intent,"Choose app")
            gallerImageLauncher.launch(Intent.createChooser(intent,"Choose app"))
        }
    }

}