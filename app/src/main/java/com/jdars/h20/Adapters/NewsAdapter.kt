package com.horizam.skbhub.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdars.h20.R
import com.jdars.h20.databinding.NewsItemBinding



class NewsAdapter(
    private var newsList: ArrayList<String>
): RecyclerView.Adapter<NewsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: NewsItemBinding = NewsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<String>){
        newsList = list
        notifyDataSetChanged()
    }

    inner class Holder(
        var binding: NewsItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int) {
            val image = newsList[position]
            Glide.with(itemView.context).load(image)
                .placeholder(R.drawable.shopkart_placeholder)
                .into(binding.ivNews)
        }
    }
}