package com.mspw.staythefuckathome.data.user

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("users/")
    fun getUserList(@Query("provider_uid")provide_id:String): Call<ListResponse<User>>

    @POST("signup/")
    fun registerUser(@Header("Authorization") firebaseToken:String, @Body signUp:SignUp): Call<Any>

    @GET("users/me/")
    fun getMyUser(@Header("Authorization") firebaseToken:String): Call<User>

    @GET("users/me/")
    fun findMyInfo(@Header("Authorization") firebaseToken: String): Single<User>

}