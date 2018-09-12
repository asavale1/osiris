package com.osiris.ui;

import android.support.v4.media.session.MediaSessionCompat;

import com.osiris.ui.common.SongModel;

import java.util.List;

public interface LibraryFragmentListener {
    void buildQueue(String apiRequestUrl, int startIndex);
    void playSongAt(int queueIndex);
    void addSongToQueue(SongModel songModel);
    List<MediaSessionCompat.QueueItem> getQueue();
}
