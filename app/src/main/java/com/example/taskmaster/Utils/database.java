package com.example.taskmaster.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.taskmaster.Model.TasksData;

import java.util.ArrayList;
import java.util.List;

public class database extends SQLiteOpenHelper {

    private static final String DB_NAME = "todoDatabase";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "TaskTable";

    private static Context context;

    public database(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        if (context != null) {
            database.context = context.getApplicationContext();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String q = "CREATE TABLE " + TABLE_NAME + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Taskno TEXT, " +
                "tasktitle TEXT, " +
                "TaskTime TEXT, " +
                "isComplete INTEGER DEFAULT 0" +
                ")";

        db.execSQL(q);
    }

    public static Context getAppContext() {
        return database.context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // INSERT DATA
    public boolean insertData(String taskNo, String taskTitle, String taskTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Taskno", taskNo);
        values.put("tasktitle", taskTitle);
        values.put("TaskTime", taskTime);
        values.put("isComplete", 0);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }

    public ArrayList<TasksData> getAllCompletedTasks() {

        ArrayList<TasksData> tasks = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM TaskTable WHERE isComplete = 1",
                null
        );

        while (cursor.moveToNext()) {

            TasksData task = new TasksData();

            task.id = cursor.getInt(0);          // ID
            task.taskno = cursor.getString(1);   // Taskno
            task.task = cursor.getString(2);     // tasktitle
            task.time = cursor.getString(3);     // TaskTime
            task.isComplete = cursor.getInt(4);  // isComplete

            tasks.add(task);
        }

        cursor.close();

        return tasks;
    }
    // GET ALL DATA CURSOR
    public Cursor getInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // DELETE USING ID
    public boolean deleteData(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                "ID = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // UPDATE USING ID
    public boolean updateData(int id,
                              String taskNo,
                              String task,
                              String taskTime,
                              int isComplete) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Taskno", taskNo);
        values.put("tasktitle", task);
        values.put("TaskTime", taskTime);
        values.put("isComplete", isComplete);

        int result = db.update(
                TABLE_NAME,
                values,
                "ID = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // MARK COMPLETE USING ID
    public boolean setComplete(int id, int isComplete) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("isComplete", isComplete);

        int result = db.update(
                TABLE_NAME,
                values,
                "ID = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // GET STATUS USING ID
    public int getStatus(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT isComplete FROM " + TABLE_NAME + " WHERE ID = ?",
                new String[]{String.valueOf(id)}
        );

        int status = 0;

        if (cursor.moveToFirst()) {
            status = cursor.getInt(0);
        }

        cursor.close();

        return status;
    }

    // GET LIST DATA
    public List<TasksData> getData() {

        List<TasksData> data = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME,
                null
        );

        while (cursor.moveToNext()) {

            TasksData task = new TasksData();

            task.id = cursor.getInt(0);       // ID
            task.taskno = cursor.getString(1);
            task.task = cursor.getString(2);
            task.time = cursor.getString(3);
            task.isComplete = cursor.getInt(4);

            data.add(task);
        }

        cursor.close();

        return data;
    }
}