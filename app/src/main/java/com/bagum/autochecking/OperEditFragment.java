package com.bagum.autochecking;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import db.DBController;
import db.DBHelper;
import db.DBTheme;


/**
 * A simple {@link Fragment} subclass.
 */
public class OperEditFragment extends Fragment  implements View.OnClickListener  {
    private static final String TAG = OperEditFragment.class.getSimpleName();
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
        setHasOptionsMenu(true);

        dbCont = new DBController(getActivity().getBaseContext());

        fe = new DBTheme.Operation();
        fe.setId(getArguments().getLong("id", -1));
        fe.setId_auto(getArguments().getLong("id_auto", 0));
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

        setupOdoTrip();
        setupSummaPrice();
        setupButtons();
        makePhotosPreview();


        return mView;

    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        toSave.putString("curPic", mCurrentPhotoPath);
        toSave.putStringArrayList("photosList", mPhotosList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                Log.d(TAG, "=Activity result " + " requestCode:" + requestCode + "  resultCode:" + resultCode);
                mPhotosList.add(mCurrentPhotoPath);
                makePhotosPreview();
            }
        } else {
            Log.d(TAG, "=Activity error result " + " requestCode:" + requestCode + "  resultCode:" + resultCode);
        }
    }


    private void setupButtons() {
        ImageButton btn = (ImageButton) mView.findViewById(R.id.fe_btn_save);
        //if (fe.getId() == -1) btn.setText(R.string.feBtnAdd);
        //else btn.setText(R.string.feBtnUpdate);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBTheme.Operation Operation = new DBTheme.Operation();
                Operation.setId(fe.getId());
                Operation.setId_auto(fe.getId_auto());
                Operation.setDate(mDate.getText().toString());
                Operation.setOdo(mOdo.getText().toString());
                Operation.setTrip(mTrip.getText().toString());
                Operation.setSumma(mSumma.getText().toString());
                Operation.setQty(mQty.getText().toString());
                Operation.setPrice(mPrice.getText().toString());
                Operation.setType(DBHelper.OPR_TYPE_FUEL);
                Operation.setState(DBHelper.OPR_STATE_INCOMPLETE);
                if (fe.getId() == -1) { // add new event
                    fe.setId(dbCont.addOperation(Operation));
                    for (int i = 0; i < mPhotosList.size(); i++) {
                        dbCont.addPhoto(fe.getId(), (String) mPhotosList.get(i));
                    }
                } else {
                    dbCont.updateOperation(Operation);
                    dbCont.delAllPhotos(fe.getId());
                    for (int i = 0; i < mPhotosList.size(); i++) {
                        dbCont.addPhoto(fe.getId(), (String) mPhotosList.get(i));
                    }
                }
               // dbCont.changeCursorOperation(adapterOperation, spinnerAutos.getSelectedItemId());

                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    boolean done = getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });

        ImageButton imbtn = (ImageButton) mView.findViewById(R.id.fe_btn_photo);
        imbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void setupSummaPrice() {
        mSumma = (EditText) mView.findViewById(R.id.feSumma);
        mPrice = (EditText) mView.findViewById(R.id.fePrice);
        mQty = (EditText) mView.findViewById(R.id.feQty);
        if (fe.getId() > 0) {
            mSumma.setText(fe.getSummaString());
            mPrice.setText(fe.getPriceString());
            mQty.setText(fe.getQtyString());
        }

        TextWatcher tw = (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {   }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSumma.getText().hashCode() == s.hashCode()) {
                    if (mSumma.isFocused()) {
                        DBTheme.Operation ff = new DBTheme.Operation();
                        ff.setSumma(mSumma.getText().toString());
                        ff.setPrice(mPrice.getText().toString());
                        if (ff.getPrice() > 0) {
                            ff.setQty(ff.getSumma() / ff.getPrice());
                            //mLitres.setText(String.format("%.2f", curLitres));
                            mQty.setText(ff.getQtyString());
                        }
                    }
                } else if (mPrice.getText().hashCode() == s.hashCode()) {
                    if (mPrice.isFocused()) {
                        DBTheme.Operation ff = new DBTheme.Operation();
                        ff.setSumma(mSumma.getText().toString());
                        ff.setPrice(mPrice.getText().toString());
                        if (ff.getPrice() > 0) {
                            ff.setQty(ff.getSumma() / ff.getPrice());
                            //mLitres.setText(String.format("%.2f", curLitres));
                            mQty.setText(ff.getQtyString());
                        }
                    }
                }
            }
        });

        mPrice.addTextChangedListener(tw);
        mSumma.addTextChangedListener(tw);
    }

    private void setupOdoTrip() {

        mOdo = (EditText) mView.findViewById(R.id.feOdo);
        mTrip = (EditText) mView.findViewById(R.id.feTrip);
        if (fe.getId() > 0) {
            mOdo.setText(fe.getOdoString());
            mTrip.setText(fe.getTripString());
            fe.setOdo(fe.getOdo()-fe.getTrip()); // for update info
        }


        TextWatcher tw = (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mOdo.getText().hashCode() == s.hashCode()) {
                    if (mOdo.isFocused()) {
                        DBTheme.Operation ff = new DBTheme.Operation();
                        ff.setOdo(mOdo.getText().toString());
                        if (ff.getOdo() > 0) {
                            ff.setTrip(ff.getOdo() - fe.getOdo()); // curent ODO(ff) minus previos ODO(fe)
                            if (ff.getTrip() > 0) mTrip.setText(ff.getTripString());
                        }
                    }
                } else if (mTrip.getText().hashCode() == s.hashCode()) {
                    if (mTrip.isFocused() && fe.getTrip()==-1) {
                        DBTheme.Operation ff = new DBTheme.Operation();
                        ff.setTrip(mTrip.getText().toString());
                        if (ff.getTrip() > 0) {
                            ff.setOdo(fe.getOdo() + ff.getTrip()); //  prev ODO(fe) plus new Trip(ff)
                            if (ff.getOdo() > 0) mOdo.setText(ff.getOdoString());
                        }
                    }
                }
            }
        });

        mOdo.addTextChangedListener(tw);
        mTrip.addTextChangedListener(tw);

    }
    private void makePhotosPreview() {
        View linearLayout = mView.findViewById(R.id.fe_left_column);
        for (int i = 0; i < mPhotosList.size(); i++) {
            if (mView.findViewById(i) == null) {
                ImageView iv = new ImageView(getActivity().getBaseContext());
                iv.setId(i);
                iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                iv.setPadding(5, 5, 5, 5);
                iv.setOnClickListener(this);

                ImageManager im = new ImageManager(getActivity().getBaseContext(), 150, 150);
                im.setIsResize(true);
                im.setIsScale(true);
                im.setUseOrientation(true);
                iv.setImageBitmap(im.getFromFile((String) mPhotosList.get(i)));

                ((LinearLayout) linearLayout).addView(iv);
            }
        }
    }

    private void deletePhotoFiles(ArrayList PhotosList) {
        View linearLayout = mView.findViewById(R.id.fe_left_column);
        try {
            for (int i = 0; i < mPhotosList.size(); i++) {
                File f = new File((String) mPhotosList.get(i));
                f.delete();

                View v = mView.findViewById(i);
                if (v!=null) ((LinearLayout) linearLayout).removeView(v);
            }
        }
        catch (Exception e) {  Log.d(TAG, e.getMessage());  }
        finally {
            mPhotosList.clear();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storeDir = new File(storageDir, "AutoAccounting/");
        storeDir.mkdir();
        File image = File.createTempFile(imageFileName, ".jpg", storeDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri uri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*
    * ===================================================================================
    *               Preview photos
    * ===================================================================================
    */
    protected void viewImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Log.d(TAG, "===Send file to view: " + Uri.parse(mCurrentPhotoPath).toString());
        intent.setDataAndType(Uri.parse(mCurrentPhotoPath), "image/*");
        startActivityForResult(intent, REQUEST_VIEW_PHOTO);
    }

    @Override
    public void onClick(View v) {
        if (ImageView.class.isInstance(v)) {
            Log.d(TAG, "==Clcked ImageView id: " + v.getId());

            mCurrentPhotoPath = "file://"+(String)mPhotosList.get(v.getId());
            viewImage();
        }
    }

/*
* ===================================================================================
*               Working with menu
* ===================================================================================
*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_operation_editor, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_make_photo: {
                dispatchTakePictureIntent();
                makePhotosPreview();
                return true;
            }
            case R.id.action_clear_all_photo : {
                // delete all photos from mPhotosList
                deletePhotoFiles(mPhotosList);
                // try delete all photos from db
                dbCont.delAllPhotos(fe.getId());
            }

        }
        return super.onOptionsItemSelected(item);
    }




}
