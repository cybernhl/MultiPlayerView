package com.yyl.multiplayerview.impl

import com.yyl.multiplayerview.MultiPlayerView


/**
 * Created by yyl on 2018/2/28.
 */
interface MultiVideoPlayerProvider {
    fun getMultiVideoView(): MultiPlayerView
}