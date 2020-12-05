package com.cvsoftware.chatcloud;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;

import androidx.core.graphics.ColorUtils;

import com.cvsoftware.chatcloud.utils.ColorSelectorView;
import com.cvsoftware.chatcloud.utils.FontSelectorView;
import com.cvsoftware.chatcloud.utils.Rectangle;
import com.cvsoftware.chatcloud.utils.RecyclerObject;
import com.cvsoftware.chatcloud.utils.SQLHelper;
import com.cvsoftware.chatcloud.utils.Synchronizer;
import com.cvsoftware.chatcloud.utils.Utils;
import com.cvsoftware.chatcloud.utils.Word;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




public class Sketch{
    private RecyclerObject myObject = null;
    public boolean update=false;
    boolean recreateCloud = true;
    private Activity myActivity;
    private ProgressListener listener;
    private static final int THREAD_COUNT = 8;
    private float multiplier = 1;
    private Typeface font;
    ArrayList<WordContainer> cloud;
    ArrayList<WordContainer> words;
    public interface ProgressListener{
        void onProgress(Status status,long progress);
    }

    public enum Status{
        BEGIN,
        CREATING_ARRAY,
        CREATING_CLOUD,
        WRITING_TO_FILE,
        FINISHED
    }

    public Sketch(Activity a,RecyclerObject object){
        myActivity=a;
        setData(object);
    }
    public Sketch(Activity a,RecyclerObject object, boolean recreateCloud){
        myActivity=a;
        setData(object);
        this.update = true;
        this.recreateCloud = recreateCloud;
    }
    public void setFontData(int i){
        font = FontSelectorView.getFont(getActivity(),i);
    }
    public void setData(RecyclerObject object){
        this.myObject = object;
        setFontData(object.font);
    }


    public void setProgressListener(ProgressListener listener){
        this.listener=listener;
    }


    public void start() {
        cloud = new ArrayList<>();

        long c = System.currentTimeMillis();
        // TODO Auto-generated method stub

        System.out.println("File read in " + (System.currentTimeMillis()-c)+ "milliseconds");
        words = new ArrayList<>();
        setListenerStatus(Status.BEGIN,0);
        if(!myObject.getWords().isEmpty()){
        if(recreateCloud){
            float overall = 0;
            for(int i = 0; i < myObject.getWords().size(); i++){
                overall += myObject.getWords().get(i).getFreq();
            }
            multiplier = ((float)myObject.getWords().size()*(20 + myObject.quality*10))/overall;
          
        }

            int numOfCloud = 0;
            for(Word w : myObject.getWords()){
                Log.i("Adding Word",w.getWord()+" : " + numOfCloud + " : " + (w.getFreq()));
                setListenerStatus(Status.CREATING_ARRAY,numOfCloud);
                WordContainer container = new WordContainer(w.getWord(),recreateCloud ? (int)(w.getFreq()*multiplier) : w.getSize(),((float)numOfCloud/(float)myObject.getWords().size()));
                w.setSize(recreateCloud ? (int)(w.getFreq()*multiplier) : w.getSize());
                words.add(container);
                System.gc();
                numOfCloud++;
            }


            if(recreateCloud){
            createCloud(words);}else{
                createCloudLazy();
            }

            writeToFile();
        }
        setListenerStatus(Status.FINISHED,myObject.getId());
    }

    private void setListenerStatus(Status status,long progress){
        if(listener!=null)listener.onProgress(status,progress);
    }

    private Activity getActivity(){
        return myActivity;
    }

