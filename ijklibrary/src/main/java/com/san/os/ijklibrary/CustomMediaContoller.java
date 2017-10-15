package com.san.os.ijklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.san.os.ijklibrary.R;

import java.util.Formatter;
import java.util.Locale;


/**
 * 视频控制器
 */
public class CustomMediaContoller extends RelativeLayout implements IMediaController {


    private static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 5000;
    private static final int MESSAGE_SHOW_PROGRESS = 2;
    private static final int PAUSE_IMAGE_HIDE = 3;

    //直播状态 1预告 2直播 3转码 4录播
    public static final int STYLE_ADVANCE = 1;
    public static final int STYLE_LIVE = 2;
    public static final int STYLE_TRANSCODE = 3;
    public static final int STYLE_REBROADCAST = 4;  //普通视频


    private int mStyle = STYLE_REBROADCAST;
    private boolean isShow;
    private long duration;
    private MediaController.MediaPlayerControl mVideoPlayer;
    private IKJContronleronClickListener mIKJContronleronClickListener;

    private SeekBar seekBar;
    AudioManager audioManager;


    private boolean isSound;
    private boolean isDragging;


    private boolean isShowContoller;
    private ImageView sound, mFullScreenBtn, mPlay, mBack;
    private TextView mDurrenTime, mTotalTime, mTitle;
    private Context context;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEW_HIDE:
                    isShow = false;
                    setVisibility(View.GONE);
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
                case PAUSE_IMAGE_HIDE:
                    break;
            }
        }
    };

    public CustomMediaContoller(Context context) {
        super(context);
        init(context);
    }

    public CustomMediaContoller(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomMediaContoller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setStyle(int style){
        mStyle = style;//屏蔽进度条用
        if (mStyle == STYLE_LIVE) {
            sound.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            mTotalTime.setVisibility(View.INVISIBLE);
            mDurrenTime.setVisibility(View.INVISIBLE);
            mPlay.setVisibility(View.INVISIBLE);
        }
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.media_contoller, this, true);
        setVisibility(View.GONE);
        isShow = false;
        isDragging = false;

        isShowContoller = false;
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        initView();
        initAction();
    }

    public void initView() {
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        mTotalTime = (TextView) findViewById(R.id.all_time);
        mDurrenTime = (TextView) findViewById(R.id.time);
        mFullScreenBtn = (ImageView) findViewById(R.id.full);
        mBack = (ImageView) findViewById(R.id.btn_player_back);
        mTitle = (TextView) findViewById(R.id.txt_player_title);
        sound = (ImageView) findViewById(R.id.sound);
        mPlay = (ImageView) findViewById(R.id.player_btn);
        mPlay.setSelected(true);
    }

    public void setIKJContronleronClickListener(IKJContronleronClickListener listener) {
        mIKJContronleronClickListener = listener;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setHeaderInfoVisible(boolean isVisible) {
        mBack.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        mTitle.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void onStart() {
        setVisibility(View.GONE);
        mPlay.setSelected(true);
    }

    public void pause(boolean isByControler) {
        mPlay.setSelected(false);
        if (mIKJContronleronClickListener != null) {
            mIKJContronleronClickListener.pauseByControler(isByControler);
        }

    }

    public void reStart(boolean isByControler) {
        mPlay.setSelected(true);
        handler.sendEmptyMessageDelayed(PAUSE_IMAGE_HIDE, 100);
        if (mIKJContronleronClickListener != null) {
            mIKJContronleronClickListener.startByControler(isByControler);
        }
    }


    private void initAction() {
        isSound = false;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                mDurrenTime.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                show();
                handler.removeMessages(SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                if (mIKJContronleronClickListener != null) {
                    mIKJContronleronClickListener.seekToByControler((int) (duration * seekBar.getProgress() * 1.0f / 100));
                }
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isDragging = false;
                handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
                show();
            }
        });


        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSound) {
                    //静音
                    sound.setImageResource(R.mipmap.sound_mult_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                } else {
                    //取消静音
                    sound.setImageResource(R.mipmap.sound_open_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                isSound = !isSound;
            }
        });


        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoPlayer.isPlaying()) {
                    pause(true);
                } else {
                    reStart(true);
                }
            }
        });

        mFullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLandScape) {
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }

        });

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLandScape) {
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    ((Activity) getContext()).finish();
                }
            }
        });
    }

    @Override
    public void hide() {
        if (isShow) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(SET_VIEW_HIDE);
            setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void setAnchorView(View view) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }


    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        this.mVideoPlayer = player;
    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        if (!isShowContoller) {
            isShowContoller = !isShowContoller;
            return;
        }
        isShow = true;
        setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
    }

    @Override
    public void showOnce(View view) {
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }


    private void setProgress() {
        if (mStyle == STYLE_LIVE) {
            return;
        }
        if (isDragging) {
            return;
        }
        if (mVideoPlayer==null){
            return;
        }
        int position = mVideoPlayer.getCurrentPosition();
        int duration = mVideoPlayer.getDuration();
        Log.i("lulu_video", mVideoPlayer.getCurrentPosition() + "");
        this.duration = duration;

        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = mVideoPlayer.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        if (mTotalTime != null)
            mTotalTime.setText(stringForTime(duration));
        if (mDurrenTime != null)
            mDurrenTime.setText(stringForTime(position));
    }

    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    private boolean mIsLandScape;

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mIsLandScape = true;
            mFullScreenBtn.setSelected(true);
        } else {
            mFullScreenBtn.setSelected(false);
            mIsLandScape = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }
}
