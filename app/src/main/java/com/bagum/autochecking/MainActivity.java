package com.bagum.autochecking;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.List;
import db.DBAdapter;
import db.DBController;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private static final String TAG = DBController.class.getSimpleName();

    private SearchView mSearchView;
    public static DBController dbCont = null;
    public static SimpleCursorAdapter adapter = null;
    public static SimpleCursorAdapter adapterSpin = null;
    public static DBAdapter adapterfuel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbCont = new DBController(getBaseContext());

        adapter = dbCont.getAdapter(getBaseContext());

        handleIntent(getIntent());


        adapterSpin = dbCont.getAdapterSpin2(getBaseContext());
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spin itemSelect: position = " + position + ", id = " + id);
                dbCont.changeCursorFuel(adapterfuel, id);
            }
            public void onNothingSelected(AdapterView<?> parent) {  Log.d(TAG, "Spin itemSelect: nothing");  }
        });
        spinner.setAdapter(adapterSpin);



        final ListView fv = (ListView) findViewById(R.id.flist);
        adapterfuel = dbCont.getAdapterFuel(getBaseContext());
        dbCont.changeCursorFuel(adapterfuel, fv.getSelectedItemId());
        fv.setAdapter(adapterfuel);
        View v = getLayoutInflater().inflate(R.layout.row_fuel_header, null);
        fv.addHeaderView(v);

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
        if (id == R.id.test_xml) {
            Intent intent = new Intent(MainActivity.this, FuelEditorActivity.class);
            startActivity(intent);

            return true;
        }



        return super.onOptionsItemSelected(item);
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
