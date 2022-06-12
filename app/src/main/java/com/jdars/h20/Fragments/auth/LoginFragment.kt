package com.jdars.h20.Fragments.auth

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jdars.h20.MainActivity
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentLoginBinding
import com.jdars.shared_online_business.Utils.BaseUtils
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.hideProgressbar
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.showProgressbar
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.showSnackBar
import kotlinx.coroutines.MainScope


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    var mDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        initViews()
        setClickListeners()


        return binding.root
    }

    private fun initViews() {
        auth = Firebase.auth
    }

    private fun setClickListeners() {
        binding.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.forgot_password_Fragment)
            }
            tvDontHaveAnAccount.setOnClickListener {
                findNavController().navigate(R.id.registerFragment)
            }
            buttonLogin.setOnClickListener {
               validateData()
            }
        }
    }

    private fun validateData() {
        if(!binding.etEmail.text.isNullOrEmpty() && !binding.etPassword.text.isNullOrEmpty()){
            showProgressbar(requireContext())
            loginUser()
        }else{
            showSnackBar(binding.root,"Invalid Input",true)
        }
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    hideProgressbar()
                    var intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    hideProgressbar()
                    showSnackBar( binding.root,
                        task.exception!!.message ?:"An unknown error occurred.",
                        true)
                }
            }
    }

}