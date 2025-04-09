package com.example.tikiparkapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Caching with sqlite
public class LocalCache extends SQLiteOpenHelper {

    private static final String DB_NAME = "TikiParkCache.db";
    private static final int DB_VERSION = 1;

    public LocalCache(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS UserSession (username TEXT, role TEXT, isLoggedIn INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS UserSession");
        onCreate(db);
    }

    public void cacheUserSession(String username, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM UserSession");
        db.execSQL("INSERT INTO UserSession (username, role, isLoggedIn) VALUES (?, ?, 1)",
                new Object[]{username, role});
    }

    public Cursor getUserSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM UserSession WHERE isLoggedIn=1", null);
    }

    public void clearSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM UserSession");
    }
}
