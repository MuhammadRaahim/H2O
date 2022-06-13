package com.jdars.h20.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.skbhub.Adapters.CartItemAdapter
import com.horizam.skbhub.Adapters.ProductAdapter
import com.horizam.skbhub.Utils.Constants
import com.horizam.skbhub.Utils.Constants.Companion.ODER_DATABASE_ROOT
import com.jdars.h20.CallBacks.CartItemCalculationCallBAck
import com.jdars.h20.R
import com.jdars.h20.databinding.ActivityCartListBinding
import com.jdars.h20.databinding.FragmentProductDetailsBinding
import com.jdars.h20.models.CartItem
import com.jdars.h20.models.Order
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.Utils.BaseUtils
import java.lang.Exception

class CartListActivity : AppCompatActivity(),CartItemCalculationCallBAck {

    private lateinit var binding: ActivityCartListBinding
    private lateinit var adapter: CartItemAdapter
    private lateinit var cartList: ArrayList<CartItem>
    private lateinit var cartReference: CollectionReference
    private lateinit var oderReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentFirebaseUser: FirebaseUser
    var total: Int = 0
    var subtotal: Int = 0
    var shiping: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setACartItemRecyclerView()
        getCartItems()
        setClickListeners()
    }

    private fun setClickListeners() {
        BaseUtils.showProgressbar(this)
        binding.buttonCheckout.setOnClickListener {
            var ref = oderReference.document()
            val oder = Order(
                currentFirebaseUser.uid,
                cartList[0],
                cartList[0].title,
                cartList[0].image,
                subtotal.toString(),
                shiping.toString(),
                total.toString(),
                System.currentTimeMillis(),
                ref.id
            )
            uploadOder(oder)
        }
    }

    private fun uploadOder(order: Order) {
        val ref = oderReference.document(order.id)
        ref.set(order).addOnSuccessListener {
            removeCartItem()
        }.addOnFailureListener{
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    fun removeCartItem() {
        db.collection(Constants.CART_DATABASE_ROOT)
            .document(cartList[0].id)
            .delete()
            .addOnSuccessListener {
                BaseUtils.hideProgressbar()
                BaseUtils.showSnackBar(binding.root,"Product Ordered !",false)
            }
            .addOnFailureListener {
                BaseUtils.hideProgressbar()
                BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
            }
    }

    private fun getCartItems() {
        db.collection(Constants.CART_DATABASE_ROOT)
            .whereEqualTo("userId", currentFirebaseUser.uid)
            .get()
            .addOnSuccessListener {queryDocumentSnapshots ->
                for (documentSnapshot in queryDocumentSnapshots) {
                    val cartItem = documentSnapshot.toObject(CartItem::class.java)
                    try {
                            cartList.add(cartItem)
                        }catch (ex: Exception) {
                            BaseUtils.hideProgressbar()
                            BaseUtils.showSnackBar(binding.root,ex.message.toString(),true)
                        }
                }
                if (cartList.isNotEmpty()){
                    adapter.updateList(cartList)
                    binding.rvCartItems.visibility = View.VISIBLE
                    binding.tvNoItem.visibility = View.GONE
                    BaseUtils.hideProgressbar()
                }else{
                    binding.rvCartItems.visibility = View.GONE
                    binding.tvNoItem.visibility = View.VISIBLE
                    BaseUtils.hideProgressbar()
                }
            }
            .addOnFailureListener {
                BaseUtils.hideProgressbar()
                BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
            }
    }

    private fun initViews() {
        cartList = ArrayList()
        db = Firebase.firestore
        cartReference = db.collection(Constants.CART_DATABASE_ROOT)
        oderReference = db.collection(ODER_DATABASE_ROOT)
        auth = FirebaseAuth.getInstance()
        currentFirebaseUser = auth.currentUser!!
    }

    private fun setACartItemRecyclerView() {
        binding.rvCartItems.layoutManager =  LinearLayoutManager(this,  RecyclerView.VERTICAL, false)
        adapter = CartItemAdapter(cartList,this)
        binding.rvCartItems.adapter = adapter
    }

    override fun onCalculation(total: Int, subTotal: Int, shipping: Int) {
        this.total = total
        this.shiping = 20
        this.subtotal = subtotal
        binding.tvShippingValue.text = "RS ${shipping}/="
        binding.tvTotalValue.text = "RS ${total}/="
        binding.tvSubtotalValue.text = "RS ${subTotal}/="
    }
}