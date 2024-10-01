package com.yyl.multiplayerview.manager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.yyl.multiplayerview.R
import com.yyl.multiplayerview.base.PlayerBase
import kotlinx.android.synthetic.main.yyl_videoplayer.view.*


/**
 * Created by yyl on 2018/2/27.
 */
class VideoPlayerView : FrameLayout {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        LayoutInflater.from(context).inflate(R.layout.yyl_videoplayer, this)
    }

    val controllerMinView = media_controll_small

    fun setVideoPlayerView(playerBase: PlayerBase) {
        addView(playerBase.getInstanceView(), 0, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        media_controll_small.videoPlayer = playerBase
    }



}