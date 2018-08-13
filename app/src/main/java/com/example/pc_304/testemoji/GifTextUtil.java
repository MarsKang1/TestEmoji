package com.example.pc_304.testemoji;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class GifTextUtil {

    private static final String TAG = "GifTextUtil";

    @Deprecated
    public static void setText(final TextView textView, final CharSequence text) {
        //需要SPANNABLE，保证textView中取出来的是SpannableString
        setText(textView, text, TextView.BufferType.SPANNABLE);
    }

    @Deprecated
    public static void setText(final TextView textView, final CharSequence nText,
                               final TextView.BufferType type) {
        //type 默认SPANNABLE，保证textView中取出来的是Spannable类型
        textView.setText(nText, type);
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            ImageSpan[] gifSpans = ((Spannable) text).getSpans(0, text.length(), ImageSpan.class);
            //将之前的Callback disable
            Object oldCallback = textView
                    .getTag(R.id.drawable_callback_tag);
            if (oldCallback != null && oldCallback instanceof CallbackForTextView) {
                ((CallbackForTextView) oldCallback).disable();
            }
            Drawable.Callback callback = new CallbackForTextView(textView);
            //callback在drawable中是弱引用
            //让textview持有callback,防止callback被回收
            textView.setTag(R.id.drawable_callback_tag, callback);
            for (ImageSpan gifSpan : gifSpans) {
                Drawable drawable = gifSpan.getDrawable();
                if (drawable != null) {
                    drawable.setCallback(callback);
                }
            }
            //gifSpanWatcher是SpanWatcher,继承自NoCopySpan
            //只有setText之后设置SpanWatcher才能成功
            ((Spannable) text).setSpan(unreuseSpanWatcher, 0, text.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE | Spanned.SPAN_PRIORITY);

        }
        textView.invalidate();
    }

    public static void setTextWithReuseDrawable(final TextView textView, final CharSequence nText) {
        setTextWithReuseDrawable(textView, nText, false);
    }

    public static void setTextWithReuseDrawable(final TextView textView, final CharSequence nText,
                                                boolean needAllCallback) {
        setTextWithReuseDrawable(textView, nText, needAllCallback, TextView.BufferType.SPANNABLE);
    }

    /**
     *
     * @param textView
     * @param nText
     * @param needAllCallback
     * @param type
     */
    public static void setTextWithReuseDrawable(final TextView textView, final CharSequence nText,
                                                boolean needAllCallback, final TextView.BufferType type) {
        CharSequence oldText = "";
        try {
            //EditText第一次获取到的是个空字符串，会强转成Editable，出现ClassCastException
            oldText = textView.getText();
        } catch (ClassCastException e) {
            e.fillInStackTrace();
        }
        Object cachedCallback = textView
                .getTag(R.id.drawable_callback_tag);
        CallbackForTextView callback;
        if (cachedCallback != null && cachedCallback instanceof CallbackForTextView) {
            callback = (CallbackForTextView) cachedCallback;
            if (oldText instanceof Spannable) {
                ImageSpan[] gifSpans = ((Spannable) oldText).getSpans(0, oldText.length(), ImageSpan.class);
                for (ImageSpan gifSpan : gifSpans) {
                    Drawable drawable = gifSpan.getDrawable();
                    if (drawable != null && drawable instanceof RefreshableDrawable) {
                        ((RefreshableDrawable) drawable).removeCallback(callback);
                    }
                }
            }
        } else {
            callback = new CallbackForTextView(textView);
            textView.setTag(R.id.drawable_callback_tag, callback);
        }

        //type 默认SPANNABLE，保证textView中取出来的是Spannable类型
        textView.setText(nText, type);
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {

            RefreshableDrawable temp = null;
            int tempInterval = 0;
            ImageSpan[] imageSpans = ((Spannable) text).getSpans(0, text.length(), ImageSpan.class);
            if (needAllCallback) {
                int refreshDrawableCount=0;
                for (ImageSpan gifSpan : imageSpans) {
                    Drawable drawable = gifSpan.getDrawable();
                    if (drawable != null) {
                        if (drawable instanceof RefreshableDrawable) {
                            ((RefreshableDrawable) drawable).addCallback(callback);
                            refreshDrawableCount++;
                        } else {
                            drawable.setCallback(callback);
                        }
                    }
                }
                callback.setNeedInterval(refreshDrawableCount > 5);
            } else {
                for (ImageSpan gifSpan : imageSpans) {
                    Drawable drawable = gifSpan.getDrawable();
                    if (drawable != null && drawable instanceof RefreshableDrawable) {
                        if (((RefreshableDrawable) drawable).canRefresh()) {
                            if (tempInterval == 0 || tempInterval > ((RefreshableDrawable) drawable)
                                    .getInterval()) {
                                temp = (RefreshableDrawable) drawable;
                                tempInterval = temp.getInterval();
                            }
                        }
                    }
                }
                if (temp != null) {
                    temp.addCallback(callback);
                }
            }

            //gifSpanWatcher是SpanWatcher,继承自NoCopySpan
            //只有setText之后设置SpanWatcher才能成功
            GifSpanWatcher cacheSpanWatcher;
            Object object = textView.getTag(R.id.span_watcher_tag);
            if (object != null && object instanceof GifSpanWatcher) {
                cacheSpanWatcher = (GifSpanWatcher) object;
            } else {
                cacheSpanWatcher = new GifSpanWatcher(callback);
                textView.setTag(R.id.span_watcher_tag, cacheSpanWatcher);
            }
            ((Spannable) text).setSpan(cacheSpanWatcher, 0, text.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE | Spanned.SPAN_PRIORITY);

        }
        textView.invalidate();
    }


    public static class CallbackForTextView implements Drawable.Callback {

        private boolean enable = true;
        private boolean needInterval = false;
        private long lastInvalidateTime;
        private WeakReference<TextView> textViewWeakReference;

        public CallbackForTextView(TextView textView) {
            textViewWeakReference = new WeakReference<>(textView);
        }

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            if (!enable) {
                return;
            }
            TextView textView = textViewWeakReference.get();
            if (textView == null) {
                return;
            }
            if (needInterval) {
                if (System.currentTimeMillis() - lastInvalidateTime > 40) {
                    lastInvalidateTime = System.currentTimeMillis();
                    textView.invalidate();
                }
            } else {
                textView.invalidate();
            }

        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

        }

        public void disable() {
            this.enable = false;
        }

        public void setNeedInterval(boolean needInterval) {
            this.needInterval = needInterval;
        }
    }

    private static UnreuseSpanWatcher unreuseSpanWatcher = new UnreuseSpanWatcher();
}
