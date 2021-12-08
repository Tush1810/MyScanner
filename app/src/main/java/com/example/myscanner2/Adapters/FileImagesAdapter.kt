package com.example.myscanner2.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.myscanner2.Classes.FileImages
import com.example.myscanner2.R
import com.example.myscanner2.databinding.FileImageBinding

class FileImagesAdapter() : RecyclerView.Adapter<FileImagesAdapter.ViewHolder>() {

    private lateinit var context:Context
    private lateinit var list:List<FileImages>

    constructor(context:Context,list:List<FileImages>) : this(){
        this.context=context
        this.list=list
    }

    class ViewHolder(var binding: FileImageBinding,var done:Boolean) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.file_image,parent,false)
        return ViewHolder(FileImageBinding.bind(view),false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.image.setImageBitmap(list.get(position).bitmap)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}