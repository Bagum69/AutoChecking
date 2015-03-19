package db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;

import com.bagum.autochecking.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import db.DBTheme.Autos;
import db.DBTheme.Operation;
import db.DBTheme.Operation.OperationColumns;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBAdapter.class.getSimpleName();
    private static final String DATABASE_NAME = "a4c.db";
    private static final int DATABASE_VERSION = 5;
    private static final String DEBUG_TAG = DBHelper.class.getSimpleName();
    private static final boolean LOGV = true;
    public static final int OPR_STATE_INCOMPLETE = 0;
    public static final int OPR_STATE_COMPLETE = 5;
    public static final int OPR_TYPE_FUEL = 1;

    private final Context fContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        fContext = context;
    }

    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (LOGV) {
            Log.d(DEBUG_TAG, "onCreate()");
        }

        db.execSQL("CREATE TABLE " + Autos.TABLE + " (" + BaseColumns._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                        + Autos.AutosColumns.MARK + " TEXT NOT NULL, "
                        + Autos.AutosColumns.MODEL + " TEXT NOT NULL );"
        );

        db.execSQL("INSERT INTO " + Autos.TABLE + "("+Autos.AutosColumns.MARK+", "+Autos.AutosColumns.MODEL+") "+ "VALUES ('Toyota', 'LandCruiser');");
        db.execSQL("INSERT INTO " + Autos.TABLE + "("+Autos.AutosColumns.MARK+", "+Autos.AutosColumns.MODEL+") "+ "VALUES ('Toyota', 'FunCargo');");


        db.execSQL("CREATE TABLE " + Operation.TABLE + " (" + BaseColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                + OperationColumns.ID_AUTO+ " INTEGER NOT NULL, "
                + OperationColumns.DATE+ " INTEGER NOT NULL, "
                + OperationColumns.ODO + " REAL NOT NULL, "
                + OperationColumns.TRIP + " REAL NOT NULL, "
                + OperationColumns.SUMMA+ " REAL NOT NULL, "
                + OperationColumns.PRICE+ " REAL NOT NULL, "
                + OperationColumns.QTY+ " REAL NOT NULL, "
                + OperationColumns.TYPE+ " INTEGER NOT NULL, "
                + OperationColumns.STATE+ " INTEGER NOT NULL, "
                + OperationColumns.PQTY+ " REAL, "
                + OperationColumns.QTYTRIP+ " REAL "
                +");" );

        ContentValues values = new ContentValues();
        Resources res = fContext.getResources();
        XmlResourceParser xml = res.getXml(R.xml.fuel);
        Date d = new Date();

        try {
            // Ищем конец документа
            int eventType = xml.getEventType();
            boolean startRec = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Ищем теги record
                //Log.d(TAG, "xml=" + xml.getName());
                if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("record"))) {
                    startRec = true;
                }
                if ((eventType == XmlPullParser.END_TAG) && (xml.getName().equals("record"))) {
                    values.put("id_auto", 2);
                    values.put("type", OPR_TYPE_FUEL);
                    values.put("state", OPR_STATE_COMPLETE);
                    db.insert(DBTheme.Operation.TABLE, null, values); //write dto db
                    startRec = false;
                }

                if (startRec) {
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("date"))) {
                        eventType = xml.next();
                        d = stringToDate(xml.getText(), "dd.MM.yyyy");
                        //String s = DateFormat.format("yyyy-MM-dd HH:mm:ss", d).toString();
                        String s =  "" + d.getTime();
                        values.put("date",s);
                    }
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("odo"))) {
                        eventType = xml.next();
                        values.put("odo", xml.getText());
                    }
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("trip"))) {
                        eventType = xml.next();
                        values.put("trip", xml.getText());
                    }
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("summa"))) {
                        eventType = xml.next();
                        values.put("summa", xml.getText());
                    }
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("qty"))) {
                        eventType = xml.next();
                        values.put("qty", xml.getText());
                    }
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("pr"))) {
                        eventType = xml.next();
                        values.put("pr", xml.getText());
                    }
                    /*
                    String title = xml.getAttributeValue(0);
                    String color = xml.getAttributeValue(1);
                    values.put("title", title);
                    values.put("color", color);
                    */

                }
                eventType = xml.next();
            }
        }
        // Catch errors
        catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);

        } finally {
            // Close the xml file
            xml.close();
        }

        try {
            final Cursor c = db.query(Operation.TABLE, new String[] {
                    BaseColumns._ID,
                    OperationColumns.QTY,
                    OperationColumns.TRIP},
                    null, null, null, null, BaseColumns._ID);
            long pid=-1, id;
            float pqty, qty=0, trip, qtytrip;
            if (c.moveToFirst()) {
                do {
                    id = c.getLong(0);
                    trip = c.getLong(2);
                    if (pid > 0) {
                        pqty = qty;
                        qtytrip = pqty/trip*100;
                        String quer = String.format("UPDATE %s SET %s='%s', %s='%s' WHERE %s='%s'",
                                Operation.TABLE,
                                OperationColumns.PQTY, pqty,
                                OperationColumns.QTYTRIP, qtytrip,
                                BaseColumns._ID, id
                        );
                        db.execSQL(quer);
                    }
                    qty = c.getFloat(1);
                    pid = id;

                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


        db.execSQL("CREATE TABLE " + DBTheme.Photos.TABLE + " (" + BaseColumns._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                        + DBTheme.Photos.PhotosColumns.ID_F + " INTEGER NOT NULL, "
                        + DBTheme.Photos.PhotosColumns.NAME + " TEXT NOT NULL );"
        );

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
        db.execSQL("DROP TABLE IF EXISTS " + Autos.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Operation.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBTheme.Photos.TABLE);
    }

}

