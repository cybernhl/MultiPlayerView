package com.yyl.multiplayerview.controller

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.yyl.multiplayerview.MultiPlayerView
import com.yyl.multiplayerview.R
import com.yyl.multiplayerview.impl.MultiVideoPlayerProvider
import com.yyl.multiplayerview.impl.VideoState
import com.yyl.multiplayerview.utils._i
import com.yyl.multiplayerview.utils.isWifiConnected
import com.yyl.multiplayerview.utils.visiable
import kotlinx.android.synthetic.main.yyl_videoplayer_window_min.view.*


/**
 * Created by yyl on 2018/2/28.
 */
class VideoPlayerWindowMinView : FrameLayout {

    private var isPlayState = false
    private var videoState = VideoState.List

    private var videoPath: String? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.yyl_videoplayer_window_min, this)
        video_error_layout.setOnClickListener {
            onClickStart()
        }
        smallPlayIcon.setOnClickListener {
            onCoverOnClick()
        }
    }

    //    var progressMin: SeekBar? = null
    private var maxView: MultiPlayerView? = null
    val layoutCover = videoplayer_min_layout_cover
    val rootMinContainer = root_min_container!!
    val bufferLayout = video_loading_layout
    val bufferText = video_loading_text

    //关联到列表播放器
    private fun onAttachedListMinView() {
        videoPath?.apply {
            val listener = context as? MultiVideoPlayerProvider ?: return@apply
            maxView = listener.getMultiVideoView()
            maxView?.apply {
                onDetachedLastListMinView()
                minListWindowView = this@VideoPlayerWindowMinView
                onAttachedListMinView()
                setDataToVideo(videoPath)
            }
        }
    }


    private fun onClickStart() {
        maxView?.checkNetWorkPlay()
    }


    //开始准备播放
    fun videoOpenSuccess() {
        isPlayState = true
        layoutCover.isEnabled = false
        smallPlayIcon.visibility = View.GONE
        showError(false)
    }

    fun videoOpenStartPlay() {
        isPlayState = true
        layoutCover.isEnabled = false
        smallPlayIcon.isEnabled = false
        layoutCover.visibility = View.GONE
        smallPlayIcon.visibility = View.GONE
        showError(false)
//        progressMin?.visibility = View.VISIBLE
    }

    fun closeWindowMinView() {
        if (isPlayState) {
            isPlayState = false
            layoutCover.isEnabled = true
            smallPlayIcon.isEnabled = true
            layoutCover.visibility = View.VISIBLE
            smallPlayIcon.visibility = View.VISIBLE

//            progressMin?.visibility = View.GONE
            bufferLayout.visibility = View.GONE
            showError(false)
        }
        _i("closeWindowMinView")
    }

    fun setListStateInit() {
        if (isPlayState) {
            closeWindowMinView()
//            progressMin?.setOnSeekBarChangeListener(null)
            maxView?.onStopVideo()
        }
    }

    fun setVideoPath(videoPath: String) {
        this.videoPath = videoPath
    }


    fun setCover(url: String?) {
        layoutCover?.setImageURL(url)
    }

    private fun onCoverOnClick() {
        _i("videoplayer_min_layout_cover")
        videoPath?.apply {
            onAttachedListMinView()
            onClickStart()
        }
    }

    fun onAutoPlay() {
        _i("onAutoPlay")
        when (videoState) {
            VideoState.List -> {
                videoPath?.apply {
                    if (isWifiConnected(context)) {
                        onAttachedListMinView()
                        maxView?.autoPlay()
                    }
                }
            }
        }
    }

    fun showError(isShowError: Boolean) {
        video_error_layout.visiable(isShowError)
    }


//    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
//        super.onVisibilityChanged(changedView, visibility)
//        if (isPlayState) {
//            //onVisibilityChanged=8切到其它界面
//            //onVisibilityChanged=0切回来
//            _i("onVisibilityChanged=" + visibility)
//        }
//    }

}

