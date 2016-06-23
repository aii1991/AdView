package com.jasonzhang.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.jasonzhang.library.R;
import com.jasonzhang.library.bean.AdBean;
import com.jasonzhang.library.bean.ReqAd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjh
 * @date 2016/3/30
 * 广告控件 实现自动轮播,单击时停止自动滚动,松开手后恢复自动滚动,onPause时停止滚动,onresume恢复滚动
 * 加载网络图片,获取图片失败,自动从缓存中获取上次图片地址
 * 提供刷新数据接口
 */
public abstract class AdView extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private List<AdBean> adBeans;
    private ViewPager viewPager;
    private LinearLayout pointGroup;
    private Context context;
    private static final int MOVE_TO_FIRST = 100;

    private final int DEF_POINT_GROUP_MARGIN_BOTTOM = 20; //默认指示点组距离底部距离
    private int pointGroupMarginBottom;
    private final int DEF_POINT_RIGHT_MARGIN = 20; //默认指示点与指示点之间的间距
    private int pointRightMargin;
    private final int DEF_POINT_BG_RES_ID = R.drawable.point_bg; //指示点默认背景图
    private int pointBgResId; //指示点背景图片
    private final int DEF_DELAYED_TIMER = 2 * 2000; //滚动到下一张图片的默认时间
    private int delayedTimer; //滚动到下一张图片的时间

    private int lastPosition; //上一个页面的位置
    private boolean isRunning = true; //是否循环轮播

    private OnItemClickListener onItemClickListener;
    private List<ImageView> imgList;

    private GestureDetector gestureDetector;
    private boolean isStopRun; //是否停止滚动
    private boolean isRefresh; //是否为下拉刷新
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (adBeans.size() == 2){
                if (viewPager.getCurrentItem() >= adBeans.size() - 1){
                    if (distanceX > 0){
                        //右滑
                        viewPager.setCurrentItem(0);
                    }
                }
            }
            int distance = (int) Math.sqrt(Math.pow(distanceX,2.0)+Math.pow(distanceY,20));
            if(distance > 10){
                handler.removeMessages(0);
                isRunning = false;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            //让viewPager 滑动到下一页
            if (adBeans == null){
                return;
            }
            if (msg.what == 0){
                if(adBeans.size() == 2){
                    viewPager.setCurrentItem(viewPager.getCurrentItem() == 0 ? 1 : 0);
                }else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
                if(isRunning) {
                    handler.sendEmptyMessageDelayed(0, delayedTimer);
                }
            }else if (msg.what == MOVE_TO_FIRST){
                viewPager.setCurrentItem(0);
            }

        }
    };

    public void onDestroy(){
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public void onPause(){
        if(isRunning && handler != null){
            isStopRun = true;
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void onResume(){
        if (isStopRun && handler != null){
            isStopRun = false;
            handler.sendEmptyMessageDelayed(0, delayedTimer);
        }
    }



    public AdView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AdView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        initAttrs(attrs);
        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        if(ev.getAction() == MotionEvent.ACTION_UP){
            if (adBeans != null && adBeans.size() <= 1){
                isRunning = false;
                handler.sendEmptyMessage(MOVE_TO_FIRST);
            }else {
                if(!isRunning){
                    isRunning = true;
                    handler.sendEmptyMessageDelayed(0, delayedTimer);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化
     */
    private void init(){
        gestureDetector = new GestureDetector(context, gestureListener);
        addViewPager();
        addPointGroup();
    }

    /**
     * 初始化属性
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.AdView);
        delayedTimer = typedArray.getInt(R.styleable.AdView_delayed_timer,DEF_DELAYED_TIMER);
        pointGroupMarginBottom = (int) typedArray.getDimension(R.styleable.AdView_point_group_margin_bottom,DEF_POINT_GROUP_MARGIN_BOTTOM);
        pointRightMargin = (int) typedArray.getDimension(R.styleable.AdView_point_right_margin,DEF_POINT_RIGHT_MARGIN);
        pointBgResId = typedArray.getResourceId(R.styleable.AdView_point_background,DEF_POINT_BG_RES_ID);
    }

    /**
     * 添加指示组
     */
    private void addPointGroup() {
        pointGroup = new LinearLayout(context);
        pointGroup.setHorizontalGravity(LinearLayout.HORIZONTAL);
        LayoutParams pointGroupLayParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pointGroupLayParams.addRule(CENTER_HORIZONTAL);
        pointGroupLayParams.addRule(ALIGN_PARENT_BOTTOM);
        pointGroupLayParams.bottomMargin = getPointGroupMarginBottom();
        pointGroup.setLayoutParams(pointGroupLayParams);
        addView(pointGroup);
    }

    /**
     * 添加viewPager
     */
    private void addViewPager() {
        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(1);
        addView(viewPager);
    }

    /**
     * 设置数据
     * @param list 数据源
     */
    public <T extends AdBean> void setData(List<T> list){
        setAdViewData(list);
    }

    private <T extends AdBean> void setAdViewData(List<T> list){
        if(list != null && !list.isEmpty()){
            this.adBeans = (List<AdBean>) list;
            if (isRefresh){
                lastPosition = 0;
            }
            if(adBeans.size() <= 1){
                isRunning = false;
            }
            addPoint(adBeans.size());
            addImgData();
            viewPager.setAdapter(new AdvertisementAdapter());
            if (handler != null && !isRefresh && isRunning){
                handler.sendEmptyMessageDelayed(0, delayedTimer);
            }
        } else {
            isRunning = false;
            setVisibility(GONE);
        }

        useDeafultItemClick();
    }

    /**
     * 设置数据请求数据
     * @param reqData
     */
    public void setData(ReqAd reqData){
        //TODO 通过API获取广告图片地址
    }

    /**
     * 使用默认的点击事件
     */
    private void useDeafultItemClick(){
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdBean adBean, int position) {
                //TODO 点击事件
            }
        };
    }

    /**
     * 添加img数据
     */
    private void addImgData() {
        if (imgList == null){
            imgList = new ArrayList<>();
        }
        imgList.clear();
        for (int i=0; i < adBeans.size(); i++){
            final ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final int position = i;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(adBeans.get(position),position);
                    }
                }
            });
            imgList.add(imageView);
        }
    }

    /**
     * 添加指示点
     * @param length 指示点的个数
     */
    private void addPoint(int length){
        if (isRefresh){
            pointGroup.removeAllViews();
        }
        for (int i = 0; i < length; i++){
            ImageView point =new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = getPointRightMargin();
            point.setLayoutParams(params);
            point.setBackgroundResource(getPointBgResId());
            if(i==0){
                point.setEnabled(true);
            }else{
                point.setEnabled(false);
            }
            pointGroup.addView(point);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //页面正在滑动的时候，回调
    }

    @Override
    public void onPageSelected(int position) {
        //页面切换后调用,position  新的页面位置
        position = position% adBeans.size();

        //改变指示点的状态
        //把当前点enbale 为true
        pointGroup.getChildAt(position).setEnabled(true);
        //把上一个点设为false
        pointGroup.getChildAt(lastPosition).setEnabled(false);
        lastPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //当页面状态发生变化的时候，回调
    }

    /**
     * 设置滑动动画
     * @param reverseDrawingOrder  是否倒叙插入
     * @param pageTransformer 自定义动画
     */
    public void setPageTransformer(boolean reverseDrawingOrder,ViewPager.PageTransformer pageTransformer){
        viewPager.setPageTransformer(reverseDrawingOrder,pageTransformer);
    }

    private class AdvertisementAdapter extends PagerAdapter {

        @Override
        /**
         * 获得页面的总数
         */
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        /**
         * 获得相应位置上的view
         * container  view的容器，其实就是viewpager自身
         * position 	相应的位置
         */
        public Object instantiateItem(ViewGroup container, int position) {
            // 给 container 添加一个view
            int size = adBeans.size();
            position = position % size;
            AdBean adBean = adBeans.get(position);
            ImageView imageView = imgList.get(position);
            displayImage(imageView,adBean.getAdvPic());
            ViewParent vp =imageView.getParent();
            if (vp != null){
                ViewGroup parent = (ViewGroup)vp;
                parent.removeView(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if(view == object){
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (adBeans.size() >= 3){
                container.removeView((View) object);
                object = null;
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public int getPointGroupMarginBottom() {
        return pointGroupMarginBottom == 0 ? DEF_POINT_GROUP_MARGIN_BOTTOM : pointGroupMarginBottom;
    }

    public void setPointGroupMarginBottom(int pointGroupMarginBottom) {
        this.pointGroupMarginBottom = pointGroupMarginBottom;
    }

    public int getPointRightMargin() {
        return pointRightMargin == 0 ? DEF_POINT_RIGHT_MARGIN : pointRightMargin;
    }

    public void setPointRightMargin(int pointRightMargin) {
        this.pointRightMargin = pointRightMargin;
    }

    public int getPointBgResId() {
        return pointBgResId == 0 ? DEF_POINT_BG_RES_ID : pointBgResId;
    }

    public void setPointBgResId(int pointBgResId) {
        this.pointBgResId = pointBgResId;
    }

    public int getDelayedTimer() {
        return delayedTimer == 0 ? DEF_DELAYED_TIMER : delayedTimer;
    }

    public void setDelayedTimer(int delayedTimer) {
        this.delayedTimer = delayedTimer;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(AdBean adBean,int position);
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    /**
     * 自行实现图片加载
     * @param imageView
     * @param imgUrl
     */
    public abstract void displayImage(ImageView imageView,String imgUrl);
}
