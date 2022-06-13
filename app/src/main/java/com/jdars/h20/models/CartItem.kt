package com.jdars.h20.models

data class CartItem (
    var userId: String? = "",
    var productOwnerId: String = "",
    var productId: String = "",
    var title: String = "",
    var price: String = "",
    var image: String = "",
    var cart_quantity: String = "",
    var stock_quantity: String = "",
    var id: String = "",
)
