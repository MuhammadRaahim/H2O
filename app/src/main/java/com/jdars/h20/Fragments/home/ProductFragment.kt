package com.jdars.h20.Fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.skbhub.Adapters.ProductAdapter
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.CallBacks.OnItemClickListener
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentProductBinding
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.Utils.BaseUtils
import java.lang.Exception


class ProductFragment : Fragment(),OnItemClickListener {

    private lateinit var binding: FragmentProductBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: ArrayList<Product>
    private lateinit var productReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentFirebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)

        initViews()
        setProductRecyclerView()
        getProducts()
        setClickListeners()

        return binding.root
    }

    private fun initViews() {
        productList = ArrayList()
        db = Firebase.firestore
        productReference = db.collection(Constants.PRODUCT_DATABASE_ROOT)
        auth = FirebaseAuth.getInstance()
        currentFirebaseUser = auth.currentUser!!
    }

    private fun setClickListeners() {
        binding.apply {
            ivAdd.setOnClickListener{
                findNavController().navigate(R.id.addProductFragment)
            }
        }
    }

    private fun setProductRecyclerView() {
        binding.rvProducts.layoutManager =  LinearLayoutManager(requireActivity(),  RecyclerView.VERTICAL, false)
        adapter = ProductAdapter(productList,this)
        binding.rvProducts.adapter = adapter
    }

    private fun getProducts() {
        BaseUtils.showProgressbar(requireContext())
        productList.clear()
        db.collection(Constants.PRODUCT_DATABASE_ROOT).get().addOnSuccessListener { documentSnapshots ->
            for (documentSnapshot in documentSnapshots) {
                val product  = documentSnapshot.toObject(Product::class.java)
                try {
                    if (product.user_id == currentFirebaseUser.uid){
                            productList.add(product)
                        }
                } catch (ex: Exception) {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,ex.message.toString(),true)
                }
            }
            if (productList.isNotEmpty()){
                adapter.updateList(productList)
                binding.rvProducts.visibility = View.VISIBLE
                binding.tvNoProduct.visibility = View.GONE
                BaseUtils.hideProgressbar()
            }else{
                binding.rvProducts.visibility = View.GONE
                binding.tvNoProduct.visibility = View.VISIBLE
                BaseUtils.hideProgressbar()
            }

        }.addOnFailureListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    override fun <T> onItemClick(item: T) {
        if (item is Product){
            val bundle =  bundleOf(Constants.PRODUCT to item)
            findNavController().navigate(R.id.productDetailsFragment,bundle)
        }
    }
}