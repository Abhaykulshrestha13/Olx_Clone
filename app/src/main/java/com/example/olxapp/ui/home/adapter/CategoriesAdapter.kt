package com.example.olxapp.ui.home.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olxapp.R
import com.example.olxapp.model.CategoriesModel
import de.hdodenhof.circleimageview.CircleImageView

class CategoriesAdapter(private var categotieslist: MutableList<CategoriesModel>,var itemClickListener: ItemClickListener):RecyclerView.Adapter<CategoriesAdapter.ViewHolder>(){
    private lateinit var context: Context
    class  ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle:TextView = itemView.findViewById(R.id.tvTitle)
        val imageView:CircleImageView = itemView.findViewById(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.adapter_categories,parent,false)
        return ViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = categotieslist[position].key
        Glide.with(context)
            .load(categotieslist[position].image)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return categotieslist.size
    }

    fun updateList(temp: MutableList<CategoriesModel>) {
        categotieslist = temp
        notifyDataSetChanged()
    }

    interface ItemClickListener{
        fun onItemClick(position: Int)
    }
}