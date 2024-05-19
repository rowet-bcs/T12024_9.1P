package com.example.task91p;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteManager extends SQLiteOpenHelper {

    // Custom class to set up SQLite database for persistent user and advert data storage
    private static SQLiteManager db;
    // Set database details
    private static final String DATABASE_NAME = "Adverts";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String TABLE2_NAME = "adverts";

    // Set database fields
    private static final String USER_ID_FIELD = "userId";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String ADVERT_ID_FIELD = "advertId";
    private static final String ADVERT_TYPE = "type";
    private static final String ADVERT_TITLE = "title";
    private static final String ADVERT_DESCRIPTION = "description";
    private static final String ADVERT_DATE = "date";
    private static final String ADVERT_LOCATION = "location";
    private static final String ADVERT_LAT = "lat";
    private static final String ADVERT_LONG = "long";
    private static final String ADVERT_USER_ID = "userId";
    private static final String ADVERT_PHONE = "phone";
    private static final String DELETED_FLAG_FIELD = "deletedFlag";
    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        // Return database instance, creating a new database on first call
        if(db == null){
            db = new SQLiteManager(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create database tables
        StringBuilder createUserTable;
        createUserTable = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(USER_ID_FIELD)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(USERNAME_FIELD)
                .append(" TEXT NOT NULL UNIQUE, ")
                .append(PASSWORD_FIELD)
                .append(" TEXT NOT NULL)");

        db.execSQL(createUserTable.toString());

        StringBuilder createAdvertTable;
        createAdvertTable = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE2_NAME)
                .append(" (")
                .append(ADVERT_ID_FIELD)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ADVERT_TYPE)
                .append(" TEXT, ")
                .append(ADVERT_TITLE)
                .append(" TEXT, ")
                .append(ADVERT_DESCRIPTION)
                .append(" TEXT, ")
                .append(ADVERT_DATE)
                .append(" TEXT, ")
                .append(ADVERT_LOCATION)
                .append(" TEXT, ")
                .append(ADVERT_LAT)
                .append(" TEXT, ")
                .append(ADVERT_LONG)
                .append(" TEXT, ")
                .append(ADVERT_USER_ID)
                .append(" INTEGER, ")
                .append(ADVERT_PHONE)
                .append(" TEXT, ")
                .append(DELETED_FLAG_FIELD)
                .append(" TEXT)");

        db.execSQL(createAdvertTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Standard function to recreate table on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    public void addUserToDatabase(String username, String password){
        // Add new user to users table
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME_FIELD, username);
        contentValues.put(PASSWORD_FIELD, password);

        db.insert(TABLE_NAME, null, contentValues);
    }

    public void addAdvertToDatabase(Advert advert){
        // Add new advert to adverts table
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ADVERT_TYPE, advert.getType());
        contentValues.put(ADVERT_TITLE, advert.getTitle());
        contentValues.put(ADVERT_DESCRIPTION, advert.getDescription());
        contentValues.put(ADVERT_DATE, advert.getDate());
        contentValues.put(ADVERT_LOCATION, advert.getLocation());
        contentValues.put(ADVERT_LAT, advert.getLat());
        contentValues.put(ADVERT_LONG, advert.getLong());
        contentValues.put(ADVERT_USER_ID, advert.getUserId());
        contentValues.put(ADVERT_PHONE, advert.getPhone());
        contentValues.put(DELETED_FLAG_FIELD, advert.getDeleteFlag());

        db.insert(TABLE2_NAME, null, contentValues);
    }

    public void populateAdvertArray(){
        // Populate current advert list from database
        SQLiteDatabase db = this.getReadableDatabase();

        // Empty task list before repopulating
        Advert.advertList.clear();

        try (Cursor result = db.rawQuery("SELECT *  FROM " + TABLE2_NAME + " WHERE " + DELETED_FLAG_FIELD + " = 'N' order by " + ADVERT_DATE + " DESC", null)){
            if(result.getCount() > 0){
                while (result.moveToNext()){
                    int id = result.getInt(0);
                    String type = result.getString(1);
                    String title = result.getString(2);
                    String desc = result.getString(3);
                    String date = result.getString(4);
                    String location = result.getString(5);
                    String latitude = result.getString(6);
                    String longitude = result.getString(7);
                    int userId = result.getInt(8);
                    String phone = result.getString(9);
                    String deletedFlag = result.getString(10);

                    // Create new advert from database values and add to advert list
                    Advert advert = new Advert(id, type, title, desc, date, location, latitude, longitude, userId, phone, deletedFlag);
                    Advert.advertList.add(advert);
                }
            }
        }
    }

    public int getNextAdvertId(){
        // Query database to set the next advert Id number
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor result = db.rawQuery("SELECT * FROM " + TABLE2_NAME, null)){
            return result.getCount();
        }
    }

    public void updateAdvertInDB(Advert advert){
        // Update existing advert in database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ADVERT_TYPE, advert.getType());
        contentValues.put(ADVERT_TITLE, advert.getTitle());
        contentValues.put(ADVERT_DESCRIPTION, advert.getDescription());
        contentValues.put(ADVERT_DATE, advert.getDate());
        contentValues.put(ADVERT_LOCATION, advert.getLocation());
        contentValues.put(ADVERT_LAT, advert.getLat());
        contentValues.put(ADVERT_LONG, advert.getLong());
        contentValues.put(ADVERT_USER_ID, advert.getUserId());
        contentValues.put(ADVERT_PHONE, advert.getPhone());
        contentValues.put(DELETED_FLAG_FIELD, advert.getDeleteFlag());

        db.update(TABLE2_NAME, contentValues, ADVERT_ID_FIELD + " =?", new String[]{String.valueOf(advert.getId())});
    }

    public int validateUser(String username, String password){
        // Check if username/password combination is in the database
        SQLiteDatabase db = this.getReadableDatabase();

        int match = -1;

        try (Cursor result = db.rawQuery("SELECT " + USER_ID_FIELD + " FROM " + TABLE_NAME + " WHERE " + USERNAME_FIELD + " = '" + username + "' AND " + PASSWORD_FIELD + " = '" + password + "'", null)){
            if(result.getCount() > 0){
                while (result.moveToNext()){
                    match = result.getInt(0);
                }
            }
        }

        return match;
    }

    public int checkUsername(String username){
        // Query database to see if username already in use
        SQLiteDatabase db = this.getReadableDatabase();
        int match = -1;

        try (Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_FIELD + " = '" + username + "'", null)){
            if(result.getCount() > 0){
                while (result.moveToNext()){
                    match = result.getInt(0);
                }
            }
        }

        return match;
    }
}
