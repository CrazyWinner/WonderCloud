package com.cvsoftware.chatcloud;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.cvsoftware.chatcloud.utils.ColorSelectorView;
import com.cvsoftware.chatcloud.utils.LoadingDialog;
import com.cvsoftware.chatcloud.utils.RecyclerObject;
import com.cvsoftware.chatcloud.utils.SQLHelper;
import com.cvsoftware.chatcloud.utils.Utils;
import com.cvsoftware.chatcloud.utils.Word;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ProcessingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        Intent intent = getIntent();
        File folder = new File(Utils.getImagesFolder(this));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        folder = new File(Utils.getImagesFolder(this) + ".thumbs");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File nomediaFile= new File(Utils.getImagesFolder(this) + ".nomedia");
        if(!nomediaFile.exists()){
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final ArrayList<Word> topWords = (ArrayList<Word>)intent.getSerializableExtra("words");
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        String nameToRead = intent.getExtras().getString("name","null");
        String name = nameToRead;
        File file = new File(Utils.getImagesFolder(this) + name + ".png");
        int fileNo = 1;
        while(file.exists()){
            name = nameToRead + "(" + fileNo + ")";
            file = new File(Utils.getImagesFolder(this) + name + ".png");
            fileNo++;
        }
        RecyclerObject object = new RecyclerObject(
                name,
                new Date(),
                topWords,
                new int[]{
                        intent.getIntExtra("colorModeBackground", ColorSelectorView.COLOR_SINGLE),
                        intent.getIntExtra("colorModeWord", ColorSelectorView.COLOR_RANDOM)
                },
                new int[]{
                        intent.getIntExtra("colorBackground", Color.GREEN),
                        intent.getIntExtra("colorWordSingle", Color.WHITE),
                        intent.getIntExtra("colorWordRange1", Color.RED),
                        intent.getIntExtra("colorWordRange2", Color.GREEN)
                },
                intent.getIntExtra("quality",50),
                intent.getIntExtra("font",1)

        );

        final Sketch sketch = new Sketch(this,object);

        sketch.setProgressListener(new Sketch.ProgressListener() {
            @Override
            public void onProgress(final Sketch.Status status, final long progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(status == Sketch.Status.CREATING_ARRAY){
                            float a = 15f * progress / topWords.size();
                            loadingDialog.setProgress((int)a);
                        }else if(status == Sketch.Status.CREATING_CLOUD){
                            float a = 15 + 80f * progress / topWords.size();
                            loadingDialog.setProgress((int)a);
                        }else if(status == Sketch.Status.WRITING_TO_FILE){
                            loadingDialog.setProgress(95);
                        }
                        else if(status == Sketch.Status.FINISHED){
                            loadingDialog.cancel();
                            Intent i = new Intent(ProcessingActivity.this,SavedActivity.class);
                            i.putExtra("id",progress);
                            startActivity(i);
                            finish();
                        }

                    }
                });

            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sketch.start();
            }
        });
        t.start();






    }


}
