package com.cvsoftware.chatcloud.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializator {
    public static String serialize(Object object){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes=null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();

            } catch (IOException ex) {
                // ignore close exception
            }
        }
        if(bytes==null)return null;
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    public static Object deserialize(String string){
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(string,Base64.DEFAULT));
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
       return null;
    }

}
