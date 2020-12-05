package com.cvsoftware.chatcloud.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.cvsoftware.chatcloud.R;
import com.google.android.material.appbar.AppBarLayout;

public class ExpandableCardView extends CardView{
    private AnimationStartListener startListener;
    private AnimationEndListener endListener;
    private ScrollView scrollView;
    private NestedScrollView nestedScrollView;
    private AnimationFrameListener frameListener;
    public interface AsyncCreateFinishedListener{
        public void onAsyncCreateFinish();
    }

    public void setFrameListener(AnimationFrameListener frameListener) {
        this.frameListener = frameListener;
    }

    public void setEndListener(AnimationEndListener endListener) {
        this.endListener = endListener;
    }
    public void attachToNestedScrollView(NestedScrollView nestedScrollView){
        this.nestedScrollView = nestedScrollView;
    }
    public void setStartListener(AnimationStartListener startListener) {
        this.startListener = startListener;
    }

    public void attachToScrollView(ScrollView scrollView){
        this.scrollView = scrollView;
    }
    public interface AnimationStartListener{
        void onStart(boolean isExpanding);
    }
    public interface AnimationFrameListener{
        void onFrame();
    }
    public interface AnimationEndListener{
        void end();
    }
    boolean isAnimating = false;
    private boolean isExpanded=false;

    public void onCreateContentView(View view){

    }
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){ }
    public ExpandableCardView(Context c){
        super(c);


        LinearLayout view = (LinearLayout) inflate(c,R.layout.expandablecardview,null);
                addView(view);
                if(getResId() != 0){
                    View expandedView = inflate(c,getResId(),null);
                    onCreateContentView(expandedView);
                    ((LinearLayout)findViewById(R.id.expanded_content)).addView(expandedView);
                    Log.i("ADDEDVIEW","1");
                }
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Utils.dpToPx(getContext(),100)

                );
                setLayoutParams(params);
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expandCard();
                    }
                });


                onCreatingInnerContent((TextView)findViewById(R.id.title),(TextView)findViewById(R.id.desc),(ImageView)findViewById(R.id.image));

    }

    public ExpandableCardView(Context c, final AsyncCreateFinishedListener listener){
        super(c);
        final AsyncLayoutInflater inflater = new AsyncLayoutInflater(c);
        inflater.inflate(R.layout.expandablecardview, null, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, @Nullable ViewGroup parent) {
                addView(view);
                if(getResId() != 0){
                    inflater.inflate(getResId(), null, new AsyncLayoutInflater.OnInflateFinishedListener() {
                        @Override
                        public void onInflateFinished(@NonNull View expandedView, int resid, @Nullable ViewGroup parent) {
                            onCreateContentView(expandedView);
                            ((LinearLayout)findViewById(R.id.expanded_content)).addView(expandedView);
                            Log.i("ADDEDVIEW","1");
                            listener.onAsyncCreateFinish();
                        }
                    });

                }
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Utils.dpToPx(getContext(),100)

                );
                setLayoutParams(params);
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expandCard();
                    }
                });


                onCreatingInnerContent((TextView)findViewById(R.id.title),(TextView)findViewById(R.id.desc),(ImageView)findViewById(R.id.image));
            }
        });




    }

    public @LayoutRes int getResId(){
        return 0;
    }


    public void expandCard() {

        if (!isAnimating) {
            ValueAnimator anim;
            if (!isExpanded) {
                findViewById(R.id.expand_arrow).animate().rotation(180).start();
                measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                isExpanded = true;
                int targetHeight = getMeasuredHeight();
                anim = CustomAnimator.createAnimation(this, CustomAnimator.AnimationType.HEIGHT,
                        Utils.dpToPx(getContext(),100), targetHeight,200);
            } else {
                findViewById(R.id.expand_arrow).animate().rotation(0).start();
                anim = CustomAnimator.createAnimation(this, CustomAnimator.AnimationType.HEIGHT,
                        getHeight(), Utils.dpToPx(getContext(),100),200);
                isExpanded = false;
            }
            if(startListener!=null)startListener.onStart(isExpanded);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if(frameListener!=null)frameListener.onFrame();
                    if(scrollView != null) scrollView.requestChildFocus(ExpandableCardView.this,ExpandableCardView.this);
                    if(nestedScrollView != null) nestedScrollView.requestChildFocus(ExpandableCardView.this,ExpandableCardView.this);
                    /*
                    if(nested){
                        ((NestedScrollView)scrollView).requestChildFocus(layout, layout);
                    }else{
                        ((ScrollView)scrollView).requestChildFocus(layout, layout);
                    }
                  */
                }
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isAnimating=false;
                    if(isExpanded){
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        setLayoutParams(layoutParams);
                        if(scrollView != null) scrollView.requestChildFocus(ExpandableCardView.this,ExpandableCardView.this);
                        if(nestedScrollView != null) nestedScrollView.requestChildFocus(ExpandableCardView.this,ExpandableCardView.this);
                        /*
                        if(nested){
                            ((NestedScrollView)scrollView).requestChildFocus(layout, layout);
                        }else{
                            ((ScrollView)scrollView).requestChildFocus(layout, layout);
                        }
*/
                    }
                }
            });
        }
        isAnimating=true;
    }
}
