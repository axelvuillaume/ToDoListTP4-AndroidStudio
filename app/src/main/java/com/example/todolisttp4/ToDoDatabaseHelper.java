package com.example.todolisttp4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todolist.db";
    private static final int DATABASE_VERSION = 1;

    public ToDoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE jobs (id INTEGER PRIMARY KEY AUTOINCREMENT, job TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS jobs");
        onCreate(db);
    }

    public void addJob(String job) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("job", job);
        db.insert("jobs", null, values);
        db.close();
    }

    public Cursor getAllJobs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM jobs", null);
    }

    public void deleteJob(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("jobs", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateJob(int id, String newJob) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("job", newJob);
        db.update("jobs", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
