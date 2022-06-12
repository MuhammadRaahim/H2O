package com.jdars.h20.Fragments.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentProductDetailsBinding
import com.jdars.h20.models.Product


class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var product: Product
    private lateinit var currentFirebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        product = requireArguments().getSerializable(Constants.PRODUCT) as Product
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
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
            }
        }
    }


}