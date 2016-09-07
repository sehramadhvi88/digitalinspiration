package com.accenture.madhvisehra.digi;

import android.os.Environment;

import java.io.File;

/**
 * Created by dell on 23-08-2016.
 */
public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}