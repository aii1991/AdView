package com.jasonzhang.adview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jasonzhang.adview.view.MyAdView;
import com.jasonzhang.library.bean.AdBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyAdView my_ad_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_ad_view = (MyAdView) findViewById(R.id.my_ad_view);
        my_ad_view.setData(getData());

    }

    private List<AdBean> getData() {
        List<AdBean> adBeans = new ArrayList<>();
        adBeans.add(new AdBean("http://img5.imgtn.bdimg.com/it/u=1483984522,4206251963&fm=21&gp=0.jpg"));
        adBeans.add(new AdBean("http://c.hiphotos.baidu.com/zhidao/pic/item/cc11728b4710b912b2648f77c1fdfc0393452295.jpg"));
        adBeans.add(new AdBean("http://d.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=898bbdb064d0f703e6e79dd83dca7d0b/7a899e510fb30f24413f07a4ce95d143ac4b03eb.jpg"));
        return adBeans;
    }

    @Override
    protected void onDestroy() {
        if (my_ad_view != null){
            my_ad_view.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (my_ad_view != null){
            my_ad_view.onPause();
        }
        super.onPause();
    }
}
