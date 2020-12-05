package com.cvsoftware.chatcloud.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.cvsoftware.chatcloud.R;
import com.shawnlin.numberpicker.NumberPicker;

public class FontSelectorView extends ExpandableCardView{
    NumberPicker picker;
    public FontSelectorView(Context c, ExpandableCardView.AsyncCreateFinishedListener listener){
        super(c,listener);
    }
    public FontSelectorView(Context c) {
        super(c);
    }
    @Override
    public @DrawableRes
    int getResId(){
        return R.layout.fontselector;
    }
    @Override
    public void onCreateContentView(View view) {
        final ViewGroup layout = (ViewGroup) view;
        String[] data = new String[]{"Default","Bold","Monospace","Sans Serif","Serif","Roboto"};
        picker = (NumberPicker) layout.findViewById(R.id.font_picker);
        picker.setMinValue(1);
        picker.setMaxValue(data.length);
        picker.setDisplayedValues(data);
        picker.setValue(1);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ((TextView)layout.findViewById(R.id.text_preview)).setTypeface(getFont(getContext(),newVal));
            }
        });
    }
    public void setSelectedFont(int i){
        picker.setValue(i);
    }

    public int getSelectedFont(){
        return picker.getValue();
    }
    public static Typeface getFont(Context c, int i){
        switch (i){
            case 1:
                return Typeface.DEFAULT;
            case 2:
                return Typeface.DEFAULT_BOLD;
            case 3:
                return Typeface.MONOSPACE;
            case 4:
                return Typeface.SANS_SERIF;
            case 5:
                return Typeface.SERIF;
            case 6:
                return Typeface.createFromAsset(c.getAssets(),"Roboto-Black.ttf");

        }
        return null;

    }

    @Override
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){
        title.setText(R.string.font);
        desc.setText(R.string.desc_font);
        image.setImageResource(R.drawable.ic_font_download_black_24dp);
    }

}
