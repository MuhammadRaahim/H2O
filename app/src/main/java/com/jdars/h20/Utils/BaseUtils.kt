package com.jdars.shared_online_business.Utils

import android.R
import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class BaseUtils {
    companion object{
        var mDialog: Dialog? = null

        fun showSnackBar(view: View, message: String, isError: Boolean = false) {
            val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            val snackBarView = snackBar.view
            val context = view.context

            if (isError) {
                snackBarView.setBackgroundColor(context.resources.getColor(R.color.holo_red_light))
            } else {
                snackBarView.setBackgroundColor(context.resources.getColor(R.color.holo_green_light))
            }

            snackBar.show()
        }


        fun showProgressbar(context: Context) {
            mDialog = Dialog(context)
            mDialog.let {
                it!!.setContentView(com.jdars.h20.R.layout.progressbar_dialog_layout)
                it.setCancelable(false)
                it.setCanceledOnTouchOutside(false)
                it.show()
            }
        }

        fun hideProgressbar() {
            mDialog?.dismiss()
        }



        fun changeMiliSecondToTime(milliSeconds: Long, dateFormat: String?): String? {
            val formatter = SimpleDateFormat(dateFormat)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        fun animationOpenScreen(): NavOptions {
            return navOptions { // Use the Kotlin DSL for building NavOptions
                anim {
                    enter = R.animator.fade_in
                    exit = R.animator.fade_out
                }
            }
        }
    }
}