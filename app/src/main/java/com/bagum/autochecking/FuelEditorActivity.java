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

import db.DBTheme.Fueling;


public class FuelEditorActivity extends ActionBarActivity {
    private static final String TAG = FuelEditorActivity.class.getSimpleName();

    protected EditText mOdo;
    protected EditText mTrip;
    EditText mSumma;
    EditText mPrice;
    EditText mLitres;
    EditText mDate;
    Fueling fe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_editor);
        EditText d = (EditText) findViewById(R.id.feDate);
        //d.setOnFocusChangeListener(new DatePickerClick(this, d));
        d.setOnClickListener(new DatePickerClick(this, d));

        fe = new Fueling();
        fe.setId(getIntent().getLongExtra("_id", -1));
        fe.setId_auto(getIntent().getLongExtra("_id_auto", 1));
        fe.setDate(getIntent().getLongExtra("_date", 0));

        fe.setSumma(getIntent().getFloatExtra("_summa", 0));
        fe.setLitres(getIntent().getFloatExtra("_litres", 0));
        if (fe.getLitres()>0) fe.setPrice(fe.getSumma()/fe.getLitres()); else fe.setPrice(Float.valueOf(0));

        fe.setOdo(getIntent().getLongExtra("_prevOdo", 0));
        fe.setTrip(getIntent().getLongExtra("_prevTrip", 0));

        mDate = (EditText) findViewById(R.id.feDate);
        mDate.setText(fe.getDateString());

        setupOdoTrip();
        setupSummaPrice();



        Button btn = (Button) findViewById(R.id.fe_btn_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fueling fueling = new Fueling();
                fueling.setId(fe.getId());
                fueling.setId_auto(fe.getId_auto());
                fueling.setDate(mDate.getText().toString());
                fueling.setOdo(mOdo.getText().toString());
                fueling.setTrip(mTrip.getText().toString());
                fueling.setSumma(mSumma.getText().toString());
                fueling.setLitres(mLitres.getText().toString());
                if (fe.getId()==-1) {
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


    private void setupSummaPrice() {
        mSumma = (EditText) findViewById(R.id.feSumma);
        mPrice = (EditText) findViewById(R.id.fePrice);
        mLitres = (EditText) findViewById(R.id.feLitres);
        if (fe.getId() > 0) {
            mSumma.setText(fe.getSummaString());
            mPrice.setText(fe.getPriceString());
            mLitres.setText(fe.getLitresString());
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
                        Fueling ff = new Fueling();
                        ff.setSumma(mSumma.getText().toString());
                        ff.setPrice(mPrice.getText().toString());
                        if (ff.getPrice() > 0) {
                            ff.setLitres(ff.getSumma()/ff.getPrice());
                            //mLitres.setText(String.format("%.2f", curLitres));
                            mLitres.setText(ff.getLitresString());
                        }
                    }
                } else
                if (mPrice.getText().hashCode() == s.hashCode()) {
                    if (mPrice.isFocused()) {
                        Fueling ff = new Fueling();
                        ff.setSumma(mSumma.getText().toString());
                        ff.setPrice(mPrice.getText().toString());
                        if (ff.getPrice() > 0) {
                            ff.setLitres(ff.getSumma()/ff.getPrice());
                            //mLitres.setText(String.format("%.2f", curLitres));
                            mLitres.setText(ff.getLitresString());
                        }
                    }
                }
            }
        });

        mPrice.addTextChangedListener(tw);
        mSumma.addTextChangedListener(tw);
    }

    private void setupOdoTrip() {

        mOdo = (EditText) findViewById(R.id.feOdo);
        mTrip = (EditText) findViewById(R.id.feTrip);
        if (fe.getId() > 0) {
            mOdo.setText(fe.getOdoString());
            mTrip.setText(fe.getTripString());
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
                        Fueling ff = new Fueling();
                        ff.setOdo(mOdo.getText().toString());
                        if (ff.getOdo()>0) {
                            ff.setTrip(ff.getOdo()-fe.getOdo()); // curent ODO(ff) minus previos ODO(fe)
                            if (ff.getTrip()>0) mTrip.setText(ff.getTripString());
                        }
                    }
                } else
                if (mTrip.getText().hashCode() == s.hashCode()) {
                    if (mTrip.isFocused()) {
                        Fueling ff = new Fueling();
                        ff.setTrip(mTrip.getText().toString());
                        if (ff.getTrip()>0) {
                            ff.setOdo(fe.getOdo()+ff.getTrip()); //  prev ODO(fe) plus new Trip(ff)
                            if (ff.getOdo() > 0) mOdo.setText(ff.getOdoString());
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

}
