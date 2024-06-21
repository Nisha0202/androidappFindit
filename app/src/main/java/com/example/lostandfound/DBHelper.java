package com.example.lostandfound;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "drafts.db";
        private static final int DATABASE_VERSION = 1;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE drafts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT," +
                    "itemName TEXT," +
                    "desc TEXT," +
                    "date TEXT," +
                    "time TEXT," +
                    "location TEXT," +
                    "email TEXT," +
                    "status TEXT," +
                    "image BLOB)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS drafts");
            onCreate(db);
        }

        public void insertData(String username, String itemName, String desc, String date, String time, String location, String email, String status, byte[] image) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("itemName", itemName);
            values.put("desc", desc);
            values.put("date", date);
            values.put("time", time);
            values.put("location", location);
            values.put("email", email);
            values.put("status", status);
            values.put("image", image);
            db.insert("drafts", null, values);
        }

        // Method to delete all data
        public void deleteAllData() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("drafts", null, null);
        }
    // Method to get the latest draft
    public Cursor getLatestDraft() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("drafts", null, null, null, null, null, "id DESC", "1");
    }

}


