package com.jdars.h20.Fragments.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.skbhub.Adapters.DashBoardProductAdapter
import com.horizam.skbhub.Adapters.NewsAdapter
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.CallBacks.OnItemClickListener
import com.jdars.h20.R
import com.jdars.h20.databinding.ActivityMainBinding
import com.jdars.h20.databinding.FragmentDasboardBinding
import com.jdars.h20.models.News
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.CallBacks.DrawerHandler
import com.jdars.shared_online_business.Utils.BaseUtils
import java.lang.Exception


class DashboardFragment : Fragment(),OnItemClickListener {

    private lateinit var binding: FragmentDasboardBinding
    private lateinit var drawerHandlerCallback: DrawerHandler

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var productAdapter: DashBoardProductAdapter

    private lateinit var productList: ArrayList<Product>
    private lateinit var newsList: ArrayList<String>

    private lateinit var productReference: CollectionReference
    private lateinit var newsReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentFirebaseUser: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDasboardBinding.inflate(layoutInflater)

        initViews()
        setClickListeners()
        setNewsRecyclerView()
        setProductRecyclerView()
        getData()

        return binding.root
    }

    private fun getData() {
        BaseUtils.showProgressbar(requireContext())
        db.collection(Constants.NEWS_DATABASE_ROOT).get().addOnSuccessListener { documentSnapshots ->
            for (documentSnapshot in documentSnapshots) {
                val product = documentSnapshot.toObject(News::class.java)
                if (product.news != null){
                    newsAdapter.updateList(product.news!!)
                }
                getProducts()
            }
        }.addOnFailureListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    private fun getProducts() {
        productList.clear()
        db.collection(Constants.PRODUCT_DATABASE_ROOT).get().addOnSuccessListener { documentSnapshots ->
            for (documentSnapshot in documentSnapshots) {
                val product  = documentSnapshot.toObject(Product::class.java)
                try {
                    if (product.user_id != currentFirebaseUser.uid){
                        productList.add(product)
                    }
                } catch (ex: Exception) {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,ex.message.toString(),true)
                }
            }
            if (productList.isNotEmpty()){
                productAdapter.updateList(productList)
                BaseUtils.hideProgressbar()
            }else{
                binding.rvProducts.visibility = View.GONE
                BaseUtils.hideProgressbar()
            }

        }.addOnFailureListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    private fun initViews() {
        productList = ArrayList()
        newsList = ArrayList()
        db = Firebase.firestore
        productReference = db.collection(Constants.PRODUCT_DATABASE_ROOT)
        productReference = db.collection(Constants.NEWS_DATABASE_ROOT)
        auth = FirebaseAuth.getInstance()
        currentFirebaseUser = auth.currentUser!!

        binding.swipeRefresh.setOnRefreshListener{
            getData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivOpenDrawer.setOnClickListener {
                drawerHandlerCallback.openDrawer()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        drawerHandlerCallback = context as DrawerHandler
    }

    private fun setNewsRecyclerView() {
        binding.rvNewsAndServices.layoutManager =  LinearLayoutManager(requireActivity(),  RecyclerView.HORIZONTAL, false)
        newsAdapter = NewsAdapter(newsList)
        binding.rvNewsAndServices.adapter = newsAdapter
    }

    private fun setProductRecyclerView() {
        binding.rvProducts.layoutManager =  GridLayoutManager(requireActivity(),2,  RecyclerView.VERTICAL, false)
        productAdapter = DashBoardProductAdapter(productList,this)
        binding.rvProducts.adapter = productAdapter
    }

    override fun <T> onItemClick(item: T) {
        if (item is Product){
            val bundle =  bundleOf(Constants.PRODUCT to item)
            findNavController().navigate(R.id.productDetailsFragment,bundle)
        }
    }
}