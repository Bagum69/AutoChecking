package db;

import db.DBTheme.Autos;
import db.DBTheme.Autos.AutosColumns;
import db.DBTheme.Fueling;
import db.DBTheme.Fueling.FuelColumns;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

import com.bagum.autochecking.R;

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

    public static void addFuel(Fueling fueling) {
        String quer = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                // таблица
                Fueling.TABLE_CONT,
                // колонки
                FuelColumns.ID_AUTO, FuelColumns.DATE, FuelColumns.ODO, FuelColumns.TRIP,FuelColumns.SUMMA,FuelColumns.LITR,
                // поля
                fueling.getId_auto(), fueling.getDate(), fueling.getOdo(), fueling.getTrip(), fueling.getSumma(), fueling.getLitres()
        );
        sqliteDB.execSQL(quer);
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

}
