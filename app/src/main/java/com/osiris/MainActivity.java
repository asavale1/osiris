package com.osiris;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.osiris.server.MediaPlaybackService;
import com.osiris.constants.FragmentConstants;
import com.osiris.ui.CreatePlaylistFragment;
import com.osiris.ui.LibraryFragment;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.VerifyAccountFragment;
import com.osiris.ui.common.SongModel;
import com.osiris.utility.CacheManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerControllerListener, LibraryFragmentListener {

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

        String userId = CacheManager.getInstance(this).readString(getString(R.string.cache_user_id), "");
        if(userId.isEmpty()){
            replaceFragment(FragmentConstants.FRAGMENT_VERIFY_ACCOUNT);
        }else{
            replaceFragment(FragmentConstants.FRAGMENT_LIBRARY);
        }
    }

    public void replaceFragment(int fragmentType){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment;

        switch (fragmentType){
            case FragmentConstants.FRAGMENT_LIBRARY:
                fragment = new LibraryFragment();
                break;
            case FragmentConstants.FRAGMENT_CREATE_PLAYLIST:
                Log.i(TAG, "Create Playlist Fragment");
                fragment = new CreatePlaylistFragment();
                break;
            case FragmentConstants.FRAGMENT_VERIFY_ACCOUNT:
                fragment = new VerifyAccountFragment();
                break;
            default:
                fragment = null;
                break;
        }

        if(fragment != null){

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
        Log.i(TAG, "In onResume");
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

            Log.i(TAG, "In onChildrenloaded");

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
            Log.i(TAG, "In onMetadataChanged");
            if(metadata == null){
                Log.i(TAG, "metadata is null");
                return;
            }

            LibraryFragment libraryFragment = isLibraryFragmentVisible();
            if(libraryFragment != null){
                Log.i(TAG, "Library Fragment is not null");
                libraryFragment.onMetadataChanged(metadata);
            }

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.i(TAG, "In onPlaybackStateChanged");

            LibraryFragment libraryFragment = isLibraryFragmentVisible();
            if(libraryFragment != null){
                Log.i(TAG, "Library Fragment is not null");
                libraryFragment.onPlaybackStateChanged(state);
            }

        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();

            Log.i(TAG, "In onSessionDestroyed");
        }

    };

    /**
     * LibraryFragment callbacks for handling the library view
     */
    @Override
    public void playSongAt(int queueIndex){
        Bundle bundle = new Bundle();
        bundle.putInt("queueIndex", queueIndex);
        getOsirisMediaController().sendCommand("playSongAt", bundle, null);
    }



    @Override
    public void addSongToQueue(SongModel songModel){
        Log.i(TAG, "In addSongToQueue");

        Bundle bundle = new Bundle();
        bundle.putString("songModel", new Gson().toJson(songModel));

        getOsirisMediaController().sendCommand("addSongToQueue", bundle, null);


    }

    @Override
    public List<MediaSessionCompat.QueueItem> getQueue(){
        return getOsirisMediaController().getQueue();
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
    public void onStopSong(){
        if(getPlaybackState() != PlaybackStateCompat.STATE_STOPPED){
            getTransportControls().stop();
        }
    }

    @Override
    public void onSkipToNextSong(){
        Log.i(TAG, "In onSkipToNextSong");
        getTransportControls().skipToNext();
    }

    @Override
    public void onSkipToPreviousSong(){
        Log.i(TAG, "In onSkipToPreviousSong");
        getTransportControls().skipToPrevious();
    }

}
