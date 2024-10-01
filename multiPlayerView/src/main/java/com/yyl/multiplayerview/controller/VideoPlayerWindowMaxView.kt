package com.tiaooo.aaron.video.list.manager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.yyl.multiplayerview.controller.WindowLogic
import com.yyl.multiplayerview.impl.VideoState
import com.yyl.multiplayerview.utils.MySensorListener
import com.yyl.multiplayerview.utils._i

/**
 * Created by yyl on 2018/2/28.
 */
abstract class VideoPlayerWindowMaxView : WindowLogic {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //进度条线程
    private val handerProgress = HandlerProgress()
    private val sensorListener by lazy {
        MySensorListener(context).setSensorLandspace { isFullState ->
            if (isFullState) {
                if (playerBase.isPrepare() && playerBase.isPlaying) {
                    return@setSensorLandspace onWindowChangeEvent(false)
                }
            } else {
                if (playerBase.isPrepare() && playerBase.isPlaying) {
                    return@setSensorLandspace onWindowChangeEvent(true)
                }
            }
            return@setSensorLandspace false
        }
    }

    private var isAttachMinWindows = false
    //    private var isAttachMaxWindows = true
    private var isAttachWindowList = false

    var isResume = false

    //关联列表小窗口
    fun onAttachedListMinView() {
        if (!isAttachMinWindows)
            movePlayerToListMinView()
    }

    private fun changeListToSmall() {
        if (!isFullScreenState) return
        isFullScreenState = false
        changeWindowsState()
        movePlayerToListMinView()
    }

    private fun movePlayerToListMinView() {
        _i("movePlayerToListMinView    ")
        visibility = View.GONE
        controllerMaxView.dettachWindow()
        //分离view容器
        checkListVideoRemoveParentView()
        controllerMinView.closeAll()
        controllerMinView.attachWindow()
        //加入小屏容器
        minListWindowView?.rootMinContainer?.addView(videoPlayerView)
//        isAttachMaxWindows = false
        isAttachMinWindows = true
        isAttachWindowList = true
    }

    private fun changeListToMax() {
        if (isFullScreenState) return
        isFullScreenState = true
        changeWindowsState()

        //分离view容器
        checkListVideoRemoveParentView()
        //关闭小窗口 UI
        controllerMinView.dettachWindow()
        //加入大屏容器
        rootMaxContainer.addView(videoPlayerView)
        //关联控制器
        visibility = View.VISIBLE
        controllerMaxView.attachWindow()
//        isAttachMaxWindows = true
        isAttachMinWindows = false
    }

    //分离列表小窗口
    override fun onDetachedLastListMinView() {
        _i("onDetachedLastListMinView    $isAttachWindowList")

        if (!isAttachWindowList)
            return
        //关闭小屏播放器窗口
        controllerMinView.dettachWindow()
        minListWindowView?.closeWindowMinView()
        minListWindowView = null
        if (isFullScreenState) {//如果是大屏就恢复小屏状态
            isFullScreenState = false
            //这时只切换竖屏     播放器还留在MaxView里面
            changeWindowsState()
            visibility = View.GONE
        }
        checkListVideoRemoveParentView()
        isAttachWindowList = false
        onStopVideo()

    }

    /**
     * 分离view容器
     * 防止重复增加
     */
    private fun checkListVideoRemoveParentView() {
//        isAttachMaxWindows = false
        isAttachMinWindows = false
        if (videoPlayerView.parent != null && videoPlayerView.parent is ViewGroup) {
            val parentViewGroup = videoPlayerView.parent as ViewGroup
            parentViewGroup?.removeAllViews()
        }

    }

    private fun changeVideoToMax() {
        if (isFullScreenState) return
        isFullScreenState = true
        changeWindowsState()
        controllerMinView.dettachWindow()
        controllerMaxView.attachWindow()
    }

    private fun changeVideoToMin() {
        if (!isFullScreenState) return
        isFullScreenState = false
        changeWindowsState()
        controllerMinView.closeAll()
        controllerMinView.attachWindow()
        controllerMaxView.dettachWindow()
        controllerMaxView.closeAll()
    }

    var isResumeStart = false
    fun resumeVideo() {
        controllerMinView.isEnabled = true
        if (isResumeStart) {
            isResumeStart = false
            playerBase.start()
        }
    }

    fun pauseVideo() {
        if (playerBase.isPlaying) {
            isResumeStart = true
        }
        controllerMinView.closeAll()
        controllerMinView.isEnabled = false
        playerBase.pause()
    }

    fun onPause() {
        isResume = false
        sensorListener.onPause()
        playerBase.onPause()
    }

    fun onStart() {
        playerBase.onStart()
    }

    fun onResume() {
        isResume = true
        sensorListener.onResume()
        playerBase.onResume()
        handerProgress.sendEmptyMessage(0)
    }

    fun onStop() {
        playerBase.onActivityStop()
        playerBase.onStop()
    }

    fun onStopVideo() {
        playerBase.onStop()
    }


    fun onBackPressed(): Boolean {
        return if (isFullScreenState) {
            when (videoState) {
                VideoState.List -> {
                    changeListToSmall()
                }
                VideoState.MinToMax -> {
                    changeVideoToMin()
                }
            }
            true
        } else {
            false
        }
    }


    @SuppressLint("HandlerLeak")
    inner class HandlerProgress : Handler() {
        override fun handleMessage(msg: Message?) {
            if (isResume) {
                sendEmptyMessageDelayed(0, 1000)
                if (isVideoOpened && playerBase.isPrepare()) {
                    controllerMaxView.onProgress()
                    controllerMinView.onProgress()
                }
            }
        }
    }


    override fun onBackEvent(isFullState: Boolean) {
        if (isFullState) {
            when (videoState) {
                VideoState.List -> {
                    changeListToSmall()
                }
                VideoState.MinToMax -> {
                    changeVideoToMin()
                }
                VideoState.Full -> {
                    aty?.finish()
                }
            }
        } else {
            aty?.onBackPressed()
        }
    }


    //isFullState当前屏幕状态是小屏就false
    override fun onWindowChangeEvent(isFullState: Boolean): Boolean {
        if (isFullState) {
            when (videoState) {
                VideoState.List -> {
                    changeListToSmall()
                    return true
                }
                VideoState.MinToMax -> {
                    changeVideoToMin()
                    return true
                }
            }
        } else {
            when (videoState) {
                VideoState.List -> {
                    changeListToMax()
                    return true
                }
                VideoState.MinToMax -> {
                    changeVideoToMax()
                    return true
                }
            }
        }
        return false
    }


    fun showCoverState(uriCache: String?) {
        // maxLayoutCover.setImageURI(uriCache)
        maxLayoutCover.visibility = View.VISIBLE
    }

    fun finishBack() {
        maxLayoutCover.visibility = View.VISIBLE
        playerBase.onStop()
    }

    abstract fun autoPlay()
}