package db;

import db.DBTheme.Autos;
import db.DBTheme.Autos.AutosColumns;
import db.DBTheme.Fueling;
import db.DBTheme.Fueling.FuelColumns;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import com.bagum.autochecking.R;


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

    public static void changeCursor(Context context, SimpleCursorAdapter adapter, String newText) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        String selection = AutosColumns.MARK + " like '"+newText+"%' or "+AutosColumns.MODEL+" like '"+newText+"%' ";
        final Cursor c = sqliteDB.query(Autos.TABLE_CONT, null, selection, null, null, null,	Autos.DEFAULT_SORT);
        adapter.changeCursor(c);
    }

    public static DBAdapter getAdapterFuel(Context context) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        final String[] from = { FuelColumns.DATE, FuelColumns.ODO};
        final int[] to = new int[] { R.id.date, R.id.odo };

        final Cursor c = sqliteDB.query(Fueling.TABLE_CONT, null, null, null, null, null,	Fueling.DEFAULT_SORT);
        final DBAdapter adapter = new DBAdapter(context, R.layout.row_fuel,	c, from, to);

        return adapter;
    }

    public static void changeCursorFuel(Context context, SimpleCursorAdapter adapter, String newText) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        String selection = FuelColumns.DATE + " = '"+newText+"' ";
        final Cursor c = sqliteDB.query(Fueling.TABLE_CONT, null, selection, null, null, null,	Fueling.DEFAULT_SORT);
        adapter.changeCursor(c);
    }

}
