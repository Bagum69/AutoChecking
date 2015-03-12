package com.bagum.autochecking;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.Date;
import java.util.List;
import db.DBAdapter;
import db.DBController;
import db.DBTheme;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SearchView mSearchView;
    public static DBController dbCont = null;
    public static SimpleCursorAdapter adapter = null;
    public static SimpleCursorAdapter adapterSpin = null;
    public static DBAdapter adapterfuel = null;
    public static ListView fv = null;
    public static Spinner spinnerAutos = null;
    public static final String PREFS_NAME = "AutoPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Long id_auto = settings.getLong("id_auto", -1);


        dbCont = new DBController(getBaseContext());
        adapter = dbCont.getAdapter(getBaseContext());

        //For Searching
        handleIntent(getIntent());

        // Make Spinner for select autos
        adapterSpin = dbCont.getAdapterSpin2(getBaseContext());
        spinnerAutos = (Spinner) findViewById(R.id.spinner);
        spinnerAutos.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spin itemSelect: position = " + position + ", id = " + id);
                dbCont.changeCursorFuel(adapterfuel, id);
            }
            public void onNothingSelected(AdapterView<?> parent) {  Log.d(TAG, "Spin itemSelect: nothing");  }
        });
        spinnerAutos.setAdapter(adapterSpin);
        // restore postion spinner
        if (id_auto != -1)  SelectSpinnerItemByValue(spinnerAutos, id_auto);

        // Setup listView for fueling
        fv = (ListView) findViewById(R.id.flist);
        adapterfuel = dbCont.getAdapterFuel(getBaseContext());
        dbCont.changeCursorFuel(adapterfuel, fv.getSelectedItemId());
        fv.setAdapter(adapterfuel);
        View v = getLayoutInflater().inflate(R.layout.row_fuel_header, null);
        fv.addHeaderView(v);

        fv.setLongClickable(true);
        fv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Long click Item position:" + position + " id: " + id);
                updateFueling();
                return true;
            }
        });

        ImageButton fvAdd = (ImageButton) findViewById(R.id.btn_add_fuel);
        fvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFueling();
            }
        });

    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String newText) {
        Log.d(TAG, "Voice query = " + newText);
        dbCont.changeCursor(MainActivity.adapter, newText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (mSearchView != null) {
            setupSearchView();
        }


        return true;
    }

    @Override
    protected void onStop () {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        Cursor cursor = adapterSpin.getCursor();
        Long id_auto = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("id_auto", id_auto);
        // Commit the edits!
        editor.commit();
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            mSearchView.setSearchableInfo(info);
        }
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
//        mSearchView.setOnSuggestionListener(this);
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
        if (id == R.id.addFuel) {
            addFueling();

            return true;
        }
        if (id == R.id.updateFuel) {
            updateFueling();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void addFueling() {
        Intent intent = new Intent(MainActivity.this, FuelEditorActivity.class);
        Cursor cursor = adapterfuel.getCursor();
        cursor.moveToFirst();// in first record placed last data
        Date d = new Date();
        intent.putExtra("_id", -1);
        intent.putExtra("_date", d.getTime());
        intent.putExtra("_prevOdo", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.ODO)));
        intent.putExtra("_prevTrip", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.TRIP)));
        intent.putExtra("_id_auto", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.ID_AUTO)));
        startActivity(intent);
    }

    private void updateFueling() {
        Intent intent = new Intent(MainActivity.this, FuelEditorActivity.class);
        Cursor cursor = adapterfuel.getCursor();
        intent.putExtra("_id", cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        intent.putExtra("_date", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.DATE)));
        intent.putExtra("_prevOdo", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.ODO)));
        intent.putExtra("_prevTrip", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.TRIP)));
        intent.putExtra("_id_auto", cursor.getLong(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.ID_AUTO)));
        intent.putExtra("_summa", cursor.getFloat(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.SUMMA)));
        intent.putExtra("_litres", cursor.getFloat(cursor.getColumnIndex(DBTheme.Fueling.FuelColumns.LITR)));
        startActivity(intent);
    }

    public static void SelectSpinnerItemByValue(Spinner spnr, long value)
    {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++)
        {
            if(adapter.getItemId(position) == value)
            {
                spnr.setSelection(position);
                return;
            }
        }
    }

    @Override
    public boolean onClose() {
        Log.d(TAG, "Closed!");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.d(TAG, "Query = " + s + " : submitted");
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.d(TAG, "Query = " + s);
        DBController.changeCursor(MainActivity.adapter, s);
        return true;
    }
}

/*
        final ListView lv = (ListView) findViewById(R.id.alist);

        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(TAG, "itemClick: position = " + position + ", id = "
                        + id);
                dbCont.changeCursorFuel(adapterfuel, id);
            }
        });
        lv.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d(TAG, "itemSelect: position = " + position + ", id = "
                        + id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "itemSelect: nothing");
            }

        });
*/
