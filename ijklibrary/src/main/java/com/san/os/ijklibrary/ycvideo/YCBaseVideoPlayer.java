package com.san.os.ijklibrary.ycvideo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import com.san.os.ijklibrary.R;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-09-05 15:57
 */

public abstract class YCBaseVideoPlayer extends FrameLayout implements TextureView.SurfaceTextureListener, YCVideoPlayListener {

    private static final String TAG = YCBaseVideoPlayer.class.getSimpleName();

    private YCRenderView mRenderView;
    private FrameLayout mVideoContainer;
    private IYCMediaControler mControlView;

    public YCBaseVideoPlayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public YCBaseVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public YCBaseVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.base_video_view, this);
        mVideoContainer = (FrameLayout) findViewById(R.id.video_container);
        mControlView = initControlView();
    }

    /**
     * 定制controlView
     *
     * @return
     */
    protected abstract IYCMediaControler initControlView();

    protected abstract void  addControlView(FrameLayout videoContainer,IYCMediaControler controlView);

    private void generateVideo(Context context) {
        mRenderView = new YCRenderView(context);
        mRenderView.setSurfaceTextureListener(this);
        mVideoContainer.addView(mRenderView);//绑定view与media
        YCVideoPlayerManager.getInstance().setListener(this);
    }



    public void satartPlay(String url) {
        mControlView.showLoadingView();
        generateVideo(getContext());
        addControlView(mVideoContainer,mControlView);
        YCVideoPlayerManager.getInstance().startPlay(url);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        YCVideoPlayerManager.getInstance().bindView(new Surface(surface));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mControlView.showLoadingView();
        YCVideoPlayerManager.getInstance().onCompletion();
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        switch (i) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                mControlView.hideLoadingView();
                mControlView.show();
                break;
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + i1);
                break;
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                break;
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                break;
            case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                break;
            case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + i1);
                if (mRenderView != null)
                    mRenderView.setVideoRotation(i1);
                break;
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                break;
        }
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }
}
