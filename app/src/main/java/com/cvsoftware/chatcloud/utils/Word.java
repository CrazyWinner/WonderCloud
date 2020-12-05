package com.cvsoftware.chatcloud.utils;

import java.io.Serializable;

public class Word implements Serializable {

    private String word;
    private int freq;
    private int x,y,size;
    public Word(String w, int f){
        word = w;
        freq = f;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
