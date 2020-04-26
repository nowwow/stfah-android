package com.mspw.staythefuckathome.uploadvideo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mspw.staythefuckathome.data.user.UserRepository
import com.mspw.staythefuckathome.data.video.VideoRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UploadVideoViewModel(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository
) {

    private val _compositeDisposable = CompositeDisposable()

    private val _isFinished = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isFinished: LiveData<Boolean> get() = _isFinished

    private val _isRequesting = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isRequesting: LiveData<Boolean> get() = _isRequesting

    fun uploadVideo(token: String, challengeId: Long, path: Uri) {
        _isRequesting.value = true
        userRepository.findMyInfo(token)
            .flatMap { videoRepository.create(it.id, challengeId, path, token) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _isRequesting.value = false
                    _isFinished.value = true
                },
                {
                    _isRequesting.value = false
                }
            )
            .also { _compositeDisposable.add(it) }
    }

}