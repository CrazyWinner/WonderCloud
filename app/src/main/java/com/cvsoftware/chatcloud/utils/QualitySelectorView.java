package com.cvsoftware.chatcloud.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.cvsoftware.chatcloud.R;
import com.shawnlin.numberpicker.NumberPicker;

public class QualitySelectorView extends ExpandableCardView {
    NumberPicker qualityPicker;
    public QualitySelectorView(Context c) {
        super(c);
    }
    public void onCreateContentView(View view) {
        ViewGroup layout = (ViewGroup) view;
        qualityPicker = layout.findViewById(R.id.number_picker);
        String[] data = view.getContext().getResources().getStringArray(R.array.qualities);
        Utils.setToMaximumWidth(qualityPicker,data);
        qualityPicker.setMinValue(1);
        qualityPicker.setMaxValue(data.length);
        qualityPicker.setDisplayedValues(data);
        qualityPicker.setValue(4);
        qualityPicker.setWrapSelectorWheel(false);
    }

    public QualitySelectorView(Context c, ExpandableCardView.AsyncCreateFinishedListener listener){
        super(c,listener);
    }

    @Override
    public @DrawableRes
    int getResId(){
        return R.layout.qualityselector;
    }
    public void setValue(int value){
        qualityPicker.setValue(value);
    }
    public int getValue(){
        return qualityPicker.getValue();
    }
    @Override
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){
        title.setText(R.string.quality);
        desc.setText(R.string.desc_quality);
        image.setImageResource(R.drawable.ic_data_usage_black_24dp);
    }


}
