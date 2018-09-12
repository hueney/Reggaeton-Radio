package com.example.reggaetonradio;

import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import java.net.URI;
import java.net.URL;

/**
 * Created by בן גולן on 29/08/2018.
 */

public class Station  {

    private String imageUri;
    private String name;
    private String song;
    private String singer;
    private String stationId;

    Station(String mImageUri, String mName, String mSinger, String mSong, String mStationId){
        imageUri = mImageUri;
        name = mName;
        song = mSong;
        singer = mSinger;
        stationId = mStationId;
    }

    public String getImageUri() {return imageUri; }
    public String getName(){return name;}
    public  String getSinger() {return singer;}
    public  String getSong() {return song;}
    public  String getStationId() {return stationId;}

}
