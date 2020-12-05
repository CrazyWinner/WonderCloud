package com.cvsoftware.chatcloud.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class CustomAnimator {
    enum AnimationType{
        WIDTH,
        HEIGHT,
        ALPHA,
        COLOR
    }
    public static ValueAnimator createAnimation(final View v,final AnimationType type,int start,int end,int duration){
        ValueAnimator anim = ValueAnimator.ofInt(start,end);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                switch (type){
                    case HEIGHT:
                        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                        layoutParams.height = val;
                        v.setLayoutParams(layoutParams);
                        break;
                    case WIDTH:
                        ViewGroup.LayoutParams layoutParams2 = v.getLayoutParams();
                        layoutParams2.width = val;
                        v.setLayoutParams(layoutParams2);
                        break;
                    case ALPHA:
                        v.setAlpha(val/1024f);
                        break;
                    case COLOR:
                        v.setBackgroundColor(val);
                        break;
                }

            }
        });
        anim.setDuration(duration);
        anim.start();
        return anim;

    }
}
