package com.example.pc_304.testemoji;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pc_304.testemoji.test.FullHeightSpan;

import pl.droidsonroids.gif.GifDrawable;

public class MainActivity extends AppCompatActivity {
    private EditText spEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spEditText = findViewById(R.id.et_main);
        spEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
/*//        TestDrawable drawable = new TestDrawable();
        ImageView iv_main = findViewById(R.id.iv_main);
//        iv_main.setImageDrawable(drawable);
        iv_main.setImageDrawable(new TestDrawable(this,R.drawable.a));*/



     /*   ImageView iv_main = findViewById(R.id.iv_main);
        try {
            iv_main.setImageDrawable( new RefreshGifDrawable(getResources().getAssets(), "timg.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void glideLoad(View view) {
        try {
            //从网络加载
            /*GifDrawable gifDrawable = new RefreshGifDrawable(getResources(), R.drawable.a);
            GlidePreDrawable glidePreDrawable = new GlideReusePreDrawable();
            GlideApp.with(this).load("http://5b0988e595225.cdn.sohucs.com/images/20170919/1ce5d4c52c24432e9304ef942b764d37.gif").placeholder(gifDrawable).into(new DrawableTarget(glidePreDrawable));
            CharSequence charSequence = DrawableUtil.getDrawableText("[c]", glidePreDrawable);
            spEditText.insertSpecialStr(charSequence, false, charSequence, null); */

//            Drawable d = getResources().getDrawable(R.drawable.about_info);
            //这行不能少 设置固有宽高
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            ImageSpan span=new ImageSpan(this,R.drawable.about_info);

            //从资源文件里面加载
            GifDrawable d1 = new GifDrawable(getResources().getAssets(), "timg.gif");
            FullHeightSpan span = new FullHeightSpan(d1);
            Spannable spannable = new SpannableString("sss");
            spannable.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spEditText.setText(spannable);
            spEditText.setSelection(spEditText.getText().toString().length());
            Toast.makeText(this, spEditText.getText().toString(), Toast.LENGTH_SHORT).show();

        /*    GifDrawable gifDrawable = new RefreshGifDrawable(getResources().getAssets(), "timg.gif");
            GlidePreDrawable glidePreDrawable = new GlideReusePreDrawable();
            GlideApp.with(this).load(gifDrawable).into(new DrawableTarget(glidePreDrawable));
            CharSequence charSequence = DrawableUtil.getDrawableText("[c]", glidePreDrawable);
            spEditText.insertSpecialStr(charSequence, false, charSequence, null);
            spEditText.setSelection(spEditText.getText().toString().length());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
