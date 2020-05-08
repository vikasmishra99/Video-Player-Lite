package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.storageProcess;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter.VideoAdapter;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RenameVideo extends DialogFragment {

    static VideoItemModel videoItemModel;
    static Context context;
    static VideoAdapter videoAdapter;
    public static RenameVideo newInstance(Context context, VideoAdapter videoAdapter, VideoItemModel videoItemModel) {
        RenameVideo dialog = new RenameVideo();
        RenameVideo.videoItemModel = videoItemModel;
        RenameVideo.context = context;
        RenameVideo.videoAdapter = videoAdapter;
        return dialog;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(context).positiveText(getString(R.string.rename_video)).negativeText(getString(R.string.confirm_cancel))
                .title(getString(R.string.rename_video)).input(getString(R.string.enter_new_name), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(isNewNamePossible(input.toString())){
                            if(renameMedia(context,input.toString())){
                                videoAdapter.updateData(videoAdapter.getVideoItemModels());
                            }
                        }else {
                            Toast.makeText(context,context.getString(R.string.fileNameExist),Toast.LENGTH_LONG).show();
                        }

                    }
                }).build();
    }
    public static boolean renameMedia(Context context, String newName) {
        boolean success = false;
        Uri external = MediaStore.Files.getContentUri("external");
        try {
            File from = new File(videoItemModel.getPath());
            if(from.exists()) {
                File dir = from.getParentFile();
                String fileEx = AppUtils.getFileExtension(videoItemModel.getPath());
                File to = new File(dir, newName + "." + fileEx);
                if (success = StorageHelper.moveFile(context, from, to)) {
                    context.getContentResolver().delete(external,
                            MediaStore.MediaColumns.DATA + "=?", new String[]{from.getPath()});

                    scanFile(context, new String[]{to.getAbsolutePath()});
                    videoItemModel.setVideoTitle(newName);
                    videoItemModel.setPath(to.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    public static void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(), path, null, null);
    }
    private boolean isNewNamePossible(String newName){
        if(videoItemModel == null) return false;
        String fileEx = AppUtils.getFileExtension(videoItemModel.getPath());
        if(AppGlobalVar.getInstance().folderItem == null || AppGlobalVar.getInstance().folderItem.getVideoItemModels() == null)
            return false;
        for(VideoItemModel videoItemModel : AppGlobalVar.getInstance().folderItem.getVideoItemModels()){
            if(newName.equals(videoItemModel.getVideoTitle()) && fileEx.equals(AppUtils.getFileExtension(videoItemModel.getPath())))
                return false;
        }
        return true;
    }
}