package com.jdars.h20.Fragments.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jdars.h20.Activities.AddServiceActivity
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentSoldPoductBinding


class ServicesFragment : Fragment() {

    private lateinit var binding:FragmentSoldPoductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSoldPoductBinding.inflate(layoutInflater)

        setCLickListeners()
        return binding.root
    }

    private fun setCLickListeners() {
        binding.apply {
            mcWatter.setOnClickListener {
                val intent = Intent(requireContext(),AddServiceActivity::class.java)
                intent.putExtra("service","Water Supply")
                startActivity(intent)
            }
            mcChlorine.setOnClickListener {
                val intent = Intent(requireContext(),AddServiceActivity::class.java)
                intent.putExtra("service","Chlorine Cleaning")
                startActivity(intent)
            }

        }
    }

}