    private int getRandomColor(){
        return Color.argb(255,(int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }







    void writeToFile(){
        setListenerStatus(Status.WRITING_TO_FILE,0);
        int minx = cloud.get(0).rect.x;
        int miny = cloud.get(0).rect.y;
        int maxx = cloud.get(0).rect.x;
        int maxy = cloud.get(0).rect.y;
        for(WordContainer w : cloud){
            if(w.rect.x + w.rect.width > maxx){maxx = w.rect.x + w.rect.width;}
            if(w.rect.x < minx){minx = w.rect.x;}
            if(w.rect.y + w.rect.height > maxy){maxy = w.rect.y + w.rect.height;}
            if(w.rect.y < miny){miny = w.rect.y;}

        }
        Paint lazyPaint = new Paint();
        String yazilacak = "Created with WonderCloud";
        Paint.FontMetrics fm = lazyPaint.getFontMetrics();
        lazyPaint.setTypeface(font);
        lazyPaint.setTextSize((maxy-miny)/20f);
        Rect rectBounds = new Rect();
        lazyPaint.getTextBounds(yazilacak ,0,yazilacak.length(),rectBounds);
        int myWidth=(int)(rectBounds.width()*1.05f),myHeight=rectBounds.height()+(int)fm.descent;

        Bitmap lazyBitmap = Bitmap.createBitmap(maxx-minx, maxy-miny+myHeight, Bitmap.Config.ARGB_8888);
//    lazyGraphics = (Graphics2D) lazyImage.getGraphics();
        Canvas lazyCanvas = new Canvas(lazyBitmap);
        if(myObject.colorModes[ColorSelectorView.BACKGROUND]==ColorSelectorView.COLOR_SINGLE){
        lazyCanvas.drawColor(myObject.colors[ColorSelectorView.backgroundColor], PorterDuff.Mode.ADD);}else
            if(myObject.colorModes[ColorSelectorView.BACKGROUND] == ColorSelectorView.COLOR_RANDOM){
        lazyCanvas.drawColor(getRandomColor(), PorterDuff.Mode.ADD);
        }

        for(int i  = 0; i < cloud.size(); i++){
            cloud.get(i).wDraw(lazyCanvas,lazyPaint,minx,miny);
        }

        lazyPaint.setColor(Color.WHITE);
        lazyCanvas.drawText(yazilacak, maxx-minx-myWidth,maxy-miny+myHeight, lazyPaint);



        try (FileOutputStream out = new FileOutputStream(Utils.getImagesFolder(getActivity()) +myObject.getName() + ".png")) {
            lazyBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored9
        } catch (IOException e) {
            e.printStackTrace();
        }

        float scale = 200f / Math.max(lazyBitmap.getHeight(),lazyBitmap.getWidth());
        Bitmap resized = Bitmap.createScaledBitmap(lazyBitmap,(int)(lazyBitmap.getWidth() * scale),(int)(lazyBitmap.getHeight() * scale),false);
        try (FileOutputStream out = new FileOutputStream(Utils.getImagesFolder(getActivity()) + ".thumbs/" + myObject.getName() + ".png")) {
            resized.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored9
        } catch (IOException e) {
            e.printStackTrace();
        }

        SQLHelper helper = new SQLHelper(getActivity());
        if(update){
            helper.updateObject(myObject);
        }
        else{
            Log.i("SAVING",myObject.getName());
            myObject.setId(helper.saveObject(myObject));
        }

    }


    private void createCloud(ArrayList<WordContainer> words){
        int i = 0;
        Long now = System.currentTimeMillis();
        for(final WordContainer w : words){

            final Synchronizer synchronizer = new Synchronizer();
            ExecutorService es = Executors.newCachedThreadPool();
            for(int tid = 0; tid < THREAD_COUNT; tid++){
                es.execute(new Runnable() {
                    @Override
                    public void run(){
                        int aci = synchronizer.getKacinci();
                        Rectangle yeniRect = new Rectangle();
                        Rectangle intersectionRect = new Rectangle();
                        while(synchronizer.getDevam()){
                            yeniRect.setRect( - w.rect.width/2 + (int)(aci*Math.cos(aci)),- w.rect.height/2 + (int)(aci*Math.sin(aci)),w.rect.width,w.rect.height);
                            boolean collide = false;
                            for(int i  = 0; i < cloud.size(); i++){
                                if(w.doesCollide(cloud.get(i),yeniRect,intersectionRect)){
                                    collide = true;
                                    break;
                                }
                            }
                            if(!collide){
                                synchronizer.setResult(aci);
                            }
                            aci = synchronizer.getKacinci();
                        }

                    }
                });
            }
            es.shutdown();
            try{
                boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            }
            catch(Exception e){

            }
            w.rect.x =  - w.rect.width/2 + (int)(synchronizer.getResult()*Math.cos(synchronizer.getResult()));
            w.rect.y =  - w.rect.height/2 + (int)(synchronizer.getResult()*Math.sin(synchronizer.getResult()));
            myObject.getWords().get(i).setX(w.rect.x);
            myObject.getWords().get(i).setY(w.rect.y);
            setListenerStatus(Status.CREATING_CLOUD,i);
            Log.i("Creating Cloud",  (++i)+" " +w.rect.width + " : " + w.rect.height);
            cloud.add(w);
            System.gc();
        }
        Log.i("LOG","Cloud created in " + (System.currentTimeMillis() - now) +"ms." );
    }

   private void createCloudLazy(){
        for(int i = 0; i < myObject.getWords().size(); i++){
         WordContainer container = words.get(i);
         container.rect.x = myObject.getWords().get(i).getX();
         container.rect.y = myObject.getWords().get(i).getY();
         cloud.add(container);
        }
   }



    public class WordContainer{
        int[] myPixels;
        Bitmap myBitmap;
        int col;
        Rectangle rect;


        public WordContainer(String word,int size,float num){
            if(myObject.colorModes[ColorSelectorView.WORD] == ColorSelectorView.COLOR_SINGLE){
                col = myObject.colors[ColorSelectorView.wordSingleColor];
            } else if(myObject.colorModes[ColorSelectorView.WORD] == ColorSelectorView.COLOR_RANDOM){
            col = getRandomColor();}
            else{
                col = ColorUtils.blendARGB(myObject.colors[ColorSelectorView.wordRangeColor1], myObject.colors[ColorSelectorView.wordRangeColor2], num);
            }

            Paint lazyPaint = new Paint();
            lazyPaint.setAntiAlias(true);



            lazyPaint.setTypeface(font);


            Rect rectBounds = new Rect();

            lazyPaint.setTextSize(size);
            Paint.FontMetrics fm = lazyPaint.getFontMetrics();
            lazyPaint.getTextBounds(word,0,word.length(),rectBounds);
            int myWidth=(int)(rectBounds.width()*1.05f),myHeight=rectBounds.height()+(int)fm.descent;
            Bitmap lazyBitmap = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.ARGB_8888);

            Canvas lazyCanvas = new Canvas(lazyBitmap);
            lazyCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            lazyPaint.setColor(col);
            lazyCanvas.drawText(word, 0, rectBounds.height(), lazyPaint);


            rect = new Rectangle();
            rect.setRect(-myWidth/2,-myHeight/2,myWidth,myHeight);
            myPixels = new int[myHeight*myWidth];
            lazyBitmap.getPixels(myPixels, 0, myWidth, 0, 0, myWidth, myHeight);
            myBitmap=lazyBitmap;
        }


        void wDraw(Canvas canvas,Paint paint,int minx,int miny){
           canvas.drawBitmap(myBitmap,rect.x - minx,rect.y - miny,paint);
        }

        boolean doesCollide(WordContainer other,Rectangle Gr,Rectangle intersection){
            if (!Gr.intersects(other.rect)) {    // r1 bottom edge past r2 top
                return false;
            }else{
                Gr.intersection(other.rect,intersection);
                for(int i = intersection.y; i < intersection.y+intersection.height; i++){
                    for(int j = intersection.x; j < intersection.x+intersection.width; j++){
                        if(alpha(myPixels[(j-Gr.x) + (i-Gr.y) * Gr.width]) > 0 && alpha(other.myPixels[(j-other.rect.x) + (i-other.rect.y) * other.rect.width]) > 0){
                            return true;
                        }
                    }}
                return false;
            }


        }

     public int alpha(int color){
            return (color&0xff000000)>>>24;
     }


    }
}