package com.cvsoftware.chatcloud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.cvsoftware.chatcloud.utils.ColorSelectorView;
import com.cvsoftware.chatcloud.utils.DateSelectorView;
import com.cvsoftware.chatcloud.utils.ExcludedWordsSelectorView;
import com.cvsoftware.chatcloud.utils.FileUtils;
import com.cvsoftware.chatcloud.utils.FontSelectorView;
import com.cvsoftware.chatcloud.utils.LoadingDialog;
import com.cvsoftware.chatcloud.utils.MaxWordsSelectorView;
import com.cvsoftware.chatcloud.utils.MinLengthSelectorView;
import com.cvsoftware.chatcloud.utils.QualitySelectorView;
import com.cvsoftware.chatcloud.utils.Word;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.appbar.AppBarLayout;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SendActivity extends AppCompatActivity implements ColorPickerDialogListener {

    boolean didLoad = false;
    private String name;
    boolean dateFormatFound=false;
    DateFormat dateFormat;
    FontSelectorView fontSelector;
    ColorSelectorView colorSelector;
    MinLengthSelectorView minLengthSelector;
    MaxWordsSelectorView maxWordsSelector;
    DateSelectorView dateSelector;
    QualitySelectorView qualitySelector;
    ExcludedWordsSelectorView excludedWordsSelector;
    ArrayList<File> filesToRead;
    AppBarLayout appBar;
    HashMap<String,Integer> kelimeler;
    ArrayList<Word> topWords;
    LoadingDialog loadingDialog;
    NestedScrollView scrollView;
    AdView mAdView;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        long a = System.currentTimeMillis();
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_send);
       scrollView = findViewById(R.id.nestedScrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return !didLoad;
            }
        });

        appBar = findViewById(R.id.app_bar);
        Intent intent = getIntent();
        filesToRead = new ArrayList<>();
        topWords = new ArrayList<>();
        kelimeler = new HashMap<>();
        String action = intent.getAction();
        String type = intent.getType();
        if(type != null){
        Log.i("TYPE",type);
        if (type.startsWith("text/") || type.startsWith("*/")) {
            if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                handleMultipleText(intent);
            }else if(Intent.ACTION_SEND.equals(action)){
                handleSingleText(intent);
            }

        }}else{
            Toast.makeText(this,R.string.notwhatsapp,Toast.LENGTH_LONG).show();
            finish();
        }

        for(File f : filesToRead){
            findDateFormat(f.getAbsolutePath());
        }

         didLoad = true;
         fontSelector = new FontSelectorView(this);
         fontSelector.attachToNestedScrollView(scrollView);
         colorSelector = new ColorSelectorView(this);
         colorSelector.attachToNestedScrollView(scrollView);
         dateSelector = new DateSelectorView(this);
         dateSelector.setDateFormatFound(dateFormatFound);
         dateSelector.attachToNestedScrollView(scrollView);
         qualitySelector = new QualitySelectorView(this);
         qualitySelector.attachToNestedScrollView(scrollView);
         minLengthSelector = new MinLengthSelectorView(this);
         minLengthSelector.attachToNestedScrollView(scrollView);
         maxWordsSelector = new MaxWordsSelectorView(this);
         maxWordsSelector.attachToNestedScrollView(scrollView);
         excludedWordsSelector = new ExcludedWordsSelectorView(this);
         excludedWordsSelector.attachToNestedScrollView(scrollView);
         Log.i("LOCALE", Locale.getDefault().getDisplayLanguage());
        Log.i("LOCALE", Locale.getDefault().getISO3Country());
        String[] defaultExcluded = getResources().getStringArray(R.array.defaultexcluded);
        for(int i = 0; i < defaultExcluded.length; i++){
            excludedWordsSelector.adapter.addItem(defaultExcluded[i]);
        }
        LinearLayout contentView = scrollView.findViewById(R.id.nestedScrollContent);
        contentView.addView(qualitySelector);
        contentView.addView(minLengthSelector);
        contentView.addView(maxWordsSelector);
        contentView.addView(excludedWordsSelector);
        contentView.addView(colorSelector);
        contentView.addView(dateSelector);
        contentView.addView(fontSelector);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView


    }
    @Override
    public void onResume() {
        super.onResume();

        // Resume the AdView.
        mAdView.resume();
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        mAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        mAdView.destroy();

        super.onDestroy();
    }



    public void readFiles(View w){
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                kelimeler.clear();
                topWords.clear();
                for(File f : filesToRead){
                    readFile(f.getAbsolutePath());
                    name = f.getName();
                }
                sirala();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.cancel();
                        startCreatingWordCloud();
                    }
                });
            }
        });
       t.start();

    }

    private void startCreatingWordCloud(){
        if(didLoad){
            if(!topWords.isEmpty()){
        Intent i = new Intent(this,ProcessingActivity.class);
        String fileExtension = getExtension(name);
        String sendName = "null";
        if(name.length()>2){
        sendName = name.substring(0,name.length()-fileExtension.length()-1);}
        i.putExtra("colorModeWord",colorSelector.getColorModes()[ColorSelectorView.WORD]);
        i.putExtra("colorModeBackground",colorSelector.getColorModes()[ColorSelectorView.BACKGROUND]);
        i.putExtra("colorWordSingle",colorSelector.getColors()[ColorSelectorView.wordSingleColor]);
        i.putExtra("colorWordRange1",colorSelector.getColors()[ColorSelectorView.wordRangeColor1]);
        i.putExtra("colorWordRange2",colorSelector.getColors()[ColorSelectorView.wordRangeColor2]);
        i.putExtra("colorBackground",colorSelector.getColors()[ColorSelectorView.backgroundColor]);
        i.putExtra("quality",qualitySelector.getValue());
        i.putExtra("font",fontSelector.getSelectedFont());
        i.putExtra("words",topWords);
        i.putExtra("name",sendName);
        startActivity(i);
        finish();}else{
                Toast.makeText(this,R.string.cantfind,Toast.LENGTH_LONG).show();
                finish();
            }}
    }


    private boolean checkDateValid(DateFormat format, String filename){
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            Log.i("Reading Date Format",filename);
            int linenum=0;
            String line;

            while ((line = reader.readLine()) != null)
            {
                linenum++;
                line = line.toLowerCase();
                if(line.contains("-") && line.indexOf('-')!=0){
                    line = line.substring(0,line.indexOf('-')-1);
                    if(checkDatePattern(format,line)){
                        return true;
                    }
                }
                if(linenum>9)break;
            }
            reader.close();

            // createCloud(words);

        }
        catch (Exception e)
        {
            return false;
        }
        return false;

    }

    private void findDateFormat(String filename){
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        if(checkDateValid(dateFormat,filename)){
            dateFormatFound=true;
        }else{
            dateFormat = new SimpleDateFormat("MM/dd/yy, hh:mm");
            if(checkDateValid(dateFormat,filename)){
                dateFormatFound=true;
            }
        }

    }
    private boolean checkDatePattern(DateFormat format,String data) {
        try {
            format.parse(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private void readFile(String filename)
    {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("Reading File..",filename);
            long linenum=0;
            String line=null;
            Date tarih=null;
            boolean devamet = true;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
                line = line.toLowerCase();
                linenum++;
                if (linenum % 400 == 0) {
                    final int newProgress = (int) (linenum / 400);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.setProgress(newProgress);
                        }
                    });
                }

                if (line.contains("-") && line.contains(":")) {
                    if (dateSelector.isRange() && dateFormatFound) {
                        Date tarih1 = null;
                        try {
                            tarih1 = dateFormat.parse(line.substring(0, line.indexOf('-')-2));
                        } catch (Exception ignored) {

                        }
                        if (tarih1 == null) {
                            if (tarih != null) {
                                devamet = tarih.before(dateSelector.getToDate()) && tarih.after(dateSelector.getFromDate());
                            } else {
                                devamet = false;
                            }
                        } else {
                            devamet = tarih1.before(dateSelector.getToDate()) && tarih1.after(dateSelector.getFromDate());
                        }
                        tarih = tarih1;

                    }
                    try{
                    line = line.substring(line.indexOf('-') + 1);
                    line = line.substring(line.indexOf(':') + 2);}
                    catch (Exception ignored){
                    }
                }

                 if(devamet){
                for (int i = 0; i < excludedWordsSelector.getItems().size(); i++) {
                    line = line.replace(excludedWordsSelector.getItems().get(i), "");
                }

                String[] bulunan = line.split(" ");
                for (int i = 0; i < bulunan.length; i++) {
                    String kelime = bulunan[i];
                    if (kelime.length() >= minLengthSelector.getValue()) {

                        if (kelimeler.containsKey(kelime)) {
                            kelimeler.put(kelime, kelimeler.get(kelime) + 1);
                        } else {
                            kelimeler.put(kelime, 1);
                        }
                    }
                }
            }
                 devamet=true;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            Log.i("Finished reading file.",filename);
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // createCloud(words);

    }
    void sirala(){
        Object[] a = kelimeler.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());

            }
        });
        System.out.println("\n\n\n\n\n\n");
        int i = 0;

        for (Object e : a) {
            // System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
            //         + ((Map.Entry<String, Integer>) e).getValue());
            topWords.add(new Word(((Map.Entry<String, Integer>) e).getKey(),((Map.Entry<String, Integer>) e).getValue()));
            if(i++ > maxWordsSelector.getValue()) break;
        }

    }

    void handleSingleText(Intent intent) {
        if(intent.getClipData()!=null && intent.getClipData().getItemAt(0)!=null){
            FileUtils util = new FileUtils(this);
            String path = util.getPath(intent.getClipData().getItemAt(0).getUri());
            if(getExtension(path)!=null && getExtension(path).equals("txt")){
                File file = new File(path);
                if(file.exists()){
                    filesToRead.add(file);
                }
            }
        }else{
            Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show();
        }



    }
    void handleMultipleText(Intent intent) {

         FileUtils util = new FileUtils(this);
        ArrayList<Uri> textUris =  intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (textUris != null && !textUris.isEmpty()) {
            for(Uri a : textUris){
                if(util.isWhatsAppFile(a)) {
                    String path = util.getPath(a);
                    if (getExtension(path) != null && getExtension(path).equals("txt")) {
                        File file = new File(path);
                        if (file.exists()) {
                            filesToRead.add(file);
                        }
                    }
                }
            }
            // Update UI to reflect multiple images being shared
        }else{
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    public String getExtension(String file) {
        if(file.contains(".")){
            return file.substring(file.lastIndexOf(".") + 1);
        }
        return null;
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        colorSelector.onColorSelected(dialogId,color);
    }


    @Override
    public void onDialogDismissed(int dialogId) {
      colorSelector.onDialogDismissed(dialogId);
    }

}
