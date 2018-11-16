package com.osiris;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.osiris.api.GetUser;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.BundleConstants;
import com.osiris.constants.MediaConstants;
import com.osiris.model.AlbumDetailedModel;
import com.osiris.server.MediaPlaybackService;
import com.osiris.constants.FragmentConstants;
import com.osiris.ui.library.CreatePlaylistFragment;
import com.osiris.ui.LibraryFragment;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.VerifyAccountFragment;
import com.osiris.model.PlaylistDetailedModel;
import com.osiris.model.SongModel;
import com.osiris.ui.library.UserSettingsFragment;
import com.osiris.ui.library.ViewAlbumFragment;
import com.osiris.ui.library.ViewPlaylistFragment;
import com.osiris.utility.CacheManager;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements PlayerControllerListener, LibraryFragmentListener {

    private MediaBrowserCompat mediaBrowser;
    private FragmentManager fragmentManager;
    private MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback;

    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MediaPlaybackService.class), connectionCallback, null);
        mediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();

        fragmentManager = getSupportFragmentManager();

        String userId = CacheManager.getInstance(this).readString(getString(R.string.cache_user_id), "");
        if(userId.isEmpty()){
            replaceFragment(FragmentConstants.FRAGMENT_VERIFY_ACCOUNT, null);
        }else{
            new GetUser(userId, new RESTCallbackListener() {
                @Override
                public void onComplete(RESTClient.RESTResponse response) {
                    if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                        replaceFragment(FragmentConstants.FRAGMENT_LIBRARY, null);
                    }else{
                        replaceFragment(FragmentConstants.FRAGMENT_VERIFY_ACCOUNT, null);
                    }
                }
            }).execute();
        }
    }

    public void replaceFragment(int fragmentType, Bundle bundle){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment;

        switch (fragmentType){
            case FragmentConstants.FRAGMENT_LIBRARY:
                fragment = new LibraryFragment();
                break;
            case FragmentConstants.FRAGMENT_CREATE_PLAYLIST:
                fragment = new CreatePlaylistFragment();
                break;
            case FragmentConstants.FRAGMENT_VERIFY_ACCOUNT:
                fragment = new VerifyAccountFragment();
                break;
            case FragmentConstants.FRAGMENT_VIEW_PLAYLIST:
                fragment = new ViewPlaylistFragment();
                break;
            case FragmentConstants.FRAGMENT_VIEW_ALBUM:
                fragment = new ViewAlbumFragment();
                break;
            case FragmentConstants.FRAGMENT_USER_SETTINGS:
                fragment = new UserSettingsFragment();
                break;
            default:
                fragment = null;
                break;
        }

        if(fragment != null){

            if(bundle != null) {
                fragment.setArguments(bundle);
            }

            if(fragmentManager.getFragments().size() == 0){
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }else{
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if(currentFragment instanceof  LibraryFragment){
                    fragmentTransaction.addToBackStack("LibraryToPlayer");
                }
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }

            fragmentTransaction.commit();
        }

    }



    @Override
    public void onStart(){
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    public void onStop(){
        Log.i(TAG, "In onStop");
        super.onStop();
        if(MediaControllerCompat.getMediaController(MainActivity.this) != null){
            Log.i(TAG, "Disconnect media controller");
            MediaControllerCompat.getMediaController(MainActivity.this).unregisterCallback(controllerCallback);
        }
        if(mediaBrowser != null && mediaBrowser.isConnected()) {
            Log.i(TAG, "Disconnect media browser");
            mediaBrowser.disconnect();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    /**
     * Media Connection and Browser callbacks
     */
    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            if(!children.isEmpty()){
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

                Log.i(TAG, "Connection setup");

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
        if(getOsirisMediaController().getPlaybackState() != null)
            return getOsirisMediaController().getPlaybackState().getState();
        return -1;
    }

    private MediaControllerCompat getOsirisMediaController(){
        return MediaControllerCompat.getMediaController(MainActivity.this);
    }

    private LibraryFragment isLibraryFragmentVisible(){
        Fragment displayFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(displayFragment instanceof LibraryFragment){
            return (LibraryFragment) displayFragment;
        }
        return null;
    }

    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if(metadata == null){
                return;
            }

            LibraryFragment libraryFragment = isLibraryFragmentVisible();
            if(libraryFragment != null){
                libraryFragment.onMetadataChanged(metadata);
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {

            LibraryFragment libraryFragment = isLibraryFragmentVisible();
            if(libraryFragment != null){
                libraryFragment.onPlaybackStateChanged(state);
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

    };

    /**
     * LibraryFragment callbacks for handling the library view
     */
    @Override
    public void playSongAt(int queueIndex){
        Log.i(TAG, "playSongAt");
        Bundle bundle = new Bundle();
        bundle.putInt(BundleConstants.QUEUE_INDEX, queueIndex);
        getOsirisMediaController().sendCommand(MediaConstants.COMMAND_PLAY_SONG_AT, bundle, null);
    }

    @Override
    public void addSongToQueue(SongModel songModel){
        Log.i(TAG, "Add song to queue");
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.SONG_MODEL, new Gson().toJson(songModel));
        getOsirisMediaController().sendCommand(MediaConstants.COMMAND_ADD_SONG_TO_QUEUE, bundle, null);
    }

    @Override
    public void addPlaylistToQueue(PlaylistDetailedModel playlist){
        Log.i(TAG, "Add playlist to queue");
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.PLAYLIST_MODEL, new Gson().toJson(playlist));
        getOsirisMediaController().sendCommand(MediaConstants.COMMAND_ADD_PLAYLIST_TO_QUEUE, bundle, null);
    }

    @Override
    public void addAlbumToQueue(AlbumDetailedModel album){
        Log.i(TAG, "Add album to queue");
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.ALBUM_MODEL, new Gson().toJson(album));
        getOsirisMediaController().sendCommand(MediaConstants.COMMAND_ADD_ALBUM_TO_QUEUE, bundle, null);
    }

    @Override
    public List<MediaSessionCompat.QueueItem> getQueue(){
        Log.i(TAG, "Get queue");
        if(getOsirisMediaController() != null) {
            return getOsirisMediaController().getQueue();
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public MediaMetadataCompat getCurrentMediaMetadata(){
        Log.i(TAG, "Get current media metadata");
        return getOsirisMediaController().getMetadata();
    }

    @Override
    public void clearQueue(ResultReceiver callback){
        Log.i(TAG, "Clear queue");
        getOsirisMediaController().sendCommand(MediaConstants.COMMAND_CLEAR_QUEUE, null, callback);
    }


    /**
     * PlayerFragment callbacks for handling the media player
     */

    @Override
    public void onPlayPauseSong() {
        if(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING){
            getTransportControls().play();
        }else if(getPlaybackState() != -1){
            getTransportControls().pause();
        }
    }

    @Override
    public void onPlaySong(){
        if(getPlaybackState() != PlaybackStateCompat.STATE_PLAYING){
            getTransportControls().play();
        }
    }

    @Override
    public void onPauseSong(){
        if(getPlaybackState() == PlaybackStateCompat.STATE_PLAYING){
            getTransportControls().pause();
        }
    }

    @Override
    public void onStopSong(){
        if(getPlaybackState() != PlaybackStateCompat.STATE_STOPPED){
            getTransportControls().stop();
        }
    }

    @Override
    public void onSkipToNextSong(){
        getTransportControls().skipToNext();
    }

    @Override
    public void onSkipToPreviousSong(){
        getTransportControls().skipToPrevious();
    }

    @Override
    public boolean isMediaPlaying(){
        return (getPlaybackState() == PlaybackStateCompat.STATE_PLAYING);
    }

}
