package com.jasonzhang.adview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jasonzhang.library.view.AdView;

/**
 * @author zjh
 * @date 2016/6/16
 */
public class MyAdView extends AdView {
    public MyAdView(Context context) {
        super(context);
    }

    public MyAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void displayImage(ImageView imageView, String imgUrl) {
        Glide.with(getContext())
                .load(imgUrl)
                .crossFade()
                .into(imageView);
    }
}
