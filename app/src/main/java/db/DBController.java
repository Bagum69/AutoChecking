package db;

import db.DBTheme.Autos;
import db.DBTheme.Autos.AutosColumns;
import db.DBTheme.Operation;
import db.DBTheme.Operation.OperationColumns;
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
import java.util.Calendar;
import java.util.Date;


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

        final Cursor c = sqliteDB.query(Autos.TABLE, null, null, null, null, null,	Autos.DEFAULT_SORT);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, R.layout.row_auto,	c, from, to);

        return adapter;
    }

    public static SimpleCursorAdapter getAdapterSpin2(Context context) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        final String[] from = {"MARK_MODEL"};
        final int[] to = new int[] { android.R.id.text1 };

        //final Cursor c = sqliteDB.query(Autos.TABLE, null, null, null, null, null,	Autos.DEFAULT_SORT);
        final Cursor c = sqliteDB.rawQuery("SELECT _id, (MARK || ' ' || MODEL) as  MARK_MODEL from " +Autos.TABLE+ "", null);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, android.R.layout.simple_spinner_item,	c, from, to);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        return adapter;
    }

    public static ArrayList<String> getAutoList() {

        ArrayList<String> list = new ArrayList<String>();
        try {

            final String[] from = {AutosColumns._ID, AutosColumns.MARK, AutosColumns.MODEL};
            final int[] to = new int[] { R.id.id, R.id.mark, R.id.model };

            final Cursor c = sqliteDB.query(Autos.TABLE, null, null, null, null, null,	Autos.DEFAULT_SORT);
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
        final Cursor c = sqliteDB.query(Autos.TABLE, null, selection, null, null, null,	Autos.DEFAULT_SORT);
        adapter.changeCursor(c);
    }

    public static DBAdapter getAdapterOperation(Context context) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        final String[] from = { OperationColumns.DATE, OperationColumns.ODO, OperationColumns.TRIP, OperationColumns.SUMMA};
        final int[] to = new int[] { R.id.date, R.id.odo, R.id.trip, R.id.summa };

        final Cursor c = sqliteDB.query(Operation.TABLE, null, null, null, null, null,	Operation.DEFAULT_SORT, "20");
        final DBAdapter adapter = new DBAdapter(context, R.layout.row_fuel,	c, from, to);

        return adapter;
    }

    public static void changeCursorOperation(SimpleCursorAdapter adapter, Long id) {
        //DBHelper dbhelper = new DBHelper(context);
        //SQLiteDatabase sqliteDB = dbhelper.getReadableDatabase();
        String selection = OperationColumns.ID_AUTO + " = '"+ id +"' ";
        final Cursor c = sqliteDB.query(Operation.TABLE, null, selection, null, null, null,	Operation.DEFAULT_SORT,  "20");
        adapter.changeCursor(c);
    }

    public static long addOperation(Operation Operation) {
        ContentValues values = new ContentValues();
        values.put(OperationColumns.ID_AUTO, Operation.getId_auto());
        values.put(OperationColumns.DATE,    Operation.getDate());
        values.put(OperationColumns.ODO,     Operation.getOdo());
        values.put(OperationColumns.TRIP,    Operation.getTrip());
        values.put(OperationColumns.SUMMA,   Operation.getSumma());
        values.put(OperationColumns.QTY,    Operation.getQty());
        values.put(OperationColumns.PRICE,    Operation.getPrice());
        values.put(OperationColumns.TYPE,    Operation.getType());
        values.put(OperationColumns.STATE,    Operation.getState());
        return sqliteDB.insert(Operation.TABLE, null, values);
        /*
        String quer = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                 Operation.TABLE,// таблица
                OperationColumns.ID_AUTO, OperationColumns.DATE, OperationColumns.ODO, OperationColumns.TRIP,OperationColumns.SUMMA,OperationColumns.LITR,// колонки
                Operation.getId_auto(), Operation.getDate(), Operation.getOdo(), Operation.getTrip(), Operation.getSumma(), Operation.getLitres()// поля
         );
        sqliteDB.execSQL(quer);
        */
    }

    public static void updateOperation(Operation Operation) {
        String quer = String.format("update %s set %s='%s', %s='%s',"+
                        " %s='%s', %s='%s', %s='%s', %s='%s'"+
                        " where %s='%s'",
                // таблица
                Operation.TABLE,
                // колонки
                OperationColumns.ID_AUTO, Operation.getId_auto(),
                OperationColumns.DATE, Operation.getDate(),
                OperationColumns.ODO, Operation.getOdo(),
                OperationColumns.TRIP,Operation.getTrip(),
                OperationColumns.SUMMA,Operation.getSumma(),
                OperationColumns.QTY, Operation.getQty(),
                OperationColumns.PRICE, Operation.getPrice(),
                OperationColumns.STATE, Operation.getState(),
                OperationColumns.TYPE, Operation.getType(),
                BaseColumns._ID, Operation.getId()

        );
        sqliteDB.execSQL(quer);
    }
    public static void deleteOperation(long id) {
        String quer = String.format("delete from %s"+
                        " where %s='%s'",
                // таблица
                Operation.TABLE,
                // колонки
                BaseColumns._ID, id
        );
        sqliteDB.execSQL(quer);
    }


    public static void addPhoto(Long id_event, String name) {
        String quer = String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s');",
                Photos.TABLE,  PhotosColumns.ID_F, PhotosColumns.NAME, id_event, name
        );
        sqliteDB.execSQL(quer);
    }

    public static void delAllPhotos(Long id_event) {
        String quer = String.format("DELETE FROM %s WHERE %s='%s'",
                Photos.TABLE,  PhotosColumns.ID_F, id_event
        );
        sqliteDB.execSQL(quer);
    }

    public static ArrayList<String> getPhotosList(Long idEvent) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            final Cursor c = sqliteDB.query(Photos.TABLE, null, " "+PhotosColumns.ID_F+"='"+idEvent+"' ", null, null, null, Photos.DEFAULT_SORT);
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
                Photos.TABLE,  BaseColumns._ID, id
        );
        sqliteDB.execSQL(quer);
    }

    public static void integrityCheck() {
        try {
            final Cursor c = sqliteDB.query(Photos.TABLE, null, null, null, null, null, Photos.DEFAULT_SORT);
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

    public Stat getStat(long id_auto) {
        Stat stat = new Stat();

        final Calendar end = Calendar.getInstance();
        int year = end.get(Calendar.YEAR);
        int month = end.get(Calendar.MONTH);
        int day = end.get(Calendar.DAY_OF_MONTH);


        try {
            final Cursor c = sqliteDB.query(Operation.TABLE, new String[]{
                            OperationColumns.DATE,
                            OperationColumns.QTYTRIP,
                            OperationColumns.PQTY,
                            OperationColumns.TRIP},
                    OperationColumns.ID_AUTO + " = '" + id_auto + "'",
                    null, null, null, Operation.DEFAULT_SORT, "20");
            if (c.moveToFirst()) {
                stat.setLastDate(c.getLong(0));
                stat.setLastRate(c.getFloat(1));
                stat.setLastQty(c.getFloat(2));
                stat.setLastTrip(c.getFloat(3));
            };
            c.close();

        }catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        Calendar start = Calendar.getInstance();
        start.set(year, month, 1);
        Date ds = new Date(start.getTimeInMillis());
        Date de = new Date(end.getTimeInMillis());

        try {
            String  ss = "SELECT "
                    + " SUM (" + OperationColumns.PQTY + ") as SUM_PQTY, "
                    + " SUM (" + OperationColumns.TRIP + ") as SUM_TRIP "
                    + " FROM " + Operation.TABLE
                    + " WHERE "
                    + OperationColumns.ID_AUTO + " = '" + id_auto + "' AND DATE BETWEEN '" + ds.getTime() + "' and '" + de.getTime() + "' "
                    + " ORDER BY " +Operation.DEFAULT_SORT;
            final Cursor c = sqliteDB.rawQuery(ss, null);
            c.moveToFirst();
            stat.setMonthQty(c.getFloat(0));
            stat.setMonthTrip(c.getFloat(1));

            c.close();
            stat.setMonthRate(stat.getMonthQty()/stat.getMonthTrip()*100);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }


        start.roll(Calendar.MONTH, false);
        end.roll(Calendar.MONTH, false);
        end.set(Calendar.DATE, end.getActualMaximum(Calendar.DATE));

        ds = new Date(start.getTimeInMillis());
        de = new Date(end.getTimeInMillis());

        try {
            String  ss = "SELECT "
                    + " SUM (" + OperationColumns.PQTY + ") as SUM_PQTY, "
                    + " SUM (" + OperationColumns.TRIP + ") as SUM_TRIP "
                    + " FROM " + Operation.TABLE
                    + " WHERE "
                    + OperationColumns.ID_AUTO + " = '" + id_auto + "' AND DATE BETWEEN '" + ds.getTime() + "' and '" + de.getTime() + "' "
                    + " ORDER BY " +Operation.DEFAULT_SORT;
            final Cursor c = sqliteDB.rawQuery(ss, null);
            c.moveToFirst();
            stat.setPMonthQty(c.getFloat(0));
            stat.setPMonthTrip(c.getFloat(1));

            c.close();
            stat.setPMonthRate(stat.getPMonthQty()/stat.getPMonthTrip()*100);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }


        return stat;
    }

}
