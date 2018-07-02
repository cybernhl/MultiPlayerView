package com.yyl.multiplayerview.base

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class BaseMaxCoverView : ImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    fun setImageURL(path: String?) {

    }
}