package com.mspw.staythefuckathome.data.challenge

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ChallengeService {

    @GET("")
    fun findOneById(@Path("id") id: String): Single<Challenge>

}
