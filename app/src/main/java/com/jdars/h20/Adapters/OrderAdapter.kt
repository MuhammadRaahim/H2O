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
import com.jdars.h20.CallBacks.OnItemDeleteListener
import com.jdars.h20.R
import com.jdars.h20.databinding.NewsItemBinding
import com.jdars.h20.databinding.ProductItemBinding
import com.jdars.h20.models.Order
import com.jdars.h20.models.Product


class OrderAdapter(
    private var orderList: ArrayList<Order>,
    private var onItemDeleteListener: OnItemDeleteListener
): RecyclerView.Adapter<OrderAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ProductItemBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Order>){
        orderList = list
        notifyDataSetChanged()
    }

    inner class Holder(
        var binding: ProductItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int) {
            val order = orderList[position]
            Glide.with(itemView.context).load(order.image)
                .placeholder(R.drawable.shopkart_placeholder)
                .into(binding.ivProductItemImage)
            binding.ivProductItemTitle.text = order.title
            binding.ivProductItemPrice.text = "RS: ${order.items.price}/="

            binding.ivDeleteProduct.setOnClickListener {
                onItemDeleteListener.onItemClick(order)
            }
        }
    }
}