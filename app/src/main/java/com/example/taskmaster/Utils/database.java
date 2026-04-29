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
    private static final String dbname="todoDatabase";
    private  static Context context;
    public database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        if (context != null) {
            database.context = context.getApplicationContext();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q=("create table Student(ID integer primary key autoincrement,name text,Username text,password text )");
        sqLiteDatabase.execSQL(q);
    }
    public static Context getAppContext(){
        return database.context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Student");
        onCreate(db);

    }
    public  Boolean InsertData(String name,String Username,String password){
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("username",Username);
        contentValues.put("password",password);

        SQLiteDatabase database=this.getWritableDatabase();
        Long r=database.insert("Student",null,contentValues);
        if(r==-1){
            return false;
        }
        else {
        return true;
        }

    }
    public  Cursor getInfo(){
        SQLiteDatabase sqLiteDatabase =this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from Student",null,null);
        return cursor;
    }

    public boolean deleteData(String Taskno){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from Student where name=?",new String[]{Taskno});
        if(cursor.getCount()>0)
        {
            long r=sqLiteDatabase.delete("Student","name=?",new String[]{Taskno});
            if(r==-1){
                return false;

            }
            else {
                return true;
            }
        }
        else {
            return  false;
        }


    }
public  boolean updateData(String Taskno,String Task,String TaskTime){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
    contentValues.put("name",Taskno);
    contentValues.put("Username",Task);
    contentValues.put("password",TaskTime);
        Cursor cursor=sqLiteDatabase.rawQuery("select * from Student where name=?",new String[]{Taskno},null);
        if(cursor.getCount()>0){
        long r=sqLiteDatabase.update("Student",contentValues,"name=?",new String[]{Taskno});
        if(r==-1){
            return false;
        }
        else {
            return true;
        }

    }
    else {return false;}

}
public List<TasksData> getData(){
        List<TasksData> data=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("Select * from Student",null,null);
        while(cursor.moveToNext()){
            TasksData studen=new TasksData();
            studen.taskno=cursor.getString(1);
            studen.task=cursor.getString(2);
            studen.time=cursor.getString(3);
            data.add(studen);
        }
        return data;
}
}

