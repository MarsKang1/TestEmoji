package com.example.pc_304.testemoji;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class DrawableUtil {

    public static Spannable getDrawableText(CharSequence text, Drawable gifDrawable) {
        Spannable spannable = new SpannableString(text);
        ImageSpan imageSpan = new EqualHeightSpan(gifDrawable);
        spannable.setSpan(imageSpan,
                0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}