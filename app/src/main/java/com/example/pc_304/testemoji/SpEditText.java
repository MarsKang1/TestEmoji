package com.example.pc_304.testemoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

/**
 * Created by KangJH on 2018/8/7.
 * The harder you work, the luckier you will be.
 */

public class SpEditText  extends AppCompatEditText {

    private static String TAG = "SpEditText";
    private KeyReactListener mKeyReactListener;
    private char[] reactKeys;
    private int spMode;

    public SpEditText(Context context) {
        super(context);
        readAttrs(context, null);
    }

    public SpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        readAttrs(context, attrs);
    }


    public void setReactKeys(String reactKeys) {
        this.reactKeys = reactKeys.toCharArray();
    }

    /**
     * 设置文本变化监听
     * 设置删除事件监听
     */ {
        setEditableFactory(ImageEditableFactory.getInstance());
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //如果SpData字符串完整性被破坏，移除Spdata和对应的样式
                if (count == 1 && after == 0) {
                    SpData[] spDatas = getSpDatas();
                    for (SpData spData : spDatas) {
                        if (start < spData.end && start >= spData.start) {
                            Editable editable = getEditableText();
                            editable.removeSpan(spData);
                            if (spData.customSpan != null) {
                                editable.removeSpan(spData.customSpan);
                            }
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (reactKeys == null) {
                    return;
                }
                for (Character character : reactKeys) {
                    if (count == 1 && !TextUtils.isEmpty(charSequence)) {
                        char mentionChar = charSequence.toString().charAt(start);
                        if (character.equals(mentionChar) && mKeyReactListener != null) {
                            handKeyReactEvent(character);
                            return;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void readAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpEditText);
            if (ta != null) {
                String reactKeyStr = ta.getString(R.styleable.SpEditText_react_keys);
                if (!TextUtils.isEmpty(reactKeyStr)) {
                    reactKeys = reactKeyStr.toCharArray();
                }
                spMode = ta.getInteger(R.styleable.SpEditText_sp_mode, 0);
                ta.recycle();
            }

        }

    }

    private boolean onDpadRightEvent() {
        if (spMode == 1) {
            return false;
        }
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd != selectionStart) {
            return false;
        }
        SpData[] spDatas = getSpDatas();
        for (int i = 0; i < spDatas.length; i++) {
            SpData spData = spDatas[i];
            if (selectionStart == spData.start) {
                setSelection(spData.end, spData.end);
                return true;
            }
        }
        return false;
    }

    private void handKeyReactEvent(final Character character) {
        post(new Runnable() {
            @Override
            public void run() {
                mKeyReactListener.onKeyReact(character.toString());
            }
        });

    }

    private boolean onDeleteEvent() {
        if (spMode == 1) {
            return false;
        }
        int selectionStart = getSelectionStart();
        final int selectionEnd = getSelectionEnd();
        if (selectionEnd != selectionStart) {
            return false;
        }
        SpData[] spDatas = getSpDatas();
        for (int i = 0; i < spDatas.length; i++) {
            SpData spData = spDatas[i];
            if (selectionStart == spData.end) {
                Editable editable = getText();
//        if ((editable.length() - spData.end) > 15) {
//          SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
//          spannableStringBuilder.delete(rangeStart, selectionEnd);
//          GifTextUtil.setText(SpEditText.this, spannableStringBuilder);
//          setSelection(rangeStart);
//        } else {
//          editable.delete(spData.start, spData.end);
//        }
                editable.delete(spData.start, spData.end);
                return true;
            }

        }
        return false;
    }


    public SpData[] getSpDatas() {
        Editable editable = getText();
        SpData[] spanneds = editable.getSpans(0, getText().length(), SpData.class);
        if (spanneds != null && spanneds.length > 0) {
            for (SpData spData : spanneds) {
                int start = editable.getSpanStart(spData);
                int end = editable.getSpanEnd(spData);
                spData.setEnd(end);
                spData.setStart(start);
            }
            sortSpans(spanneds, 0, spanneds.length - 1);
            return spanneds;
        } else {
            return new SpData[]{};
        }


    }

    private void sortSpans(SpData[] spDatas, int left, int right) {
        if (left >= right) {
            return;
        }
        int i = left;
        int j = right;
        SpData keySpan = spDatas[left];
        int key = spDatas[left].start;
        while (i < j) {
            while (i < j && key <= spDatas[j].start) {
                j--;
            }
            spDatas[i] = spDatas[j];
            while (i < j && key >= spDatas[i].start) {
                i++;
            }

            spDatas[j] = spDatas[i];
        }

        spDatas[i] = keySpan;
        sortSpans(spDatas, left, i - 1);
        sortSpans(spDatas, i + 1, right);

    }

    /**
     * 监听光标位置
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (spMode == 1) {
            return;
        }
        SpData[] spDatas = getSpDatas();
        for (int i = 0; i < spDatas.length; i++) {
            SpData spData = spDatas[i];
            int startPosition = spData.start;
            int endPosition = spData.end;
            if (changeSelection(selStart, selEnd, startPosition, endPosition, false)) {
                return;
            }
        }
    }

    /**
     * 不让光标进入特殊字符串内部
     *
     * @param selStart 光标起点
     * @param selEnd 光标终点
     * @param startPosition 特殊字符串起点
     * @param endPosition 特殊字符串终点
     * @param toEnd 将光标放起点还是终点,暂时没用上,都是放起点
     */
    private boolean changeSelection(int selStart, int selEnd, int startPosition, int endPosition,
                                    boolean toEnd) {
        boolean hasChange = false;
        if (selStart == selEnd) {
            if (startPosition != -1 && startPosition < selStart && selStart < endPosition) {
                if (toEnd) {
                    setSelection(endPosition);
                } else {
                    setSelection(startPosition);
                }
                hasChange = true;
            }
        } else {
            if (startPosition != -1 && startPosition < selStart && selStart < endPosition) {
                if (toEnd) {
                    setSelection(endPosition, selEnd);
                } else {
                    setSelection(startPosition, selEnd);
                }

                hasChange = true;
            }
            if (endPosition != -1 && startPosition < selEnd && selEnd < endPosition) {
                setSelection(selStart, endPosition);
                hasChange = true;
            }
        }
        return hasChange;
    }

    public void insertNormalStr(CharSequence showContent) {
        insertNormalStr(showContent, false);
    }

    public void insertNormalStr(CharSequence showContent, boolean rollBack) {
        insertSpecialStr(showContent, false, rollBack, null, null);
    }


    /**
     * 插入特殊字符串
     *
     * @param showContent 特殊字符串显示在文本框中的内容
     * @param rollBack 是否往前删除一个字符，因为@的时候可能留了一个字符在输入框里
     * @param customData 特殊字符串的数据结构
     * @param customSpan 特殊字符串的样式
     */
    public void insertSpecialStr(CharSequence showContent, boolean rollBack, Object customData,
                                 Object customSpan) {
        insertSpecialStr(showContent, true, rollBack, customData, customSpan);
    }


    public void insertSpecialStr(CharSequence showContent, boolean needSpSpan, boolean rollBack,
                                 Object customData,
                                 Object customSpan) {
        if (TextUtils.isEmpty(showContent)) {
            return;
        }
        int index = getSelectionStart();
        Editable editable = getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
        SpannableString spannableString = new SpannableString(showContent);
        if (needSpSpan) {
            SpData spData = new SpData();
            spData.setShowContent(showContent);
            spData.setCustomData(customData);
            spannableString
                    .setSpan(spData, 0, spannableString.length(),
                            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (customSpan != null) {
                spData.setCustomSpan(customSpan);
            }
        }
        if (customSpan != null) {
            spannableString
                    .setSpan(customSpan, 0, spannableString.length(),
                            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (rollBack) {
            spannableStringBuilder.delete(index - 1, index);
            index--;
        }
        spannableStringBuilder.insert(index, spannableString);
        GifTextUtil.setTextWithReuseDrawable(this, spannableStringBuilder, true);
        setSelection(index + spannableString.length());
    }


    public interface KeyReactListener {

        void onKeyReact(String key);
    }


    public void setKeyReactListener(KeyReactListener keyReactListener) {
        this.mKeyReactListener = keyReactListener;
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new SpInputConnectionWrapper(super.onCreateInputConnection(outAttrs), true);
    }

    public class SpData {

        /**
         * EditText中显示的内容
         */
        private CharSequence showContent;
        /**
         * 特殊内容的数据结构
         */
        private Object customData;
        private Object customSpan;
        private int start;
        private int end;


        public Object getCustomSpan() {
            return customSpan;
        }

        private void setCustomSpan(Object customSpan) {
            this.customSpan = customSpan;
        }

        public int getStart() {
            return start;
        }

        private void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        private void setEnd(int end) {
            this.end = end;
        }

        public Object getCustomData() {
            return customData;
        }

        private void setCustomData(Object customData) {
            this.customData = customData;
        }

        public CharSequence getShowContent() {
            return showContent;
        }

        private void setShowContent(CharSequence showContent) {
            this.showContent = showContent;
        }

        @Override
        public String toString() {
            return "SpData{" +
                    "showContent='" + showContent + '\'' +
                    ", customData=" + customData +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }


    private class SpInputConnectionWrapper extends InputConnectionWrapper {


        /**
         * Initializes a wrapper.
         *
         * <p><b>Caveat:</b> Although the system can accept {@code (InputConnection) null} in some
         * places, you cannot emulate such a behavior by non-null {@link InputConnectionWrapper} that
         * has {@code null} in {@code target}.</p>
         *
         * @param target the {@link InputConnection} to be proxied.
         * @param mutable set {@code true} to protect this object from being reconfigured to target
         * another {@link InputConnection}.  Note that this is ignored while the target is {@code
         * null}.
         */
        public SpInputConnectionWrapper(InputConnection target, boolean mutable) {
            super(target, mutable);
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            boolean result = false;
            if (beforeLength == 1 && afterLength == 0) {
                result = onDeleteEvent();
            }
            return result || super.deleteSurroundingText(beforeLength, afterLength);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            boolean result = false;
            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                result = onDeleteEvent();
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                result = onDpadRightEvent();
            }
            return result || super.sendKeyEvent(event);

        }
    }

}

