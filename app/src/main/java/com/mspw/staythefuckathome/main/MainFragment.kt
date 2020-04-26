package com.mspw.staythefuckathome.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.challengedetails.ChallengeDetailsActivity
import com.mspw.staythefuckathome.data.challenge.Challenge

class MainFragment : Fragment(), MainChallengeAdapter.OnItemClickedListener {

    private lateinit var mAdapter: MainChallengeAdapter
    private val items = ArrayList<Challenge>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        mAdapter = MainChallengeAdapter(items, this)
        return v
    }

    override fun onClick(v: View, position: Int, item: Challenge) {
        val intent = Intent(context, ChallengeDetailsActivity::class.java)
        startActivity(intent)
    }


}
