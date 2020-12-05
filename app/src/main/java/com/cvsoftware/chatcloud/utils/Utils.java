package com.cvsoftware.chatcloud.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;
public class Utils {
    public static String getImagesFolder(Context c){
        return Environment.getExternalStorageDirectory() + "/WonderCloud/";
    }
    public static void setToMaximumWidth(NumberPicker picker,String[] data){
        float px = picker.getTextSize();
        float sp = px / picker.getResources().getDisplayMetrics().density;
        TextView denemeTahtasi = new TextView(picker.getContext());

        denemeTahtasi.setTextSize(sp);
        denemeTahtasi.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        float maxVal = 0;
        for(int i = 0; i < data.length; i++){
            denemeTahtasi.setText(data[i]);
            denemeTahtasi.measure(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if(denemeTahtasi.getMeasuredWidth() > maxVal){
                maxVal = denemeTahtasi.getMeasuredWidth();
            }
        }
        float a = dpToPx(picker.getContext(),picker.getDividerDistance());
        float newa = pxToDp(picker.getContext(),maxVal+15);
        Log.i("UTILS","old:" + a + " new:" + newa);
      //  picker.setDividerDistance((int)newa);
        Log.i("UTILS",picker.getTextSize()+" : "+picker.getSelectedTextSize());
        picker.setTextSize((float)(picker.getSelectedTextSize()*a/newa*0.8));
        picker.setSelectedTextSize(picker.getSelectedTextSize()*a/newa);

    }
    public static int measureTextSizeInPx(Context context,int size,TextView tw){
        tw.setTextSize(size);
        tw.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return tw.getMeasuredWidth();
    }



    public static int pxToDp(Context c, float px) {
        return (int) (px / c.getResources().getDisplayMetrics().density);
    }
    public static int dpToPx(Context c, float dp) {
        return (int) (dp * c.getResources().getDisplayMetrics().density);
    }
}
