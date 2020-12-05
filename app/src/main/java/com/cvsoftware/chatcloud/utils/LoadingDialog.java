package com.cvsoftware.chatcloud.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.cvsoftware.chatcloud.R;

public class LoadingDialog {
    ProgressDialog progressBar;
    public LoadingDialog (Context c){
        progressBar = new ProgressDialog(c);
        progressBar.setTitle(R.string.pleasewait);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage(c.getResources().getString(R.string.loading));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar
    }
    public void show(){

        progressBar.show();
    }
    public void cancel(){
        progressBar.cancel();
    }
    public void setProgress(int progress){
     progressBar.setProgress(progress);
    }

}