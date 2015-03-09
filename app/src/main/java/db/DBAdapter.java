package db;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bagum.autochecking.R;

import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.logging.SimpleFormatter;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */
public class DBAdapter extends SimpleCursorAdapter {
    private static final String TAG = DBAdapter.class.getSimpleName();

    public DBAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView e  = (TextView)view.findViewById(R.id.date);
        if (e != null) {
            Date d = new Date();
            //Log.d(TAG, "Date = " + cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.DATE)));
            d.setTime(cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.DATE)));
            e.setText(DateFormat.format("dd.MM.yyyy", d));
        }

        TextView odo  = (TextView)view.findViewById(R.id.odo);
        if (odo != null) {
            odo.setText(String.format("%,d", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.ODO))));
        }

        TextView summa = (TextView)view.findViewById(R.id.summa);
        if (summa != null) {
            summa.setText(String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.SUMMA))));
        }

        TextView litr = (TextView)view.findViewById(R.id.litr);
        if (litr != null) {
            litr.setText(String.format("%.1f", cursor.getFloat(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.LITR))));
        }

        TextView price = (TextView)view.findViewById(R.id.price);
        if (price != null) {
            price.setText(String.format("%.2f",
                    cursor.getFloat(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.SUMMA))/
                            cursor.getFloat(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.LITR))));
        }

    }

}
