package com.example.umutserifler.beerworld;

import android.content.Context;

/**
 * Created by umutserifler on 19.11.2017.
 */

public class BWBeerModalClassSingleton {
    private static BWBeerModalClassSingleton beerClassSingleton = null;

    private BWBeerModalClass beerClass;

    private BWBeerModalClassSingleton(Context context){
        beerClass = new BWBeerModalClass(context);
    }

    public static BWBeerModalClassSingleton getInstance(Context context) {
        if(beerClassSingleton == null) {
            beerClassSingleton = new BWBeerModalClassSingleton(context);
        }
        return beerClassSingleton;
    }

    public BWBeerModalClass getBeerClass(){
        return this.beerClass;
    }

    public void resetBWBeerModalClass(){
        beerClassSingleton = null;
    }


}
