package com.jdars.shared_online_business.Utils

import android.content.Context
import android.content.res.Resources
import android.util.Patterns
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.jdars.h20.R


class Validator {

    companion object {

        private fun getText(data: Any): String {
            var str = ""
            if (data is EditText) {
                str = data.text.toString().trim()
            } else if (data is String) {
                str = data
            }
            return str
        }

        fun isValidName(data: Any, updateUI: Boolean = true, context: Context): Boolean {
            val str = getText(data)
            val valid = str.trim().length > 2
            if (updateUI) {
                val error: String? = if (valid) null else ""
                setError(data, error)
            }
            return valid
        }

        fun isValidUserName(data: Any, updateUI: Boolean = true , context: Context): Boolean {
            val str = getText(data)
            val valid = str.trim().length > 2
            if (updateUI) {
                val error: String? = if (valid) null else context.getString(R.string.str_enter_valid_name)
                setError(data, error)
            }
            return valid
        }

        fun isValidEmail(data: Any, updateUI: Boolean = true, context: Context): Boolean {
            val str = getText(data)
            val valid = Patterns.EMAIL_ADDRESS.matcher(str).matches()
            if (updateUI) {
                val error: String? = if (valid) null else context
                    .getString(R.string.str_enter_valid_email_address)
                setError(data, error)
            }
            return valid
        }

        fun isValidGender(data: Any, updateUI: Boolean = true, context: Context): Boolean {
            val str = getText(data)
            var valid = true
            if(str.isNullOrEmpty()){
                valid = false
            }
            if (updateUI) {
                val error: String? = if (valid) null else context
                    .getString(R.string.str_enter_valid_email_address)
                setError(data, error)
            }
            return valid
        }


        fun isValidPassword(data: Any, updateUI: Boolean = true, context: Context): Boolean {
            val str = getText(data)
            var valid = true
            if (str.length < 6) {
                valid = false
            }
            if (updateUI) {
                val error: String? = if (valid) null else context
                    .getString(R.string.str_enter_valid_password_policy)
                setError(data, error)
            }
            return valid
        }

        fun isValidPhone(data: Any, updateUI: Boolean = true, context: Context): Boolean {
            val str = getText(data)
            val valid = checkPhone(str)
            // Set error if required
            if (updateUI) {
                val error: String? = if (valid) null else context
                    .getString(R.string.str_enter_valid_phone)
                setError(data, error)
            }
            return valid
        }

        fun checkPhone(phone:String): Boolean{
            if(phone.length < 7 ||  phone.length > 13){
                return false
            }
            return true
        }

        fun isValidAddress(data: Any, updateUI: Boolean = true,context: Context): Boolean {
            val str = getText(data)
            val valid = str.trim().isNotEmpty()

            // Set error if required
            if (updateUI) {
                val error: String? = if (valid) null else context
                    .getString(R.string.str_enter_valid_address)
                setError(data, error)
            }

            return valid
        }

        private fun setError(data: Any, error: String?) {
            if (data is EditText) {
                    data.error = error
            }
        }

    }

}