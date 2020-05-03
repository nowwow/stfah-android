package com.mspw.staythefuckathome.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.mspw.staythefuckathome.AppContainer
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.user.User
import com.mspw.staythefuckathome.login.LoginActivity
import com.mspw.staythefuckathome.main.home.HomeFragment
import com.mspw.staythefuckathome.main.map.MapActivity
import com.mspw.staythefuckathome.my_page.MyPageFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.*
import kotlinx.android.synthetic.main.drawer_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.sql.DataSource
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var appContainer: AppContainer

    private val list = listOf(
        "https://media.tenor.com/images/5608cf63c47add970c1989e0732f3689/tenor.gif",
        "https://media.tenor.com/images/629c0911af2a4d2bd848b68bf3bf7ead/tenor.gif",
        "https://media.tenor.com/images/d3e5f7473ef989771bfce995a99fa552/tenor.gif",
        "https://media.tenor.com/images/d489cd8d5d7f3b587ca4904a6b04b8c9/tenor.gif",
        "https://media.tenor.com/images/1fbc817ca8f9ade4231fa9ce1d122aab/tenor.gif",
        "https://media.tenor.com/images/d79c10794976fcc5cc6c560ff967ffcf/tenor.gif",
        "https://media.tenor.com/images/7c0c5ad3425e52d62968884cc594c01b/tenor.gif",
        "https://media.tenor.com/images/00cb910abdcf166081a1265186659c06/tenor.gif",
        "https://media.giphy.com/media/Y3GPYKwenR9SbriKvr/giphy.gif",
        "https://media.giphy.com/media/SqMjwNds2rQ31s9Vp3/giphy.gif",
        "http://optimal.inven.co.kr/upload/2019/11/26/bbs/i15108711150.gif"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appContainer = (application as BaseApplication).appContainer
        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
        replaceFragment(HomeFragment())


        val shared = SharedPreferencesUtil(this)
        val token = shared.getToken()
        appContainer.userRepository.getUserData("Bearer $token").enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                t.printStackTrace()
                Log.e("Get user data fail", t.message.toString())
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    nameText.text = user?.name ?: ""
                    Picasso.get().load(user?.image)
                        .transform(CropCircleTransformation())
                        .into(userProfile)

                    if (shared.getAddress() != user?.address) {
                        gifPlayer.visibility = View.VISIBLE
                        Glide.with(this@MainActivity)
                            .asGif()
                            .load("https://media.tenor.com/images/629c0911af2a4d2bd848b68bf3bf7ead/tenor.gif")
                            .listener(object : RequestListener<GifDrawable?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<GifDrawable?>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: GifDrawable?,
                                    model: Any?,
                                    target: Target<GifDrawable?>?,
                                    dataSource: com.bumptech.glide.load.DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    resource?.setLoopCount(1)
                                    resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                                        override fun onAnimationEnd(drawable: Drawable) {
                                            gifPlayer.visibility = View.GONE
                                        }
                                    })
                                    return false
                                }
                            })
                            .into(gifPlayer)
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
            signOutBtn.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                SharedPreferencesUtil(this@MainActivity).setToken("")
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            settingBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, MapActivity::class.java)
                startActivity(intent)
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
