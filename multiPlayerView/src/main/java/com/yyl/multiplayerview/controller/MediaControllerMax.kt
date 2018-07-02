package com.yyl.multiplayerview.controller

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.yyl.multiplayerview.utils.visiable
import com.yyl.multiplayerview.utils.visiable1
import kotlinx.android.synthetic.main.yyl_videoplayer_max.view.*

/**
 * Created by yyl on 2018/2/28.
 */
class MediaControllerMax : FrameLayout, ControllerImpl {

    private val lookTime = 4000L

    var videoState = VideoState.IDLE
    var videoPlayer: PlayerBase? = null
    var onClickListenerEvent: VideoPlayerOnClickEventListener? = null
    private val mGestureDetector by lazy { GestureDetector(context, MyGestureListener()) }
    private var isShow = false

    private var mDragging = false
    private var showTimeStart = 0L
    var isRefreshTime = false
    private val mSeekListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(bar: SeekBar) {
            mDragging = true
        }

        override fun onProgressChanged(bar: SeekBar, progressChanged: Int, fromuser: Boolean) {
            if (!fromuser)
                return
            videoPlayer?.apply {
                if (!isRefreshTime) {
                    isRefreshTime = true
                    postDelayed({
                        setTimeText(duration * progressChanged / 1000, duration)
                        max_seekbar?.progress = progressChanged
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
        LayoutInflater.from(context).inflate(R.layout.yyl_videoplayer_max, this)
        initOnClick()
        max_seekbar.setOnSeekBarChangeListener(mSeekListener)
    }

    val fastForwardView = max_fast_forward

    override fun show() {
        showTimeStart = System.currentTimeMillis()
        if (isShow) return
        isShow = true
        startOpenAnimation()
    }


    override fun hide() {
        if (isShow) {
            isShow = false
            startHideAnimation()
        }
    }

    override fun closeAll() {
        isShow = false
        max_up_layout.visibility = INVISIBLE
        max_conter_layout.visibility = INVISIBLE
        max_down_layout.visibility = INVISIBLE
    }

    override fun attachWindow() {
        visibility = View.VISIBLE
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
        if (!isShow || mDragging)
            return
        videoPlayer?.apply {
            max_seekbar?.apply {
                if (duration > 0) {
                    val pos = 1000L * currentPosition / (duration + 1)
                    progress = pos.toInt()
                    secondaryProgress = bufferPercentage * 10
                }
            }
            max_play_pause.isSelected = isPlaying
            if (!isMoveSpeed)
                setTimeText(currentPosition, duration)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTimeText(currentPosition: Int, duration: Int) {
        max_time?.text = "${generateTime(currentPosition)} / ${generateTime(duration)}"
    }

    fun setFileName(fileName: String?) {
        max_title?.text = fileName
    }


    private fun initOnClick() {
        max_back.setOnClickListener { onClickListenerEvent?.onBackEvent(true) }
        max_play_pause.setOnClickListener { doPauseClick() }
    }


    private fun doPauseClick() {
        videoPlayer?.apply {
            if (isPlaying) {
                pause()
            } else {
                start()
            }
            max_play_pause?.isSelected = isPlaying
        }

    }


    private fun startOpenAnimation() {
        max_up_layout.visibility = View.VISIBLE
        max_conter_layout.visibility = View.VISIBLE
        max_down_layout.visibility = View.VISIBLE

        max_conter_layout.clearAnimation()
        max_conter_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_fade))
        max_up_layout.clearAnimation()
        max_up_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_up))
        max_down_layout.clearAnimation()
        max_down_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_down))
    }

    private fun startHideAnimation() {
        val out_from_fade = AnimationUtils.loadAnimation(context, R.anim.out_from_fade)
        out_from_fade.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                max_up_layout?.visibility = INVISIBLE
                max_conter_layout?.visibility = INVISIBLE
                max_down_layout?.visibility = INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        max_conter_layout.clearAnimation()
        max_conter_layout.startAnimation(out_from_fade)

        max_up_layout.clearAnimation()
        max_up_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_from_up))

        max_down_layout.clearAnimation()
        max_down_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_from_down))
    }

    var newposition = 0
    var fastShow = false
    fun setSpeedTimelayout(move: Float) {
        videoPlayer?.apply {
            if (isPrepare() && canControl()) {
                val moveLimit = currentPosition + (move * 40).toInt()
                //总位置数乘 现在位置百分比
                newposition = when {
                    moveLimit > duration -> duration
                    moveLimit < 0 -> 0
                    else -> moveLimit
                }
                isMoveSpeed = true
                setTimeText(newposition, duration)
                max_fast_forward.visiable(true)
                max_fast_forward.text = "${generateTime(newposition)}  /  ${generateTime(duration)}"
                if (isShow) {
                    fastShow = true
                    hide()
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isMoveSpeed) {
                        videoPlayer?.apply {
                            seekTo(newposition)
                            if (fastShow) {
                                fastShow = false
                                show()
                            }
                        }
                        isMoveSpeed = false
                    }
                    max_fast_forward?.visiable1(false)
                }
            }
        }
        return mGestureDetector.onTouchEvent(event)
    }

    var isMoveSpeed = false

    //总位置数乘 现在位置百分比
    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {


        override fun onDown(e: MotionEvent): Boolean {
            isMoveSpeed = false
            return true
        }


        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {

            e1?.apply {
                e2?.apply {
                    setSpeedTimelayout(e2.x - e1.x)
                }
            }
            return true
        }


        //单击
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (isShow) {
                hide()
            } else {
                show()
            }
            return true

        }

        //双击了
        override fun onDoubleTap(e: MotionEvent): Boolean {
            doPauseClick()
            return true
        }


    }
}