package com.bagum.autochecking;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import db.DBController;
import db.DBHelper;
import db.DBTheme;


/**
 * A simple {@link Fragment} subclass.
 */
public class OperEditFragment extends Fragment {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_VIEW_PHOTO = 2;

    private String mCurrentPhotoPath;
    private ArrayList mPhotosList = null;

    protected EditText mOdo;
    protected EditText mTrip;
    EditText mSumma;
    EditText mPrice;
    EditText mQty;
    EditText mDate;
    View mView;
    ImageView mImageView;
    DBTheme.Operation fe;
    public static DBController dbCont = null;


    public OperEditFragment() {
        // Required empty public constructor
    }

    public static OperEditFragment newInstance(int cmd, DBTheme.Operation oper) {
        OperEditFragment fragment = new OperEditFragment();
        Bundle args = new Bundle();
        args.putLong("id", oper.getId());
        args.putLong("id_auto", oper.getId_auto());
        args.putString("date", oper.getDateString());
        args.putFloat("odo", oper.getOdo());
        args.putFloat("trip", oper.getTrip());
        args.putFloat("summa", oper.getSumma());
        args.putFloat("price", oper.getPrice());
        args.putFloat("qty", oper.getQty());
        args.putLong("type", oper.getType());
        args.putLong("state", oper.getState());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_oper_edit, container, false);


        dbCont = WorkActivity.dbCont;

        fe = new DBTheme.Operation();
        fe.setId(getArguments().getLong("id", -1));
        fe.setId_auto(getArguments().getLong("id_auto", 1));
        fe.setDate(getArguments().getString("date", ""));

        fe.setSumma(getArguments().getFloat("summa", 0));
        fe.setQty(getArguments().getFloat("qty", 0));
        if (fe.getQty() > 0) fe.setPrice(fe.getSumma() / fe.getQty());
        else fe.setPrice(Float.valueOf(0));

        fe.setOdo(getArguments().getFloat("odo", 0));
        fe.setTrip(getArguments().getFloat("trip", 0));


        fe.setType(DBHelper.OPR_TYPE_FUEL);
        fe.setState(DBHelper.OPR_STATE_INCOMPLETE);

        mDate = (EditText) mView.findViewById(R.id.feDate);
        mDate.setText(fe.getDateString());
        //mDate.setOnFocusChangeListener(new DatePickerClick(this, d));
        mDate.setOnClickListener(new DatePickerClick(getActivity(), mDate));

        dbCont.integrityCheck();

        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString("curPic");
            mPhotosList = savedInstanceState.getIntegerArrayList("photosList");
        } else {
            mPhotosList = dbCont.getPhotosList(fe.getId());
            mCurrentPhotoPath = "";
        }


        return mView;

    }


}
