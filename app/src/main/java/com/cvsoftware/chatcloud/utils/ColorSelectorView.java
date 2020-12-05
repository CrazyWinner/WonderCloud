package com.cvsoftware.chatcloud.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.cvsoftware.chatcloud.R;
import com.cvsoftware.chatcloud.SendActivity;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.shawnlin.numberpicker.NumberPicker;


public class ColorSelectorView extends ExpandableCardView{
    int[] colors;
    int colorModes[];


    public ColorSelectorView(Context c) {
        super(c);
        colors = new int[]{Color.BLACK,Color.WHITE,Color.RED,Color.BLUE};
        colorModes = new int[]{COLOR_SINGLE,COLOR_RANDOM};
        applyColorToViews();
    }
    public static final int backgroundColor=0;
    public static final int wordSingleColor=1;
    public static final int wordRangeColor1=2;
    public static final int wordRangeColor2=3;
    public static final int BACKGROUND=0;
    public static final int WORD=1;

    NumberPicker backgroundPicker;
    NumberPicker wordPicker;

    private ValueAnimator[] animators = new ValueAnimator[2];
    private boolean[] isExpanded = new boolean[]{true,false};


    public static final int COLOR_SINGLE=1;
    public static final int COLOR_RANDOM=2;
    public static final int COLOR_RANGE=3;


    public void setColorModes(int[] colorModes,boolean update){

        this.colorModes = colorModes;
        if(update)applyColorToViews();
    }
    public void setColors(int[] colors,boolean update){
        this.colors = colors;
        if(update)applyColorToViews();
    }
    public int[] getColorModes(){
        return colorModes;
    }
    public int[] getColors(){
        return colors;
    }
    public ColorSelectorView(Context c, ExpandableCardView.AsyncCreateFinishedListener listener){
        super(c,listener);
    }

