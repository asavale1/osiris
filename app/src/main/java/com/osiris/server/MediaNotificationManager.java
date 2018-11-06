package com.osiris.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.osiris.MainActivity;
import com.osiris.R;

class MediaNotificationManager {

    private static final String TAG = MediaNotificationManager.class.getName();

    private MediaPlaybackService mediaPlaybackService;
    private NotificationManager notificationManager;
    private NotificationCompat.Action mPlayAction;
    private NotificationCompat.Action mPauseAction;
    private NotificationCompat.Action mNextAction;
    private NotificationCompat.Action mPrevAction;

    private static final String CHANNEL_ID = "com.osiris.mediaplayer.channel";
    private static final int REQUEST_CODE = 420;
    static final int NOTIFICATION_ID = 421;


    MediaNotificationManager(MediaPlaybackService mediaPlaybackService){
        this.mediaPlaybackService = mediaPlaybackService;

        notificationManager = (NotificationManager) mediaPlaybackService.getSystemService(Context.NOTIFICATION_SERVICE);

        mPlayAction =
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_play,
                        "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mediaPlaybackService,
                                PlaybackStateCompat.ACTION_PLAY));
        mPauseAction =
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_pause,
                        "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mediaPlaybackService,
                                PlaybackStateCompat.ACTION_PAUSE));
        mNextAction =
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_next,
                        "Next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mediaPlaybackService,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        mPrevAction =
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_previous,
                        "Previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mediaPlaybackService,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        notificationManager.cancelAll();
    }

    Notification getNotification(MediaMetadataCompat metadata, PlaybackStateCompat state,
                                 MediaSessionCompat.Token token){

        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        MediaDescriptionCompat mediaDescription = metadata.getDescription();
        NotificationCompat.Builder builder = buildNotification(state, token, isPlaying, mediaDescription);
        return builder.build();
    }

    NotificationManager getNotificationManager() {
        return notificationManager;
    }


    private NotificationCompat.Builder buildNotification(@NonNull  PlaybackStateCompat state, MediaSessionCompat.Token token,
                                                         boolean isPlaying, MediaDescriptionCompat mediaDescription){
        Log.i(TAG, "In buildNotification");
        if(isAndroidOOrHigher()){
            createChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mediaPlaybackService, CHANNEL_ID);
        builder.setColor(ContextCompat.getColor(mediaPlaybackService, R.color.colorPrimaryDark))
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentIntent(createContentIntent())
                .setContentTitle(mediaDescription.getTitle())
                .setContentText(mediaDescription.getSubtitle())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mediaPlaybackService, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            builder.addAction(mPrevAction);
        }

        Log.i(TAG, "Get Actions: " + state.getActions());
        Log.i(TAG, "PlaybackStateCompat.SKIP_NEXT: " + (PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        Log.i(TAG, "" + (state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        Log.i(TAG, "PlaybackStateCompat.SKIP_PREVIOUS: " + PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        Log.i(TAG, "" + (state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        builder.addAction(isPlaying ? mPauseAction : mPlayAction);

        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            builder.addAction(mNextAction);
        }

        builder.setStyle(
                new MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        mediaPlaybackService,
                                        PlaybackStateCompat.ACTION_STOP)));

        return builder;
    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel(){
        if(notificationManager.getNotificationChannel(CHANNEL_ID) == null){
            // The user-visible name of the channel.
            CharSequence name = "OsirisMediaSession";
            // The user-visible description of the channel.
            String description = "MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mediaPlaybackService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mediaPlaybackService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
