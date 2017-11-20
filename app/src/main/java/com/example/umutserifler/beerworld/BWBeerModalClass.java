package com.example.umutserifler.beerworld;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * Created by umutserifler on 19.11.2017.
 */

class BWBeerModalClass {

    private Context context;

    private static String TAG = BWBeerModalClass.class.getSimpleName();

    private static BWBeerModelClassListener listener;

    private static final String URLTYPO = "https://api.punkapi.com/v2/beers";

    private static final String PAGETYPO = "?page=";

    private static final String IDTYPO = "?ids=";

    private static final String PERPAGETYPO = "&per_page=80";

    private static String CURRENT_URL = "";

    private static int pageNumber = 1;
    private static int selectedBeerID;
    private static Boolean selectedBeerFavouriteState = false;
    public static Boolean isInternetReachable = true;
    private int currentListViewStatus = 0;
    private int selectedFavouriteBeerCount = 0;

    private Boolean isAllBeerListTaken = false;

    ArrayList<DataListProperties> beerListViewArrayList = new ArrayList<DataListProperties>();

    private static JSONArray savedAllBeer = new JSONArray();
    private static JSONArray savedFavouriteBeers = new JSONArray();

    BWBeerModalClass(Context context) {
        setContext(context);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.context.openFileOutput("savedJSONARRAY.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("savedJSONARRAY.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    public void setURLInit() {
        if (CURRENT_URL.isEmpty()){
            CURRENT_URL = URLTYPO + PAGETYPO + String.valueOf(pageNumber) + PERPAGETYPO;
            pageNumber++;
        }
    }

    private void setContext(Context context) {
        this.context = context;
    }

    interface BWBeerModelClassListener {
        void updateCurrentList(ArrayList<DataListProperties> str);

        void showLoadingDialogWindow(Boolean state);

        void newPageWithSelectedBeer(String selectedBeerInfo, String selectedBeerImageURL);

        void updateBeerFavouriteState();

    }

    void getRequestNewDataWithPageNumber(){
        if (isAllBeerListTaken || !isInternetReachable) return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Log.d(TAG, "update UI");
                if (listener != null)
                    listener.showLoadingDialogWindow(true);
            }
        });

