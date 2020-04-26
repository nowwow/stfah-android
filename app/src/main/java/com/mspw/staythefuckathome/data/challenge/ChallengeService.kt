package com.mspw.staythefuckathome.data.challenge

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ChallengeService {

    @GET("challenges/")
    fun findAll(): Flowable<ListResponse<Challenge>>

    @GET("challenges/{id}/")
    fun findOneById(@Path("id") id: String): Single<Challenge>

}
