package com.jdars.shared_online_business.CallBacks

interface GenericHandler {
    fun showProgressBar(show:Boolean = false)
    fun showMessage(message:String)
    fun showNoInternet(show:Boolean = false)
}