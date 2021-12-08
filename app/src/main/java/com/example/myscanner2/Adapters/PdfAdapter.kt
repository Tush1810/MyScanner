package com.example.myscanner2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myscanner2.Classes.Pdf
import com.example.myscanner2.R
import com.example.myscanner2.databinding.PdfViewBinding

class PdfAdapter(): RecyclerView.Adapter<PdfAdapter.ViewHolder>() {
    private lateinit var context:Context
    private lateinit var list:MutableList<Pdf>
    constructor(c:Context,l:MutableList<Pdf>):this(){
        this.context=c
        this.list=l
    }

    private lateinit var mlistener:onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position:Int,binding: PdfViewBinding)
    }

    public fun setOnItemClickListener(listener:onItemClickListener){
        mlistener=listener
    }

    public class ViewHolder(var binding:PdfViewBinding,listener: onItemClickListener):RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener{
                listener.onItemClick(adapterPosition,binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.pdf_view,parent,false)
        return ViewHolder(PdfViewBinding.bind(view),mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.image.setImageBitmap(list.get(position).bitmap)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}