package com.bagum.autochecking;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.Date;
import java.util.List;
import db.DBAdapter;
import db.DBController;
import db.DBTheme;


import static android.widget.SearchView.OnQueryTextListener;


public class WorkActivity extends ActionBarActivity  implements OnQueryTextListener,
        SearchView.OnCloseListener {
    private static final String TAG = WorkActivity.class.getSimpleName();
    private SearchView mSearchView;
    public static DBController dbCont = null;
    public static SimpleCursorAdapter adapter = null;
    public static SimpleCursorAdapter adapterSpin = null;
    public static DBAdapter adapterOperation = null;
    public static ListView fv = null;
    public static Spinner spinnerAutos = null;
    public static final String PREFS_NAME = "AutoPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

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
        spinnerAutos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spin itemSelect: position = " + position + ", id = " + id);
                dbCont.changeCursorOperation(adapterOperation, id);
            }
            public void onNothingSelected(AdapterView<?> parent) {  Log.d(TAG, "Spin itemSelect: nothing");  }
        });
        spinnerAutos.setAdapter(adapterSpin);
        // restore postion spinner
        if (id_auto != -1)  SelectSpinnerItemByValue(spinnerAutos, id_auto);

        // Setup listView for Operation
        fv = (ListView) findViewById(R.id.flist);
        adapterOperation = dbCont.getAdapterOperation(getBaseContext());
        dbCont.changeCursorOperation(adapterOperation, fv.getSelectedItemId());
        fv.setAdapter(adapterOperation);
        View v = getLayoutInflater().inflate(R.layout.row_fuel_header, null);
        fv.addHeaderView(v);

        registerForContextMenu(fv);

        /*
        fv.setLongClickable(true);
        fv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Long click Item position:" + position + " id: " + id);
                updateOperation();
                return true;
            }
        });
        */

        ImageButton fvAdd = (ImageButton) findViewById(R.id.btn_add_fuel);
        fvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOperation();
            }
        });

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
        DBController.changeCursor(adapter, s);
        return true;
    }

    /*
        * ===================================================================================
        *               Context menu
        * ===================================================================================
        */
    public static final int IDM_EDIT = 101;
    public static final int IDM_DELETE = 102;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, IDM_EDIT, Menu.NONE, "Изменить");
        menu.add(Menu.NONE, IDM_DELETE, Menu.NONE, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        CharSequence message;
        switch (item.getItemId())
        {
            case IDM_EDIT:
                //message = "Выбран пункт Изменить";
                updateOperation();
                break;
            case IDM_DELETE:
                //message = "Выбран пункт Удалить";
                deleteOperation();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        //Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        //toast.show();
        return true;
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
            addOperation();
            return true;
        }
        if (id == R.id.updateFuel) {
            updateOperation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
        * ===================================================================================
        *               Search functions
        * ===================================================================================
        */

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
        dbCont.changeCursor(adapter, newText);
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            mSearchView.setSearchableInfo(info);
        }
        mSearchView.setOnQueryTextListener((SearchView.OnQueryTextListener)this);
        mSearchView.setOnCloseListener(this);
//        mSearchView.setOnSuggestionListener(this);
    }

    /*
    * ===================================================================================
    *               Working with operations
    * ===================================================================================
    */

    private void addOperation() {
        Intent intent = new Intent(this, OperationEditorActivity.class);
        Cursor cursor = adapterOperation.getCursor();
        cursor.moveToFirst();// in first record placed last data
        Date d = new Date();
        intent.putExtra("_id", -1);
        intent.putExtra("_date", d.getTime());
        intent.putExtra("_prevOdo", cursor.getFloat(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.ODO)));
        intent.putExtra("_prevTrip", cursor.getFloat(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.TRIP)));
        intent.putExtra("_id_auto", cursor.getLong(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.ID_AUTO)));
        startActivity(intent);
    }


    private void updateOperation() {
        Intent intent = new Intent(this, OperationEditorActivity.class);
        Cursor cursor = adapterOperation.getCursor();
        intent.putExtra("_id", cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        intent.putExtra("_date", cursor.getLong(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.DATE)));
        intent.putExtra("_prevOdo", cursor.getFloat(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.ODO)));
        intent.putExtra("_prevTrip", cursor.getFloat(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.TRIP)));
        intent.putExtra("_id_auto", cursor.getLong(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.ID_AUTO)));
        intent.putExtra("_summa", cursor.getFloat(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.SUMMA)));
        intent.putExtra("_qty", cursor.getFloat(cursor.getColumnIndex(DBTheme.Operation.OperationColumns.QTY)));
        startActivity(intent);
    }

    private void deleteOperation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Cursor cursor = adapterOperation.getCursor();
                        dbCont.deleteOperation(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
                        dbCont.changeCursorOperation(adapterOperation, spinnerAutos.getSelectedItemId());

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
                dbCont.changeCursorOperation(adapterOperation, id);
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
