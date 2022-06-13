package com.horizam.skbhub.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.CallBacks.CartItemCalculationCallBAck
import com.jdars.h20.CallBacks.OnItemClickListener
import com.jdars.h20.R
import com.jdars.h20.databinding.NewsItemBinding
import com.jdars.h20.databinding.ProductItemBinding
import com.jdars.h20.models.CartItem
import com.jdars.h20.models.Product


class CartItemAdapter(
    private var cartItemList: ArrayList<CartItem>,
    private var cartItemCalculationCallBAck: CartItemCalculationCallBAck
): RecyclerView.Adapter<CartItemAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ProductItemBinding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<CartItem>){
        cartItemList = list
        notifyDataSetChanged()
    }

    inner class Holder(
        var binding: ProductItemBinding
    ):RecyclerView.ViewHolder(binding.root){

        var total:Int = 0
        var shipping:Int = 0
        var subtotal:Int = 0

        fun bind(position: Int) {
            val cartItem = cartItemList[position]

            Glide.with(itemView.context).load(cartItem.image)
                .placeholder(R.drawable.shopkart_placeholder)
                .into(binding.ivProductItemImage)
            binding.ivProductItemTitle.text = cartItem.title
            binding.ivProductItemPrice.text = "RS: ${cartItem.price}/="

            subtotal += cartItem.price.toInt()
            shipping += 20
            total = subtotal + shipping

            cartItemCalculationCallBAck.onCalculation(total,subtotal,shipping)

        }
    }
}