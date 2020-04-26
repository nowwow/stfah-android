package com.mspw.staythefuckathome.data.video

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Single
import retrofit2.http.*

interface VideoService {

    @POST("videos/")
    fun create(
        @Header("Authorization") token: String,
        @Body createVideo: CreateVideo
    ): Single<Video>

    @GET("videos/")
    fun findAllByChallengeId(
        @Query("challenge_id") challengeId: Long
    ): Flowable<ListResponse<Video>>

    @GET("videos/")
    fun findAllVideoByCreatorId(
        @Query("creator") creatorId: Long
    ): Call<ListResponse<Video>>

}