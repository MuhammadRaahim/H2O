package com.horizam.skbhub.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.CallBacks.OnItemClickListener
import com.jdars.h20.R
import com.jdars.h20.databinding.NewsItemBinding
import com.jdars.h20.databinding.ProductItemBinding
import com.jdars.h20.models.Product


class ProductAdapter(
    private var productList: ArrayList<Product>,
    var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<ProductAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ProductItemBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Product>){
        productList = list
        notifyDataSetChanged()
    }

    inner class Holder(
        var binding: ProductItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int) {
            val product = productList[position]

            Glide.with(itemView.context).load(product.image)
                .placeholder(R.drawable.shopkart_placeholder)
                .into(binding.ivProductItemImage)
            binding.ivProductItemTitle.text = product.title
            binding.ivProductItemPrice.text = "RS: ${product.price}/="

            itemView.setOnClickListener {
               onItemClickListener.onItemClick(product)
            }
        }
    }
}