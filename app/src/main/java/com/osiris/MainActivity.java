package com.osiris;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaBrowserCompat mediaBrowser;
    private Button playButton, pauseButton;
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

        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);

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

    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            Log.i(TAG, "In onChildrenloaded");
            Log.i(TAG, parentId);

            Log.i(TAG, "Size " + children.size());
            Log.i(TAG, children.get(0).getDescription().getTitle().toString());


            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                getOsirisMediaController().addQueueItem(mediaItem.getDescription());
            }

            getTransportControls().prepare();

            //MediaBrowserHelper.this.onChildrenLoaded(parentId, children);
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

    private MediaControllerCompat.TransportControls getTransportControls(){
        return getOsirisMediaController().getTransportControls();
    }

    private int getPlaybackState(){
        return getOsirisMediaController().getPlaybackState().getState();
    }

    private MediaControllerCompat getOsirisMediaController(){
        return MediaControllerCompat.getMediaController(MainActivity.this);
    }

    public void buildTransportControls(){
        Log.i(TAG, "In buildTransportControls");

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Play song? " + Boolean.toString(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING));
                if(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING){
                    getTransportControls().play();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Pause song? " + Boolean.toString(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING));

                if(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING){
                    getTransportControls().pause();
                }
            }
        });



    }

    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.i(TAG, "In onMetadataChanged");
            if(metadata == null){
                Log.i(TAG, "metadata is null");
                return;
            }
            Log.i(TAG, metadata.getDescription().getTitle().toString());
            if(metadata.getDescription().getMediaId() == null){
                Log.i(TAG, "Metadata media id is null");
            }
            Log.i(TAG, metadata.getDescription().getMediaId());
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.i(TAG, "In onPlaybackStateChanged");
        }

    };
}
