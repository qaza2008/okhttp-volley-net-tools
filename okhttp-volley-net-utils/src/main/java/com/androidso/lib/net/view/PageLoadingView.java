package com.androidso.lib.net.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jd.paipai.net.R;

/**
 * Created by liuxuegang1 on 2016/1/29.
 */
public class PageLoadingView extends FrameLayout {

    private static final int SOUFUN_LOADING_BAR = R.mipmap.loading_icon_big;
    /**
     * 后期可以改变样式
     */
    private static final int SOUFUN_LOADING = R.mipmap.loading_icon_small;

    private Animation animationRight, animationLeft;

    private ImageView iv_loading_bar;
    private ImageView iv_loading;

    public PageLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PageLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageLoadingView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        iv_loading_bar = new ImageView(context);
        iv_loading_bar.setImageResource(SOUFUN_LOADING_BAR);
        iv_loading_bar.setLayoutParams(params);

        iv_loading = new ImageView(context);
        iv_loading.setImageResource(SOUFUN_LOADING);
        params.topMargin = getResources().getDrawable(SOUFUN_LOADING_BAR).getIntrinsicWidth() - getResources().getDrawable(SOUFUN_LOADING).getIntrinsicWidth();
        params.leftMargin = getResources().getDrawable(SOUFUN_LOADING_BAR).getIntrinsicWidth() - getResources().getDrawable(SOUFUN_LOADING).getIntrinsicWidth();
        iv_loading.setLayoutParams(params);
        this.addView(iv_loading_bar);
        this.addView(iv_loading);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            animationRight = createRotateAnimationRight();
            animationLeft = createRotateAnimationLeft();
            iv_loading_bar.startAnimation(animationRight);
            iv_loading.startAnimation(animationLeft);
        }
    }

    private Animation createRotateAnimationLeft() {
        Animation animation = new RotateAnimation(359, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }

    private Animation createRotateAnimationRight() {
        Animation animation = new RotateAnimation(0, +359, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (animationRight != null)
            iv_loading_bar.startAnimation(animationRight);
        if (animationLeft != null)
            iv_loading.startAnimation(animationRight);

    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        iv_loading_bar.clearAnimation();
        iv_loading.clearAnimation();
    }
}
