package com.tiaooo.aaron.video.list.manager

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.yyl.multiplayerview.MultiPlayerView
import com.yyl.multiplayerview.R
import com.yyl.multiplayerview.base.MediaListenerEvent
import com.yyl.multiplayerview.base.PlayerBase
import com.yyl.multiplayerview.impl.VideoPlayerOnClickEventListener
import com.yyl.multiplayerview.impl.VideoState
import com.yyl.multiplayerview.utils._i
import com.yyl.multiplayerview.utils.visiable
import kotlinx.android.synthetic.main.yyl_videoplayer_window_max.view.*

/**
 * Created by yyl on 2018/3/19.
 */
abstract class WindowLogic : FrameLayout, VideoPlayerOnClickEventListener {
    private val videoTag = "VideoPlayerWindowMaxView"


    private val mediaListenerEvent = object : MediaListenerEvent {
        override fun eventEvent(event: Int, show: Boolean) {
            post {
                if (!isVideoOpened) return@post
                when (event) {
                    MediaListenerEvent.MEDIA_INFO_BUFFING_START -> {
                        showLoading(true)
                    }
                    MediaListenerEvent.MEDIA_INFO_BUFFING_END -> {
                        showLoading(false)
                    }
                    MediaListenerEvent.MEDIA_INFO_VIDEO_RENDERING_START,
                    MediaListenerEvent.MEDIA_INFO_AUDEO_RENDERING_START
                    -> {
                        onVideoOpenSuccessStartPlay()
                        showLoading(false)
                    }
                }
            }
        }

        override fun eventPlayInit(openSuccess: Boolean) {
            isVideoOpened = openSuccess
            //这里不能在线程中执行必须同步
            if (openSuccess) {
                minListWindowView?.showError(false)
                video_error_layout.visiable(false)
                when (videoState) {
                    VideoState.List, VideoState.MinToMax -> {
                        onVideoOpenSuccess()
                        showLoading(true)
                    }
                }
            } else {
                when (videoState) {
                    VideoState.List -> {
                        onDetachedLastListMinView()
                    }
                    VideoState.MinToMax -> {
                        finishVideo()
                    }
                    VideoState.Full -> {
                        aty?.finish()
                    }
                }

            }
        }

        override fun eventStop() {
            if (!isVideoOpened) return
            post {
                eventPlayInit(false)
            }
        }

        override fun eventError(show: Boolean, msg: String, error: Int) {
            post {
                if (!isVideoOpened) return@post
                showLoading(false)
                closeControllerView()
                video_error_layout.visiable(show)
                minListWindowView?.showError(show)
            }
        }

    }


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val playerBase by lazy { getVideoPlayerBase(context) }

    init {
        LayoutInflater.from(context).inflate(R.layout.yyl_videoplayer_window_max, this)
        playerBase.setMediaListenerEvent(mediaListenerEvent)
        //大屏
        controller_max.videoPlayer = playerBase
        controller_max.onClickListenerEvent = this
        //小屏
        video_player_view.setVideoPlayerView(playerBase)
        video_player_view.controllerMinView.onClickListenerEvent = this
        video_error_layout.setOnClickListener { checkNetWorkPlay() }
        videoplayer_max_layout_cover.setOnClickListener { checkNetWorkPlay() }
    }


    val aty by lazy {
        val ctx = context
        when (ctx) {
            is Activity -> ctx
            is ContextThemeWrapper -> ctx.baseContext as Activity
            else -> ctx as? Activity
        }
    }
    //全屏状态
    var isFullScreenState = false
    //视频是否被打开了
    var isVideoOpened = false

    var videoState = VideoState.IDLE

    //-----------------------基础控件--------------------------------


    val controllerMaxView = controller_max
    val controllerMinView = video_player_view.controllerMinView

    val videoPlayerView = video_player_view

    val rootMaxContainer = video_container_max

    val maxLayoutCover = videoplayer_max_layout_cover
    var minListWindowView: VideoPlayerWindowMinView? = null
    //-----------------------数据交互逻辑--------------------------------


    var isDownloadVideo = false//视频是否下载过了

    val dp10 = 20

    private fun finishVideo() {
        if (isFullScreenState) {
            isFullScreenState = false
            changeWindowsState()
        }
        maxLayoutCover.isClickable = true
        maxLayoutCover.visiable(true)
        closeControllerView()
        _i("finishVideo")
    }

    private fun closeControllerView() {
        controllerMinView.dettachWindow()
        controllerMaxView.dettachWindow()
    }

    //关联播放器成功
    fun onVideoOpenSuccess() {
        maxLayoutCover.isClickable = false
        minListWindowView?.apply {
            videoOpenSuccess()
        }
    }

    //可能会被重复调用 2 次
    fun onVideoOpenSuccessStartPlay() {
        when (videoState) {
            VideoState.List -> {
                minListWindowView?.videoOpenStartPlay()
            }
            VideoState.MinToMax -> {
                maxLayoutCover.visibility = View.GONE
            }
        }
        if (isFullScreenState) {
            controllerMinView.dettachWindow()
            controllerMaxView.attachWindow()
        } else {
            controllerMinView.attachWindow()
            controllerMaxView.dettachWindow()
        }
        _i("onVideoOpenSuccessStartPlay")
    }

