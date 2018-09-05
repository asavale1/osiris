package com.osiris.ui;

import com.osiris.ui.common.SongModel;

public interface LibraryFragmentListener {
    void buildQueue(String apiRequestUrl, int startIndex);
    void addSongToQueue(SongModel songModel);
}
