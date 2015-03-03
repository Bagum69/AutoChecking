package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Date;

import db.DBTheme.Autos;
import db.DBTheme.Fueling;
import db.DBTheme.Fueling.FuelColumns;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "autocheck.db";
    private static final int DATABASE_VERSION = 3;
    private static final String DEBUG_TAG = DBHelper.class.getSimpleName();
    private static final boolean LOGV = true;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (LOGV) {
            Log.d(DEBUG_TAG, "onCreate()");
        }

        db.execSQL("CREATE TABLE " + Autos.TABLE_CONT + " (" + BaseColumns._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                        + Autos.AutosColumns.MARK + " TEXT NOT NULL, "
                        + Autos.AutosColumns.MODEL + " TEXT NOT NULL );"
        );

        db.execSQL("INSERT INTO " + Autos.TABLE_CONT + "("+Autos.AutosColumns.MARK+", "+Autos.AutosColumns.MODEL+") "+ "VALUES ('Toyota', 'LandCruiser');");
        db.execSQL("INSERT INTO " + Autos.TABLE_CONT + "("+Autos.AutosColumns.MARK+", "+Autos.AutosColumns.MODEL+") "+ "VALUES ('Toyota', 'FunCargo');");


        db.execSQL("CREATE TABLE " + Fueling.TABLE_CONT + " (" + BaseColumns._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                        + FuelColumns.DATE+ " DATE NOT NULL, "
                        + FuelColumns.ODO + " INTEGER NOT NULL );"
        );
        Date d = new Date();
        db.execSQL("INSERT INTO " + Fueling.TABLE_CONT + "("+FuelColumns.DATE+", "+FuelColumns.ODO+") "+ "VALUES ('"+ d.getTime()  +"', '234998');");
        db.execSQL("INSERT INTO " + Fueling.TABLE_CONT + "("+FuelColumns.DATE+", "+FuelColumns.ODO+") "+ "VALUES ('"+ d.getTime()  +"', '235001');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DEBUG_TAG, "onUpgrade called");

        dropTables(db);
        onCreate(db);
    }
    public void dropTables(SQLiteDatabase db) {
        if (LOGV) {
            Log.d(DEBUG_TAG, "onDropTables called");
        }
        db.execSQL("DROP TABLE IF EXISTS " + Autos.TABLE_CONT);
        db.execSQL("DROP TABLE IF EXISTS " + Fueling.TABLE_CONT);
    }

}

