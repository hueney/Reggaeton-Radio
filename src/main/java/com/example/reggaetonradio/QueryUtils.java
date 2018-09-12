package com.example.reggaetonradio;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by בן גולן on 29/08/2018.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    QueryUtils() {}


    public static ArrayList<Station> fetchStationData(String requestUrl) throws IOException, SAXException, ParserConfigurationException {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        InputStream inputStream = null;
        try {
            makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream!!!!!!!!!!********", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Station> stations = extractStations(requestUrl);

        // Return the {@link Event}
        return stations;
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL!!!!!!!!!******* ", e);
        }
        return url;
    }


    private static void makeHttpRequest(URL url) throws IOException {

        // If the URL is null, then return early.
        if (url == null) {
            return ;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the station Xml results!!!!!!!!*******", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    public static ArrayList<Station> extractStations(String stationUri) throws ParserConfigurationException, IOException, SAXException {

        int counter=0;
        String stationName;
        String logo;
        String song;
        String singer;
        String[] parts;
        String stationId;

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Station> stations = new ArrayList<>();

        try{

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stationUri);

        Element element=doc.getDocumentElement();
        element.normalize();

        NodeList nList = doc.getElementsByTagName("station");

        for (int i=0; i<nList.getLength()&& counter<30; i++) {

            Node node = nList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) node;

                stationName = eElement.getAttribute("name");

                logo = eElement.getAttribute("logo");

                song = eElement.getAttribute("ct");

                stationId = eElement.getAttribute("id");



                if (logo.contains("http") && song.contains("-")) {

                    parts = eElement.getAttribute("ct").split("-");
                    singer = parts[0];
                    song = parts[1];

                    stations.add(new Station(logo, stationName, singer, song, stationId));
                    counter++;
                }
            }

        }

    } catch (Exception e) {
            Log.e(LOG_TAG,"Problem with the XML DOM!!!!!!!!!*******");
        }

        return stations;

}

}


