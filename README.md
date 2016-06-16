#screenshot
![](https://github.com/aii1991/AdView/blob/master/screenshot/QQ%E5%9B%BE%E7%89%8720160616140237.png)
![](https://github.com/aii1991/AdView/blob/master/screenshot/QQ%E5%9B%BE%E7%89%8720160616140326.png)
# AdView
通用的广告栏控件，轻松实现广告轮播效果。支持无限循环,支持配置自己的图片加载框架。并且智能控制轮播,当手指按下时停止轮播,手指松开时恢复轮播,actiivty onpause停止轮播 onresume时恢复轮播。可自行配置广告滑动动画和指示点样式等。
# Use

    1.public class MyAdView extends AdView {
        public MyAdView(Context context) {
            super(context);
        }

        public MyAdView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void displayImage(ImageView imageView, String imgUrl) {
          //自行实现加载图片
            Glide.with(getContext())
                .load(imgUrl)
                .crossFade()
                .into(imageView);
        }
    }

    2.<com.jasonzhang.adview.view.MyAdView
        android:id="@+id/my_ad_view"
        android:layout_width="match_parent"
        android:layout_height="250dp" />

    3.my_ad_view = (MyAdView) findViewById(R.id.my_ad_view);
      my_ad_view.setData(getData());
# attr
    <declare-styleable name="AdView">
        <!-- 轮播间隔时间 -->
        <attr name="delayed_timer" format="integer"/>
        <!-- 指示点组离底部距离 -->
        <attr name="point_group_margin_bottom" format="dimension"/>
        <!-- 指示点间距 -->
        <attr name="point_right_margin" format="dimension"/>
        <!-- 指示点背景 -->
        <attr name="point_background" format="reference"/>
    </declare-styleable>
