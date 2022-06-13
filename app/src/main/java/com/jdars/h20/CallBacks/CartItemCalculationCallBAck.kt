package com.jdars.h20.CallBacks


interface CartItemCalculationCallBAck {
    fun onCalculation(total:Int,subTotal:Int,shipping:Int)
}