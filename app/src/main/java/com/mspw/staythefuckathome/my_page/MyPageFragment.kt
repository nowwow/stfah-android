package com.mspw.staythefuckathome.my_page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.ListResponse
import com.mspw.staythefuckathome.data.user.User
import com.mspw.staythefuckathome.data.video.Video
import com.mspw.staythefuckathome.playchallenge.PlayChallengeActivity
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_my_page.view.*
import kotlinx.android.synthetic.main.fragment_my_page.view.profile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment(),
    MyPageVideoAdapter.OnItemClickedListener {
    private lateinit var mAdapter: MyPageVideoAdapter
    private val items = ArrayList<Video>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_my_page, container, false)
        val appContainer = (activity?.application as BaseApplication).appContainer
        mAdapter = MyPageVideoAdapter(items, this)
        v.run {
            list.adapter = mAdapter
            appContainer.userRepository.getUserData("Bearer ${SharedPreferencesUtil(context!!).getToken()}")
                .enqueue(object : Callback<User> {
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        t.printStackTrace()
                        Log.e("Get user data error", t.message)
                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Picasso.get()
                                .load(response.body()?.image)
                                .transform(CropCircleTransformation())
                                .into(profile)
                            nameText.text = response.body()?.name
                            addressText.text = response.body()?.address?.split(":")?.get(0) ?: "address"
                            getUserVideo(response.body()?.id)
                        } else {
                            Toast.makeText(context, "Get data error", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }
        return v
    }

    private fun getUserVideo(id: Long?) {
        (activity?.application as BaseApplication).appContainer.videoRepository
            .findAllVideoByCreatorId(id ?: 0)
            .enqueue(object : Callback<ListResponse<Video>> {
                override fun onFailure(call: Call<ListResponse<Video>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Get video", t.message)
                }

                override fun onResponse(c: Call<ListResponse<Video>>, response: Response<ListResponse<Video>>) {
                    if(response.isSuccessful){
                        response.body()?.results?.let{
                            items.addAll(it)
                            mAdapter.notifyDataSetChanged()
                        }
                    }else{
                        Toast.makeText(context, "Get data error", Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }


    override fun onClick(v: View, position: Int, item: Video) {
        val intent = Intent(context, PlayChallengeActivity::class.java)
        intent.putExtra("video", item)
        startActivity(intent)
    }
}