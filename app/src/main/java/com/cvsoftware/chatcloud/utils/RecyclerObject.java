package com.cvsoftware.chatcloud.utils;


import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerObject implements Serializable {
    private long id;
    private ArrayList<Word> words;
    private Date date;
    public int quality;
    public int[] colors;
    public int[] colorModes;
    public int font;
    private String name;

    public RecyclerObject(){
        words = new ArrayList<>();
        Date date = new Date();
        quality=4;
        colors = new int[]{Color.BLACK,Color.WHITE,Color.RED,Color.GREEN};
        colorModes = new int[]{ColorSelectorView.COLOR_SINGLE,ColorSelectorView.COLOR_RANDOM};
        font=1;
        name="null";
    }
    public RecyclerObject(String name, Date date, ArrayList<Word> words, int[] colorModes,int[] colors,
                          int quality, int font){
        this.words = words;
        this.date = date;
        this.name = name;
        this.colorModes = colorModes;
        this.colors = colors;
        this.quality = quality;
        this.font = font;

    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
