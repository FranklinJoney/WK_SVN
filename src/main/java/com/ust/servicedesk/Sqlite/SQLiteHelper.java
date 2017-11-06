package com.ust.servicedesk.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ust.servicedesk.model.SqliteLocationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikunj on 27-08-2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLiteDatabase.db";

    public static final String TABLE_NAME = "LOCATION";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_ZIP = "zip";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_NAME = "name";

    private SQLiteDatabase database;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_STREET + " VARCHAR, " + COLUMN_STATE + " VARCHAR,"
                  +COLUMN_CITY + " VARCHAR, " +COLUMN_ZIP + " VARCHAR, " +COLUMN_COUNTRY + " VARCHAR, " +
                COLUMN_NAME + " VARCHAR" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean create(SqliteLocationModel myObj) {

        boolean createSuccessful = false;

        if(!checkIfExists(myObj.getName())){

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_STREET, myObj.getStreet());
            values.put(COLUMN_STATE, myObj.getState());
            values.put(COLUMN_CITY, myObj.getCity());
            values.put(COLUMN_ZIP, myObj.getZip());
            values.put(COLUMN_COUNTRY, myObj.getCountry());
            values.put(COLUMN_NAME, myObj.getName());
            createSuccessful = db.insert(TABLE_NAME, null, values) > 0;

            db.close();

            if(createSuccessful){
                Log.e("TEsting", myObj.getName() + " created.");
            }
        }

        return createSuccessful;
    }
    public boolean checkIfExists(String objectName){

        boolean recordExists = false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = '" + objectName + "'", null);

        if(cursor!=null) {

            if(cursor.getCount()>0) {
                recordExists = true;
            }
        }

        cursor.close();
        db.close();

        return recordExists;
    }
    public void  addContact(SqliteLocationModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STREET, contact.getStreet());
        values.put(COLUMN_STATE, contact.getState());
        values.put(COLUMN_CITY, contact.getCity());
        values.put(COLUMN_ZIP, contact.getZip());
        values.put(COLUMN_COUNTRY, contact.getCountry());
        values.put(COLUMN_NAME, contact.getName());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    SqliteLocationModel getLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { COLUMN_NAME,
                         }, COLUMN_CITY + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SqliteLocationModel location = new SqliteLocationModel(String.valueOf(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return contact
        return location;
    }

    // Getting All Contacts
/*    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }*/

    // Updating single contact
    public int updateContact(SqliteLocationModel location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, location.getName());


        // updating row
        return db.update(TABLE_NAME, values, COLUMN_CITY + " = ?",
                new String[] { String.valueOf(location.getName()) });
    }

    // Deleting single contact
    public void deleteContact(SqliteLocationModel location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_CITY + " = ?",
                new String[] { String.valueOf(location.getName()) });
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public List<SqliteLocationModel> read(String searchTerm) {

        List<SqliteLocationModel> recordsList = new ArrayList<SqliteLocationModel>();

        // select query
        String sql = "";
        sql += "SELECT * FROM " + TABLE_NAME;
        sql += " WHERE " + COLUMN_NAME + " LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY " + COLUMN_ID + " DESC";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                String objectName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                SqliteLocationModel myObject = new SqliteLocationModel(objectName);

                // add to list
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return the list of records
        return recordsList;
    }
   /* public ArrayList<String> getAllTableName()
    {
        database = this.getReadableDatabase();
        ArrayList<String> allTableNames=new ArrayList<String>();
        Cursor cursor=database.rawQuery("SELECT name FROM" + TABLE_NAME + "WHERE type='table'",null);
        if(cursor.getCount()>0)
        {
            for(int i=0;i<cursor.getCount();i++)
            {
                cursor.moveToNext();
                allTableNames.add(cursor.getString(cursor.getColumnIndex("name")));
            }
        }
        cursor.close();
        database.close();
        return allTableNames;
    }*/

}
