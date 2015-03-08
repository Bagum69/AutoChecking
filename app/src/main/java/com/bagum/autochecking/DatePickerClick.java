package com.bagum.autochecking;

/**
 * Created by tabunshikov.vadim on 06.03.2015.
 */

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.TextView;


public class DatePickerClick implements OnFocusChangeListener, OnDateSetListener, OnClickListener {

    private Context mContext;
    private TextView field;
    private DatePickerDialog tpd;

    public DatePickerClick(Context context, TextView field) {
        mContext = context;
        this.field = field;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            tpd = new DatePickerDialog(mContext, this, year, month, day);
            tpd.show();
        } else {
            tpd.cancel();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String day;
        String month;
        if (dayOfMonth < 10) day = "0" + dayOfMonth;
        else day = "" + dayOfMonth;
        if (monthOfYear + 1 < 10) month = "0" + (monthOfYear + 1);
        else month = "" + (monthOfYear + 1);
        field.setText(day + "." + month + "." + year);
    }

    @Override
    public void onClick(View arg0) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        tpd = new DatePickerDialog(mContext, this, year, month, day);
        tpd.show();
    }
}