package com.horizam.skbhub.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jdars.h20.databinding.DashboardProductItemBinding
import com.jdars.h20.databinding.NewsItemBinding



class DashBoardProductAdapter(): RecyclerView.Adapter<DashBoardProductAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: DashboardProductItemBinding = DashboardProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 6
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun updateList(list: ArrayList<Brand>){
//        brandList = list
//        notifyDataSetChanged()
//    }

    inner class Holder(
        var binding: DashboardProductItemBinding
    ):RecyclerView.ViewHolder(binding.root){



    }
}