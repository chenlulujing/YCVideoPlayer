package com.san.os.ijklibrary.ycvideo;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-09-06 08:17
 */

public interface YCVideoPlayListener extends IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener {
}
