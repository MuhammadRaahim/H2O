package com.jdars.h20.Activities

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.R
import com.jdars.h20.databinding.ActivityEditProfileBinding
import com.jdars.h20.models.User
import com.jdars.shared_online_business.Utils.BaseUtils
import com.jdars.shared_online_business.Utils.ImageFilePath
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var userReference: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private var imagePath: String? = null
    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setClickListeners()
        getFirebaseUser()

    }

    private fun setClickListeners() {
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivProfileImage.setOnClickListener {
                launchGalleryIntent()
            }
            buttonSaveButton.setOnClickListener {
                when {
                    etFirstName.text.toString().trim().isEmpty() -> {
                        etFirstName.requestFocus()
                        etFirstName.error = getString(R.string.str_invalid_field)
                    }
                    etLastName.text.toString().trim().isEmpty() -> {
                        etLastName.requestFocus()
                        etLastName.error = getString(R.string.str_invalid_field)
                    }
                    etEmail.text.toString().trim().isEmpty() -> {
                        etEmail.requestFocus()
                        etEmail.error = getString(R.string.str_invalid_field)
                    }
                    etPhone.text.toString().trim().isEmpty() -> {
                        etPhone.requestFocus()
                        etPhone.error = getString(R.string.str_invalid_field)
                    }
                    etGender.text.toString().trim().isEmpty() -> {
                        etGender.requestFocus()
                        etGender.error = getString(R.string.str_invalid_field)
                    }
                    else ->{
                        BaseUtils.showProgressbar(this@EditProfileActivity)
                        saveProfile()

                    }
                }
            }
        }
    }

    private fun saveProfile() {
        if (imagePath != null) {
            uploadImageToStorage(imagePath!!)
        } else {
            createProfile(image!!)
        }
    }

    private fun uploadImageToStorage(imagePath: String){
        val file = File(imagePath)
        val uniqueId = UUID.randomUUID().toString()
        val storagePath = "Users/${currentFirebaseUser.uid}/Image/${uniqueId}"
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
                        createProfile(uri.toString())
                    }
                }
            }
        }.addOnProgressListener {
        }.addOnFailureListener { e ->
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,e.message.toString(),true)
        }
    }

    private fun createProfile(image:String){
        val userId = currentFirebaseUser.uid
        val fName = binding.etFirstName.text.toString().trim()
        val lName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()

        val user = User(
            userId,
            fName,
            lName,
            email,
            image,
            phone,
            gender
        )
        updateProfile(user)
    }

    private fun updateProfile(user: User) {
        val ref = userReference.document(currentFirebaseUser.uid)
        ref.set(user).addOnSuccessListener {
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,"Save successfully")
        }.addOnFailureListener{
            BaseUtils.hideProgressbar()
            BaseUtils.showSnackBar(binding.root,it.message.toString(),true)
        }
    }

    private fun initView() {
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        userReference = db.collection(Constants.USERS_DATABASE_ROOT)
        firebaseStorage = FirebaseStorage.getInstance()
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
            image = user.image
            Glide.with(this).load(user.image).placeholder(R.drawable.drawable_profile_image_background)
                .into(binding.ivProfileImage)
        }
        binding.etFirstName.setText(user.firstName)
        binding.etLastName.setText(user.lastName)
        binding.etEmail.setText(user.email)
        binding.etPhone.setText(user.mobile)
        binding.etGender.setText(user.gender)
        BaseUtils.hideProgressbar()
    }

    private fun launchGalleryIntent() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getImageFromGallery.launch("image/*")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
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
                Toast.makeText(this,getString(R.string.permission_required)
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
                action(this.findViewById(android.R.id.content))
            }.show()
        } else {
            snackBar.show()
        }
    }

    private fun setPicture(uri: Uri?) {
        imagePath = ImageFilePath().getFilePath(uri!!,this)
        Glide.with(this).load(uri).placeholder(R.drawable.profile_placeholder)
            .into(binding.ivProfileImage)
    }
}