package com.jdars.h20.Fragments.product

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.R
import com.jdars.h20.databinding.FragmentAddProductBinding
import com.jdars.h20.models.Product
import com.jdars.shared_online_business.Utils.BaseUtils
import com.jdars.shared_online_business.Utils.ImageFilePath
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var productReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private var imagePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        initView()
        setClickListeners()


        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            ivCameraIcon.setOnClickListener{
                launchGalleryIntent()
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSaveButton.setOnClickListener{
                when {
                    etProductTitle.text.toString().trim().isEmpty() -> {
                        etProductTitle.requestFocus()
                        etProductTitle.error = getString(R.string.str_invalid_field)
                    }
                    etProductPrice.text.toString().trim().isEmpty() -> {
                        etProductTitle.requestFocus()
                        etProductPrice.error = getString(R.string.str_invalid_field)
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
                        BaseUtils.showProgressbar(requireContext())
                        getData()
                    }
                }
            }
        }
    }

    private fun getData() {
        if (imagePath!= null){
            uploadImageToStorage(imagePath!!)
        }else{
            addProduct("")
        }
    }

    private fun uploadImageToStorage(imagePath: String){
        val file = File(imagePath)
        val uniqueId = UUID.randomUUID().toString()
        val storagePath = "Products Pictures/${currentFirebaseUser.uid}/".plus(uniqueId)
        uploadFile(file, storagePath)
    }

    private fun uploadFile(file: File, storagePath: String) {
        val ext: String = file.extension
        if (ext.isEmpty()) {
            BaseUtils.showSnackBar(binding.root,"Something went wrong",true)
            BaseUtils.hideProgressbar()
            return
        }
        val storageReference = firebaseStorage.reference.child("$storagePath.$ext")
        val uriFile = Uri.fromFile(file)
        val uploadTask: UploadTask = storageReference.putFile(uriFile)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            if (taskSnapshot.metadata != null && taskSnapshot.metadata!!.reference != null) {
                val result = taskSnapshot.storage.downloadUrl
                result.addOnSuccessListener { uri ->
                    if (uri != null) {
                        addProduct(uri.toString())
                    }
                }
            }
        }.addOnProgressListener { taskSnapshot ->
        }.addOnFailureListener { e ->
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,e.message.toString(),true)
        }
    }

    private fun addProduct(image: String) {
        binding.apply {

            val ref = productReference.document()
            val id: String = ref.id
            val userId: String = currentFirebaseUser.uid
            val title: String = etProductTitle.text.toString().trim()
            val prize: String = etProductPrice.text.toString().trim()
            val description: String = etProductDescription.text.toString().trim()
            val quantity: String = etQuantity.text.toString().trim()

            val product = Product(id = id, user_id = userId, title = title,
                price = prize, description = description, stock_quantity = quantity,
                image = image
            )

            uploadProduct(product)

        }

    }

    private fun uploadProduct(product: Product) {
        val ref = productReference.document(product.id)
        ref.set(product).addOnSuccessListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,"Product Add Successfully !",false)
            findNavController().popBackStack()
        }.addOnFailureListener{
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    private fun initView() {
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        db = Firebase.firestore
        productReference = db.collection(Constants.PRODUCT_DATABASE_ROOT)
        firebaseStorage = FirebaseStorage.getInstance()
    }

    private fun launchGalleryIntent() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getImageFromGallery.launch("image/*")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showSnackBar(
                    binding.root,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    "Ok"
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                getImageFromGallery.launch("image/*")
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(requireContext(),getString(R.string.permission_required)
                    .plus(". Please enable it settings"), Toast.LENGTH_SHORT).show()
            }
        }

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        setPicture(uri)
    }

    private fun showSnackBar(
        view: View, msg: String, length: Int, actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackBar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackBar.setAction(actionMessage) {
                action(requireView().findViewById(android.R.id.content))
            }.show()
        } else {
            snackBar.show()
        }
    }

    private fun setPicture(uri: Uri?) {
        imagePath = ImageFilePath().getFilePath(uri!!,requireContext())
        Glide.with(this).load(uri).placeholder(R.drawable.shopkart_placeholder)
            .into(binding.ivProductImage)
    }


}