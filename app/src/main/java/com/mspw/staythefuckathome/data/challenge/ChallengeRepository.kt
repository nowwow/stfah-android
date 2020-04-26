package com.mspw.staythefuckathome.data.challenge

import io.reactivex.Single

class ChallengeRepository(
    private val challengeService: ChallengeService
) {

    fun findOneById(id: String): Single<Challenge> {
        return challengeService.findOneById(id)
    }

}