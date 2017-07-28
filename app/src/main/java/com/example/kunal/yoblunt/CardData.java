package com.example.kunal.yoblunt;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Kunal on 28-07-2017.
 */

public class CardData implements Comparable<CardData> {


    public String title,tag,thumbnail;
    public double latitude,longitude,distance;

    public CardData(){

        //empty constructor
    }

    public CardData(String title,String tag,String thumbnail,double latitude,double longitude,double distance){
        this.title=title;
        this.tag=tag;
        this.thumbnail=thumbnail;
        this.latitude=latitude;
        this.longitude=longitude;
        this.distance=distance;
    }

    public double getDistance(){
        return distance;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull CardData o) {
        return 0;
    }
}