    @Override
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){
        title.setText(R.string.colors);
        desc.setText(R.string.desc_colors);
        image.setImageResource(R.drawable.ic_palette_black_24dp);
    }
    @Override
    public @DrawableRes int getResId(){
        return R.layout.colorselector;
    }
    @Override
    public void onCreateContentView(View view) {
        ViewGroup layout = (ViewGroup) view;
        backgroundPicker = (NumberPicker) layout.findViewById(R.id.background_picker);
        String[] ceviri = view.getContext().getResources().getStringArray(R.array.colormodes);
        String[] data = new String[3];
        data[0] = ceviri[0];
        data[1] = ceviri[1];
        data[2] = ceviri[3];
        Utils.setToMaximumWidth(backgroundPicker,data);
        backgroundPicker.setMinValue(1);
        backgroundPicker.setMaxValue(data.length);
        backgroundPicker.setDisplayedValues(data);
        backgroundPicker.setValue(1);
        backgroundPicker.setWrapSelectorWheel(false);
        backgroundPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                colorModes[BACKGROUND] = newVal;
                if(newVal==1){
                    animateViewHeight(0,true);
                }else{
                    animateViewHeight(0,false);
                }

            }
        });

        wordPicker = (NumberPicker) layout.findViewById(R.id.word_picker);
        String[] dataWord = new String[3];

        dataWord[0] = ceviri[0];
        dataWord[1] = ceviri[1];
        dataWord[2] = ceviri[2];
        Utils.setToMaximumWidth(wordPicker,dataWord);
        wordPicker.setMinValue(1);
        wordPicker.setMaxValue(dataWord.length);
        wordPicker.setDisplayedValues(dataWord);
        wordPicker.setValue(2);
        wordPicker.setWrapSelectorWheel(false);
        wordPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                colorModes[WORD] = newVal;
                if(newVal==2){
                    animateViewHeight(1,false);
                }else{
                    setVisibilityOfSecondButton(newVal==1?View.GONE:View.VISIBLE);
                    int selectedColor = colors[newVal==1?wordSingleColor:wordRangeColor1];
                    findViewById(R.id.selectColorWord1).setBackgroundColor(selectedColor);
                    ((Button)findViewById(R.id.selectColorWord1)).setTextColor(getInverseBW(selectedColor));
                    findViewById(R.id.selectColorWord2).setBackgroundColor(colors[wordRangeColor2]);
                    ((Button)findViewById(R.id.selectColorWord2)).setTextColor(getInverseBW(colors[wordRangeColor2]));

                    animateViewHeight(1,true);
                }


            }
        });
        layout.findViewById(R.id.selectColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setColor(colors[backgroundColor])
                        .setShowAlphaSlider(false)
                        .setDialogId(backgroundColor)
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .show((AppCompatActivity)getContext());
            }
        });
        layout.findViewById(R.id.selectColorWord1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setColor(colors[wordPicker.getValue()==3?wordRangeColor1 : wordSingleColor])
                        .setShowAlphaSlider(false)
                        .setDialogId(wordPicker.getValue()==3?wordRangeColor1 : wordSingleColor)
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .show((AppCompatActivity)getContext());
            }
        });

        layout.findViewById(R.id.selectColorWord2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setColor(colors[wordRangeColor2])
                        .setShowAlphaSlider(false)
                        .setDialogId(wordRangeColor2)
                        .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .show((AppCompatActivity)getContext());
            }
        });
    }

    public void setVisibilityOfSecondButton(int visibility){
        findViewById(R.id.selectColorWord2).setVisibility(visibility);
        findViewById(R.id.arrow_right).setVisibility(visibility);
    }


    public void applyColorToViews(){
        backgroundPicker.setValue(colorModes[BACKGROUND]);
        wordPicker.setValue(colorModes[WORD]);
        findViewById(R.id.selectColor).setBackgroundColor(colors[backgroundColor]);
        ((Button)findViewById(R.id.selectColor)).setTextColor(getInverseBW(colors[backgroundColor]));
        animateViewHeight(BACKGROUND,backgroundPicker.getValue() == COLOR_SINGLE);
        if(wordPicker.getValue() == COLOR_SINGLE){
            animateViewHeight(WORD,true);
            setVisibilityOfSecondButton(View.GONE);
            findViewById(R.id.selectColorWord1).setBackgroundColor(colors[wordSingleColor]);
            ((Button)findViewById(R.id.selectColorWord1)).setTextColor(getInverseBW(colors[wordSingleColor]));
        }else if(wordPicker.getValue() == COLOR_RANDOM){
            animateViewHeight(WORD,false);
        }else{
            animateViewHeight(WORD,true);
            findViewById(R.id.selectColorWord1).setBackgroundColor(colors[wordRangeColor1]);
            ((Button)findViewById(R.id.selectColorWord1)).setTextColor(getInverseBW(colors[wordRangeColor1]));
            findViewById(R.id.selectColorWord2).setBackgroundColor(colors[wordRangeColor2]);
            ((Button)findViewById(R.id.selectColorWord2)).setTextColor(getInverseBW(colors[wordRangeColor2]));
        }

    }



        public int getInverseBW(int rgb) {
            float luminance = (float)(0.2126*((rgb>>>16)&0xff) + 0.7152*((rgb>>>8)&0xff) + 0.0722*(rgb&0xff));
            return (luminance < 140) ? Color.WHITE : Color.BLACK;
        }


        public void animateViewHeight(final int viewNo, boolean height){
            final View viewToAnimate = (viewNo==0) ? findViewById(R.id.background_expandview) : findViewById(R.id.word_expandview);
        if(!isExpanded[viewNo]==height){
            ValueAnimator anim;
            if(animators[viewNo] != null) {
                animators[viewNo].cancel();
                animators[viewNo] = null;
            }

            if(height){
                viewToAnimate.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int targetHeight = viewToAnimate.getMeasuredHeight();
                anim = CustomAnimator.createAnimation(viewToAnimate, CustomAnimator.AnimationType.HEIGHT,0,targetHeight,200);}else{
                anim = CustomAnimator.createAnimation(viewToAnimate, CustomAnimator.AnimationType.HEIGHT,viewToAnimate.getHeight(),0,200);
            }
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animators[viewNo] = null;
                }
            });
            animators[viewNo] = anim;
            isExpanded[viewNo] = height;
        }else{
            if(height){
                viewToAnimate.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int targetHeight = viewToAnimate.getMeasuredHeight();
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)viewToAnimate.getLayoutParams();
                params.height = targetHeight;
                viewToAnimate.setLayoutParams(params);}else{
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)viewToAnimate.getLayoutParams();
                params.height = 0;
                viewToAnimate.setLayoutParams(params);
            }

        }
    }

    public void onColorSelected(int dialogId, int color) {
        colors[dialogId]=color;
    }

    public void onDialogDismissed(int dialogId) {
        applyColorToViews();
    }

}
