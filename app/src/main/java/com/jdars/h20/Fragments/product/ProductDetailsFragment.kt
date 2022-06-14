package com.jdars.h20.Fragments.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.Activities.CartListActivity
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentProductDetailsBinding
import com.jdars.h20.models.CartItem
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.Utils.BaseUtils


class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var product: Product
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var cartReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)

        initViews()
        setClickListeners()

        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            buttonAddToCart.setOnClickListener{
                addToCart()
            }
            buttonGoToCart.setOnClickListener {
                startActivity(Intent(requireContext(),CartListActivity::class.java))
            }
        }
    }

    private fun checkProductExist(){
        BaseUtils.showProgressbar(requireContext())
        db.collection(Constants.CART_DATABASE_ROOT)
                .whereEqualTo("userId", currentFirebaseUser.uid)
                .whereEqualTo("productId", product.id)
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        binding.buttonAddToCart.visibility = View.GONE
                        binding.buttonGoToCart.visibility = View.VISIBLE
                    } else {
                        binding.buttonAddToCart.visibility = View.VISIBLE
                        binding.buttonGoToCart.visibility = View.GONE
                    }
                    BaseUtils.hideProgressbar()
                }
                .addOnFailureListener {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
                }
    }

    private fun addToCart() {
        BaseUtils.showProgressbar(requireContext())
        val ref = cartReference.document()
        val cartItem = CartItem(
            currentFirebaseUser.uid,
            product.user_id,
            product.id,
            product.title,
            product.price,
            product.image,
            "1",
            product.stock_quantity,
            ref.id
        )
        uploadProduct(cartItem)
    }

    private fun uploadProduct(cartItem: CartItem) {
        val ref = cartReference.document(cartItem.id)
        ref.set(cartItem).addOnSuccessListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,"Cart Add Successfully !",false)
            binding.buttonAddToCart.visibility = View.GONE
            binding.buttonGoToCart.visibility = View.VISIBLE
        }.addOnFailureListener{
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    private fun initViews() {
        product = requireArguments().getSerializable(Constants.PRODUCT) as Product
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        db = Firebase.firestore
        cartReference = db.collection(Constants.CART_DATABASE_ROOT)
        firebaseStorage = FirebaseStorage.getInstance()
        setData()
    }

    private fun setData() {
        binding.apply {
            Glide.with(requireContext()).load(product.image)
                .placeholder(R.drawable.shopkart_placeholder)
                .into(binding.ivProductDetailImage)
            tvProductDetailTitle.text = product.title
            tvProductDetailPrice.text = "RS ${product.price}/="
            ivProductDetailDescValue.text = product.description
            ivProductDetailStockQuantityValue.text = product.stock_quantity
            if (product.user_id == currentFirebaseUser.uid){
                buttonAddToCart.visibility = View.GONE
                buttonGoToCart.visibility = View.GONE
            }else{
                checkProductExist()
            }
        }
    }


}