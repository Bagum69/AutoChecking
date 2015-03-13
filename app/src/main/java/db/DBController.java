package db;

import db.DBTheme.Autos;
import db.DBTheme.Autos.AutosColumns;
import db.DBTheme.Fueling;
import db.DBTheme.Fueling.FuelColumns;
import db.DBTheme.Photos;
import db.DBTheme.Photos.PhotosColumns;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

import com.bagum.autochecking.R;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */
public class DBController {
    private static final boolean LOGV = true;
    private static final String TAG = DBController.class.getSimpleName();
    private static DBHelper dbhelper = null;
    private static SQLiteDatabase sqliteDB = null;
    private static int maxRowsInNames = -1;

    public DBController(Context context) {
        Log.d(TAG, "MainController constr ");
        dbhelper = new DBHelper(context);
        sqliteDB = dbhelper.getReadableDatabase();
    }

    public static int getMaxRowsInNames() {
        return maxRowsInNames;
    }

    public static SimpleCursorAdapter getAdapter(Context context) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        final String[] from = {AutosColumns._ID, AutosColumns.MARK, AutosColumns.MODEL};
        final int[] to = new int[] { R.id.id, R.id.mark, R.id.model };

        final Cursor c = sqliteDB.query(Autos.TABLE_CONT, null, null, null, null, null,	Autos.DEFAULT_SORT);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, R.layout.row_auto,	c, from, to);

        return adapter;
    }

    public static SimpleCursorAdapter getAdapterSpin2(Context context) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        final String[] from = {"MARK_MODEL"};
        final int[] to = new int[] { android.R.id.text1 };

        //final Cursor c = sqliteDB.query(Autos.TABLE_CONT, null, null, null, null, null,	Autos.DEFAULT_SORT);
        final Cursor c = sqliteDB.rawQuery("SELECT _id, (MARK || ' ' || MODEL) as  MARK_MODEL from " +Autos.TABLE_CONT+ "", null);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, android.R.layout.simple_spinner_item,	c, from, to);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        return adapter;
    }

    public static ArrayList<String> getAutoList() {

        ArrayList<String> list = new ArrayList<String>();
        try {

            final String[] from = {AutosColumns._ID, AutosColumns.MARK, AutosColumns.MODEL};
            final int[] to = new int[] { R.id.id, R.id.mark, R.id.model };

            final Cursor c = sqliteDB.query(Autos.TABLE_CONT, null, null, null, null, null,	Autos.DEFAULT_SORT);
            if (c.moveToFirst()) {
                do {

                    String ID = c.getString(0);
                    String MARK = c.getString(1);
                    String MODEL = c.getString(2);
                    list.add(MARK + " " + MODEL);

                } while (c.moveToNext());
            }

            c.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to select Names.", e);
        }
        return list;

    }

    public static void changeCursor(SimpleCursorAdapter adapter, String newText) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        String selection = AutosColumns.MARK + " like '"+newText+"%' or "+AutosColumns.MODEL+" like '"+newText+"%' ";
        final Cursor c = sqliteDB.query(Autos.TABLE_CONT, null, selection, null, null, null,	Autos.DEFAULT_SORT);
        adapter.changeCursor(c);
    }

    public static DBAdapter getAdapterFuel(Context context) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        final String[] from = { FuelColumns.DATE, FuelColumns.ODO, FuelColumns.TRIP, FuelColumns.SUMMA};
        final int[] to = new int[] { R.id.date, R.id.odo, R.id.trip, R.id.summa };

        final Cursor c = sqliteDB.query(Fueling.TABLE_CONT, null, null, null, null, null,	Fueling.DEFAULT_SORT, "20");
        final DBAdapter adapter = new DBAdapter(context, R.layout.row_fuel,	c, from, to);

        return adapter;
    }

    public static void changeCursorFuel(SimpleCursorAdapter adapter, Long id) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        String selection = FuelColumns.ID_AUTO + " = '"+ id +"' ";
        final Cursor c = sqliteDB.query(Fueling.TABLE_CONT, null, selection, null, null, null,	Fueling.DEFAULT_SORT,  "20");
        adapter.changeCursor(c);
    }

    public static long addFuel(Fueling fueling) {
        ContentValues values = new ContentValues();
        values.put(FuelColumns.ID_AUTO, fueling.getId_auto());
        values.put(FuelColumns.DATE,    fueling.getDate());
        values.put(FuelColumns.ODO,     fueling.getOdo());
        values.put(FuelColumns.TRIP,    fueling.getTrip());
        values.put(FuelColumns.SUMMA,   fueling.getSumma());
        values.put(FuelColumns.LITR,    fueling.getLitres());
        return sqliteDB.insert(Fueling.TABLE_CONT, null, values);
        /*
        String quer = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                 Fueling.TABLE_CONT,// таблица
                FuelColumns.ID_AUTO, FuelColumns.DATE, FuelColumns.ODO, FuelColumns.TRIP,FuelColumns.SUMMA,FuelColumns.LITR,// колонки
                fueling.getId_auto(), fueling.getDate(), fueling.getOdo(), fueling.getTrip(), fueling.getSumma(), fueling.getLitres()// поля
         );
        sqliteDB.execSQL(quer);
        */
    }

    public static void updateFuel(Fueling fueling) {
        String quer = String.format("update %s set %s='%s', %s='%s',"+
                        " %s='%s', %s='%s', %s='%s', %s='%s'"+
                        " where %s='%s'",
                // таблица
                Fueling.TABLE_CONT,
                // колонки
                FuelColumns.ID_AUTO, fueling.getId_auto(),
                FuelColumns.DATE, fueling.getDate(),
                FuelColumns.ODO, fueling.getOdo(),
                FuelColumns.TRIP,fueling.getTrip(),
                FuelColumns.SUMMA,fueling.getSumma(),
                FuelColumns.LITR, fueling.getLitres(),
                BaseColumns._ID, fueling.getId()

        );
        sqliteDB.execSQL(quer);
    }

    public static void addPhoto(Long id_event, String name) {
        String quer = String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s');",
                Photos.TABLE_NAME,  PhotosColumns.ID_F, PhotosColumns.NAME, id_event, name
        );
        sqliteDB.execSQL(quer);
    }

    public static void delAllPhotos(Long id_event) {
        String quer = String.format("DELETE FROM %s WHERE %s='%s'",
                Photos.TABLE_NAME,  PhotosColumns.ID_F, id_event
        );
        sqliteDB.execSQL(quer);
    }

    public static ArrayList<String> getPhotosList(Long idEvent) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            final Cursor c = sqliteDB.query(Photos.TABLE_NAME, null, " "+PhotosColumns.ID_F+"='"+idEvent+"' ", null, null, null, Photos.DEFAULT_SORT);
            if (c.moveToFirst()) {
                do {
                    String ID = c.getString(0);
                    String ID_F = c.getString(1);
                    String NAME = c.getString(2);
                    list.add(NAME);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to select Photos.", e);
        }
        return list;
    }

    public static void delPhoto(long id) {
        String quer = String.format("DELETE FROM %s WHERE %s='%s'",
                Photos.TABLE_NAME,  BaseColumns._ID, id
        );
        sqliteDB.execSQL(quer);
    }

    public static void integrityCheck() {
        try {
            final Cursor c = sqliteDB.query(Photos.TABLE_NAME, null, null, null, null, null, Photos.DEFAULT_SORT);
            if (c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    String NAME = c.getString(2);

                    try {
                        File f = new File(NAME);
                        if(!f.isFile()) {
                            delPhoto(id);
                        }
                    }
                    catch (Exception ee) {
                        Log.d(TAG, "", ee);
                    }


                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to select Photos.", e);
        }
    }

}
