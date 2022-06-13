package com.jdars.h20.Activities

import android.app.Activity
import android.app.Service
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.R
import com.jdars.h20.databinding.ActivityAddServiceBinding
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.Utils.BaseUtils

class AddServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddServiceBinding
    private lateinit var serviceName: String
    private lateinit var apiKey: String
    private lateinit var latLng: com.google.android.gms.maps.model.LatLng
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var serviceReference: CollectionReference
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setClickListeners()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setClickListeners() {
        binding.apply {
            etDate.setOnClickListener {
                BaseUtils.showDatePicker(etDate, this@AddServiceActivity)
            }
            etAddress.setOnClickListener {
                getAddress()
            }
            buttonSaveButton.setOnClickListener {
                when {
                    etServiceName.text.toString().trim().isEmpty() -> {
                        etServiceName.requestFocus()
                        etServiceName.error = getString(R.string.str_invalid_field)
                    }
                    etPhone.text.toString().trim().isEmpty() -> {
                        etPhone.requestFocus()
                        etPhone.error = getString(R.string.str_invalid_field)
                    }
                    etAddress.text.toString().trim().isEmpty() -> {
                        etAddress.requestFocus()
                        etAddress.error = getString(R.string.str_invalid_field)
                    }
                    etDate.text.toString().trim().isEmpty() -> {
                        etDate.requestFocus()
                        etDate.error = getString(R.string.str_invalid_field)
                    }
                    etProductDescription.text.toString().trim().isEmpty() -> {
                        etProductDescription.requestFocus()
                        etProductDescription.error = getString(R.string.str_invalid_field)
                    }
                    etQuantity.text.toString().trim().isEmpty() -> {
                        etQuantity.requestFocus()
                        etQuantity.error = getString(R.string.str_invalid_field)
                    }
                    else -> {
                        BaseUtils.showProgressbar(this@AddServiceActivity)
                        getData()
                    }
                }
            }
        }

    }

    private fun getData() {
        var ref = serviceReference.document()
        var service = com.jdars.h20.models.Service(
            ref.id,
            currentFirebaseUser.uid,
            binding.etServiceName.text.toString().trim(),
            binding.etPhone.text.toString().trim(),
            binding.etAddress.text.toString().trim(),
            latLng,
            binding.etDate.text.toString().trim(),
            binding.etProductDescription.text.toString().trim(),
            binding.etQuantity.text.toString().trim()
        )
        uploadService(service)
    }

    private fun uploadService(service: com.jdars.h20.models.Service) {
        val ref = serviceReference.document(service.id!!)
        ref.set(service).addOnSuccessListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,"Service Add Successfully !",false)
            finish()
        }.addOnFailureListener{
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    private fun initViews() {
        serviceName = intent.getStringExtra("service")!!
        binding.etServiceName.setText(serviceName)
        apiKey = "AIzaSyCMR63Gt-Iwztwwt4dtpgwicYXq27oI03k"
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        db = Firebase.firestore
        serviceReference = db.collection(Constants.Service_DATABASE_ROOT)
    }

    private fun getAddress() {
        val fields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startForResult.launch(intent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)
                        latLng = place.latLng!!
                        binding.etAddress.setText(place.address)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    result.data?.let {
                        val status = Autocomplete.getStatusFromIntent(it)
                        Toast.makeText(this,status.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}