package com.bagum.autochecking;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.DBTheme;


public class FuelEditorActivity extends ActionBarActivity {
    private static final String TAG = FuelEditorActivity.class.getSimpleName();

    protected EditText mOdo;
    protected EditText mTrip;
    EditText mSumma;
    EditText mPrice;
    EditText mLitres;
    Long prevOdo;
    Long prevTrip;
    Long id_auto;
    Long fid;
    Long fdate;
    Float fsumma;
    Float fprice;
    Float flitres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_editor);
        EditText d = (EditText) findViewById(R.id.feDate);
        //d.setOnFocusChangeListener(new DatePickerClick(this, d));
        d.setOnClickListener(new DatePickerClick(this, d));

        fid = getIntent().getLongExtra("_id", -1);
        id_auto = getIntent().getLongExtra("_id_auto", 1);


        setupOdoTrip();

        setupSummaPrice();

        Button btn = (Button) findViewById(R.id.fe_btn_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBTheme.Fueling fueling = new DBTheme.Fueling();
                fueling.setId(fid);
                fueling.setId_auto(id_auto);
                fueling.setDate(((EditText) findViewById(R.id.feDate)).getText().toString());
                fueling.setOdo(stringToLong(mOdo.getText().toString()));
                fueling.setTrip(stringToLong(mTrip.getText().toString()));
                fueling.setSumma(stringToFloat(mSumma.getText().toString()));
                fueling.setLitres(stringToFloat(mLitres.getText().toString()));
                if (fid==-1) {
                    MainActivity.dbCont.addFuel(fueling);
                    MainActivity.dbCont.changeCursorFuel(MainActivity.adapterfuel, MainActivity.spinnerAutos.getSelectedItemId());
                }
                else {
                    MainActivity.dbCont.updateFuel(fueling);
                    MainActivity.dbCont.changeCursorFuel(MainActivity.adapterfuel, MainActivity.spinnerAutos.getSelectedItemId());
                }
                finish();
            }
        });
    }

    Float stringToFloat(String s) {
        try {
            return Float.valueOf(s);
        }
        catch (Exception e) {
            Log.d(TAG, "String to float error: "+ e.getMessage());
            return Float.valueOf(0);
        }
    }

    Long stringToLong(String s) {
        try {
            return Long.valueOf(s);
        }
        catch (Exception e) {
            return Long.valueOf(0);
        }
    }

    private void setupSummaPrice() {
        mSumma = (EditText) findViewById(R.id.feSumma);
        mPrice = (EditText) findViewById(R.id.fePrice);
        mLitres = (EditText) findViewById(R.id.feLitres);
        if (fid > 0) {
            fsumma = getIntent().getFloatExtra("_summa", 0);
            flitres = getIntent().getFloatExtra("_litres", 0);
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            mSumma.setText(formatter.format(fsumma));
            if (flitres>0) {
                mPrice.setText(formatter.format(fsumma / flitres));
            }
            mLitres.setText(formatter.format(flitres));
        }

        TextWatcher tw = (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (mSumma.getText().hashCode() == s.hashCode()){
                    if (mSumma.isFocused()) {
                        Float curSumma = stringToFloat(mSumma.getText().toString());
                        Float curPrice = stringToFloat(mPrice.getText().toString());
                        if (curPrice > 0) {
                            Float curLitres = curSumma/curPrice;
                            //mLitres.setText(String.format("%.2f", curLitres));
                            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                            mLitres.setText(formatter.format(curLitres));

                        }
                    }
                } else
                if (mPrice.getText().hashCode() == s.hashCode()) {
                    if (mPrice.isFocused()) {
                        Float curSumma = stringToFloat(mSumma.getText().toString());
                        Float curPrice = stringToFloat(mPrice.getText().toString());
                        if (curPrice > 0) {
                            Float curLitres = curSumma/curPrice;
                            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                            mLitres.setText(formatter.format(curLitres));
                        }
                    }
                }
            }
        });

        mPrice.addTextChangedListener(tw);
        mSumma.addTextChangedListener(tw);
    }

    private void setupOdoTrip() {
        prevOdo = getIntent().getLongExtra("_prevOdo", 0);
        prevTrip = getIntent().getLongExtra("_prevTrip", 0);

        mOdo = (EditText) findViewById(R.id.feOdo);
        mTrip = (EditText) findViewById(R.id.feTrip);
        if (fid > 0) {
            mOdo.setText(prevOdo.toString());
            mTrip.setText(prevTrip.toString());
        }


        TextWatcher tw = (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (mOdo.getText().hashCode() == s.hashCode()){
                    if (mOdo.isFocused()) {
                        String ss = mOdo.getText().toString();
                        if (ss != "") {
                            Long curTrip = Long.valueOf(ss) - prevOdo;
                            if (curTrip > 0) mTrip.setText(curTrip.toString());
                        }
                    }
                } else
                if (mTrip.getText().hashCode() == s.hashCode()) {
                    if (mTrip.isFocused()) {
                        String ss = mTrip.getText().toString();
                        if (ss != "") {
                            Long curOdo = prevOdo + Long.valueOf(ss);
                            if (curOdo > 0) mOdo.setText(curOdo.toString());
                        }
                    }
                }
            }
        });

        mOdo.addTextChangedListener(tw);
        mTrip.addTextChangedListener(tw);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
