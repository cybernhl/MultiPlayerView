package com.yyl.multiplayerview

import android.content.Context
import android.util.AttributeSet
import com.tiaooo.aaron.video.list.manager.VideoPlayerWindowMaxView
import com.yyl.multiplayerview.base.PlayerBase

class MultiPlayerView : VideoPlayerWindowMaxView {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    companion object {
        val STATE_VIDEO = 10001
    }

    fun setDataToVideo(videoPath: String?) {
        videoPath?.apply { playerBase.setPath(videoPath) }
    }

    override fun getVideoPlayerBase(context: Context): PlayerBase {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun autoPlay() {

    }

    override fun checkNetWorkPlay() {

    }

    override fun isWifiAutoPlayListVideo(): Boolean {


        return true
    }
//    //关联跳星球数据
//    fun setDataToVideo(path: String,videoTitle:String,state: VideoState) {
//        _i("setCircleToVideo")
//        videoState =state
//        controllerMaxView.videoState = videoState
//        controllerMinView.videoState = videoState
//        if (isFull) {
//            isFullScreenState = true
//            controllerMaxView.attachWindow()
//        } else {
//            var isReloadCover = isReloadCoverUrl
//            //延迟一下动画
//            if (videoViewSurface.isJumpWindowsCurrent) {
//                //有正在播放的播放器进来
//                maxLayoutCover.setImageURIReplice(resultT?.cover)
//                isReloadCover = false
//            } else {
//                maxLayoutCover.visibility = View.VISIBLE
//            }
//            if (isShowAnimation) {
//                if (isReloadCover) {
//                    maxLayoutCover?.setImageURIReplice(resultT?.cover)
//                }
//            } else {
//                maxLayoutCover?.setImageURIReplice(resultT?.cover)
//            }
//            controllerMinView.attachWindow()
//        }
//        // controllerMaxView.setFileName("")
//        val path = resultT.video
//        isDownloadVideo = isDownLoadState(path)
//        playerBase.setPath(path)
//        autoPlay()
//    }
//
//
//    //关联列表数据
//    fun setListItemToVideo(task2Items: Task2Items?, where: String?) {
//        this.task2Item = task2Items
//        this.where = where
//        task2Items?.apply {
//            maxLayoutCover.visibility = View.GONE
//            isDownloadVideo = isDownLoadState(video)
//            showNext(false)
//            showLast(false)
//            setMirror(false)
//            setSpeed1X()
//            //  controllerMaxView.setFileName("")
//            playerBase.setPath(video)
//            videoState = VideoState.List
//            controllerMaxView.videoState = videoState
//            controllerMinView.videoState = videoState
//        }
//    }

}
