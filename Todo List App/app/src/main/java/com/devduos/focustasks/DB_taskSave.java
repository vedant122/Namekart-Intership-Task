package com.devduos.focustasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB_taskSave extends SQLiteOpenHelper {
    public DB_taskSave(@Nullable Context context) {
        super(context, "taskdetails.db", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table taskdetails(id Text ,task Text,date Text,time Text,reminder Text,isChecked Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists taskdetails");
    }

    public boolean inserttaskdata(String id ,String task,String date,String time,String reminder,String isChecked){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",id);
        contentValues.put("task",task);
        contentValues.put("date",date);
        contentValues.put("time",time);
        contentValues.put("reminder",reminder);
        contentValues.put("isChecked",isChecked);
        long result=DB.insert("taskdetails",null,contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updatetaskdata(String id ,String task,String date,String time,String reminder,String isChecked){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",id);
        contentValues.put("task",task);
        contentValues.put("date",date);
        contentValues.put("time",time);
        contentValues.put("reminder",reminder);
        contentValues.put("isChecked",isChecked);
        Cursor cursor=DB.rawQuery("Select * from taskdetails where id=?",new String[]{id});

        if(cursor.getCount()>0){
            long result=DB.update("taskdetails",contentValues,"id=?",new String[] {id});
            if(result==-1){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    public boolean deletetaskdata(String id){
        SQLiteDatabase DB=this.getWritableDatabase();
        Cursor cursor=DB.rawQuery("Select * from taskdetails where id=?",new String[]{id});

        if(cursor.getCount()>0){
            long result=DB.delete("taskdetails","id=?",new String[] {id});
            if(result==-1){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    public Cursor gettaskdata(){
        SQLiteDatabase DB=this.getWritableDatabase();
        Cursor cursor=DB.rawQuery("Select * from taskdetails",null);
        return cursor;
    }
}