        CURRENT_URL = URLTYPO + PAGETYPO + String.valueOf(pageNumber) + PERPAGETYPO;
        pageNumber++;
        getRequestBeer();



    }

    public void getRequestBeer(){
        if (!isInternetReachable){
            if (listener != null)
            listener.showLoadingDialogWindow(false);
            return;
        }
        String result;
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            result = getRequest.execute(CURRENT_URL).get();
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() == 0){
                isAllBeerListTaken = true;
                listener.showLoadingDialogWindow(false);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("favouriteBeer",false);
                DataListProperties ld = new DataListProperties();
                ld.setTitle(String.valueOf(jsonObject.getString("name")));
                beerListViewArrayList.add(ld);
                savedAllBeer.put(jsonObject);
            }
            writeToFile(savedAllBeer.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Log.d(TAG, "update UI");
                if (listener != null)
                    listener.updateCurrentList(beerListViewArrayList);
            }
        });
    }

    public void parseJSONFromFile(){
        String result;
        try {
            result = readFromFile();
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("favouriteBeer",false);
                DataListProperties ld = new DataListProperties();
                ld.setTitle(String.valueOf(jsonObject.getString("name")));
                beerListViewArrayList.add(ld);
                savedAllBeer.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Log.d(TAG, "update UI");
                if (listener != null)
                    listener.updateCurrentList(beerListViewArrayList);
            }
        });
    }

    void filterStatusHasBeenChanged(int filterState){
        currentListViewStatus = filterState;
        JSONArray jsonArray;
        if (currentListViewStatus == 0){
            jsonArray = savedAllBeer;
        }
        else jsonArray = savedFavouriteBeers;

        try {
            JSONObject jsonObject;
            beerListViewArrayList.clear();
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                DataListProperties ld = new DataListProperties();
                ld.setTitle(String.valueOf(jsonObject.getString("name")));
                beerListViewArrayList.add(ld);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Log.d(TAG, "update UI");
                if (listener != null)
                    listener.updateCurrentList(beerListViewArrayList);
            }
        });
    }

    void getSelectedBeer(int selectedBeer){
        CURRENT_URL = "";
        selectedBeerID = selectedBeer;
        CURRENT_URL = URLTYPO + IDTYPO + String.valueOf(selectedBeer + 1);
        getRequestSelectedBeer();

    }

    private void getRequestSelectedBeer(){
        JSONObject tempJsonObject;
        try {
            tempJsonObject = (currentListViewStatus == 0) ? savedAllBeer.getJSONObject(selectedBeerID) : savedFavouriteBeers.getJSONObject(selectedBeerID);
            parseJSONObjectForSelectedBeer(tempJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSONObjectForSelectedBeer(JSONObject takenBeerJSONObject) throws JSONException {
        String beerInfoString = "";
        String imageURL = "";

        selectedBeerFavouriteState = Boolean.valueOf(takenBeerJSONObject.getString("favouriteBeer"));

        imageURL = String.valueOf(takenBeerJSONObject.getString("image_url"));

        beerInfoString = "ID : " + String.valueOf(takenBeerJSONObject.getString("id")) + "\n";
        beerInfoString += "Name : " + String.valueOf(takenBeerJSONObject.getString("name")) + "\n";
        beerInfoString += "Tagline :" + String.valueOf(takenBeerJSONObject.getString("tagline")) + "\n";
        beerInfoString += "First Brewed : " + String.valueOf(takenBeerJSONObject.getString("first_brewed")) + "\n";
        beerInfoString += "Description : " + String.valueOf(takenBeerJSONObject.getString("description")) + "\n";
        beerInfoString += "ABV :" + String.valueOf(takenBeerJSONObject.getString("abv")) + "\n";
        beerInfoString += "IBU" + String.valueOf(takenBeerJSONObject.getString("ibu")) + "\n";
        beerInfoString += "Target FG : " + String.valueOf(takenBeerJSONObject.getString("target_fg")) + "\n";
        beerInfoString += "Target OG : " + String.valueOf(takenBeerJSONObject.getString("target_og")) + "\n";
        beerInfoString += "EBC : " + String.valueOf(takenBeerJSONObject.getString("ebc")) + "\n";
        beerInfoString += "SRM : " + String.valueOf(takenBeerJSONObject.getString("srm")) + "\n";
        beerInfoString += "PH : " + String.valueOf(takenBeerJSONObject.getString("ph")) + "\n";
        beerInfoString += "Attenuation Level : " + String.valueOf(takenBeerJSONObject.getString("attenuation_level")) + "\n";

        final String finalBeerInfoString = beerInfoString;
        final String finalImageURL = imageURL;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                if (listener != null)
                    listener.newPageWithSelectedBeer(finalBeerInfoString, finalImageURL);
                listener.updateBeerFavouriteState();
            }
        });
    }

    void setListener(BWBeerModelClassListener listener) {
        BWBeerModalClass.listener = listener;
    }

    void martSelectedBeerFavouriteOrNot(){
        if (currentListViewStatus == 1) return;
        JSONObject jsonObjectToMarkFavourite = new JSONObject();
        try {
            jsonObjectToMarkFavourite = savedAllBeer.getJSONObject(selectedBeerID);
            Boolean selBeerFavouriteState;
            selBeerFavouriteState = !Boolean.valueOf(jsonObjectToMarkFavourite.getString("favouriteBeer"));
            selectedBeerFavouriteState = selBeerFavouriteState;
            jsonObjectToMarkFavourite.put("favouriteBeer",selectedBeerFavouriteState);
            savedAllBeer.put(selectedBeerID,jsonObjectToMarkFavourite);
            if(selectedBeerFavouriteState){
                selectedFavouriteBeerCount = 0;
                for (int i = 0; i < savedAllBeer.length(); i++){
                    jsonObjectToMarkFavourite = savedAllBeer.getJSONObject(i);
                    if (Boolean.valueOf(jsonObjectToMarkFavourite.getString("favouriteBeer"))){
                        savedFavouriteBeers.put(selectedFavouriteBeerCount,savedAllBeer.getJSONObject(i));
                        selectedFavouriteBeerCount++;
                    }
                }
            }
            Log.d(TAG, String.valueOf(String.valueOf(savedAllBeer.getJSONObject(selectedBeerID).getString("favouriteBeer"))));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                if (listener != null)
                    listener.updateBeerFavouriteState();
            }
        });
    }

    Boolean getBeerFavouriteState(){
        return selectedBeerFavouriteState;
    }

}
