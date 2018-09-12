package com.example.reggaetonradio;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Created by בן גולן on 29/08/2018.
 */

public class StationAdapter extends ArrayAdapter<Station> {


    public StationAdapter(Context context, List<Station> stations) {
        super(context, 0,stations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView==null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.station_list, parent, false);
        }
        Station currentStation = getItem(position);

        String StationImage = currentStation.getImageUri();
        String StationName = currentStation.getName();

        ImageView imageView = (ImageView) listView.findViewById(R.id.station_image);
        TextView textView = (TextView) listView.findViewById(R.id.station_name);

        textView.setText(StationName);

        Picasso.with(getContext()).load(Uri.parse(StationImage)).into(imageView);

        return listView;

    }
}
