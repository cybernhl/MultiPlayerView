package com.yyl.multiplayerview.utils

import android.util.Log
import android.view.View


inline fun generateTime(time: Int): String {


    return ""
}


inline fun View.visiable(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

inline fun View.visiable1(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.INVISIBLE
}

inline fun _i(msg: String) {
    Log.i("yyl", msg)
}