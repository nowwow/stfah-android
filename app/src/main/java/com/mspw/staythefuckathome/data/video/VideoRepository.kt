package com.mspw.staythefuckathome.data.video

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Call
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VideoRepository(
    private val videoService: VideoService
) {

    fun findAllByChallengeId(challengeId: Long): Flowable<ListResponse<Video>> {
        return videoService.findAllByChallengeId(challengeId)
    }

    fun create(userId: Long, challengeId: Long, path: Uri, token: String): Single<Video> {
        return Single.create<String> { emitter ->
            val storageRef = FirebaseStorage.getInstance().reference
            storageRef.child("video").child(path.path.toString()).putFile(path)
                .addOnSuccessListener { task ->
                    task.metadata?.reference?.downloadUrl
                    ?.addOnSuccessListener { emitter.onSuccess(it.toString()) }
                    ?.addOnFailureListener { emitter.onError(it) }
                }
                .addOnFailureListener { emitter.onError(it) }
        }
        .flatMap {
            videoService.create("Bearer $token", CreateVideo(it, challengeId, userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    companion object {
        private val TAG = VideoRepository::class.java.simpleName
    }

    fun findAllVideoByCreatorId(creator:Long): Call<ListResponse<Video>> {
        return videoService.findAllVideoByCreatorId(creator)
    }

}