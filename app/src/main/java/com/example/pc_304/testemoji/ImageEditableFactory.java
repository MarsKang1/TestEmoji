package com.example.pc_304.testemoji;

import android.annotation.SuppressLint;
import android.support.annotation.GuardedBy;
import android.support.annotation.Nullable;
import android.text.Editable;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class ImageEditableFactory extends Editable.Factory {

    private static final Object sInstanceLock = new Object();
    @GuardedBy("sInstanceLock")
    private static volatile Editable.Factory sInstance;
    @Nullable
    private static Class<?> sWatcherClass;

    @SuppressLint({"PrivateApi"})
    private ImageEditableFactory() {
        try {
            String className = "android.text.DynamicLayout$ChangeWatcher";
            sWatcherClass = this.getClass().getClassLoader().loadClass(className);
        } catch (Throwable var2) {
            ;
        }
    }
}