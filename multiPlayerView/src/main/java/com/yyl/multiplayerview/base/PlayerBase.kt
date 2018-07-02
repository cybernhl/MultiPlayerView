package com.yyl.multiplayerview.base

import android.view.View
import android.widget.MediaController

interface PlayerBase : MediaController.MediaPlayerControl {
    fun isPrepare(): Boolean
    fun canControl(): Boolean
    fun startPlay()

    fun setPath(path: String)
    fun setMediaListenerEvent(mediaListenerEvent: MediaListenerEvent)

    fun onStop()


    fun onPause()
    fun onStart()
    fun onResume()
    fun onActivityStop()
    fun getInstanceView():View

}