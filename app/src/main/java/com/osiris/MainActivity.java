package com.osiris;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.osiris.server.MediaPlaybackService;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.PlayerFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerControllerListener {

    private MediaBrowserCompat mediaBrowser;
    private FragmentManager fragmentManager;
    private MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback;

    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "In OnCreate");

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MediaPlaybackService.class), connectionCallback, null);
        mediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();

        fragmentManager = getSupportFragmentManager();


    }

    public void replaceFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragmentManager.getFragments().size() == 0){
            fragmentTransaction.add(R.id.fragment_container, new PlayerFragment());
        }

        fragmentTransaction.commit();

        Log.i(TAG, "In replaceFragment");
        Log.i(TAG, "Size: " + fragmentManager.getFragments().size());
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "In onStart");
        mediaBrowser.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(MediaControllerCompat.getMediaController(MainActivity.this) != null){
            MediaControllerCompat.getMediaController(MainActivity.this).unregisterCallback(controllerCallback);
        }
        mediaBrowser.disconnect();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "In onResume");
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPlaySong() {
        Log.i(TAG, "Play song? " + Boolean.toString(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING));
        if(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING){
            getTransportControls().play();
        }
    }

    @Override
    public void onPauseSong(){
        Log.i(TAG, "Pause song? " + Boolean.toString(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING));
        if(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING){
            getTransportControls().pause();
        }
    }

    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            Log.i(TAG, "In onChildrenloaded");
            Log.i(TAG, parentId);

            if(!children.isEmpty()){
                Log.i(TAG, "Size " + children.size());
                Log.i(TAG, children.get(0).getDescription().getTitle().toString());


                for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                    getOsirisMediaController().addQueueItem(mediaItem.getDescription());
                }

                getTransportControls().prepare();
            }
        }
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            Log.i(TAG, "In onConnected");
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

            try {

                MediaControllerCompat controller = new MediaControllerCompat(MainActivity.this, token);

                MediaControllerCompat.setMediaController(MainActivity.this, controller);

                MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(MainActivity.this);

                mediaController.registerCallback(controllerCallback);

                controllerCallback.onMetadataChanged(mediaController.getMetadata());

                mediaBrowser.subscribe(mediaBrowser.getRoot(), mediaBrowserSubscriptionCallback);

                buildTransportControls();

                replaceFragment();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended() {
            Log.i(TAG, "In onConnSuspended");
        }

        @Override
        public void onConnectionFailed() {
            Log.i(TAG, "In onConnFailed");
        }

    };

    public MediaControllerCompat.TransportControls getTransportControls(){
        return getOsirisMediaController().getTransportControls();
    }

    public int getPlaybackState(){
        return getOsirisMediaController().getPlaybackState().getState();
    }

    public MediaControllerCompat getOsirisMediaController(){
        return MediaControllerCompat.getMediaController(MainActivity.this);
    }

    public void buildTransportControls(){
        Log.i(TAG, "In buildTransportControls");

        /*playButton.setOnClickListener(controlsClickListener);
        pauseButton.setOnClickListener(controlsClickListener);
        previousButton.setOnClickListener(controlsClickListener);
        nextButton.setOnClickListener(controlsClickListener);
        refreshButton.setOnClickListener(controlsClickListener);
        stopButton.setOnClickListener(controlsClickListener);*/

    }

    /*View.OnClickListener controlsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "In onClick");
            switch (v.getId()){
                case R.id.play_button:
                    Log.i(TAG, "Play song? " + Boolean.toString(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING));
                    if(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING){
                        getTransportControls().play();
                    }
                    break;
                case R.id.pause_button:
                    Log.i(TAG, "Pause song? " + Boolean.toString(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING));
                    if(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING){
                        getTransportControls().pause();
                    }
                    break;
                case R.id.previous_button:
                    Log.i(TAG, "Play previous");
                    getTransportControls().skipToPrevious();
                    break;
                case R.id.next_button:
                    Log.i(TAG, "Play next");
                    getTransportControls().skipToNext();
                    break;
                case R.id.stop_button:
                    Log.i(TAG, "Stop song");
                    getTransportControls().stop();
                    break;
                case R.id.refresh_button:
                    getOsirisMediaController().sendCommand("TestCommand", null, null);
                    break;
            }

        }
    };*/




    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.i(TAG, "In onMetadataChanged");
            if(metadata == null){
                Log.i(TAG, "metadata is null");
                return;
            }


            //songTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.i(TAG, "In onPlaybackStateChanged");
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();

            Log.i(TAG, "In onSessionDestroyed");
        }

    };
}
