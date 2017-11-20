package com.example.umutserifler.beerworld;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements BWBeerModalClass.BWBeerModelClassListener{

    private ListView beerListView;

    BWBeerModalClass beerClass = BWBeerModalClassSingleton.getInstance(MainActivity.this).getBeerClass();

    private static String TAG = MainActivity.class.getSimpleName();

    public BWBeerBaseAdapter beerBaseAdapter;
    ArrayList<DataListProperties> beerListViewArrayList = new ArrayList<DataListProperties>();


    ProgressDialog loadingDialog;

    RelativeLayout beerListViewRelativeLayout;
    RelativeLayout selectedBeerRelativeLayout;
    ImageButton selectedBeerViewCloseButton;
    ImageButton selectBeerFavouriteImageButton;
    EditText selectedBeerInfoEditText;
    ImageView selectedBeerImageView;

    Spinner filterBeerSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isNetworkConnected()){
            beerClass.isInternetReachable = true;
            beerClass.setURLInit();
            beerClass.getRequestBeer();
        }else {
            beerClass.isInternetReachable = false;
        beerClass.parseJSONFromFile();
        }

        prepPropertyDefinition();
        callLoadingWindow();

        selectBeerFavouriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                beerClass.martSelectedBeerFavouriteOrNot();
            }
        });

        selectedBeerViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectedBeerRelativeLayout.setVisibility(View.INVISIBLE);
                beerListViewRelativeLayout.setVisibility(View.VISIBLE);
            }
        });

        filterBeerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d(TAG, String.valueOf(position));
                beerClass.filterStatusHasBeenChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        beerClass = new BWBeerModalClass(MainActivity.this);
        beerClass.setListener(this);


        beerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedBeerImageView.setImageResource(android.R.color.transparent);
                beerClass.getSelectedBeer(position);
            }
        });



    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void callLoadingWindow(){
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setIndeterminate(true);
        loadingDialog.show();
    }

    private void prepPropertyDefinition(){
        beerListViewRelativeLayout = (RelativeLayout) findViewById(R.id.beerlistViewRelativeLayout);
        selectedBeerRelativeLayout = (RelativeLayout) findViewById(R.id.selectedBeerViewRelativeLayout);
        filterBeerSpinner = (Spinner) findViewById(R.id.spinnerForBeerChoosing);
        String[] spinnerItems = new String[]{"ALL", "FAVOURITE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        filterBeerSpinner.setAdapter(adapter);
        selectedBeerInfoEditText = (EditText) findViewById(R.id.selectedBeerInfoEditText);
        selectedBeerImageView = (ImageView) findViewById(R.id.selectedBeerImageView);
        selectedBeerRelativeLayout.setVisibility(View.INVISIBLE);
        selectBeerFavouriteImageButton = (ImageButton) findViewById(R.id.selectBeerFavouriteImageButton);
        selectedBeerViewCloseButton = (ImageButton) findViewById(R.id.selectedBeerViewCloseButton);
        beerListView = (ListView) findViewById(R.id.beerListView);
        beerBaseAdapter = new BWBeerBaseAdapter(MainActivity.this,beerListViewArrayList, 0);
        beerListView.setAdapter(beerBaseAdapter);
    }


    @Override
    public void updateCurrentList(ArrayList<DataListProperties> takenDataList) {
        //Log.d(TAG, "updateCurrentList Method");
        beerListViewArrayList = takenDataList;
        beerBaseAdapter = new BWBeerBaseAdapter(MainActivity.this,beerListViewArrayList, 0);
        beerListView.setAdapter(beerBaseAdapter);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized(beerListView){
                    beerListView.notify();
                }
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void showLoadingDialogWindow(Boolean state) {
        if (!state){
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            return;
        }
        callLoadingWindow();
    }

    @Override
    public void newPageWithSelectedBeer(String selectedBeerInfo, String selectedBeerImageURL) {
        selectedBeerRelativeLayout.setVisibility(View.VISIBLE);
        beerListViewRelativeLayout.setVisibility(View.INVISIBLE);
        selectedBeerInfoEditText.setText(selectedBeerInfo);
        if(isNetworkConnected())new BWDownloadImageTask(selectedBeerImageView).execute(selectedBeerImageURL);
        Log.d(TAG, selectedBeerInfo);
    }

    @Override
    public void updateBeerFavouriteState() {
       selectBeerFavouriteImageButton.setImageResource(beerClass.getBeerFavouriteState() ? R.drawable.selected_icon : R.drawable.not_selected_icon);
    }


}