    //旋转屏幕
    fun changeWindowsState() {
        if (context is Activity) {
            val activity = context as Activity
            if (isFullScreenState) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    fun showLoading(isShow: Boolean) {
        _i("showLoading   $isShow")
        if (isShow) {
            when (videoState) {
                VideoState.List -> {
                    if (isFullScreenState) {
                        video_loading_layout.visiable(true)
                        minListWindowView?.bufferLayout?.visiable(false)
                    } else {
                        video_loading_layout.visiable(false)
                        minListWindowView?.bufferLayout?.visiable(true)
                    }
                }
                else -> {
                    video_loading_layout.visiable(true)
                }
            }
        } else {
            video_loading_layout.visiable(false)
            when (videoState) {
                VideoState.List -> {
                    minListWindowView?.bufferLayout?.visiable(false)
                }
            }
        }
    }

    fun setBufferingText(buffing: String) {
        when (videoState) {
            VideoState.List -> {
                if (isFullScreenState) {
                    video_loading_text?.text = buffing
                } else {
                    minListWindowView?.bufferText?.text = buffing
                }
            }
            else -> {
                video_loading_text?.text = buffing
            }
        }
    }
    //--------------------------列表播放器----------------------------


    fun attachRecyclerView(recyclerView1: RecyclerView?) {
        recyclerView1?.removeOnScrollListener(onScrollVideoEvent)
        recyclerView1?.addOnScrollListener(onScrollVideoEvent)
    }

    val locationInWindow = IntArray(2)
    var currentState = 0
    private val onScrollVideoEvent by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (isFullScreenState) return
                if (playerBase.isPrepare()) return
                currentState = newState
                post({
                    if (currentState == RecyclerView.SCROLL_STATE_IDLE && isWifiAutoPlayListVideo()) {
                        val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
                        val first = layoutManager.findFirstCompletelyVisibleItemPosition()
                        if (first == -1) {//没一个全显示
                            val firstVisiable = layoutManager.findFirstVisibleItemPosition()
                            val isPlay = startPlayListItem(recyclerView, layoutManager, firstVisiable)
                            if (!isPlay) startPlayListItem(recyclerView, layoutManager, firstVisiable + 1)
                            return@post
                        }
                        if (recyclerView.adapter?.getItemViewType(first) == MultiPlayerView.STATE_VIDEO && !playerBase.isPrepare()) {
                            _i("onAutoPlay    first=$first")
                            layoutManager.findViewByPosition(first)?.findViewWithTag<VideoPlayerWindowMinView>(videoTag)?.onAutoPlay()
                        }
                    }
                })

            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (isFullScreenState) return
                if (!isVideoOpened) return
                minListWindowView?.apply {
                    if (playerBase.isPrepare()) {
                        post {
                            getLocationInWindow(locationInWindow)
                            val y = locationInWindow[1]
                            if (y < 0) {//滑动到最上面了
                                if (height * 1 / 4 + 40 < Math.abs(y)) {// 在滑至顶部1/3高度位置回收view  40是状态栏的大概高度
                                    playerBase.onStop()
                                }
                            } else if (y > (recyclerView?.height ?: 0-(height / 2)
                                    +40)) {// 在滑至底部1/3高度位置回收view  40是状态栏的大概高度
                                playerBase.onStop()
                            }
                        }
                    }
                }
            }
        }
    }


    //播放可见到的item视频
    private inline fun startPlayListItem(recyclerView: RecyclerView, layoutManager: LinearLayoutManager, firstVisiable: Int): Boolean {
        if (!playerBase.isPrepare()) {//选一个未显示全的item播放
            if (recyclerView.adapter?.getItemViewType(firstVisiable) == MultiPlayerView.STATE_VIDEO) {
                var videoView = layoutManager.findViewByPosition(firstVisiable)?.findViewWithTag<VideoPlayerWindowMinView>(videoTag)
                videoView?.apply {
                    getLocationInWindow(locationInWindow)
                    val y = locationInWindow[1]
                    if (y < 0) {//滑动到最上面了
                        if (Math.abs(y) < dp10) {// 在小于顶部dp10高度位置还能看到播放器就播放视频
                            onAutoPlay()
                            _i("onAutoPlay   在小于顶部dp10高度位置 ")
                            return true
                        }
                    } else {
                        onAutoPlay()//下面可见的直接播放
                        _i("onAutoPlay   下面可见直接播放")
                        return true
                    }
                }
            }
        }
        return false
    }


    abstract fun checkNetWorkPlay()
    abstract fun onDetachedLastListMinView()
    abstract fun isWifiAutoPlayListVideo(): Boolean
    abstract fun getVideoPlayerBase(context: Context): PlayerBase
}