package db;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import com.bagum.autochecking.R;

import java.util.Date;

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
        EditText e  = (EditText)view.findViewById(R.id.date);
        if (e != null) {
            Date d = new Date();
            Log.d(TAG, "Date = " + cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.DATE)));
            d.setTime(cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.DATE)));
            e.setText(DateFormat.format("dd.MM.yyyy", d));
        }

    }

}
