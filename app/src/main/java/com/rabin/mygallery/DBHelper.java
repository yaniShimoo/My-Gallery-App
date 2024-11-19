package com.rabin.mygallery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;

    public DBHelper (@Nullable Context context,@Nullable String name,@Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate (SQLiteDatabase db){
        db.execSQL("create table Paintings ("
                + "idP integer primary key autoincrement,"
                + "nameP text,"
                + "widthP integer,"
                + "heightP integer,"
                + "dateP text,"
                + "descriptionP text,"
                + "paintingPic text,"
                + "uriP text,"
                + "costP integer"
                + ");");

        db.execSQL("create table Customers ("
                + "idC integer primary key autoincrement,"
                + "nameC text,"
                + "phoneC text,"
                + "adressC text"
                + ");");

        db.execSQL("create table Orders ("
                + "idOrd integer primary key autoincrement,"
                + "nameCusOrd text,"
                + "namePaintOrd text,"
                + "phoneOrd text,"
                + "dateOrd text,"
                + "timeOrd text,"
                + "adressOrd text,"
                + "priceOrd text,"
                + "complete text,"
                + "shipping text"
                + ");");

        db.execSQL("create table Messages ("
                + "idM integer primary key autoincrement,"
                + "textM text,"
                + "dateM text,"
                + "phoneM text"
                + ");");

        db.execSQL("create table Sketches ("
                + "idS integer primary key autoincrement,"
                + "nameS text,"
                + "dateS text,"
                + "descriptionS text,"
                + "sketchPic text"
                + ");");

    }

    @Override
    public void onUpgrade ( SQLiteDatabase db , int oldVersion , int newVersion) {

        if (oldVersion < 2){
            db.execSQL("ALTER TABLE " + "Orders" +
                    " RENAME COLUMN " + "emailOrd" +
                    " TO " + "phoneOrd");
        }
        if (oldVersion < 3){
            db.execSQL("ALTER TABLE Paintings" +
                    " ADD widthP integer;");
            db.execSQL("ALTER TABLE Paintings" +
                    " ADD heightP integer;");
        }
        if (oldVersion < 4){
            db.execSQL("ALTER TABLE Sketches" +
                    " ADD sketchPic text;");
            db.execSQL("ALTER TABLE Paintings" +
                    " ADD uriP text;");
        }
    }

}
