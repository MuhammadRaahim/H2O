package com.jdars.h20.Fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentRegisterBinding
import com.jdars.h20.models.User
import com.jdars.shared_online_business.Utils.BaseUtils
import com.jdars.shared_online_business.Utils.Validator


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var userReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        setUpUI()
        setClickListeners()

        return binding.root
    }

    private fun setUpUI() {
        auth = Firebase.auth
        db = Firebase.firestore
        userReference = db.collection(Constants.USERS_DATABASE_ROOT)
        firebaseStorage = FirebaseStorage.getInstance()
    }

    private fun setClickListeners() {
        binding.apply {
            ivBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }
            tvAlreadyHaveAnAccount.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
            buttonRegistration.setOnClickListener {
                if (!Validator.isValidUserName(etFirstName, true,requireContext())) {
                    return@setOnClickListener
                }else if (!Validator.isValidUserName(etLastName, true,requireContext())) {
                    return@setOnClickListener
                }else if (!Validator.isValidEmail(etEmail, true, requireContext())) {
                    return@setOnClickListener
                }else if (etPassword.text.toString() != etConfirmPassword.text.toString()){
                    etConfirmPassword.error = "Password does't Match"
                    return@setOnClickListener
                } else {
                    BaseUtils.showProgressbar(requireContext())
                    signUp()
                }
            }
        }
    }

    private fun signUp() {
        auth.createUserWithEmailAndPassword(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    getData(task.result.user!!.uid)
                } else {
                    BaseUtils.hideProgressbar()
                    BaseUtils.showSnackBar(binding.root,task.exception!!.message.toString(),true)
                }
            }
    }

    private fun getData(id: String) {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        val user = User(
            uid = id, firstName = firstName, lastName = lastName, email = email
        )

        createProfile(user)
    }

    private fun createProfile(user: User) {
        val ref = userReference.document(user.uid!!)
        ref.set(user).addOnSuccessListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,"Register Successfully",false)
            findNavController().popBackStack()
        }.addOnFailureListener{
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

}