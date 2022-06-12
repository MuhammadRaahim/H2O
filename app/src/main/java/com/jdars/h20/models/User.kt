package com.jdars.h20.models

data class User(
    var uid: String? = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var image: String = "",
    var mobile: String = "",
    var gender: String = "",
    var profileCompleted: Int = 0
)
