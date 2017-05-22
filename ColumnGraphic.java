package com.example.danie.rccolumndetailer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ColumnGraphic extends AppCompatActivity {

    private static final String LOG_TAG = ColumnGraphic.class.getSimpleName();

    private static final int EXISTING_COLUMN_LOADER = 0;
    private static final String STATE_URI = "STATE_URI";

    private Uri mCurrentColumnUri;

    private double mXDimension;
    private double mYDimension;
    private int mCompressiveStrength;
    private int mBarSize;
    private int mBarsInXDirection;
    private int mBarsInYDirection;
    private int mPresetBarsX;
    private int mPresetBarsY;
    private double mEffectiveLength;

    //private TextView mColXDimTextView;
    private TextView mColYDimTextView;
    private TextView mColBarSizeTextView;
    private TextView mColBarsInXTextView;
    private TextView mColBarsInYTextView;

    //private int mColXDim = g

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_graphic);

        Intent graphicIntent = getIntent();
        Bundle graphicBundle = getIntent().getExtras();
        mXDimension = graphicBundle.getDouble("XDIM_KEY");
        mYDimension = graphicBundle.getDouble("YDIM_KEY");
        mCompressiveStrength = graphicBundle.getInt("FC_KEY");
        mBarSize = graphicBundle.getInt("BARSIZE_KEY");
        mBarsInXDirection = graphicBundle.getInt("BARSX_KEY");
        mBarsInYDirection = graphicBundle.getInt("BARSY_KEY");
        mEffectiveLength = graphicBundle.getDouble("LE_KEY");

        int mXDimensionInt = (int) mXDimension;
        int mYDimensionInt = (int) mYDimension;

        //mPresetBarsX =

        String colXDimString = "Column Dim X = " + mXDimensionInt + "mm";
        String colYDimString = "Column Dim Y = " + mYDimensionInt + "mm";
        String barSizeString = "Column Reo Bar Size = " + mBarSize + "mm";
        String barsInXString = "Bars in X Direction = " + mBarsInXDirection;
        String barsInYString = "Bars in Y Direction = " + mBarsInYDirection;

        TextView mColXDimTextView = (TextView) findViewById(R.id.column_dimx);
        mColXDimTextView.setText(colXDimString);

        TextView mColYDimTextView = (TextView) findViewById(R.id.column_dimy);
        mColYDimTextView.setText(colYDimString);

        TextView mBarSizeTextView = (TextView) findViewById(R.id.column_barsize);
        mBarSizeTextView.setText(barSizeString);

        TextView mBarsInXTextView = (TextView) findViewById(R.id.column_barsinX);
        mBarsInXTextView.setText(barsInXString);

        TextView mBarsInYTextView = (TextView) findViewById(R.id.column_barsinY);
        mBarsInYTextView.setText(barsInYString);

        Button mGoToInputButton = (Button) findViewById(R.id.back_to_inputscreen);
        mGoToInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ColumnGraphic.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
