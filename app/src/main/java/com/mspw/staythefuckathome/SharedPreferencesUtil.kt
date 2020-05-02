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
        editor.putString(TOKEN, token)
        editor.commit()
    }

    fun getToken(): String = preferences.getString(TOKEN, "") ?: ""

    fun setAddress(address: String) {
        editor.putString(ADDRESS, address)
        editor.commit()
    }

    fun getAddress(): String {
        return preferences.getString(ADDRESS, "") ?: ""
    }

    companion object {
        private const val TOKEN = "token"
        private const val ADDRESS = "address"
    }

}