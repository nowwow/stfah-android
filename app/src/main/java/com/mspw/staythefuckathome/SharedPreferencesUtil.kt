package com.mspw.staythefuckathome

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences(
        "stf", Context.MODE_PRIVATE
    )
    private var editor: SharedPreferences.Editor


    init {
        this.editor = preferences.edit()
    }

    fun setToken(token: String) {
        editor.putString("token", token)
        editor.commit()
    }

    fun getToken(): String = preferences.getString("token", "") ?: ""
}