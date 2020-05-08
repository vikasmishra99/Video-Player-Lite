package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VideoLoader {

    private final static String TAG = "VideoLoader";

    private final Context context;
    private ExecutorService executorService;

    public VideoLoader(Context context) {
        this.context = context;
    }

    public void loadDeviceVideos(final VideoLoadListener listener) {
        getExecutorService().execute(new VideoLoadRunnable(listener, context));
    }

    public void abortLoadVideos() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }


    private static class VideoLoadRunnable implements Runnable {

        private final VideoLoadListener listener;
        private final Context context;
        private final Handler handler = new Handler(Looper.getMainLooper());

        private final String[] projection = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DATE_TAKEN
        };


        public VideoLoadRunnable(VideoLoadListener listener, Context context) {
            this.listener = listener;
            this.context = context;
        }

        @Override
        public void run() {
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            if (cursor == null) {
                handler.post(() -> listener.onFailed(new NullPointerException()));
                return;
            }

            final ArrayList<VideoItemModel> videoItemModels = new ArrayList<>(cursor.getCount());

            if (cursor.moveToLast()) {
                do {
                    String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                    if (path == null) continue;

                    Log.d(TAG, "pick video from device path = " + path);


                    String duration = AppUtils.makeShortTimeString(context, cursor.getLong(cursor.getColumnIndex(projection[1])) / 1000);

                    Log.d(TAG, "pick video from device duration = " + duration);

                    String title = cursor.getString(cursor.getColumnIndex(projection[2]));

                    String resolution = cursor.getString(cursor.getColumnIndex(projection[3]));
                    //This will give a LONG
                    String date_added = cursor.getString(cursor.getColumnIndex(projection[4]));
                    date_added = convertDate(date_added, "dd/MM/yyyy hh:mm:ss");
                    Log.e(TAG, "date" + date_added);
                    File file = new File(path);

                    if (file.exists()) {
                        long fileSize = file.length();
                        String folderName = "Unknow Folder";
                        File _parentFile = file.getParentFile();
                        if (_parentFile.exists()) {
                            folderName = _parentFile.getName();
                        }
                        videoItemModels.add(new VideoItemModel(
                                title,
                                path,
                                duration,
                                folderName, AppUtils.getStringSizeLengthFile(fileSize), resolution, fileSize, date_added));
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            handler.post(() -> listener.onVideoLoaded(videoItemModels));
        }
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {

        String convertedDate = "Unknown";
        try {
            convertedDate = DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDate;

    }


}
