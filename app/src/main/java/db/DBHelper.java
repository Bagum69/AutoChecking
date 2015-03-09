package db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
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
import db.DBTheme.Fueling;
import db.DBTheme.Fueling.FuelColumns;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "autocheck.db";
    private static final int DATABASE_VERSION = 24;
    private static final String DEBUG_TAG = DBHelper.class.getSimpleName();
    private static final boolean LOGV = true;

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

        db.execSQL("CREATE TABLE " + Autos.TABLE_CONT + " (" + BaseColumns._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                        + Autos.AutosColumns.MARK + " TEXT NOT NULL, "
                        + Autos.AutosColumns.MODEL + " TEXT NOT NULL );"
        );

        db.execSQL("INSERT INTO " + Autos.TABLE_CONT + "("+Autos.AutosColumns.MARK+", "+Autos.AutosColumns.MODEL+") "+ "VALUES ('Toyota', 'LandCruiser');");
        db.execSQL("INSERT INTO " + Autos.TABLE_CONT + "("+Autos.AutosColumns.MARK+", "+Autos.AutosColumns.MODEL+") "+ "VALUES ('Toyota', 'FunCargo');");


        db.execSQL("CREATE TABLE " + Fueling.TABLE_CONT + " (" + BaseColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
                + FuelColumns.ID_AUTO+ " INTEGER NOT NULL, "
                + FuelColumns.DATE+ " INTEGER NOT NULL, "
                + FuelColumns.ODO + " INTEGER NOT NULL, "
                + FuelColumns.TRIP + " INTEGER NOT NULL, "
                + FuelColumns.SUMMA+ " REAL NOT NULL, "
                + FuelColumns.LITR+ " REAL NOT NULL "
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
                    db.insert(DBTheme.Fueling.TABLE_CONT, null, values); //write dto db
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
                    if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("litr"))) {
                        eventType = xml.next();
                        values.put("litr", xml.getText());
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
            Log.e("Test", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("Test", e.getMessage(), e);

        } finally {
            // Close the xml file
            xml.close();
        }

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

