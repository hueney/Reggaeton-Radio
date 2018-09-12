package com.example.reggaetonradio;

import android.content.Context;
import android.content.Loader;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RadioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<ArrayList<Station>> {

    private static final String Station_REQUEST_URL = "http://api.shoutcast.com/legacy/genresearch?f=json&k=U5F3uwzkJF6JW9Pf&genre=reggaeton&limit=80";
    private static final String tuneRadio = "http://yp.shoutcast.com/<base>?id=";
    private ListView stationListView;
    private StationAdapter stationAdapter;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Timer timer = new Timer();

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_radio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(1, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<ArrayList<Station>> onCreateLoader(int id, Bundle args) {

        StationLoader stationLoader = new StationLoader(this, Station_REQUEST_URL);

        return stationLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Station>> loader, ArrayList<Station> stations) {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        if (stations != null && stations.size() > 0) {
            updateUi(stations);
        } else {
            stationListView = (ListView) findViewById(R.id.list);

            TextView emptyViewText = (TextView) findViewById(R.id.empty_view);

            stationListView.setEmptyView(emptyViewText);

            emptyViewText.setText("No stations found");


        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Station>> loader) {

    }


    private void updateUi(ArrayList<Station> stations) {
        stationListView = (ListView) findViewById(R.id.list);
        stationAdapter = new StationAdapter(this, stations);
        stationListView.setAdapter(stationAdapter);


        stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                TextView stationName = (TextView) findViewById(R.id.station_name) ;
                ImageView stationImage = (ImageView) findViewById(R.id.imageView) ;
                TextView song = (TextView) findViewById(R.id.song) ;
                TextView singer = (TextView) findViewById(R.id.singer) ;

                final Station currentStation = stationAdapter.getItem(position);

                stationName.setText(currentStation.getName());
                Picasso.with(getBaseContext()).load(Uri.parse(currentStation.getImageUri())).into(stationImage);
                singer.setText(currentStation.getSinger());
                song.setText(currentStation.getSong());

                audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

                releaseMediaPlayer();

                int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start playback
                    MediaPlayerLoad mediaPlayerLoad = new MediaPlayerLoad("http://yp.shoutcast" +
                            ".com/" + "/sbin/tunein-station.m3u" + "?id=" + currentStation.getStationId());
                    mediaPlayerLoad.execute("");
                }


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);



    }
        });
    }



    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            // Abandon audio focus when playback complete
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }

    @Override
    public void onDestroy(){
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        super.onDestroy();
    }

    public void playButton(View view){
        if (mediaPlayer.isPlaying() == false)
        mediaPlayer.start();
    }

    public void pauseButton(View view){
        mediaPlayer.pause();
    }


    private class MediaPlayerLoad extends AsyncTask<String, Void, MediaPlayer> {

        private String url;

        MediaPlayerLoad(String murl){
            url = murl;
        }

        @Override
        protected MediaPlayer doInBackground(String... params) {
            Uri songUri = Uri.parse(url);
            mediaPlayer = MediaPlayer.create(getBaseContext(), songUri);
            return mediaPlayer;
        }

        @Override
        protected void onPostExecute(MediaPlayer result) {
            mediaPlayer.start();
        }

        @Override
        protected void onPreExecute() {
            releaseMediaPlayer();
        }
    }

}