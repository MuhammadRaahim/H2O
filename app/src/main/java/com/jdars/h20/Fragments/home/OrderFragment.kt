package com.jdars.h20.Fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.skbhub.Adapters.OrderAdapter
import com.horizam.skbhub.Adapters.ProductAdapter
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.CallBacks.OnItemDeleteListener
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentOderBinding
import com.jdars.h20.models.Order
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.Utils.BaseUtils
import java.lang.Exception


class OrderFragment : Fragment(),OnItemDeleteListener {

    private lateinit var binding: FragmentOderBinding
    private lateinit var adapter: OrderAdapter
    private lateinit var oderList: ArrayList<Order>
    private lateinit var orderReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentFirebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOderBinding.inflate(layoutInflater)

        initViews()
        setOrderRecyclerView()
        getOrders()

        return binding.root
    }

    private fun initViews() {
        oderList = ArrayList()
        db = Firebase.firestore
        orderReference = db.collection(Constants.PRODUCT_DATABASE_ROOT)
        auth = FirebaseAuth.getInstance()
        currentFirebaseUser = auth.currentUser!!
    }

    private fun setOrderRecyclerView() {
        binding.rvOrders.layoutManager =  LinearLayoutManager(requireActivity(),  RecyclerView.VERTICAL, false)
        adapter = OrderAdapter(oderList,this)
        binding.rvOrders.adapter = adapter
    }

    private fun getOrders() {
        BaseUtils.showProgressbar(requireContext())
        oderList.clear()
        db.collection(Constants.ODER_DATABASE_ROOT).get().addOnSuccessListener { documentSnapshots ->
            for (documentSnapshot in documentSnapshots) {
                val order  = documentSnapshot.toObject(Order::class.java)
                try {
                    if (order.user_id == currentFirebaseUser.uid){
                        oderList.add(order)
                    }
                } catch (ex: Exception) {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,ex.message.toString(),true)
                }
            }
            if (oderList.isNotEmpty()){
                adapter.updateList(oderList)
                binding.rvOrders.visibility = View.VISIBLE
                binding.tvNoProduct.visibility = View.GONE
                BaseUtils.hideProgressbar()
            }else{
                binding.rvOrders.visibility = View.GONE
                binding.tvNoProduct.visibility = View.VISIBLE
                BaseUtils.hideProgressbar()
            }

        }.addOnFailureListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    override fun <T> onItemClick(item: T) {
        if(item is Order){
            db.collection(Constants.ODER_DATABASE_ROOT)
                .document(item.id)
            .delete()
                .addOnSuccessListener {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,"oder delete!",false)
                    getOrders()
                }
                .addOnFailureListener {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
                }
        }
    }

}