package com.example.myscanner2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myscanner2.Classes.InternalPdfFiles
import com.example.myscanner2.R
import com.example.myscanner2.databinding.DocumentViewBinding

class DocumentAdapter() : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    lateinit var context:Context
    private lateinit var list:List<InternalPdfFiles>
    private lateinit var mlistener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position:Int,binding:DocumentViewBinding)
    }

    fun setOnClickListener(listener: OnItemClickListener){
        mlistener=listener
    }

    constructor(c:Context,lis:List<InternalPdfFiles>):this(){
        this.context=c
        this.list=lis
    }
    class ViewHolder(var binding:DocumentViewBinding,listener: OnItemClickListener):RecyclerView.ViewHolder(binding.root) {
        init{
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition,binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.document_view,parent,false)
        return ViewHolder(DocumentViewBinding.bind(view),mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var pdf= list[position]
        holder.binding.imageView.setImageBitmap(pdf.bitmaps[0])
        holder.binding.textView.text=pdf.name
    }

    override fun getItemCount(): Int {
        return list.size
    }
}