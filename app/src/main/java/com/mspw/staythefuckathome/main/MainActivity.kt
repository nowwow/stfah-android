package com.mspw.staythefuckathome.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.mspw.staythefuckathome.AppContainer
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.user.User
import com.mspw.staythefuckathome.login.LoginActivity
import com.mspw.staythefuckathome.main.home.HomeFragment
import com.mspw.staythefuckathome.main.map.MapFragment
import com.mspw.staythefuckathome.my_page.MyPageFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*
import kotlinx.android.synthetic.main.drawer_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var appContainer: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        appContainer = (application as BaseApplication).appContainer
        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }


        val token = SharedPreferencesUtil(this).getToken()
        appContainer.userRepository.getUserData("Bearer $token").enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                t.printStackTrace()
                Log.e("Get user data fail", t.message)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    nameText.text = response.body()?.name ?: ""
                    Picasso.get().load(response.body()?.image)
                        .transform(CropCircleTransformation())
                        .into(userProfile)
                    if (response.body()?.address == "" ) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, MapFragment()).commit()
                        toolbar.visibility = View.GONE
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, HomeFragment()).commit()
                    }
                } else {
                    Log.e("Get user data error", response.code().toString() + response.message())
                }
            }
        })


        setDrawer()
    }


    private fun setDrawer() {

        drawer.run {
            closeBtn.setOnClickListener {
                drawerLayout.closeDrawer(Gravity.LEFT)
            }
            homeBtn.setOnClickListener {
                if (supportFragmentManager.findFragmentById(R.id.fragment) is HomeFragment) {
                    return@setOnClickListener
                } else {
                    replaceFragment(HomeFragment())
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }
            }
            settingBtn.setOnClickListener {
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment, MapFragment()).commit()
                toolbar.visibility = View.GONE
                drawerLayout.closeDrawer(Gravity.LEFT)
            }
            signOutBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            myPageBtn.setOnClickListener {
                if (supportFragmentManager.findFragmentById(R.id.fragment) is MyPageFragment) {
                    return@setOnClickListener
                } else {
                    replaceFragment(MyPageFragment())
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }
            }

        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment).commit()
    }
}
