package com.bagum.autochecking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import db.DBTheme.Fueling;


public class FuelEditorActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = FuelEditorActivity.class.getSimpleName();

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_VIEW_PHOTO = 2;

    private String mCurrentPhotoPath;
    private ArrayList mPhotosList = null;

    protected EditText mOdo;
    protected EditText mTrip;
    EditText mSumma;
    EditText mPrice;
    EditText mLitres;
    EditText mDate;
    ImageView mImageView;
    Fueling fe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_fuel_editor);

        fe = new Fueling();
        fe.setId(getIntent().getLongExtra("_id", -1));
        fe.setId_auto(getIntent().getLongExtra("_id_auto", 1));
        fe.setDate(getIntent().getLongExtra("_date", 0));

        fe.setSumma(getIntent().getFloatExtra("_summa", 0));
        fe.setLitres(getIntent().getFloatExtra("_litres", 0));
        if (fe.getLitres() > 0) fe.setPrice(fe.getSumma() / fe.getLitres());
        else fe.setPrice(Float.valueOf(0));

        fe.setOdo(getIntent().getLongExtra("_prevOdo", 0));
        fe.setTrip(getIntent().getLongExtra("_prevTrip", 0));

        mDate = (EditText) findViewById(R.id.feDate);
        mDate.setText(fe.getDateString());
        //mDate.setOnFocusChangeListener(new DatePickerClick(this, d));
        mDate.setOnClickListener(new DatePickerClick(this, mDate));


        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString("curPic");
            mPhotosList = savedInstanceState.getIntegerArrayList("photosList");
        } else {
            mPhotosList = MainActivity.dbCont.getPhotosList(fe.getId());
            mCurrentPhotoPath = "";
        }


        setupOdoTrip();
        setupSummaPrice();
        setupButtons();
        makePhotosPreview();

    }


    private void makePhotosPreview() {
        View linearLayout = findViewById(R.id.fe_left_column);
        for (int i = 0; i < mPhotosList.size(); i++) {
            if (findViewById(i) == null) {
                ImageView iv = new ImageView(this);
                iv.setId(i);
                iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                iv.setPadding(5, 5, 5, 5);
                iv.setOnClickListener(this);
                //iv.setBackgroundColor(Color.GRAY);
                //MarginLayoutParams.


                ImageManager im = new ImageManager(getBaseContext(), 150, 150);
                im.setIsResize(true);
                im.setIsScale(true);
                im.setUseOrientation(true);
                String ss = (String) mPhotosList.get(i);
                iv.setImageBitmap(im.getFromFile(ss));

                ((LinearLayout) linearLayout).addView(iv);
            }
        }
    }

    private void setupButtons() {
        Button btn = (Button) findViewById(R.id.fe_btn_save);
        if (fe.getId() == -1) btn.setText(R.string.feBtnAdd);
        else btn.setText(R.string.feBtnUpdate);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fueling fueling = new Fueling();
                fueling.setId(fe.getId());
                fueling.setId_auto(fe.getId_auto());
                fueling.setDate(mDate.getText().toString());
                fueling.setOdo(mOdo.getText().toString());
                fueling.setTrip(mTrip.getText().toString());
                fueling.setSumma(mSumma.getText().toString());
                fueling.setLitres(mLitres.getText().toString());
                if (fe.getId() == -1) { // add new event
                    fe.setId(MainActivity.dbCont.addFuel(fueling));
                    for (int i = 0; i < mPhotosList.size(); i++) {
                        MainActivity.dbCont.addPhoto(fe.getId(), (String) mPhotosList.get(i));
                    }
                } else {
                    MainActivity.dbCont.updateFuel(fueling);
                    MainActivity.dbCont.delAllPhotos(fe.getId());
                    for (int i = 0; i < mPhotosList.size(); i++) {
                        MainActivity.dbCont.addPhoto(fe.getId(), (String) mPhotosList.get(i));
                    }
                }
                MainActivity.dbCont.changeCursorFuel(MainActivity.adapterfuel, MainActivity.spinnerAutos.getSelectedItemId());
                finish();
            }
        });

        ImageButton imbtn = (ImageButton) findViewById(R.id.fe_btn_photo);
        imbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        toSave.putString("curPic", mCurrentPhotoPath);
        toSave.putStringArrayList("photosList", mPhotosList);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storeDir = new File(storageDir, "Autochecking/");
        storeDir.mkdir();
        File image = File.createTempFile(imageFileName, ".jpg", storeDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                Log.d(TAG, "=Activity result " + " requestCode:" + requestCode + "  resultCode:" + resultCode);
                mPhotosList.add(mCurrentPhotoPath);
                makePhotosPreview();
            }
        } else {
            Log.d(TAG, "=Activity error result " + " requestCode:" + requestCode + "  resultCode:" + resultCode);
        }
    }

    private void setupSummaPrice() {
        mSumma = (EditText) findViewById(R.id.feSumma);
        mPrice = (EditText) findViewById(R.id.fePrice);
        mLitres = (EditText) findViewById(R.id.feLitres);
        if (fe.getId() > 0) {
            mSumma.setText(fe.getSummaString());
            mPrice.setText(fe.getPriceString());
            mLitres.setText(fe.getLitresString());
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
                if (mSumma.getText().hashCode() == s.hashCode()) {
                    if (mSumma.isFocused()) {
                        Fueling ff = new Fueling();
                        ff.setSumma(mSumma.getText().toString());
                        ff.setPrice(mPrice.getText().toString());
                        if (ff.getPrice() > 0) {
                            ff.setLitres(ff.getSumma() / ff.getPrice());
                            //mLitres.setText(String.format("%.2f", curLitres));
                            mLitres.setText(ff.getLitresString());
                        }
                    }
                } else if (mPrice.getText().hashCode() == s.hashCode()) {
                    if (mPrice.isFocused()) {
                        Fueling ff = new Fueling();
                        ff.setSumma(mSumma.getText().toString());
                        ff.setPrice(mPrice.getText().toString());
                        if (ff.getPrice() > 0) {
                            ff.setLitres(ff.getSumma() / ff.getPrice());
                            //mLitres.setText(String.format("%.2f", curLitres));
                            mLitres.setText(ff.getLitresString());
                        }
                    }
                }
            }
        });

        mPrice.addTextChangedListener(tw);
        mSumma.addTextChangedListener(tw);
    }

    private void setupOdoTrip() {

        mOdo = (EditText) findViewById(R.id.feOdo);
        mTrip = (EditText) findViewById(R.id.feTrip);
        if (fe.getId() > 0) {
            mOdo.setText(fe.getOdoString());
            mTrip.setText(fe.getTripString());
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
                        Fueling ff = new Fueling();
                        ff.setOdo(mOdo.getText().toString());
                        if (ff.getOdo() > 0) {
                            ff.setTrip(ff.getOdo() - fe.getOdo()); // curent ODO(ff) minus previos ODO(fe)
                            if (ff.getTrip() > 0) mTrip.setText(ff.getTripString());
                        }
                    }
                } else if (mTrip.getText().hashCode() == s.hashCode()) {
                    if (mTrip.isFocused()) {
                        Fueling ff = new Fueling();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuel_editor, menu);
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

        return super.onOptionsItemSelected(item);
    }

    Float stringToFloat(String s) {
        try {
            return Float.valueOf(s);
        } catch (Exception e) {
            Log.d(TAG, "String to float error: " + e.getMessage());
            return Float.valueOf(0);
        }
    }

    Long stringToLong(String s) {
        try {
            return Long.valueOf(s);
        } catch (Exception e) {
            return Long.valueOf(0);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String imageName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imageName, options);


        return bmp;

    }

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
}