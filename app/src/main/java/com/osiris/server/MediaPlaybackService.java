package com.osiris.server;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mediaSession;
    private MusicLibrary musicLibrary;
    private MediaPlayerAdapter mediaPlayerAdapter;
    private final static String MY_MEDIA_ROOT_ID = "Osiris";
    private boolean mServiceInStartedState;

    private static final String TAG = MediaPlaybackService.class.getName();

    private MediaNotificationManager mediaNotificationManager;

    @Override
    public void onCreate(){
        super.onCreate();

        Log.i(TAG, "In onCreate");

        musicLibrary = new MusicLibrary();
        mediaPlayerAdapter = new MediaPlayerAdapter(new MediaPlaybackListener());


        mediaSession = new MediaSessionCompat(this, MediaPlaybackService.class.getSimpleName());
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setCallback(new MediaSessionCallback(MediaPlaybackService.this, mediaSession, mediaPlayerAdapter, musicLibrary));
        setSessionToken(mediaSession.getSessionToken());

        /*PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_NEXT);



        mediaSession.setPlaybackState(stateBuilder.build());*/



        Log.i(TAG, "Session Token: " + mediaSession.getSessionToken().describeContents());

        mediaNotificationManager = new MediaNotificationManager(this);



    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints){
        Log.i(TAG, "In onGetRoot");
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result){
        Log.i(TAG, "In onLoadChildren");

        result.sendResult(musicLibrary.getMediaItems());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy(){

        mediaPlayerAdapter.onStop();
        mediaSession.release();

    }

    /*public void setApiRequestUrl(String apiRequestUrl){
        this.apiRequestUrl = apiRequestUrl;
    }*/

    public class MediaPlaybackListener {
        private final ServiceManager serviceManager;

        MediaPlaybackListener() {
            serviceManager = new ServiceManager();
        }

        public void onPlaybackStateChanged(PlaybackStateCompat state){
            Log.i(TAG, "In onPlaybackStateChanged");
            mediaSession.setPlaybackState(state);

            // Manage the started state of this service.
            Log.i(TAG, "In onPlaybackStateChanged: " + state.getState());
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    Log.i(TAG, "In statePlaying");
                    serviceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    Log.i(TAG, "In statePaused");
                    serviceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    Log.i(TAG, "In stateStopped");
                    serviceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Log.i(TAG, "In moveServiceToStartedState");
                Log.i(TAG, "Session Token: " + getSessionToken().describeContents());

                Notification notification =
                        mediaNotificationManager.getNotification(
                                mediaPlayerAdapter.getCurrentMediaMetadata(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            MediaPlaybackService.this,
                            new Intent(MediaPlaybackService.this, MediaPlaybackService.class));
                    mServiceInStartedState = true;
                }

                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                Log.i(TAG, "In updateNotificationForPause");
                stopForeground(false);
                Notification notification =
                        mediaNotificationManager.getNotification(
                                mediaPlayerAdapter.getCurrentMediaMetadata(), state, getSessionToken());
                mediaNotificationManager.getNotificationManager()
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                Log.i(TAG, "In moveServiceOutOfStartedState");
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }
    }
}
