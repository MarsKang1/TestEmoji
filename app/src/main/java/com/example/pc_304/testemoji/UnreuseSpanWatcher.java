package com.example.pc_304.testemoji;

import android.graphics.drawable.Drawable;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.style.ImageSpan;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class UnreuseSpanWatcher  implements SpanWatcher {


    private static final String TAG = "GifSpanWatcher";

    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {

    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        if (what instanceof ImageSpan) {
            ImageSpan imageSpan = (ImageSpan) what;
            Drawable drawable = imageSpan.getDrawable();
            if (drawable != null) {
                drawable.setCallback(null);
            }
        }
    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart,
                              int nend) {
    }
}