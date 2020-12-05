package com.cvsoftware.chatcloud.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.cvsoftware.chatcloud.R;
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateSelectorView extends ExpandableCardView{
    public static final int DATE_ALLTIME=0;
    public static final int DATE_RANGE=1;
    ValueAnimator animator;
    boolean isExpanded=false;
    Calendar from;
    Calendar to;
    ViewGroup layout;

    public DateSelectorView(Context c) {
        super(c);
    }
    public void setDateFormatFound(boolean dateFormatFound){
        if(dateFormatFound){
            layout.findViewById(R.id.notfound).setVisibility(INVISIBLE);
        }else{
            layout.findViewById(R.id.dateview).setVisibility(INVISIBLE);
        }
    }

    public DateSelectorView(Context c, ExpandableCardView.AsyncCreateFinishedListener listener){
        super(c,listener);
    }

    public static void minusDays(Calendar c, int days)
    {
      //  c.set(Calendar.MILLISECOND,c.get(Calendar.MILLISECOND)-(long)days*1000*60*60*24);

    }

    @Override
    public @DrawableRes
    int getResId(){
        return R.layout.dateselector;
    }
    NumberPicker datePicker;
    @Override
public void onCreateContentView(View view) {
    layout = (ViewGroup) view;
    from = new GregorianCalendar();
    to = new GregorianCalendar();
    updateDates();

        layout.findViewById(R.id.notfound).setVisibility(INVISIBLE);
        datePicker = (NumberPicker) layout.findViewById(R.id.date_picker);
        String[] dataDate = datePicker.getContext().getResources().getStringArray(R.array.timemodes);
        Utils.setToMaximumWidth(datePicker,dataDate);
        datePicker.setMinValue(1);
        datePicker.setMaxValue(dataDate.length);
        datePicker.setDisplayedValues(dataDate);
        datePicker.setValue(1);
        datePicker.setWrapSelectorWheel(false);
        datePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                animateViewHeight(newVal!=1);
            }
        });
        layout.findViewById(R.id.set_from_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportedDatePickerDialog dialog = new SupportedDatePickerDialog(getContext(), new SupportedDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        from.set(Calendar.YEAR,i);
                        from.set(Calendar.MONTH,i1);
                        from.set(Calendar.DAY_OF_MONTH,i2);
                        updateDates();
                    }
                },from.get(Calendar.YEAR),from.get(Calendar.MONTH),from.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        layout.findViewById(R.id.set_to_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SupportedDatePickerDialog dialog = new SupportedDatePickerDialog(getContext(), new SupportedDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        to.set(Calendar.YEAR,i);
                        to.set(Calendar.MONTH,i1);
                        to.set(Calendar.DAY_OF_MONTH,i2);
                        updateDates();
                    }
                },to.get(Calendar.YEAR),to.get(Calendar.MONTH),to.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


}

    @Override
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){
        title.setText(R.string.time_frame);
        desc.setText(R.string.desc_time_frame);
        image.setImageResource(R.drawable.ic_date_range_black_24dp);
    }



    private void updateDates(){
    ((TextView)layout.findViewById(R.id.text_from)).setText("From:"+DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(from.getTime()));
    ((TextView)layout.findViewById(R.id.text_to)).setText("To:"+DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(to.getTime()));


}
public boolean isRange(){
        return datePicker.getValue()==2;
}
public Date getToDate(){
        return to.getTime();
}
public Date getFromDate(){
        return from.getTime();
}
private void animateViewHeight(boolean expand){
    if(expand != isExpanded){
        View viewToAnimate = layout.findViewById(R.id.date_expandview);
        if(animator!=null){
            animator.cancel();
            animator=null;
        }
        if(expand){
            viewToAnimate.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int targetHeight = viewToAnimate.getMeasuredHeight();
            animator = CustomAnimator.createAnimation(viewToAnimate, CustomAnimator.AnimationType.HEIGHT,0,targetHeight,200);}else{
            animator = CustomAnimator.createAnimation(viewToAnimate, CustomAnimator.AnimationType.HEIGHT,viewToAnimate.getHeight(),0,200);
        }
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator=null;
            }
        });
        isExpanded=expand;
    }
}

}
