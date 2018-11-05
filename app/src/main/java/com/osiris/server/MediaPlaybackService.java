package com.osiris.server;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

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

        musicLibrary = new MusicLibrary();
        mediaPlayerAdapter = new MediaPlayerAdapter(new MediaPlaybackListener());


        mediaSession = new MediaSessionCompat(this, MediaPlaybackService.class.getSimpleName());
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setCallback(new MediaSessionCallback(mediaSession, mediaPlayerAdapter, musicLibrary));
        setSessionToken(mediaSession.getSessionToken());

        mediaNotificationManager = new MediaNotificationManager(this);

    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints){
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result){
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

    class MediaPlaybackListener extends PlaybackInfoListener {
        private final ServiceManager serviceManager;

        MediaPlaybackListener() {
            serviceManager = new ServiceManager();
        }

        void onPlaybackStateChanged(PlaybackStateCompat state){
            mediaSession.setPlaybackState(state);

            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    serviceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    serviceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    serviceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        @Override
        public void onPlaybackCompleted() {
            super.onPlaybackCompleted();
            mediaSession.getController().getTransportControls().skipToNext();
            mediaSession.getController().getTransportControls().play();
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {

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
                stopForeground(false);
                Notification notification =
                        mediaNotificationManager.getNotification(
                                mediaPlayerAdapter.getCurrentMediaMetadata(), state, getSessionToken());
                mediaNotificationManager.getNotificationManager()
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }
    }
}
