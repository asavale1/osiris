package com.osiris.ui;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

public interface LibraryFragmentListener {
    List<MediaSessionCompat.QueueItem> getMediaItems();
}
