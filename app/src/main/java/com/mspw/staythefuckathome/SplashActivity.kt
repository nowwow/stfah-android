package com.mspw.staythefuckathome

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.mspw.staythefuckathome.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.random.Random


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val list = listOf(
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

        Glide.with(this)
            .asGif()
            .load(list[Random.nextInt(list.size)])
            .listener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override  fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.setLoopCount(1)
                    resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable) {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            finish()
                        }
                    })
                    return false
                }
            })
            .into(gifPlayer)

    }
}
