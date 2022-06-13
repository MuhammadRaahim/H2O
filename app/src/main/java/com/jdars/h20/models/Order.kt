package com.jdars.h20.models

import java.io.Serializable

data class Order(
    val user_id: String = "",
    val items: CartItem = CartItem(),
    val title: String = "",
    val image: String = "",
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val orderDateTime: Long = System.currentTimeMillis(),
    var id: String = ""
) : Serializable