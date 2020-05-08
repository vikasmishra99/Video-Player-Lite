package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexvasilkov.gestures.views.GestureFrameLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter.VideoPlayingListAdapter;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.PlayPauseDrawable;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.WrapContentLinearLayoutManager;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;


public class VideoPlayActivity extends AppCompatActivity implements PreviewView.OnPreviewChangeListener {

    private VideoItemModel currentVideo = new VideoItemModel();
    private int currentVideoPosition = - 999;
    AppGlobalVar mAppGlobalVar = AppGlobalVar.getInstance();

    private PlayerView mPlayerView;


    Runnable r;
    private boolean isVideoPlaying = false;
    private static final int CONTROL_HIDE_TIMEOUT = 3000;
    private long lastTouchTime;
    PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    FloatingActionButton btnPausePlay;
    GestureFrameLayout gestureFrameLayout;
    FrameLayout frameLayout_preview;

    RelativeLayout layout_all_control_container,layout_region_volume, layout_region_brightness;
    RelativeLayout layout_control_top, layout_btn_bottom, layout_skip_next_10, layout_skip_back_10;

    private ExpandableLayout expandableLayout, expandableRecyclerView;
    MaterialIconView btnExpandLayout, btnRotation,btnVolume,btnBrightness, btnPopupMode, btnClosePlaylist;
    MaterialIconView btn_fwb,btn_fwn,btn_next_video,btn_pre_video,btnLockControl,btnChangeMode,btnEnableAllControl,btnBackgroundAudio,btn_repeatMode;


    int  fullMode, currentMode;
    boolean isControlLocked = false;

    SeekBar seekBarVolume,seekBarBrightness;
    RequestOptions options;

    PreviewTimeBar previewTimeBar;
    PlayerControlView playerControlView;
    ImageView preViewImage;
    Point screenSize;
    Toolbar toolbar;
    VideoPlayingListAdapter videoPlayingListAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video_activity);
        fullMode = AspectRatioFrameLayout.RESIZE_MODE_FILL;
        currentMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        screenSize = getScreenSize();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initializePlayer();
        initControlView();
    }
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.more_option, menu);
//        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_menu){
            if(expandableRecyclerView!= null) {
                expandableRecyclerView.toggle();
                hideSystemUi();
            }
        }else if(id == R.id.action_info){
            if(mAppGlobalVar.playingVideo != null)
                createDialog(mAppGlobalVar.playingVideo);
        }else if(id == R.id.action_share){
            if(mAppGlobalVar.playingVideo != null)
                startActivity(Intent.createChooser(AppUtils.shareVideo(VideoPlayActivity.this, mAppGlobalVar.playingVideo),
                        getString(R.string.share_video)));
        }
        return false;

