package com.mspw.staythefuckathome.data.video

import io.reactivex.Single

class VideoRepository(
    private val videoService: VideoService
) {

    fun findOneById(id: Long): Single<Video> {
        return Single.just(Video())

    }

}