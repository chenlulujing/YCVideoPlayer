package com.san.os.ijklibrary.ycvideo;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-09-06 09:31
 */

public class YKYCVideoPlayer extends YCBaseVideoPlayer {


    public YKYCVideoPlayer(@NonNull Context context) {
        super(context);
    }

    public YKYCVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YKYCVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected IYCMediaControler initControlView() {
        return new YCCustomMediaContolView(getContext());
    }

    @Override
    protected void addControlView(FrameLayout videoContainer, IYCMediaControler controlView) {
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        videoContainer.addView((YCCustomMediaContolView)controlView, rllp);
    }
}
