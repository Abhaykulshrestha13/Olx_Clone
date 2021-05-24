package com.example.olxapp.utilites

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context:Context) {
    var sharedPref:SharedPreferences
    init {
        sharedPref = context.getSharedPreferences(Constants.SharedPrefName,0)
    }
    fun setString(key:String,value:String){
        sharedPref.edit().putString(key,value).commit()
    }
    fun getString(key:String): String? {
        return sharedPref.getString(key,"")
    }
}