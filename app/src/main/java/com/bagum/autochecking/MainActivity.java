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
import android.widget.SimpleCursorAdapter;

import java.util.List;

import db.DBController;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private static final String TAG = DBController.class.getSimpleName();

    private SearchView mSearchView;
    public static SimpleCursorAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = DBController.getAdapter(getBaseContext());

        handleIntent(getIntent());
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
        //mStatusView.setText("Query = " + newText);
        DBController.changeCursor(getBaseContext(), MainActivity.adapter, newText);
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
        DBController.changeCursor(getBaseContext(), MainActivity.adapter, s);
        return true;
    }
}
