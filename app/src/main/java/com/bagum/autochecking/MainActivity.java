package com.bagum.autochecking;

import android.app.Activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import db.DBController;
import db.DBTheme;


public class MainActivity extends ActionBarActivity implements
        PlaceholderFragment.OnFragmentInteractionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {


    /*** Fragment managing the behaviors, interactions and presentation of the navigation drawer.  */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**    * Used to store the last screen title. For use in {@link #restoreActionBar()}.  */
    private CharSequence mTitle;


    private static final String TAG = MainActivity.class.getSimpleName();
    DBController dbCont;
    private long id_auto;
    public static final String PREFS_NAME = "AutoPrefsFile";
    SimpleCursorAdapter mAdapterSpin;
    Spinner mSpinnerAutos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //===========================================================================
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_auto = settings.getLong("id_auto", -1);

        //===========================================================================
        dbCont = new DBController(getBaseContext());
        // Make Spinner for select autos
        mAdapterSpin = dbCont.getAdapterSpin2(getBaseContext());
        mSpinnerAutos = (Spinner) findViewById(R.id.spinner);
        mSpinnerAutos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "Spin itemSelect: position = " + position + ", id = " + id);

                id_auto = id;
                setIdToFragment(id);
            }
            public void onNothingSelected(AdapterView<?> parent) {  Log.d(TAG, "Spin itemSelect: nothing");  }
        });
        mSpinnerAutos.setAdapter(mAdapterSpin);
        // restore postion spinner
        if (id_auto != -1)  SelectSpinnerItemByValue(mSpinnerAutos, id_auto);
        //===========================================================================


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0: fragmentManager.beginTransaction()
                    .replace(R.id.container, StatFragment.newInstance(position + 1, id_auto))
                    .commit();
                    break;
            case 1: fragmentManager.beginTransaction()
                    .replace(R.id.container, WorkFragment.newInstance(position + 1, id_auto))
                    .commit();
                    break;
            default: fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1, id_auto))
                    .commit();
                    break;

        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    public void onFragmentInteraction(int cmd, DBTheme.Operation oper) {

        switch (cmd) {
            case 1: {// add
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                    .replace(R.id.container, OperEditFragment.newInstance(cmd, oper))
                    .addToBackStack(null)
                    .commit();
                break;
            }
            case 2: {// edit
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, OperEditFragment.newInstance(cmd, oper))
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        //Toast.makeText(this, "Cmd from fragment:"+cmd + " id:" + oper.getId(), Toast.LENGTH_SHORT).show();
    }

    //================================================================================
    //
    //================================================================================
    public static void SelectSpinnerItemByValue(Spinner spnr, long value)
    {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++)
            if(adapter.getItemId(position) == value)
            {
                spnr.setSelection(position);
                return;
            }
    }
    @Override

    public void onStop () {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        try {
            Cursor cursor = mAdapterSpin.getCursor();
            Long id_auto = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("id_auto", id_auto);
            // Commit the edits!
            editor.commit();
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setIdToFragment(long id) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null & fragment.isVisible()){
                if (PlaceholderFragment.class.isAssignableFrom(fragment.getClass())) {
                    ((PlaceholderFragment) fragment).setIdFromActivity(id);
                }
            }
        }
    }
}
