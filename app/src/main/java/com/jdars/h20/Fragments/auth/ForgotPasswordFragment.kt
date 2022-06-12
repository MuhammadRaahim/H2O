package com.jdars.h20.Fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentForgotPasswordBinding
import com.jdars.shared_online_business.Utils.BaseUtils
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.hideProgressbar
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.showProgressbar
import com.jdars.shared_online_business.Utils.BaseUtils.Companion.showSnackBar
import com.jdars.shared_online_business.Utils.Validator


class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)


        setOnClickListeners()
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.apply {
            ivBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSubmit.setOnClickListener {
                validateData()
            }
        }
    }

    private fun validateData() {
        if (!Validator.isValidEmail(binding.etEmail, true, requireContext())){
            return
        }else{
            setForgetPasswordEmail()
        }
    }

    private fun setForgetPasswordEmail() {
        showProgressbar(requireContext())
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.etEmail.text.toString().trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideProgressbar()
                    showSnackBar(binding.root,"Email Send Successfully",false)
                    findNavController().popBackStack()
                }else{
                    hideProgressbar()
                    showSnackBar(binding.root,task.exception.toString(),true)
                }
            }
    }

}