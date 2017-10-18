package com.san.os.ijklibrary.ycvideo;

import android.media.AudioManager;
import android.text.TextUtils;

import java.io.IOException;

import tv.danmaku.ijk.media.player.AbstractMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-09-05 15:58
 */

public class YCVideoPlayerManager {

    private AbstractMediaPlayer mMediaPlayer;
    private static YCVideoPlayerManager mVideoManager;

//    private static class YCVideoPlayerManagerHodler {
//        public final static YCVideoPlayerManager instance = new YCVideoPlayerManager();
//    }
//
//    public static YCVideoPlayerManager getInstance() {
//        return YCVideoPlayerManagerHodler.instance;
//    }

    public static synchronized YCVideoPlayerManager getInstance() {
        if (mVideoManager == null) {
            mVideoManager = new YCVideoPlayerManager();
        }
        return mVideoManager;
    }


    private YCVideoPlayerManager() {
        initMeidaPlayer(null);
    }

    private void initMeidaPlayer(tv.danmaku.ijk.media.player.IjkLibLoader ijkLibLoader) {
        if (ijkLibLoader == null) {
            mMediaPlayer = new IjkMediaPlayer();
        } else {
            mMediaPlayer = new IjkMediaPlayer(ijkLibLoader);
        }


    }

    public void setListener(YCVideoPlayListener listener) {
        mMediaPlayer.setOnPreparedListener(listener);
        mMediaPlayer.setOnVideoSizeChangedListener(listener);
        mMediaPlayer.setOnCompletionListener(listener);
        mMediaPlayer.setOnErrorListener(listener);
        mMediaPlayer.setOnInfoListener(listener);
        mMediaPlayer.setOnBufferingUpdateListener(listener);
    }


    public void bindView(android.view.Surface surface) {
        mMediaPlayer.setSurface(surface);
    }


    public void startPlay(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.start();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object getMedia() {
        return mMediaPlayer;
    }

    public int getCurrentPosition() {
        return -1;
    }

    public int getDuration() {
        return -1;
    }


    public int getBufferPercentage() {
        return -1;
    }

    public boolean isPlaying() {
        return false;
    }


    public void onCompletion() {
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    /**
     * 资源释放
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }
}
