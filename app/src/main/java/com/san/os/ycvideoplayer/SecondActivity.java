package com.san.os.ycvideoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.san.os.ijklibrary.ycvideo.YKYCVideoPlayer;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-10-15 17:07
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YKYCVideoPlayer video = new YKYCVideoPlayer(this);
        video.satartPlay("http://flv.bitauto.com/2009/07/29/XMTA4ODc1NTQw.mp4");
        setContentView(video);
    }
}
