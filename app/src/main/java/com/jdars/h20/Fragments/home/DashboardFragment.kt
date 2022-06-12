package com.jdars.h20.Fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.skbhub.Adapters.DashBoardProductAdapter
import com.horizam.skbhub.Adapters.NewsAdapter
import com.jdars.h20.R
import com.jdars.h20.databinding.ActivityMainBinding
import com.jdars.h20.databinding.FragmentDasboardBinding


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDasboardBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var productAdapter: DashBoardProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDasboardBinding.inflate(layoutInflater)
        setNewsRecyclerView()
        setProductRecyclerView()

        return binding.root
    }

    private fun setNewsRecyclerView() {
        binding.rvNewsAndServices.layoutManager =  LinearLayoutManager(requireActivity(),  RecyclerView.HORIZONTAL, false)
        newsAdapter = NewsAdapter()
        binding.rvNewsAndServices.adapter = newsAdapter
    }

    private fun setProductRecyclerView() {
        binding.rvProducts.layoutManager =  GridLayoutManager(requireActivity(),2,  RecyclerView.VERTICAL, false)
        productAdapter = DashBoardProductAdapter()
        binding.rvProducts.adapter = productAdapter
    }
}