package com.mspw.staythefuckathome.data.challenge

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import io.reactivex.Single

class ChallengeRepository(
    private val challengeService: ChallengeService
) {

    fun findAll(): Flowable<ListResponse<Challenge>> {
        return challengeService.findAll()
    }

    fun findOneById(id: String): Single<Challenge> {
        return challengeService.findOneById(id)
    }

}