//        else if(id == R.id.action_delete){
//            if(videoPlayingListAdapter != null && mGlobalVar.playingVideo != null)
//                videoPlayingListAdapter.deleteVideo(mGlobalVar.playingVideo);
//        }
    }
    private void initializePlayer() {
        playerControlView = findViewById(R.id.player_control_view);
        playerControlView.setVisibilityListener(visibility -> {
            if(visibility == PlayerControlView.GONE){
                toolbar.setVisibility(View.GONE);
                hideSystemUi();
                if(layout_region_volume != null) layout_region_volume.setVisibility(View.GONE);
                if(layout_region_brightness != null) layout_region_brightness.setVisibility(View.GONE);
//                if(expandableLayout != null && expandableTitleLayout != null)
//                    if(expandableLayout.isExpanded()){
//                        expandableTitleLayout.toggle();
//                        expandableLayout.toggle();
//                    }
            }
            if(visibility == PlayerControlView.VISIBLE){
                toolbar.setVisibility(View.VISIBLE);
                delayHideControl();
                showSystemUI();
            }
        });
        playerControlView.setOnClickListener(v -> playerControlView.hide());
        playerControlView.setPlayer(mAppGlobalVar.getPlayer());


        mPlayerView = findViewById(R.id.player_view);
        mPlayerView.requestFocus();

        if(mAppGlobalVar.getPlayer() == null)
            return;

        mPlayerView.setPlayer(mAppGlobalVar.getPlayer());
        mPlayerView.setResizeMode(currentMode);


    }

    private void initControlView(){
        btn_repeatMode = findViewById(R.id.btn_repeatMode);
        if(mAppGlobalVar.getPlayer() == null) return;
        setRepeatModeIcon();
        btn_repeatMode.setOnClickListener(v -> {
            if(mAppGlobalVar.getPlayer().getShuffleModeEnabled()){
                mAppGlobalVar.getPlayer().setShuffleModeEnabled(false);
                mAppGlobalVar.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
                btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            }
            else if(mAppGlobalVar.getPlayer().getRepeatMode() == Player.REPEAT_MODE_ALL){
                mAppGlobalVar.getPlayer().setRepeatMode(Player.REPEAT_MODE_ONE);
                btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
            }else if(mAppGlobalVar.getPlayer().getRepeatMode() == Player.REPEAT_MODE_ONE){
                mAppGlobalVar.getPlayer().setRepeatMode(Player.REPEAT_MODE_OFF);
                btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_OFF);
            }else {
                mAppGlobalVar.getPlayer().setShuffleModeEnabled(true);
                btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE);
            }
        });

        btnBackgroundAudio = findViewById(R.id.bgAudio_btn);
        btnBackgroundAudio.setColor(PreferencesUtility.getInstance(this).isAllowBackgroundAudio() ? Color.RED : Color.WHITE);
        btnBackgroundAudio.setOnClickListener(v -> {
            if(PreferencesUtility.getInstance(VideoPlayActivity.this).isAllowBackgroundAudio())
                mAppGlobalVar.closeNotification();
            else mAppGlobalVar.openNotification();
            PreferencesUtility.getInstance(VideoPlayActivity.this).setAllowBackgroundAudio(!PreferencesUtility.getInstance(this).isAllowBackgroundAudio());
            btnBackgroundAudio.setColor(PreferencesUtility.getInstance(VideoPlayActivity.this).isAllowBackgroundAudio() ? Color.GREEN : Color.WHITE);

        });
        btnRotation = findViewById(R.id.videoRotate_btn);
        btnRotation.setOnClickListener(v -> {
            int requestScreenInfo = getRequestedOrientation();
            if(requestScreenInfo == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||  requestScreenInfo == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }else if(requestScreenInfo == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||requestScreenInfo == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }else if(requestScreenInfo == ActivityInfo.SCREEN_ORIENTATION_SENSOR || requestScreenInfo == ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            updateScreenOrientationIco(getRequestedOrientation());

        });
        int defaultOrientation = PreferencesUtility.getInstance(this).getScreenOrientation();
        setScreenOrientation(defaultOrientation);
        seekBarBrightness = findViewById(R.id.seekBar_brightness);
        seekBarBrightness.setMax(100);
        seekBarBrightness.setProgress(getCurrentBrightness());
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    delayHideControl();
                    changeBrightness(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarVolume = findViewById(R.id.seekBar_volume);
        if(getMaxVolume() >= -1) {
            seekBarVolume.setMax(getMaxVolume());
            seekBarVolume.setProgress(getStreamVolume());
        }
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    setVolume(progress);
                    delayHideControl();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnBrightness = findViewById(R.id.videoBrigtness_btn);
        btnBrightness.setOnClickListener(v -> {
            if(layout_region_brightness!= null) layout_region_brightness.setVisibility(View.VISIBLE);
            if(layout_region_volume!= null) layout_region_volume.setVisibility(View.GONE);
            delayHideControl();
        });
        btnVolume = findViewById(R.id.videoVolume_btn);
        btnVolume.setOnClickListener(v -> {
            if(layout_region_volume!= null) layout_region_volume.setVisibility(View.VISIBLE);
            if(layout_region_brightness!= null) layout_region_brightness.setVisibility(View.GONE);
            delayHideControl();
        });

        layout_control_top = findViewById(R.id.layout_title_top);
        btnPopupMode = findViewById(R.id.popup_video_btn);
        btnPopupMode.setOnClickListener(v -> {
            showFloatingView(VideoPlayActivity.this,true);
        });

        btnExpandLayout = findViewById(R.id.videoClose_ExpandView);
        btnExpandLayout.setOnClickListener(v -> {
            delayHideControl();
            if(expandableLayout != null) expandableLayout.toggle();
        });
        gestureFrameLayout = findViewById(R.id.frame_item_layout);
        expandableLayout = findViewById(R.id.expandable_layout);
        expandableLayout.setOnExpansionUpdateListener((expansionFraction, state) -> btnExpandLayout.setRotation(expansionFraction * 180));

        layout_region_volume = findViewById(R.id.region_volume);
        layout_region_brightness = findViewById(R.id.region_brightness);

        btnEnableAllControl = findViewById(R.id.btnEnableAllControl);
        btnEnableAllControl.setOnClickListener(v -> {
            btnEnableAllControl.setVisibility(View.GONE);
            isControlLocked = false;
            showControl();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            updateScreenOrientationIco(getRequestedOrientation());
        });

        /// bot control
        layout_skip_back_10 = findViewById(R.id.skip_pre_10s_layout);
        layout_skip_next_10 = findViewById(R.id.layout_skip_next_10s);
        frameLayout_preview = findViewById(R.id.previewFrameLayout);
        preViewImage = findViewById(R.id.preImageView);
        previewTimeBar = findViewById(R.id.previewTimebar);
        previewTimeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                mAppGlobalVar.isSeekBarProcessing = true;
                delayHideControl();
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                delayHideControl();
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                mAppGlobalVar.getPlayer().seekTo(position);
                previewTimeBar.setPosition(position);
                delayHideControl();

            }
        });
        previewTimeBar.setPreviewLoader((currentPosition, max) -> {
            if(preViewImage != null)
                options = new RequestOptions().frame(currentPosition*1000);
                Glide.with(VideoPlayActivity.this)
                        .load(mAppGlobalVar.playingVideo.getPath())
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .apply(options)
                        .into(preViewImage);
        });
        previewTimeBar.addOnPreviewChangeListener(this);

        layout_btn_bottom = findViewById(R.id.all_btn_layouts_video_play);
        btnPausePlay =  findViewById(R.id.playPause_btns);
        btnPausePlay.setImageDrawable(playPauseDrawable);

        if (mAppGlobalVar.getPlayer().getPlayWhenReady()) {
            playPauseDrawable.transformToPause(true);
        } else {
            playPauseDrawable.transformToPlay(true);
        }

        btnPausePlay.setOnClickListener(v -> {
            delayHideControl();
            if(mAppGlobalVar.getPlayer()!= null) {
                isVideoPlaying = !mAppGlobalVar.getPlayer().getPlayWhenReady();

                mAppGlobalVar.pausePlay();
                playPauseDrawable.transformToPlay(true);
                playPauseDrawable.transformToPause(true);
            }

        });





        btnChangeMode = findViewById(R.id.video_Resize_btn);
        btnChangeMode.setOnClickListener(v -> {
            delayHideControl();
            currentMode += 1;
            if(currentMode > 4)
                currentMode = 0;
            mPlayerView.setResizeMode(currentMode);
        });

        btn_fwb = findViewById(R.id.video_Skip_pre_10s_btn);
        btn_fwb.setOnClickListener(v -> {
            long currentPostion = mAppGlobalVar.getPlayer().getCurrentPosition();
            if(currentPostion - 10*1000 < 0) currentPostion = 0;
            mAppGlobalVar.getPlayer().seekTo(currentPostion - 10000);
        });
        btn_fwn = findViewById(R.id.video_skip_next_10s_btn);
        btn_fwn.setOnClickListener(v -> {
            long currentPosition = mAppGlobalVar.getPlayer().getCurrentPosition();
            if(currentPosition + 10*1000 < mAppGlobalVar.getPlayer().getDuration())
                mAppGlobalVar.getPlayer().seekTo(currentPosition + 10000);
        });
        btn_next_video = findViewById(R.id.video_Skip_next_btn);
        btn_next_video.setOnClickListener(v ->{
            mAppGlobalVar.playNext();
        });
        btn_pre_video = findViewById(R.id.video_Skip_pre_btn);
        btn_pre_video.setOnClickListener(v -> {
            mAppGlobalVar.playPrevious();
        });
        btnLockControl = findViewById(R.id.videoLock_btn);
        btnLockControl.setOnClickListener(v -> {
            if(layout_all_control_container != null && btnEnableAllControl != null) {
                isControlLocked = true;
                if(playerControlView != null) playerControlView.hide();
                btnEnableAllControl.setVisibility(View.VISIBLE);

                int currentOrientation = getCurrentOrientation();
                if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }else if(currentOrientation == Configuration.ORIENTATION_PORTRAIT){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }

                updateScreenOrientationIco(getRequestedOrientation());
            }
        });

        /// container control
        layout_all_control_container = findViewById(R.id.layout_all_control_container);

        r = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastTouchTime > CONTROL_HIDE_TIMEOUT) {
                      hideControlContainer();
                }
                layout_all_control_container.postDelayed(this, 500);
                if(mAppGlobalVar.getPlayer() != null){
                    if(mAppGlobalVar.getPlayer().getPlayWhenReady() != isVideoPlaying){
                        isVideoPlaying = mAppGlobalVar.getPlayer().getPlayWhenReady();
                        if(getSupportActionBar() != null) getSupportActionBar().setTitle(mAppGlobalVar.playingVideo.getVideoTitle());
                        if (isVideoPlaying)
                            playPauseDrawable.transformToPause(false);
                        else playPauseDrawable.transformToPlay(false);
                    }
                    if(previewTimeBar != null){
                        if(currentVideo != mAppGlobalVar.playingVideo && mAppGlobalVar.getPlayer().getPlaybackState() == Player.STATE_READY){
                            if(getSupportActionBar() != null) getSupportActionBar().setTitle(mAppGlobalVar.playingVideo.getVideoTitle());
                            currentVideo = mAppGlobalVar.playingVideo;
                            previewTimeBar.setDuration(mAppGlobalVar.getPlayer().getDuration());
                        }else if(!mAppGlobalVar.isSeekBarProcessing) {
                            previewTimeBar.setPosition(mAppGlobalVar.getPlayer().getCurrentPosition());
                            //Toast.makeText(PlayVideoActivity.this,"update seek",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(currentVideoPosition != mAppGlobalVar.getCurrentPosition()){
                        currentVideoPosition = mAppGlobalVar.getCurrentPosition();
                        if(currentVideoPosition >= 0 && currentVideoPosition < mAppGlobalVar.videoItemsPlaylistModel.size())
                            recyclerView.smoothScrollToPosition(currentVideoPosition);
                    }
                }
            }
        };
        layout_all_control_container.postDelayed(r,500);
        layout_all_control_container.setOnClickListener(v ->
                showControl()
        );

        recyclerView = findViewById(R.id.recyclerView_playlist);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        videoPlayingListAdapter = new VideoPlayingListAdapter(this);
        recyclerView.setAdapter(videoPlayingListAdapter);
        videoPlayingListAdapter.updateData(mAppGlobalVar.videoItemsPlaylistModel);
        if(currentVideoPosition != mAppGlobalVar.getCurrentPosition()){
            currentVideoPosition = mAppGlobalVar.getCurrentPosition();
            if(currentVideoPosition >= 0 && currentVideoPosition < mAppGlobalVar.videoItemsPlaylistModel.size())
                recyclerView.smoothScrollToPosition(currentVideoPosition);
        }


        expandableRecyclerView = findViewById(R.id.expandable_recyclerView_layout);

        btnClosePlaylist = findViewById(R.id.btn_CloseList);
        btnClosePlaylist.setOnClickListener(v -> {
            if(expandableRecyclerView != null)
                expandableRecyclerView.toggle();
        });



        doLayoutChange(getCurrentOrientation());
    }
    private void setRepeatModeIcon(){
        if(mAppGlobalVar.getPlayer() == null) return;
        if(mAppGlobalVar.getPlayer().getShuffleModeEnabled()){
            btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE);
        }
        else if(mAppGlobalVar.getPlayer().getRepeatMode() == Player.REPEAT_MODE_OFF){
            btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_OFF);
        }else if(mAppGlobalVar.getPlayer().getRepeatMode() == Player.REPEAT_MODE_ONE){
            btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
        }else {
            btn_repeatMode.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
        }
    }
    private void changeBrightness(int value){
        if(value <= 5) value = 5;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = (float) value/100;
        getWindow().setAttributes(layoutParams);
    }
    private int getCurrentBrightness(){
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 70;
        }
    }
    boolean isVolumeVisible = false;
    private int getMaxVolume(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volumeStreamMax = 0;
        try {
            if (audioManager != null)
                volumeStreamMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (volumeStreamMax < 1)
                volumeStreamMax = 1;
            isVolumeVisible = true;
        } catch (Throwable ex) {
            isVolumeVisible = false;
            ex.printStackTrace();
            return -1;
        }
        return volumeStreamMax;
    }
    public void setVolume(int volume) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int oldVolume = getStreamVolume();
        try {
            if (audioManager != null)
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, (volume >= oldVolume) ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, 0);
        } catch (Throwable ex) {
            //too bad...
        }
        //apparently a few devices don't like to have the streamVolume changed from another thread....
        //maybe there is another reason for why it fails... I just haven't found yet :(
        try {
            if (audioManager != null)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        } catch (Throwable ex) {
            //too bad...
        }
    }
    private int getStreamVolume(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(audioManager!=null)
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return 0;
    }
    private void setScreenOrientation(int value){
        setRequestedOrientation(value);
        updateScreenOrientationIco(value);

    }
    private void updateScreenOrientationIco(int value){
        if(btnRotation != null) {
            if (value == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || value == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                btnRotation.setIcon(MaterialDrawableBuilder.IconValue.PHONE_ROTATE_LANDSCAPE);
            } else if (value == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || value == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                btnRotation.setIcon(MaterialDrawableBuilder.IconValue.PHONE_ROTATE_PORTRAIT);
            } else if (value == ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR || value == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                btnRotation.setIcon(MaterialDrawableBuilder.IconValue.SCREEN_ROTATION);
            }
        }
        PreferencesUtility.getInstance(this).setScreenOrientation(value);
    }
    private void hideControlContainer(){
        if(!isControlLocked) {
            if (playerControlView != null)
                playerControlView.hide();
            if(layout_region_volume != null) layout_region_volume.setVisibility(View.GONE);
            if(layout_region_brightness != null) layout_region_brightness.setVisibility(View.GONE);
        }else
            if(btnEnableAllControl != null)
                btnEnableAllControl.setVisibility(View.GONE);
    }
    private void delayHideControl(){
        lastTouchTime = System.currentTimeMillis();
    }
    private void showControl(){
        delayHideControl();
        if(!isControlLocked) {
            if (playerControlView != null) playerControlView.show();
        }else
            if(btnEnableAllControl != null)
                btnEnableAllControl.setVisibility(View.VISIBLE);

    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        doLayoutChange(newConfig.orientation);
        // Checking the orientation of the screen
    }
    private void doLayoutChange(int orientation) {
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            if(layout_skip_back_10 != null) layout_skip_back_10.setVisibility(View.GONE);
            if(layout_skip_next_10 != null) layout_skip_next_10.setVisibility(View.GONE);
            if(toolbar != null){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.setMarginEnd(10*(int)getDensity());
            }
            if(btnExpandLayout != null){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnExpandLayout.getLayoutParams();
                params.setMarginEnd(10*(int)getDensity());
            }



        }else if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            if(toolbar != null){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.setMarginEnd(50*(int)getDensity());
            }
            if(btnExpandLayout != null){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnExpandLayout.getLayoutParams();
                params.setMarginEnd(50*(int)getDensity());
            }
            if(layout_skip_back_10 != null) layout_skip_back_10.setVisibility(View.VISIBLE);
            if(layout_skip_next_10 != null) layout_skip_next_10.setVisibility(View.VISIBLE);
        }

    }
    private float getDensity(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (metrics.density);
    }
    private int getCurrentOrientation(){
        return getResources().getConfiguration().orientation;
    }

    private void releasePlayer() {

    }

    private Point getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void onStop(){
        super.onStop();
        if(!PreferencesUtility.getInstance(this).isAllowBackgroundAudio() && !isPopupModeEnalbe){
            if(mAppGlobalVar.getPlayer()!= null && mAppGlobalVar.getPlayer().getPlayWhenReady()) {
                mAppGlobalVar.pausePlay();
            }
        }
        isPopupModeEnalbe = false;
    }
    @Override
    public void onResume(){
        super.onResume();

        if(layout_all_control_container != null)
            layout_all_control_container.postDelayed(r,500);

    }
    @Override
    public void onPause(){
        super.onPause();
        releasePlayer();
        if(layout_all_control_container != null)
            layout_all_control_container.removeCallbacks(r);

    }


    boolean isPopupModeEnalbe = false;
    public void startPopupMode() {
        AppGlobalVar.getInstance().videoService.playVideo(mAppGlobalVar.seekPosition,true);
        isPopupModeEnalbe = true;
        finish();
    }
    int requestCode = 1;
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.requestCode == resultCode) {
            startPopupMode();
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
    private void showSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    private void hideSystemUi(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }
//    public static void setAutoOrientationEnabled(Context context, boolean enabled)
//    {
//        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
//    }
//    public boolean isAutoRotationState(){
//        if (android.provider.Settings.System.getInt(getContentResolver(),
//                Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
//            return true;
//
//        return false;
//    }
    @Override
    public void onStartPreview(PreviewView previewView, int progress) {

    }

    @Override
    public void onStopPreview(PreviewView previewView, int progress) {

    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {

    }
    private void createDialog(VideoItemModel videoItemModel){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.content_video_info,null);

        TextView txtVideoTitle = view.findViewById(R.id.mainVideoTitle);
        txtVideoTitle.setText(videoItemModel.getVideoTitle());

        TextView txtLocation = view.findViewById(R.id.videoLocation_value);
        txtLocation.setText(videoItemModel.getPath());

        TextView txtVideoFormat = view.findViewById(R.id.videoFormat_value);
        txtVideoFormat.setText(AppUtils.getFileExtension(videoItemModel.getPath()));

        TextView txtDuration = view.findViewById(R.id.videoDuration_value);
        txtDuration.setText(videoItemModel.getDuration());

        TextView txtDateAdded = view.findViewById(R.id.videoDateAdded_value);
        txtDateAdded.setText(videoItemModel.getDate_added());

        TextView txtVideoSize = view.findViewById(R.id.videoFileSize_value);
        txtVideoSize.setText(videoItemModel.getFileSize());

        TextView txtVideoResolution = view.findViewById(R.id.videoResolution_value);
        txtVideoResolution.setText(videoItemModel.getResolution());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
