package com.horizam.skbhub.Utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager( var context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    // shared pref mode
    var PRIVATE_MODE = 0

    companion object{
        // Shared preferences file name
        private const val PREF_NAME = "globleFansyPrefs"
        private const val FILE_NAME = "filename"
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
        editor = pref.edit()
    }
    fun setFile(fileName: String) {
        editor.putString(FILE_NAME, fileName)
        editor.apply()
        editor.commit()
    }
    fun getFileName(): String? {
        var fileName = pref.getString(FILE_NAME, "")
        return fileName
    }
    fun clearAll(){
        editor.clear()
        editor.commit()
    }
}