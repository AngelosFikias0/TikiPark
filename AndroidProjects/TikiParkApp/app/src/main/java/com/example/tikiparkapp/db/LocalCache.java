package com.example.tikiparkapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalCache extends SQLiteOpenHelper {

    private static final String DB_NAME = "TikiParkCache.db";
    private static final int DB_VERSION = 1;

    public LocalCache(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //Creates the user Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS UserSession (username TEXT, role TEXT, isLoggedIn INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS UserSession");
        onCreate(db);
    }

    //Caches user session
    public void cacheUserSession(String username, String role) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.beginTransaction();
            try {
                db.execSQL("DELETE FROM UserSession");
                db.execSQL("INSERT INTO UserSession (username, role, isLoggedIn) VALUES (?, ?, 1)",
                        new Object[]{username, role});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    // Return parsed user session, NOT raw cursor
    public UserSession getUserSession() {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT username, role FROM UserSession WHERE isLoggedIn=1", null)) {

            if (cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                return new UserSession(username, role);
            } else {
                return null; // No active session
            }
        }
    }

    public void clearSession() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL("DELETE FROM UserSession");
        }
    }

    // Helper class
    public static class UserSession {
        public final String username;
        public final String role;

        public UserSession(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }
}