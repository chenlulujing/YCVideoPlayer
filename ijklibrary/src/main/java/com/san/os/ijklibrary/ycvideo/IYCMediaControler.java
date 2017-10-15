package com.san.os.ijklibrary.ycvideo;

import android.view.View;
import android.widget.MediaController;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-09-06 09:18
 */

public interface IYCMediaControler {


    void hide();

    boolean isShowing();

    void setAnchorView(View view);

    void setEnabled(boolean enabled);

    void setMediaPlayer(MediaController.MediaPlayerControl player);

    void show(int timeout);

    void show();

    void showOnce(View view);

    void showLoadingView();

    void hideLoadingView();
}
