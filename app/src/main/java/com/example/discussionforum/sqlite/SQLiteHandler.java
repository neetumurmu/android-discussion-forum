package com.example.discussionforum.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by neetu on 14/8/17.
 */

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();  //Logcat Tag
    private static final int DATABASE_VERSION = 1;           // Database Version
    private static final String DATABASE_NAME = "db_name";    // Database Name
    private static final String TABLE_USER = "table_name";      // Login table name

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_PERMISSION = "permission";
    private static final String KEY_ANON_NAME = "anon_name";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create Database table
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_PERMISSION + " TEXT ," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_ANON_NAME + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if exists and create again
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Storing user details in database
    public void addUser(String name, String email, String permission , String anon_name , String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PERMISSION, permission);
        values.put(KEY_ANON_NAME , anon_name);
        values.put(KEY_CREATED_AT, created_at);

        // Insert Row and Close database connection
        long id = db.insert(TABLE_USER, null, values);
        db.close();

    }

    public void updateUser(String anon_name ,String email){

        SQLiteDatabase db = this.getWritableDatabase();
        String UPDATE_ANON_NAME = "UPDATE table_name SET anon_name ='"+ anon_name +"' WHERE email = '"+ email +"'";
        db.execSQL(UPDATE_ANON_NAME);
    }

    // Retrieving user data from database
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("permission" , cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("anon_name", cursor.getString(3));
        }
        cursor.close();
        db.close();

        return user;
    }

    // Delete all tables and create them again
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);  //Delete all rows
        db.close();
    }
}
