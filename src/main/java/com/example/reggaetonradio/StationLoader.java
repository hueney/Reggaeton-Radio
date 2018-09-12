package com.example.reggaetonradio;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by בן גולן on 29/08/2018.
 */

public class StationLoader extends AsyncTaskLoader<ArrayList<Station>> {
    private String mUrl;

    public StationLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public ArrayList<Station> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        ArrayList<Station> stations = null;
        try {
            stations = QueryUtils.fetchStationData(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return stations;
    }
}