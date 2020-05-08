package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.commonsware.cwac.provider.LegacyCompatCursorWrapper;

import java.io.File;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

public class VideoProvider extends FileProvider {

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return(new LegacyCompatCursorWrapper(Objects.requireNonNull(super.query(uri, projection, selection, selectionArgs, sortOrder))));
    }

    public static Uri getUri(Context context, File file) {
        return getUriForFile(context,  "com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.provider", file);
    }
}
