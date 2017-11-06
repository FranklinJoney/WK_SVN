package com.ust.servicedesk.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by c60678 on 11/3/2017.
 */

public class OfflineSqliteHandler extends SQLiteOpenHelper {

        //Constants for Database name, table name, and column names
        public static final String DB_NAME = "WKDB";
        public static final String TABLE_NAME = "incidents";
        public static final String COLUMN_ID = "incidents_Id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_STATUS = "status";

        //database version
        private static final int DB_VERSION = 1;

        //Constructor
        public OfflineSqliteHandler(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        //creating the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + TABLE_NAME
                    + "(" + COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                    " VARCHAR, " + COLUMN_STATUS +
                    " TINYINT);";
            db.execSQL(sql);
        }

        //upgrading the database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sql = "DROP TABLE IF EXISTS Persons";
            db.execSQL(sql);
            onCreate(db);
        }

        public boolean addName(String name, int status) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_NAME, name);
            contentValues.put(COLUMN_STATUS, status);


            db.insert(TABLE_NAME, null, contentValues);
            db.close();
            return true;
        }

        /*
        * This method taking two arguments
        * first one is the id of the name for which
        * we have to update the sync status
        * and the second one is the status that will be changed
        * */
        public boolean updateNameStatus(int id, int status) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_STATUS, status);
            db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
            db.close();
            return true;
        }

        /*
        * this method will give us all the name stored in sqlite
        * */
        public Cursor getNames() {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
            Cursor c = db.rawQuery(sql, null);
            return c;
        }


         // This method is for getting all the unsynced data

        public Cursor getUnsyncedNames() {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
            Cursor c = db.rawQuery(sql, null);
            return c;
        }
}
