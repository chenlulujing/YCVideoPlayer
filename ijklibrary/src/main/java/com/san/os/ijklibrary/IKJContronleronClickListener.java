package com.san.os.ijklibrary;

/**
 * @author luluc@yiche.com
 * @Description 视频播放器的控制器点击事件
 * @date 2017-01-05 09:53
 */

public interface IKJContronleronClickListener {
    void pauseByControler(boolean isByContronler);
    void startByControler(boolean isByContronler);
    void seekToByControler(int postion);
}
