package com.jdars.h20.models

import java.io.Serializable


data class Product(
    var id: String = "",
    var user_id: String = "",
    var title: String = "",
    var price: String = "",
    var description: String = "",
    var stock_quantity: String = "",
    var image: String = ""
) : Serializable
