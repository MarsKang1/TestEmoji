package com.example.pc_304.testemoji;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class GlideReusePreDrawable extends GlidePreDrawable implements RefreshableDrawable,
        Measurable {

    private WeakHashMap<Callback, Integer> callbackWeakHashMap;
    private CallBack callBack = new CallBack();

    @Override
    public boolean canRefresh() {
        return true;
    }

    @Override
    public int getInterval() {
        return 60;
    }


    @Override
    public void addCallback(Drawable.Callback currentCallback) {
        if (callbackWeakHashMap == null) {
            callbackWeakHashMap = new WeakHashMap<>();
            //Glide的GifDrawable的findCallback会一直去找不为Drawable的Callback
            // 所以不能直接implements Drawable.Callback
            setCallback(callBack);
        }
        if (!containsCallback(currentCallback)) {
            callbackWeakHashMap.put(currentCallback, 1);
        } else {
            int count = callbackWeakHashMap.get(currentCallback);
            callbackWeakHashMap.put(currentCallback, ++count);
        }
    }

    @Override
    public void removeCallback(Callback currentCallback) {
        if (callbackWeakHashMap == null) {
            return;
        }
        if (containsCallback(currentCallback)) {
            int count = callbackWeakHashMap.get(currentCallback);
            if (count <= 1) {
                callbackWeakHashMap.remove(currentCallback);
            } else {
                callbackWeakHashMap.put(currentCallback, --count);
            }
        }
    }

    private boolean containsCallback(Callback currentCallback) {
        return callbackWeakHashMap != null && callbackWeakHashMap.containsKey(currentCallback);
    }

    class CallBack implements Callback {

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            if (callbackWeakHashMap != null) {
                Set<Callback> set = callbackWeakHashMap.keySet();
                for (Callback callback : set) {
                    callback.invalidateDrawable(who);

                }
            }
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

        }
    }
}