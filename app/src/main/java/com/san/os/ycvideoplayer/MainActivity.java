package com.san.os.ycvideoplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.san.os.ijklibrary.ycvideo.YCVideoPlayerManager;
import com.san.os.ijklibrary.ycvideo.YKYCVideoPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout linearLayout = new RelativeLayout(this);
        YKYCVideoPlayer video = new YKYCVideoPlayer(this);
        video.satartPlay("http://flv.bitauto.com/2009/07/29/XMTA4ODc1NTQw.mp4");
        Button secondActivity = new Button(this);
        secondActivity.setOnClickListener(this);
        linearLayout.addView(video);
        linearLayout.addView(secondActivity);
        setContentView(linearLayout);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YCVideoPlayerManager.getInstance().release();
    }
}
