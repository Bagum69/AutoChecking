package com.bagum.autochecking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;


import java.util.Date;

import db.DBAdapter;
import db.DBController;
import db.DBTheme.Operation;
import db.DBHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkFragment extends PlaceholderFragment {
    private static final String TAG = WorkFragment.class.getSimpleName();
    private SearchView mSearchView;
    public static DBController dbCont = null;
    public static SimpleCursorAdapter adapter = null;
    public static SimpleCursorAdapter adapterSpin = null;
    public static DBAdapter adapterOperation = null;
    public static ListView fv = null;
    public static Spinner spinnerAutos = null;
    public static final String PREFS_NAME = "AutoPrefsFile";
    public static Activity mActivity;
    public static View mView;
    public static LayoutInflater mInflater;
    public static ViewGroup mContainer;
    private Handler handler;
    Long id_auto;


    public static WorkFragment newInstance(int sectionNumber) {
        WorkFragment fragment = new WorkFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this.getActivity();
        handler = new Handler();
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView  = (View) inflater.inflate(R.layout.fragment_work, container, false);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Inflate the layout for this fragment

                                SharedPreferences settings = mActivity.getSharedPreferences(PREFS_NAME, 0);
                            id_auto = settings.getLong("id_auto", -1);

                            dbCont = new DBController(mActivity.getBaseContext());
                            adapter = dbCont.getAdapter(mActivity.getBaseContext());

                            // Make Spinner for select autos
                            adapterSpin = dbCont.getAdapterSpin2(mActivity.getBaseContext());

                            adapterOperation = dbCont.getAdapterOperation(mActivity.getBaseContext());

                            spinnerAutos = (Spinner) mView.findViewById(R.id.spinner);
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
                            fv = (ListView) mView.findViewById(R.id.flist);

                            dbCont.changeCursorOperation(adapterOperation, fv.getSelectedItemId());
                            fv.setAdapter(adapterOperation);
                            View v = mActivity.getLayoutInflater().inflate(R.layout.row_fuel_header, null);
                            fv.addHeaderView(v);

                            registerForContextMenu(fv);

                            ImageButton fvAdd = (ImageButton) mView.findViewById(R.id.btn_add_fuel);
                            fvAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addOperation();
                                }
                            });
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }

                    }
                });
            }
        };
        new Thread(runnable).start();

        return mView;
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


    /*
    * ===================================================================================
    *               Working with operations
    * ===================================================================================
    */

    private void addOperation() {
        Operation fo = new Operation();
        Cursor cursor = adapterOperation.getCursor();
        if (cursor != null) {
            try {
                cursor.moveToFirst();// in first record placed last data
                Date d = new Date();

                fo.setId(-1);
                fo.setDate(d.getTime());
                fo.setOdo(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.ODO)));
                fo.setTrip(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.TRIP)));
                fo.setId_auto(cursor.getLong(cursor.getColumnIndex(Operation.OperationColumns.ID_AUTO)));
                fo.setSumma(Float.valueOf(0));
                fo.setPrice(Float.valueOf(0));
                fo.setQty(Float.valueOf(0));
                fo.setType(DBHelper.OPR_TYPE_FUEL);
                fo.setState(DBHelper.OPR_STATE_INCOMPLETE);
                mListener.onFragmentInteraction(1, fo);
            }
            catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }


    private void updateOperation() {
        Operation fo = new Operation();
        Cursor cursor = adapterOperation.getCursor();
        cursor.moveToFirst();// in first record placed last data

        fo.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        fo.setDate(cursor.getLong(cursor.getColumnIndex(Operation.OperationColumns.DATE)));
        fo.setOdo(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.ODO)));
        fo.setTrip(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.TRIP)));
        fo.setSumma(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.SUMMA)));
        fo.setPrice(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.PRICE)));
        fo.setQty(cursor.getFloat(cursor.getColumnIndex(Operation.OperationColumns.QTY)));

        fo.setId_auto(cursor.getLong(cursor.getColumnIndex(Operation.OperationColumns.ID_AUTO)));
        fo.setType(cursor.getInt(cursor.getColumnIndex(Operation.OperationColumns.TYPE)));
        fo.setState(cursor.getInt(cursor.getColumnIndex(Operation.OperationColumns.STATE)));

        mListener.onFragmentInteraction(2, fo);
    }

    private void deleteOperation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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


    /*
        * ===================================================================================
        *               Context menu
        * ===================================================================================
        */
    public static final int IDM_ADD = 100;
    public static final int IDM_EDIT = 101;
    public static final int IDM_DELETE = 102;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, IDM_ADD, Menu.NONE, mActivity.getString(R.string.cmAddFueling));
        menu.add(Menu.NONE, IDM_EDIT, Menu.NONE, mActivity.getString(R.string.cmEditFueling));
        menu.add(Menu.NONE, IDM_DELETE, Menu.NONE, mActivity.getString(R.string.cmDeleteFueling));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        CharSequence message;
        switch (item.getItemId())
        {
            case IDM_ADD:
                addOperation();
                break;
            case IDM_EDIT:
                updateOperation();
                break;
            case IDM_DELETE:
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
    public void onStop () {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        try {
            Cursor cursor = adapterSpin.getCursor();
            Long id_auto = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("id_auto", id_auto);
            // Commit the edits!
            editor.commit();
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
