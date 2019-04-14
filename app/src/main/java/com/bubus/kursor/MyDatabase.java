package com.bubus.kursor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

    public MyDatabase(Context context, int i){

        super(context, "kursor.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE rates(rates text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addRates(String rates){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rates", rates);
        db.insertOrThrow("rates", null, values);
    }

    public Cursor getAllRates(){
        String[] coluumns = {"rates"};
        SQLiteDatabase db = getReadableDatabase();
        return db.query("rates",  null, null, null, null, null, null);
    }

    public void deleteAllRates() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("rates",null,null);
    }

}
