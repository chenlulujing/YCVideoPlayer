package com.san.os.ijklibrary.ycvideo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.san.os.ijklibrary.IKJContronleronClickListener;
import com.san.os.ijklibrary.R;

import java.util.Formatter;
import java.util.Locale;


/**
 * 视频控制器
 */
public class YCCustomMediaContolView extends RelativeLayout implements IYCMediaControler, View.OnClickListener {


    private static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 5000;
    private static final int MESSAGE_SHOW_PROGRESS = 2;
    private static final int PAUSE_IMAGE_HIDE = 3;


    private boolean isShow;
    private long duration;
    private ProgressBar mLoadingView;
    private IKJContronleronClickListener mIKJContronleronClickListener;

    private SeekBar seekBar;
    AudioManager audioManager;


    private boolean isSound;
    private boolean isDragging;


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

    public YCCustomMediaContolView(Context context) {
        super(context);
        init(context);
    }

    public YCCustomMediaContolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public YCCustomMediaContolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.media_contoller, this, true);
        setVisibility(View.GONE);
        isShow = false;
        isDragging = false;

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
        mLoadingView = (ProgressBar) findViewById(R.id.loading);
        mPlay.setSelected(true);

//        setOnClickListener(this);
    }


    public void setTitle(String title) {
        mTitle.setText(title);
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


        sound.setOnClickListener(new OnClickListener() {
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


        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (YCVideoPlayerManager.getInstance().isPlaying()) {
                    pause(true);
                } else {
                    reStart(true);
                }
            }
        });

        mFullScreenBtn.setOnClickListener(new OnClickListener() {
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

    }


    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        Log.i("ycvideo","show_controlview");
        isShow = true;
        setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
    }

    @Override
    public void showOnce(View view) {
    }

    @Override
    public void showLoadingView() {
        mLoadingView.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        mLoadingView.setVisibility(INVISIBLE);
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }


    private void setProgress() {
        if (isDragging) {
            return;
        }
        if (YCVideoPlayerManager.getInstance().getMedia() == null) {
            return;
        }
        int position = YCVideoPlayerManager.getInstance().getCurrentPosition();
        int duration = YCVideoPlayerManager.getInstance().getDuration();
        this.duration = duration;

        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = YCVideoPlayerManager.getInstance().getBufferPercentage();
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

    @Override
    public void onClick(View v) {
        if (v == this) {
            if (isShowing()) {
                hide();
            } else {
                show();
            }
        }
    }
}
