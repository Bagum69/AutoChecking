package com.bagum.autochecking;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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
    long id_auto;

    public static StatFragment newInstance(int sectionNumber, long id_auto) {
        StatFragment fragment = new StatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putLong("id_auto", id_auto);
        fragment.setArguments(args);
        return fragment;
    }

    public StatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stat, container, false);

        id_auto = getArguments().getLong("id_auto", 1);
        getStat(id_auto);


        // Inflate the layout for this fragment
        return mView;
    }

    private void getStat(long id_auto) {

        dbCont = new DBController(getActivity().getBaseContext());
        Stat stat = dbCont.getStat(id_auto);

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

    }

    @Override
    public void setIdFromActivity(long id_auto) {
        this.id_auto = id_auto;
        getStat(id_auto);
    }
}
