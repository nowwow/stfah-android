package com.mspw.staythefuckathome.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mspw.staythefuckathome.AppContainer
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.user.User
import com.mspw.staythefuckathome.my_page.MyPageFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*
import kotlinx.android.synthetic.main.drawer_main.view.*
import kotlinx.android.synthetic.main.drawer_main.view.nameText
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


        val fm: FragmentManager = supportFragmentManager
        val fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.add(R.id.fragment, MainFragment())
        fragmentTransaction.commit()

        setDrawer()
    }

    private fun setDrawer() {
        val token = SharedPreferencesUtil(this).getToken()
        appContainer.userRepository.getUserData(token).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                t.printStackTrace()
                Log.e("Get user data fail", t.message)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    nameText.text = response.body()?.name ?: ""
                    Picasso.get().load(response.body()?.profile?.url)
                        .transform(CropCircleTransformation())
                        .into(userProfile)
                } else {

                }
            }

        })
        drawer.run {
            closeBtn.setOnClickListener {
                drawerLayout.closeDrawer(Gravity.LEFT)
            }
            homeBtn.setOnClickListener {
                if (supportFragmentManager.findFragmentById(R.id.fragment) is MainFragment) {
                    return@setOnClickListener
                } else {
                    replaceFragment(MainFragment())
                }
            }
            settingBtn.setOnClickListener {

            }
            signOutBtn.setOnClickListener {

            }
            myPageBtn.setOnClickListener {
                if (supportFragmentManager.findFragmentById(R.id.fragment) is MyPageFragment) {
                    return@setOnClickListener
                } else {
                    replaceFragment(MyPageFragment())
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
