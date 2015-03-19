package com.bagum.autochecking;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import db.DBController;
import db.Stat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatFragment extends PlaceholderFragment {
    DBController dbCont;
    View mView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public static StatFragment newInstance(int sectionNumber) {
        StatFragment fragment = new StatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stat, container, false);

        dbCont = new DBController(getActivity().getBaseContext());
        Stat stat = dbCont.getStat(2);

        TextView ld = (TextView)mView.findViewById(R.id.last_date);
        ld.setText(stat.getLastDateString());

        TextView lr = (TextView)mView.findViewById(R.id.last_rate);
        lr.setText(stat.getLastRateString());

        TextView lq = (TextView)mView.findViewById(R.id.last_qty);
        lq.setText(stat.getLastQtyString());

        TextView lt = (TextView)mView.findViewById(R.id.last_trip);
        lt.setText(stat.getLastTripString());


        TextView mr = (TextView)mView.findViewById(R.id.month_rate);
        mr.setText(stat.getMonthRateString());

        TextView mq = (TextView)mView.findViewById(R.id.month_qty);
        mq.setText(stat.getMonthQtyString());

        TextView mt = (TextView)mView.findViewById(R.id.month_trip);
        mt.setText(stat.getMonthTripString());

        TextView pmr = (TextView)mView.findViewById(R.id.pmonth_rate);
        pmr.setText(stat.getPMonthRateString());

        TextView pmq = (TextView)mView.findViewById(R.id.pmonth_qty);
        pmq.setText(stat.getPMonthQtyString());

        TextView pmt = (TextView)mView.findViewById(R.id.pmonth_trip);
        pmt.setText(stat.getPMonthTripString());



        // Inflate the layout for this fragment
        return mView;
    }

}
