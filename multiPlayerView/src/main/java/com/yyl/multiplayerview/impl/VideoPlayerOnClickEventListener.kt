package com.yyl.multiplayerview.impl

/**
 * Created by yyl on 2018/3/3.
 */
interface VideoPlayerOnClickEventListener {
    fun onBackEvent(isFullState: Boolean)

    fun onWindowChangeEvent(isFullState: Boolean):Boolean
}