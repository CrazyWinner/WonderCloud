package com.cvsoftware.chatcloud.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.cvsoftware.chatcloud.R;
import com.shawnlin.numberpicker.NumberPicker;

public class MaxWordsSelectorView extends ExpandableCardView {
    NumberPicker picker;
    public MaxWordsSelectorView(Context c) {
        super(c);
    }

    public MaxWordsSelectorView(Context c, ExpandableCardView.AsyncCreateFinishedListener listener){
        super(c,listener);
    }

    @Override
    public @DrawableRes
    int getResId(){
        return R.layout.maxwordselector;
    }

    @Override
    public void onCreateContentView(View view) {
        ViewGroup layout = (ViewGroup) view;
        picker = layout.findViewById(R.id.number_picker);
    }
    public void setValue(int value){
        picker.setValue(value);
    }
    public int getValue(){
        return picker.getValue();
    }

    @Override
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){
        title.setText(R.string.maximum_word_count);
        desc.setText(R.string.desc_maximum_word_count);
        image.setImageResource(R.drawable.ic_blur_circular_black_24dp);
    }

}
