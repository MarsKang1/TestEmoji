package com.example.pc_304.testemoji.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;
/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */
public class FullHeightSpan extends ImageSpan {
    private int drawableHeight = 0;
    private Paint.FontMetricsInt fm;

    public FullHeightSpan(Drawable d) {
        super(d);
    }

    public FullHeightSpan(Context context, Uri uri) {
        super(context, uri);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null) {
            this.fm = fm;
            drawableHeight = fm.descent - fm.ascent;
        }
        Drawable drawable = getResizedDrawable();
        Rect bounds = drawable.getBounds();
        return bounds.right;
    }

    private Drawable getResizedDrawable() {
        Drawable d = getDrawable();
        if (drawableHeight == 0) {
            return d;
        }
        d.setBounds(new Rect(0, 0, (int) (1f * drawableHeight * d.getIntrinsicWidth() / d.getIntrinsicHeight()), drawableHeight));
        return d;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable d = getResizedDrawable();
        canvas.save();
        int transY;
        fm = paint.getFontMetricsInt();
        transY = y + fm.ascent;
        canvas.translate(x, transY);
        d.draw(canvas);
        canvas.restore();
    }
}
