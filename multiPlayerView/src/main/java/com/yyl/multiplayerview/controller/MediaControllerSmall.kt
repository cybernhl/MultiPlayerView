package com.yyl.multiplayerview.controller

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.SeekBar
import com.yyl.multiplayerview.R
import com.yyl.multiplayerview.base.PlayerBase
import com.yyl.multiplayerview.impl.ControllerImpl
import com.yyl.multiplayerview.impl.VideoPlayerOnClickEventListener
import com.yyl.multiplayerview.impl.VideoState
import com.yyl.multiplayerview.utils.generateTime
import kotlinx.android.synthetic.main.yyl_videoplayer_small.view.*

/**
 * Created by yyl on 2018/2/28.
 */
class MediaControllerSmall : FrameLayout, ControllerImpl {
    private val lookTime = 4000L

    var videoState = VideoState.IDLE

    var videoPlayer: PlayerBase? = null
    var onClickListenerEvent: VideoPlayerOnClickEventListener? = null

    private var isShow = false
    private var mDragging = false

    var showTimeStart = 0L
    var isRefreshTime = false
    private val mSeekListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(bar: SeekBar) {
            mDragging = true
        }

        override fun onProgressChanged(bar: SeekBar, progressChange: Int, fromuser: Boolean) {
            if (!fromuser)
                return
            videoPlayer?.apply {
                if (!isRefreshTime) {
                    isRefreshTime = true
                    postDelayed({
                        setTimeText(duration * progressChange / 1000, duration)
                        min_progress?.progress = progressChange
                        isRefreshTime = false
                    }, 50)
                }
            }
        }

        override fun onStopTrackingTouch(bar: SeekBar) {
            videoPlayer?.apply {
                seekTo(duration * bar.progress / 1000)
            }
            show()
            mDragging = false
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        LayoutInflater.from(context).inflate(R.layout.yyl_videoplayer_small, this)
        initOnClick()
        min_seekBar.setOnSeekBarChangeListener(mSeekListener)
        //min_seekBar.setPadding(0, 0, 0, 0)
    }

    override fun show() {
        when (videoState) {
            VideoState.Full -> return
        }
        showTimeStart = System.currentTimeMillis()
        if (isShow) return
        isShow = true
        startOpenAnimation()
    }

    override fun hide() {
        if (isShow) {
            isShow = false
            mDragging = false
            startHideAnimation()
        }

    }

    override fun closeAll() {
        isShow = false
        min_conter_layout?.visibility = INVISIBLE
        min_down_layout?.visibility = INVISIBLE
        min_progress?.visibility = View.VISIBLE
    }

    override fun attachWindow() {
        visibility = View.VISIBLE
        if (!isShow) {
            min_progress?.visibility = View.VISIBLE
        }
    }

    override fun dettachWindow() {
        visibility = View.GONE
    }

    override fun onProgress() {
        if (!mDragging && isShow && System.currentTimeMillis() - showTimeStart > lookTime) {
            hide()
        }
        setProgress()
    }


    private fun setProgress() {
        if (mDragging)
            return
        videoPlayer?.apply {
            if (duration <= 0L) return
            val pos = 1000L * currentPosition / (duration + 1)
            min_seekBar?.apply {
                progress = pos.toInt()
                secondaryProgress = bufferPercentage * 10
            }
            min_progress?.apply {
                progress = pos.toInt()
                secondaryProgress = bufferPercentage * 10
            }
            if (isShow) {
                min_play_pause?.isSelected = isPlaying
                setTimeText(currentPosition, duration)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTimeText(currentPosition: Int, duration: Int) {
        min_time?.text = "${generateTime(currentPosition)} / ${generateTime(duration)}"
    }

    private fun initOnClick() {
        min_play_pause.setOnClickListener {
            doPauseClick()
            show()
        }
        min_change.setOnClickListener { onClickListenerEvent?.onWindowChangeEvent(false) }
        setOnClickListener {
            videoPlayer?.apply {
                if (!isPrepare()) {
                    dettachWindow()
                    return@setOnClickListener
                }
            }
            if (isShow) {
                hide()
            } else {
                show()
            }
        }
    }


    private fun doPauseClick() {
        videoPlayer?.apply {
            if (isPlaying) {
                pause()
            } else {
                start()
            }
            min_play_pause.isSelected = isPlaying
        }

    }


    private fun startOpenAnimation() {

        min_conter_layout.visibility = View.VISIBLE
        min_down_layout.visibility = View.VISIBLE
        min_progress.visibility = View.INVISIBLE

        min_conter_layout.clearAnimation()
        min_conter_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_fade))

        min_down_layout.clearAnimation()
        min_down_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_down))

    }

    private fun startHideAnimation() {
        val outFromFade = AnimationUtils.loadAnimation(context, R.anim.out_from_fade)
        outFromFade.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                closeAll()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        min_conter_layout.clearAnimation()
        min_conter_layout.startAnimation(outFromFade)

        min_down_layout.clearAnimation()
        min_down_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_from_down))
    }


}