package com.cvsoftware.chatcloud.utils;

public class Synchronizer{
    private int kacinci;
    private boolean devam;
    private int finalnum;
    public Synchronizer(){
        kacinci = 0;
        devam = true;
        finalnum = 0;
    }
    public synchronized int getKacinci(){
        return kacinci++;
    }
    public synchronized int getResult(){
        return finalnum;
    }
    public synchronized boolean getDevam(){
        return devam;
    }
    private synchronized void setDevam(boolean d){
        devam = d;
    }
    private synchronized void setNum(int num){
        finalnum = num;
    }
    public synchronized void setResult(int aci){
        if(getDevam()){
            setDevam(false);
            setNum(aci);
        }else{
            if(getResult() > aci){
                setNum(aci);
            }
        }
    }

}
