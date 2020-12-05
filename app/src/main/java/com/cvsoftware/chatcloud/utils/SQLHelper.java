package com.cvsoftware.chatcloud.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cvsoftware.chatcloud.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "CLOUD_DATABASE";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "CLOUDS";
    private static final String CLOUD_ID = "c_id";
    private static final String CLOUD_NAME = "c_name";
    private static final String CLOUD_WORDS = "c_words";
    private static final String CLOUD_DATE = "c_date";
    private static final String CLOUD_QUALITY = "c_quality";
    private static final String CLOUD_WORD_COLOR_MODE = "c_w_cmode";
    private static final String CLOUD_WORD_COLOR_SINGLE = "c_w_cs";
    private static final String CLOUD_WORD_COLOR_RANGE1 = "c_w_cr1";
    private static final String CLOUD_WORD_COLOR_RANGE2 = "c_w_cr2";
    private static final String CLOUD_BACKGROUND_COLOR_MODE = "c_b_cmode";
    private static final String CLOUD_BACKGROUND_COLOR = "c_b_c";
    private static final String CLOUD_FONT = "c_font";
    SimpleDateFormat formatter;
    @SuppressLint("SimpleDateFormat")
    public SQLHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {  // Databesi oluşturuyoruz.Bu methodu biz çağırmıyoruz. Databese de obje oluşturduğumuzda otamatik çağırılıyor.
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + CLOUD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CLOUD_NAME + " TEXT,"
                + CLOUD_WORDS + " TEXT,"
                + CLOUD_DATE + " TEXT,"
                + CLOUD_QUALITY + " INT,"
                + CLOUD_WORD_COLOR_MODE + " INT,"
                + CLOUD_WORD_COLOR_SINGLE + " INT,"
                + CLOUD_WORD_COLOR_RANGE1 + " INT,"
                + CLOUD_WORD_COLOR_RANGE2 + " INT,"
                + CLOUD_BACKGROUND_COLOR_MODE + " INT,"
                + CLOUD_BACKGROUND_COLOR+ " INT,"
                + CLOUD_FONT + " INT" + ")";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long saveObject(RecyclerObject object){

        SQLiteDatabase db = getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CLOUD_NAME,object.getName());
        values.put(CLOUD_DATE, formatter.format(object.getDate()));
        values.put(CLOUD_WORDS,Serializator.serialize(object.getWords()));
        values.put(CLOUD_QUALITY,object.quality);
        values.put(CLOUD_WORD_COLOR_MODE,object.colorModes[ColorSelectorView.WORD]);
        values.put(CLOUD_BACKGROUND_COLOR_MODE,object.colorModes[ColorSelectorView.BACKGROUND]);
        values.put(CLOUD_WORD_COLOR_SINGLE,object.colors[ColorSelectorView.wordSingleColor]);
        values.put(CLOUD_WORD_COLOR_RANGE1,object.colors[ColorSelectorView.wordRangeColor1]);
        values.put(CLOUD_WORD_COLOR_RANGE2,object.colors[ColorSelectorView.wordRangeColor2]);
        values.put(CLOUD_BACKGROUND_COLOR,object.colors[ColorSelectorView.backgroundColor]);
        values.put(CLOUD_FONT,object.font);
        Log.i("SAVING",object.getName());
// Insert the new row, returning the primary key value of the new row
        long ret = db.insert(TABLE_NAME, null, values);
        db.close();
        return ret;

    }
    public ArrayList<RecyclerObject> loadObjects(){

        SQLiteDatabase db = getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.


// Filter results WHERE "title" = 'My Title'
        String selection = "";
        String[] selectionArgs = {};

// How you want the results sorted in the resulting Cursor

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null            // The sort order
        );
        ArrayList<RecyclerObject> items = new ArrayList<>();
        while(cursor.moveToNext()) {
           RecyclerObject item = getNextObject(cursor);
            items.add(item);
        }
        cursor.close();
        db.close();
      return items;


    }
    public RecyclerObject loadObject(long id){

        SQLiteDatabase db = getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.


// Filter results WHERE "title" = 'My Title'
        String selection = CLOUD_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

// How you want the results sorted in the resulting Cursor

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null            // The sort order
        );
        RecyclerObject item = null;
        while(cursor.moveToNext()) {
            item = getNextObject(cursor);
        }
        cursor.close();
        db.close();
        return item;


    }

    public void updateObject(RecyclerObject object){
        SQLiteDatabase db = getWritableDatabase();

// New value for one column

        ContentValues values = new ContentValues();
        values.put(CLOUD_NAME,object.getName());
        values.put(CLOUD_DATE, formatter.format(object.getDate()));
        values.put(CLOUD_WORDS,Serializator.serialize(object.getWords()));
        values.put(CLOUD_QUALITY,object.quality);
        values.put(CLOUD_WORD_COLOR_MODE,object.colorModes[ColorSelectorView.WORD]);
        values.put(CLOUD_BACKGROUND_COLOR_MODE,object.colorModes[ColorSelectorView.BACKGROUND]);
        values.put(CLOUD_WORD_COLOR_SINGLE,object.colors[ColorSelectorView.wordSingleColor]);
        values.put(CLOUD_WORD_COLOR_RANGE1,object.colors[ColorSelectorView.wordRangeColor1]);
        values.put(CLOUD_WORD_COLOR_RANGE2,object.colors[ColorSelectorView.wordRangeColor2]);
        values.put(CLOUD_BACKGROUND_COLOR,object.colors[ColorSelectorView.backgroundColor]);
        values.put(CLOUD_FONT,object.font);

// Which row to update, based on the title
        String selection = CLOUD_ID + " = ?";
        String[] selectionArgs = { String.valueOf(object.getId()) };

        db.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();

    }
    public void deleteObject(RecyclerObject object){
        SQLiteDatabase db = getWritableDatabase();
        String selection = CLOUD_ID + " = ?";
        String[] selectionArgs = { String.valueOf(object.getId()) };

        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }
    private RecyclerObject getNextObject(Cursor cursor){
        RecyclerObject item = new RecyclerObject();

        try {
            item.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(CLOUD_ID)));
            item.setDate(formatter.parse(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(CLOUD_DATE))
            ));
            item.setWords((ArrayList<Word>) Serializator.deserialize(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(CLOUD_WORDS))
            ));
            item.setName(cursor.getString(
                    cursor.getColumnIndexOrThrow(CLOUD_NAME)));
            item.quality = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_QUALITY));
            item.colorModes[ColorSelectorView.WORD] = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_WORD_COLOR_MODE));
            item.colors[ColorSelectorView.wordSingleColor] = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_WORD_COLOR_SINGLE));
            item.colors[ColorSelectorView.wordRangeColor1] = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_WORD_COLOR_RANGE1));
            item.colors[ColorSelectorView.wordRangeColor2] = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_WORD_COLOR_RANGE2));
            item.colors[ColorSelectorView.backgroundColor] = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_BACKGROUND_COLOR));
            item.colorModes[ColorSelectorView.BACKGROUND] = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_BACKGROUND_COLOR_MODE));
            item.font = cursor.getInt(
                    cursor.getColumnIndexOrThrow(CLOUD_FONT));


        } catch (ParseException e) {
            e.printStackTrace();
        }

       return item;
    }






}
