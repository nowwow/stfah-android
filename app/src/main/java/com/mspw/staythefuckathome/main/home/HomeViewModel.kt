package com.mspw.staythefuckathome.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mspw.staythefuckathome.data.ListResponse
import com.mspw.staythefuckathome.data.challenge.Challenge
import com.mspw.staythefuckathome.data.challenge.ChallengeRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HomeViewModel(
    private val challengeRepository: ChallengeRepository
) {

    private val _compositeDisposable = CompositeDisposable()

    private val _challenges = MutableLiveData<List<Challenge>>()
    val challenges: LiveData<List<Challenge>> get() = _challenges

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadData()
    }

    private fun handleLoadData(items: ListResponse<Challenge>) {
        _isLoading.value = false
        _challenges.value = items.results
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLoadDataError(error: Throwable) { }

    private fun loadData() {
        challengeRepository.findAll()
            .delay(1000L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleLoadData, ::handleLoadDataError)
            .also { _compositeDisposable.add(it) }
    }

}