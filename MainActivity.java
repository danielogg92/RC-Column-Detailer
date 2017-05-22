package com.example.danie.rccolumndetailer;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {//} implements LoaderManager.LoaderCallbacks<Object> {

//    private static final String SHARED_DATA = "savedData";
//
//    public static String getStoredQuery(Context context){
//        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_DATA,null);
//    }

//    private SharedPreferences mPrefs;
//    public static final String MAIN_PREFS_ED = "MainPrefEd";

    public double mXDimension = 250;
    public double mYDimension = 650;
    int mCompressiveStrength = 50;
    int mBarSize = 25;
    int mBarsInXDirection = 2;
    int mBarsInYDirection = 4;
    int COVER = 50;
    double mEffectiveLength = 4150;
    double mBetaD = 0.85;
    double mDeadLoad = 1.0;
    double mLiveLoad = (mDeadLoad-mBetaD)/mBetaD;
    String mCapacity = "0.0 kN";
    int FS = 500;

    private static final int COLUMN_LOADER = 0;

    private Uri mCurrentColumnUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getIntent();

//        SharedPreferences.Editor mainEd = mPrefs.edit();
//        mainEd.putString("XDIM_KEY",Double.toString(mXDimension));
//        mainEd.putString("YDIM_KEY",Double.toString(mYDimension));
//        mainEd.putInt("FC_KEY",mCompressiveStrength);
//        mainEd.putInt("BARSIZE_KEY",mBarSize);
//        mainEd.putInt("BARSX_KEY",mBarsInXDirection);
//        mainEd.putInt("BARSY_KEY",mBarsInYDirection);
//        mainEd.putString("LE_KEY",Double.toString(mEffectiveLength));
//        mainEd.commit();
//
//        mPrefs = getSharedPreferences(MAIN_PREFS_ED,MODE_PRIVATE);

        Button mGoToGrahicsButton = (Button) findViewById(R.id.go_to_graphics);
        mGoToGrahicsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText xDimText = (EditText) findViewById(R.id.xDimInput);
                mXDimension = Double.parseDouble(xDimText.getText().toString());

                EditText yDimText = (EditText) findViewById(R.id.yDimInput);
                mYDimension = Double.parseDouble(yDimText.getText().toString());

                EditText compressiveStrengthText = (EditText) findViewById(R.id.fcInput);
                mCompressiveStrength = Integer.parseInt(compressiveStrengthText.getText().toString());

                EditText barSizeText = (EditText) findViewById(R.id.barSizeInput);
                mBarSize = Integer.parseInt(barSizeText.getText().toString());

                EditText barsInXText = (EditText) findViewById(R.id.barsInXInput);
                mBarsInXDirection = Integer.parseInt(barsInXText.getText().toString());

                EditText barsInYText = (EditText) findViewById(R.id.barsInYInput);
                mBarsInYDirection = Integer.parseInt(barsInYText.getText().toString());

                EditText effectiveLengthText = (EditText) findViewById(R.id.effectiveLengthInput);
                mEffectiveLength = Double.parseDouble(effectiveLengthText.getText().toString());

                Intent mainIntent = new Intent(MainActivity.this, ColumnGraphic.class);
                //Intent mainIntent = new Intent(getBaseContext(), ColumnGraphic.class);
                Bundle mainBundle = new Bundle();
                mainBundle.putDouble("XDIM_KEY",mXDimension);
                mainBundle.putDouble("YDIM_KEY",mYDimension);
                mainBundle.putInt("FC_KEY",mCompressiveStrength);
                mainBundle.putInt("BARSIZE_KEY",mBarSize);
                mainBundle.putInt("BARSX_KEY",mBarsInXDirection);
                mainBundle.putInt("BARSY_KEY",mBarsInYDirection);
                mainBundle.putInt("COVER_KEY",COVER);
                mainBundle.putDouble("LE_KEY",mEffectiveLength);
                mainBundle.putDouble("BETAD_KEY",mBetaD);
                mainBundle.putDouble("DL_KEY",mDeadLoad);
                mainBundle.putDouble("LL_KEY",mLiveLoad);
                mainBundle.putInt("FS_KEY",FS);
                mainIntent.putExtras(mainBundle);
                startActivity(mainIntent);
            }
        });

        //getLoaderManager().initLoader(COLUMN_LOADER, null, this);
    }

//    @Nullable
//    @Override
//    protected void onPause(){
//        super.onPause();
//
//        SharedPreferences.Editor mainEd = mPrefs.edit();
//        mainEd.putString("XDIM_KEY",Double.toString(mXDimension));
//        mainEd.putString("YDIM_KEY",Double.toString(mYDimension));
//        mainEd.putInt("FC_KEY",mCompressiveStrength);
//        mainEd.putInt("BARSIZE_KEY",mBarSize);
//        mainEd.putInt("BARSX_KEY",mBarsInXDirection);
//        mainEd.putInt("BARSY_KEY",mBarsInYDirection);
//        mainEd.putString("LE_KEY",Double.toString(mEffectiveLength));
//        mainEd.commit();
//    }

    public void calculation(View view){

        EditText xDimText = (EditText) findViewById(R.id.xDimInput);
        mXDimension = Double.parseDouble(xDimText.getText().toString());

        EditText yDimText = (EditText) findViewById(R.id.yDimInput);
        mYDimension = Double.parseDouble(yDimText.getText().toString());

        EditText compressiveStrengthText = (EditText) findViewById(R.id.fcInput);
        mCompressiveStrength = Integer.parseInt(compressiveStrengthText.getText().toString());

        EditText barSizeText = (EditText) findViewById(R.id.barSizeInput);
        mBarSize = Integer.parseInt(barSizeText.getText().toString());

        EditText barsInXText = (EditText) findViewById(R.id.barsInXInput);
        mBarsInXDirection = Integer.parseInt(barsInXText.getText().toString());

        EditText barsInYText = (EditText) findViewById(R.id.barsInYInput);
        mBarsInYDirection = Integer.parseInt(barsInYText.getText().toString());

        EditText effectiveLengthText = (EditText) findViewById(R.id.effectiveLengthInput);
        mEffectiveLength = Double.parseDouble(effectiveLengthText.getText().toString());

        Column c100 = new Column(mXDimension,mYDimension,mBarsInXDirection,mBarsInYDirection,
                mBarSize,FS,mCompressiveStrength,COVER,mBetaD,mEffectiveLength);

        Column c190 = new Column(mYDimension,mXDimension,mBarsInYDirection,mBarsInXDirection,
                mBarSize,FS,mCompressiveStrength,COVER,mBetaD,mEffectiveLength);

        double column00Cap = c100.columnCapacitySolver();
        double column90Cap = c190.columnCapacitySolver();

        if(column00Cap<=column90Cap){
            mCapacity = c100.colCapacityToString();
        }else{
            mCapacity = c190.colCapacityToString();
        }

        TextView columnCapacityText = (TextView) findViewById(R.id.columnCapacityOutput);
        columnCapacityText.setText("Column Capacity = " + mCapacity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.meun_main,menu);
        return true;
    }

//    @Override
//    public Loader<Object> onCreateLoader(int id, Bundle args) {
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Object> loader, Object data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Object> loader) {
//
//    }
}
