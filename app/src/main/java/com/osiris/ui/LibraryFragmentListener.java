package com.osiris.ui;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.ResultReceiver;

import com.osiris.model.PlaylistDetailedModel;
import com.osiris.model.SongModel;

import java.util.List;

public interface LibraryFragmentListener {
    void playSongAt(int queueIndex);
    void addSongToQueue(SongModel song);
    void addPlaylistToQueue(PlaylistDetailedModel playlist);
    void clearQueue(ResultReceiver callback);
    List<MediaSessionCompat.QueueItem> getQueue();
    MediaMetadataCompat getCurrentMediaMetadata();
}
