package com.yyl.multiplayerview.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

fun isNetworkConnected(context: Context?): Boolean {
    if (context != null) {
        val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null) {
            return mNetworkInfo.isConnected
        }
    }
    return false
}

fun isWifiConnected(context: Context): Boolean {
    val connectMgr = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return false
    val info = connectMgr.activeNetworkInfo
    return info != null && info.type == ConnectivityManager.TYPE_WIFI && info.isConnected
}

fun checkNetWorkOrToast(context: Context): Boolean {
    if (isNetworkConnected(context)) {
        return false
    } else {
        Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show()
        return true
    }
}