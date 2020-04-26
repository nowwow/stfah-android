package com.mspw.staythefuckathome.data.video

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {

    @GET("videos/")
    fun findAllByTitle(@Query("title") title: String): Flowable<ListResponse<Video>>

}