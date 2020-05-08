package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment.FolderListFrag;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment.MyFragment;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.permission.PermissionCallback;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.videoService.VideoService;

import java.util.ArrayList;


public abstract class AppBaseActivity extends AppCompatActivity{
    public static String ADMOB_APP_ID = "ca-app-pub-8259955773564722~6671352691",
            ADMOB_BANNER_ID = "ca-app-pub-8259955773564722/4363386573",
            ADMOB_INTERESTITIAL_ID = "ca-app-pub-8259955773564722/2584235196";

    AppGlobalVar mAppGlobalVar = AppGlobalVar.getInstance();
    protected static final int PERMISSION_REQUEST_CODE = 888888888;
    boolean isNeedResumePlay = false;
    MyFragment currentFragment = new FolderListFrag();

    //protected CastContext castContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme = PreferencesUtility.getInstance(this).getThemeSettings();
        if (theme < 0 || theme > 20) theme = 0;
        if (theme == 0) {
            setTheme(R.style.AppThemeLightBasic_theme0);

        } else if (theme == 1) {
            setTheme(R.style.AppThemeLightBasic_theme1);

        }
        if (theme == 2) {
            setTheme(R.style.AppThemeLightBasic_theme2);

        }
        if (theme == 3) {
            setTheme(R.style.AppThemeLightBasic_theme3);

        }
        if (theme == 4) {
            setTheme(R.style.AppThemeLightBasic_theme4);

        }
        if (theme == 5) {
            setTheme(R.style.AppThemeLightBasic_theme5);

        }
        if (theme == 6) {
            setTheme(R.style.AppThemeLightBasic_theme6);

        }
        if (theme == 7) {
            setTheme(R.style.AppThemeLightBasic_theme7);

        }
        if (theme == 8) {
            setTheme(R.style.AppThemeLightBasic_theme8);

        }
        if (theme == 9) {
            setTheme(R.style.AppThemeLightBasic_theme9);

        }
        if (theme == 10) {
            setTheme(R.style.AppThemeLightBasic_theme10);

        }
        if (theme == 11) {
            setTheme(R.style.AppThemeLightBasic_theme11);

        }
        if (theme == 12) {
            setTheme(R.style.AppThemeLightBasic_theme12);

        }
        if (theme == 13) {
            setTheme(R.style.AppThemeLightBasic_theme13);

        }
        if (theme == 14) {
            setTheme(R.style.AppThemeLightBasic_theme14);

        }
        if (theme == 15) {
            setTheme(R.style.AppThemeLightBasic_theme15);

        }
        if (theme == 16) {
            setTheme(R.style.AppThemeLightBasic_theme16);

        }
        if (theme == 17) {
            setTheme(R.style.AppThemeLightBasic_theme17);

        }
        if (theme == 18) {
            setTheme(R.style.AppThemeLightBasic_theme19);

        }
        if (theme == 19) {
            setTheme(R.style.AppThemeLightBasic_theme19);

        }
        if (theme == 20) {
            setTheme(R.style.AppThemeLightBasic_theme20);

        }
        super.onCreate(savedInstanceState);
        inItService();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppGlobalVar.getInstance().videoService != null)
            unbindService(videoServiceConnection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected ServiceConnection videoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VideoService.VideoBinder binder = (VideoService.VideoBinder) service;
            AppGlobalVar.getInstance().videoService = binder.getService();

            if (isNeedResumePlay) startPopupMode();
            if (mAppGlobalVar.isOpenFromIntent) {
                mAppGlobalVar.isOpenFromIntent = false;
                mAppGlobalVar.videoService.playVideo(0, false);
                showFloatingView(AppBaseActivity.this, true);
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnection = false;
        }
    };
    boolean isConnection = false;
    protected static Intent _playIntent;

    public void inItService() {
        _playIntent = new Intent(this, VideoService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(_playIntent);
            } else {
                startService(_playIntent);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(_playIntent);
//        } else {
//            startService(_playIntent);
//        }
        //startService(_playIntent);
        bindService(_playIntent, videoServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startPopupMode() {
        if (_playIntent != null) {
            AppGlobalVar.getInstance().videoService.playVideo(mAppGlobalVar.seekPosition, true);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (currentFragment != null) {
            currentFragment.reloadFragment(newConfig.orientation);
        }
        // Checking the orientation of the screen
    }

    public void reloadData() {

    }

    int requestCode = 1;

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == resultCode) {
            isNeedResumePlay = true;
            startPopupMode();

        } else {

        }
    }

    @SuppressLint("NewApi")
    public void showFloatingView(Context context, boolean isShowOverlayPermission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startPopupMode();
            return;
        }

        if (Settings.canDrawOverlays(context)) {
            startPopupMode();
            return;
        }

        if (isShowOverlayPermission) {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, requestCode);
        }
    }


    /// permission
    protected void checkPermissionAndThenLoad() {
        //check for permission
        if (AppUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && AppUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            AppUtils.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadStorageCallback);
        }
    }

    protected final PermissionCallback permissionReadStorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            if (AppUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && AppUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                loadEverything();
            } else if (!AppUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                AppUtils.askForPermission(AppBaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, permissionReadStorageCallback);
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AppUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void loadEverything() {

    }

    public void updateMultiSelectedState() {

    }

    public void updateListAfterDelete(ArrayList<VideoItemModel> videoItemModels) {

    }
//    protected void setupCast(){
//        try {
//            castContext = CastContext.getSharedInstance(this);
//        } catch (RuntimeException e) {
//            Throwable cause = e.getCause();
//            while (cause != null) {
//                if (cause instanceof DynamiteModule.LoadingException) {
//                    return;
//                }
//                cause = cause.getCause();
//            }
//            // Unknown error. We propagate it.
//            throw e;
//        }
//    }
}
