package com.osiris.ui;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.osiris.ui.common.PlaylistDetailedModel;
import com.osiris.ui.common.SongModel;

import java.util.List;

public interface LibraryFragmentListener {
    void playSongAt(int queueIndex);
    void addSongToQueue(SongModel song);
    void addPlaylistToQueue(PlaylistDetailedModel playlist);
    List<MediaSessionCompat.QueueItem> getQueue();
    MediaMetadataCompat getCurrentMediaMetadata();
}
