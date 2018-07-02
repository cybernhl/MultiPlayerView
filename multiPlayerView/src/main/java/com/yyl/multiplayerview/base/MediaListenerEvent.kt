package com.yyl.multiplayerview.base

interface MediaListenerEvent {
    companion object {
        val PLAY_STATE = 10001
        val MEDIA_INFO_BUFFING_FILE = -9
        val MEDIA_INFO_BUFFING_NETWORK = -8
        val MEDIA_INFO_BUFFING_CACHE = -1000
        val MEDIA_INFO_BUFFING_START = -1001
        val MEDIA_INFO_BUFFING_END = -1002
        val MEDIA_INFO_VIDEO_RENDERING_START = -10009
        val MEDIA_INFO_AUDEO_RENDERING_START = -10008
    }

    //子线程
    fun eventEvent(actionEvent: Int, show: Boolean) //子线程

    //子线程
    fun eventStop()

    //子线程
    fun eventError(show: Boolean, msg: String, error: Int)

    /**
     * 初始化开始加载
     *
     * @param openSuccess true成功加载视频   false回到初始化 --> 比如显示封面图
     *
     * 主线程 即时调用
     */
    fun eventPlayInit(openSuccess: Boolean)

}