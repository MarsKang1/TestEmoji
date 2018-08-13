package com.example.pc_304.testemoji;

import android.graphics.drawable.Drawable;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */
public interface RefreshableDrawable {

    boolean canRefresh();

    int getInterval();

    void addCallback(Drawable.Callback callback);

    void removeCallback(Drawable.Callback callback);

}
