package com.cvsoftware.chatcloud.utils;

public class Rectangle{
    public int x,y,width,height;

    public void setSize(int w,int h){
        this.width = w;
        this.height = h;
    }
    public void setRect(int x,int y,int w, int h){
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;
    }
    public boolean intersects(Rectangle r)
    {
        return r.width > 0 && r.height > 0 && width > 0 && height > 0
                && r.x < x + width && r.x + r.width > x
                && r.y < y + height && r.y + r.height > y;
    }

    public void intersection(Rectangle src2,Rectangle dest)
    {
        int x = Math.max(this.x, src2.x);
        int y = Math.max(this.y, src2.y);
        int maxx = Math.min(getMaxX(), src2.getMaxX());
        int maxy = Math.min(getMaxY(), src2.getMaxY());
        dest.setRect(x, y, maxx - x, maxy - y);
    }

    public int getMaxX()
    {
        return this.x + this.width;
    }

    public int getMaxY()
    {
        return this.y + this.height;
    }
}