package noor.callListener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by asif on 25-Feb-18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //private static final SQLiteDatabase db;

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDatabases";

    private static final String TABLE_NUMBERS = "numbers";
    private static final String TABLE_LOCATIONS = "locationss";

    private static final String KEY_ID = "id";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_NAME = "name";

    private static final String KEY_TIME = "time";
    private static final String KEY_LATTITUDE = "lattitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final String CREATE_TABLE_NUMBERS = "CREATE TABLE "
            + TABLE_NUMBERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NUMBER + " TEXT,"
            + KEY_NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "
            + TABLE_LOCATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TIME + " TEXT,"
            + KEY_LATTITUDE + " TEXT,"
            + KEY_LONGITUDE + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Log.e("DbHelper","In constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Log.e("DbHelper","In onCreate");
        sqLiteDatabase.execSQL(CREATE_TABLE_NUMBERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATIONS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NUMBERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
    }

    public long InsertNumber(Numbers numbers) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, numbers.getId());
        values.put(KEY_NUMBER, numbers.getNumber());
        values.put(KEY_NAME,numbers.getName());

        long id = db.insert(TABLE_NUMBERS, null, values);
        Log.e("DbHelper","inserted "+numbers.getNumber() );
        db.close();
        return id;
    }

    public long CreateLocations(Locations locations) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, locations.getId());
        values.put(KEY_TIME, locations.getTime());
        values.put(KEY_LATTITUDE, locations.getLattitude());
        values.put(KEY_LONGITUDE, locations.getLongitude());

        long id = db.insert(TABLE_LOCATIONS, null, values);
        db.close();
        return id;
    }

    public String[] GetNumbers() {
        String[] numbers = new String[3];

        String selectQuery = "SELECT  number FROM " + TABLE_NUMBERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        int i = 0;
        if (c.moveToFirst()) {
            do {
                numbers[i++] = c.getString((c.getColumnIndex(KEY_NUMBER)));
                //Log.e("SQLite GetNumbers",i+" "+c.getColumnIndex(KEY_NUMBER));
            } while (c.moveToNext());
        }
        db.close();
        return numbers;
    }

    public String[] GetNames() {
        String[] names = new String[3];

        String selectQuery = "SELECT  name FROM " + TABLE_NUMBERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        int i = 0;
        if (c.moveToFirst()) {
            do {
                names[i++] = c.getString((c.getColumnIndex(KEY_NAME)));
                //Log.e("SQLite GetNumbers",i+" "+c.getColumnIndex(KEY_NAME));
            } while (c.moveToNext());
        }
        db.close();
        return names;
    }

    public Locations GetLocations() {

        Locations locations = new Locations();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                locations.setId(1);
                locations.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
                locations.setLattitude(c.getString(c.getColumnIndex(KEY_LATTITUDE)));
                locations.setLongitude(c.getString(c.getColumnIndex(KEY_LONGITUDE)));
            } while (c.moveToNext());
        }
        db.close();
        return locations;
    }


    public void UpdateNumbers(int id, String num, String name){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_NUMBER, num);
        values.put(KEY_NAME,name);
        db.update(TABLE_NUMBERS,values,KEY_ID+"="+id,null);
        db.close();
        //Log.e("SQLite Update"," updated  "+id+" "+num+" "+name);
    }

    public void UpdateLocations(Locations locations){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, locations.getId());
        values.put(KEY_TIME,locations.getTime());
        values.put(KEY_LONGITUDE,locations.getLongitude());
        values.put(KEY_LATTITUDE,locations.getLattitude());

        db.update(TABLE_LOCATIONS,values,KEY_ID+"=1",null);
        db.close();
        //Log.e("Update Locations","Inserted");

    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public boolean isEmpty(){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TABLE_NUMBERS);
        database.close();
        if (NoOfRows == 0){
            return true;
        }else {
            return false;
        }
    }

    public boolean isEmptyLocations(){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TABLE_LOCATIONS);
        database.close();
        if (NoOfRows == 0){
            return true;
        }else {
            return false;
        }
    }

}
