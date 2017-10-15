package com.san.os.ijklibrary;

/**
 * @author luluc@yiche.com
 * @Description 视频播放器事件回调
 * @date 2017-01-05 09:53
 */

public interface IKJVideoEvnetListener {
    void onError();
    void onVideoSizeChangedListener();
    void onSeekCompleteListener();
    void onCompletionListener(boolean byMobileNet);
    void onPreparedListener();
}
