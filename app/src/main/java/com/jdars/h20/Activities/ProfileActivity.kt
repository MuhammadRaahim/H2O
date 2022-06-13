package com.jdars.h20.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.R
import com.jdars.h20.databinding.ActivityProfileBinding
import com.jdars.h20.models.User
import com.jdars.shared_online_business.Utils.BaseUtils

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentFirebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setOnCLickListeners()


    }

    override fun onResume() {
        super.onResume()
        getFirebaseUser()
    }

    private fun setOnCLickListeners() {
        binding.apply {
            tvEdit.setOnClickListener {
                startActivity(Intent(this@ProfileActivity,EditProfileActivity::class.java))
            }
            buttonRegistration.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@ProfileActivity,LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getFirebaseUser() {
        BaseUtils.showProgressbar(this)
        currentFirebaseUser = auth.currentUser!!
        userReference.document(currentFirebaseUser.uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val document: DocumentSnapshot = it.result
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        setData(user)
                    }
                } else {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,it.exception!!.message.toString(),true)
                }
            }
    }

    private fun setData(user: User?) {
        if (user!!.image != null){
            Glide.with(this).load(user.image).placeholder(R.drawable.drawable_profile_image_background)
                .into(binding.ivProfileImage)
        }
        binding.tvSettingsName.text = "${user.firstName} ${user.lastName}"
        binding.tvSettingsGender.text = user.gender
        binding.tvSettingsEmail.text = user.email
        binding.tvSettingsMobile.text = user.mobile
        BaseUtils.hideProgressbar()
    }


    private fun initViews() {
        db = Firebase.firestore
        userReference = db.collection(Constants.USERS_DATABASE_ROOT)
        auth = FirebaseAuth.getInstance()
        currentFirebaseUser = auth.currentUser!!
    }
}