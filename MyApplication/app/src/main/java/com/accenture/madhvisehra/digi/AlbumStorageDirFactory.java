package com.accenture.madhvisehra.digi;

import java.io.File;

/**
 * Created by dell on 23-08-2016.
 */
abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
