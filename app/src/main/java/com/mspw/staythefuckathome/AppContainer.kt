package com.mspw.staythefuckathome

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mspw.staythefuckathome.data.challenge.ChallengeRepository
import com.mspw.staythefuckathome.data.challenge.ChallengeService
import com.mspw.staythefuckathome.data.user.UserRepository
import com.mspw.staythefuckathome.data.user.UserService
import com.mspw.staythefuckathome.data.video.VideoRepository
import com.mspw.staythefuckathome.data.video.VideoService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer {

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .readTimeout(TIME_OUT_SECONDS, TimeUnit.MILLISECONDS)
        .writeTimeout(TIME_OUT_SECONDS, TimeUnit.MILLISECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    private val challengeService = retrofit.create(ChallengeService::class.java)
    private val userService = retrofit.create(UserService::class.java)
    private val videoService = retrofit.create(VideoService::class.java)

    val challengeRepository = ChallengeRepository(challengeService)
    val userRepository = UserRepository(userService)
    val videoRepository = VideoRepository(videoService)

    companion object {
        private const val BASE_URL = "https://api.staythefxxxathome.com/v1/"
        private const val TIME_OUT_SECONDS = 9000L
    }

}