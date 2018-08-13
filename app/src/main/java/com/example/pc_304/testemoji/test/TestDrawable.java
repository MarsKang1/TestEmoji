package com.example.pc_304.testemoji.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by KangJH on 2018/8/8.
 * The harder you work, the luckier you will be.
 */

public class TestDrawable extends Drawable {
    private Paint paint;//画笔
    private int mWidth;//图片宽与高的最小值
    private Bitmap bitmap;//位图

    public TestDrawable(Context context, int resID) {
        this(BitmapFactory.decodeResource(context.getResources(), resID));
    }

    public TestDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//抖动,不同屏幕尺的使用保证图片质量
        ///位图渲染器
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(bitmapShader);
        mWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, paint);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
        invalidateSelf();//更新设置
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmap.getWidth();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();//更行设置
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
