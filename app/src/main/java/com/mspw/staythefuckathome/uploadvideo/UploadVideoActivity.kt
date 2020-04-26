package com.mspw.staythefuckathome.uploadvideo

import android.Manifest
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import com.mspw.staythefuckathome.data.video.Video
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_upload_video.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class UploadVideoActivity : AppCompatActivity() {

    private var menuItem: MenuItem? = null
    private var isSelected: Boolean = false
    private val compositeDisposable = CompositeDisposable()
    private lateinit var videoAdapter: UploadVideoAdapter
    private lateinit var viewModel: UploadVideoViewModel
    private val challengeId by lazy { intent.getLongExtra("challengeId", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_video)
        setupUi()
        setupViewModel()
        setupObserve()
        loadVideosWithPermissionCheck()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.upload_video_menu, menu)
        menuItem = menu?.findItem(R.id.uploadVideo)?.apply {
            title = createSpannableString(title, R.color.gray)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.uploadVideo -> {
                val video = videoAdapter.getSelectedVideo()
                if (isSelected && video != null) {
                    uploadVideo(video)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUi() {
        setSupportActionBar(uploadVideoToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Upload Video"
        }
    }

    private fun setupViewModel() {
        val appContainer = (application as BaseApplication).appContainer
        viewModel = UploadVideoViewModel(
            appContainer.userRepository,
            appContainer.videoRepository
        )
    }

    private fun setupObserve() {
        with (viewModel) {
            isRequesting.observe(this@UploadVideoActivity, Observer {
                if (it) {
                    uploadVideoLoading.visibility = View.VISIBLE
                    return@Observer
                }

                uploadVideoLoading.visibility = View.GONE
            })
            isFinished.observe(this@UploadVideoActivity, Observer {
                if (!it) {
                    return@Observer
                }

                onBackPressed()
            })
        }
    }

    private fun toVideo(cursor: Cursor, data: String): String {
        val dataIndex = cursor.getColumnIndexOrThrow(data)
        return cursor.getString(dataIndex)
    }

    private fun handleLoadVideos(videos: List<Video>) {
        uploadVideoList.apply {
            val displayMetrics = resources.displayMetrics
            val sideSpacing = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                displayMetrics
            ).toInt()
            val bottomSpacing = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                displayMetrics
            ).toInt()
            val spacing = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                38f,
                displayMetrics
            ).toInt()
            val imageSize = (displayMetrics.widthPixels - spacing) / SPAN_COUNT
            setHasFixedSize(true)
            addItemDecoration(UploadVideoSpaceDecoration(sideSpacing, bottomSpacing))
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            videoAdapter = UploadVideoAdapter(videos, this@UploadVideoActivity, imageSize)
            adapter = videoAdapter
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLoadVideosError(error: Throwable) { }

    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun loadVideos() {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val data = MediaStore.Video.VideoColumns.DATA
        val duration = MediaStore.Video.VideoColumns.DURATION
        val dateTaken = MediaStore.Video.Media.DATE_TAKEN
        val projection = arrayOf(data, duration)
        val selection = "$duration >= ? AND $duration <= ?"
        val selectionArgs = arrayOf("1000", "15999")
        val orderBy = "$dateTaken DESC"
        val cursor = CursorLoader(this, uri, projection, selection, selectionArgs, orderBy).loadInBackground()
        Observable.fromIterable(CursorIterable.from(cursor!!))
            .doAfterNext {
                val lastPosition = it.count - 1
                if (it.position == lastPosition) {
                    it.close()
                }
            }
            .map { toVideo(it, data) }
            .map { Video(url = it) }
            .toList()
            .subscribe(::handleLoadVideos, ::handleLoadVideosError)
            .apply { compositeDisposable.add(this) }
    }

    private fun createSpannableString(title: CharSequence, @ColorRes color: Int): SpannableString {
        return SpannableString(title).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(applicationContext, color)),
                0,
                length,
                0
            )
        }
    }

    private fun getVideoFromUri(args: String): String? {
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val data = MediaStore.Video.VideoColumns.DATA
        val projection = arrayOf(data)
        val cursor = contentResolver.query(uri, projection, "$data=?", arrayOf(args), null)

        return if (cursor == null) {
            null
        } else {
            try {
                cursor.moveToFirst()
                val index = cursor.getColumnIndexOrThrow(data)
                cursor.getString(index)
            } catch (e: Exception) {
                null
            } finally {
                cursor.close()
            }
        }
    }

    private fun uploadVideo(video: Video) {
        val contentUrl = getVideoFromUri(video.url!!)
        val content = File(contentUrl)
        val token = SharedPreferencesUtil(this).getToken()
        viewModel.uploadVideo(token, challengeId, Uri.fromFile(content))
    }

    fun activeMenuItem() {
        if (!isSelected) {
            isSelected = true
            menuItem?.apply {
                title = createSpannableString(title, R.color.colorPrimary)
            }
        }
    }

    companion object {
        private val TAG = UploadVideoActivity::class.java.simpleName
        private const val SPAN_COUNT = 3
    }

}
