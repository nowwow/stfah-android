package com.mspw.staythefuckathome.uploadvideo

import android.Manifest
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.video.Video
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_upload_video.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@RuntimePermissions
class UploadVideoActivity : AppCompatActivity() {

    private var menuItem: MenuItem? = null
    private var isSelected: Boolean = false
    private val compositeDisposable = CompositeDisposable()
    private lateinit var videoAdapter: UploadVideoAdapter
    private lateinit var viewModel: UploadVideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_video)
        setupUi()
        setupViewModel()
        loadVideosWithPermissionCheck()
    }

    override fun onStart() {
        super.onStart()
//        loadVideosWithPermissionCheck()
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
        viewModel = UploadVideoViewModel(appContainer.videoRepository)
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

    private fun getOutputMediaFile(type: MediaType): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "stfah"
        )

        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    return null
                }
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return when (type) {
            MediaType.IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MediaType.VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
        }
    }

    private fun uploadVideo(video: Video) {
        val contentUrl = video.url!!
        val content = File(contentUrl)
        val retriever = MediaMetadataRetriever().apply { setDataSource(contentUrl) }
        var fileOutputStream: FileOutputStream? = null
        val image = try {
            retriever.getFrameAtTime(MICROSECOND, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (error: RuntimeException) {
            null
        }
        val file = getOutputMediaFile(MediaType.IMAGE)
        val thumbnail = try {
            fileOutputStream = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            file
        } catch (exception: ClassCastException) {
            null
        }  catch (exception: IOException) {
            null
        } finally {
            fileOutputStream?.flush()
            fileOutputStream?.close()
        }

        Log.d(TAG, "video: $content")
        Log.d(TAG, "thumbnail: $thumbnail")
    }

    fun activeMenuItem() {
        if (!isSelected) {
            isSelected = true
            menuItem?.apply {
                title = createSpannableString(title, R.color.colorPrimary)
            }
        }
    }

    enum class MediaType {
        IMAGE,
        VIDEO
    }

    companion object {
        private val TAG = UploadVideoActivity::class.java.simpleName
        private const val SPAN_COUNT = 3
        private const val MICROSECOND = 1000000L
    }

}
