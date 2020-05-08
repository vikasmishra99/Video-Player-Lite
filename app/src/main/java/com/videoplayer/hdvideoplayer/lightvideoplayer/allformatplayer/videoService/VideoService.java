package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.videoService;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.media.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.PlayPauseDrawable;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.MainActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.MediaButtonIntentReceiver;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.VideoPlayActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppPlayerManager;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.util.Random;


public class VideoService extends Service implements AudioManager.OnAudioFocusChangeListener{

    PreferencesUtility preferencesUtility;
    private AppPlayerManager appPlayerManager;
    //private SimpleExoPlayer simpleExoPlayer;

    private VideoItemModel currentVideoPlaying = new VideoItemModel();
    PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    PlayerView playerView;
    View layout_drag;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private FrameLayout container;

    private static final int CONTROL_HIDE_TIMEOUT = 4000;
    private long lastTouchTime;
    ImageButton btnPausePlay;
    ImageView btnFullScreenMode,btnClose,btnResize;
    RelativeLayout relativeLayout;
    int popup_params;
    PopupSize popupsize = PopupSize.SMALL;
    boolean isVideoPlaying  = false;
    private AudioManager mAudioManager;
    private boolean mPlayOnAudioFocus = false;
    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;
    AppGlobalVar mAppGlobalVar = AppGlobalVar.getInstance();
    private IntentFilter becomingNoisyReceiverIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if(intent.getAction() != null)
                if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                    if(isPlaying())
                        pause();

                }
        }
    };


    public static final String NOTIFICATIONCHANNELID = "id";
    private NotificationManagerCompat mNotificationManager;
    private long mNotificationPostTime = 0;
    private MediaSessionCompat mSession;

    public static final String SDCODEMUSICPLAYERPACKAGE = "com.sdcdoe.videoplayer";

    public static final String SERVICECMD = SDCODEMUSICPLAYERPACKAGE +".video_service_blutooth";
    public static final String TOGGLEPAUSE_ACTION = SDCODEMUSICPLAYERPACKAGE +".togglepause";
    public static final String PREVIOUS_ACTION = SDCODEMUSICPLAYERPACKAGE +".previous";
    public static final String PREVIOUS_FORCE_ACTION = SDCODEMUSICPLAYERPACKAGE +".previous.force";
    public static final String NEXT_ACTION = SDCODEMUSICPLAYERPACKAGE +".next";
    public static final String CLOSE_ACTION = SDCODEMUSICPLAYERPACKAGE +".close";
    public static final String REPEAT_ACTION = SDCODEMUSICPLAYERPACKAGE + ".repeat";
    public static final String SHUFFLE_ACTION = SDCODEMUSICPLAYERPACKAGE + ".shuffle";
    public static final String STOP_ACTION = SDCODEMUSICPLAYERPACKAGE + ".stop";
    public static final String PAUSE_ACTION = SDCODEMUSICPLAYERPACKAGE + "pause";
    public static final String PLAY_ACTION = SDCODEMUSICPLAYERPACKAGE + "play";
    public static final String CMDNAME = "command";
    int notificationId = hashCode();

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mPlayOnAudioFocus && !isPlaying()) {
                    handleAction(PLAY_ACTION);

                } else if (isPlaying()) {
                    setVolume(MEDIA_VOLUME_DEFAULT);
                }
                mPlayOnAudioFocus = false;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                setVolume(MEDIA_VOLUME_DUCK);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying()) {
                    mPlayOnAudioFocus = true;
                    handleAction(PAUSE_ACTION);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mAudioManager.abandonAudioFocus(this);
                mPlayOnAudioFocus = false;
                handleAction(PAUSE_ACTION);
                break;
        }
    }

    public enum PopupSize {
        SMALL,
        NORMAL,
        LARGE

    }

    public void onCreate() {
        super.onCreate();
        initExoPlayer();
        setUpMediaSession();
        preferencesUtility = PreferencesUtility.getInstance(this);
        mNotificationManager = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannelId();
            Notification notification = new Notification.Builder(this, NOTIFICATIONCHANNELID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        }
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        registerReceiver(becomingNoisyReceiver,becomingNoisyReceiverIntentFilter);



    }

    public boolean isPlaying(){
        if(appPlayerManager == null) return false;
        return appPlayerManager.getPlayWhenReady();
    }

    public void setVolume(float volume){
        if(appPlayerManager != null) appPlayerManager.setVolume(volume);
    }

    public void initExoPlayer(){
        if(appPlayerManager == null) appPlayerManager = new AppPlayerManager(getApplicationContext());
        appPlayerManager.getSimpleExoPlayer().addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == Player.STATE_ENDED){
                    if(mAppGlobalVar.videoItemsPlaylistModel.size() == 0 || getNextPosition() == -1)
                        return ;
                    if(getNextPosition() >= mAppGlobalVar.videoItemsPlaylistModel.size())
                        return;
                    mAppGlobalVar.playingVideo = mAppGlobalVar.videoItemsPlaylistModel.get(getNextPosition());
                    playVideo(0,false);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                createShuffleArray();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

                Log.d("ZZ", error.getCause().toString());
                handleAction(NEXT_ACTION);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {
               AppGlobalVar.getInstance().isSeekBarProcessing = false;
            }
        });

    }
    private boolean canDrawPopup(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ) {
           return true;
        }
        if(Settings.canDrawOverlays(getApplicationContext())){
           return true;
        }
        return false;
    }

    private void addPopupView(){
        removePopupView();
        container = new FrameLayout(this) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return super.onInterceptTouchEvent(ev);
            }
        };
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_video, container);
        playerView = view.findViewById(R.id.player_view);
        playerView.setPlayer(appPlayerManager.getCurrentPlayer());
        layout_drag = view.findViewById(R.id.layout_all_control_container);
        relativeLayout = view.findViewById(R.id.layout_control_top);
        btnFullScreenMode = view.findViewById(R.id.btnFullScreenMode);
        btnFullScreenMode.setOnClickListener(v -> {
            appPlayerManager.onFullScreen();
            removePopup();

            Intent intent = new Intent(VideoService.this, VideoPlayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        btnClose = view.findViewById(R.id.btnClosePopUp);
        btnClose.setOnClickListener(v -> removePopup());
        btnResize = view.findViewById(R.id.video_Resize_btn);
        btnResize.setOnClickListener(v -> changePopUpSize());
        btnPausePlay =  view.findViewById(R.id.playPause_btns);
        btnPausePlay.setImageDrawable(playPauseDrawable);
        playPauseDrawable.transformToPause(false);

        btnPausePlay.setOnClickListener(v -> {
            if(appPlayerManager != null) isVideoPlaying = appPlayerManager.getPlayWhenReady();
            handleAction(TOGGLEPAUSE_ACTION);
        });
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            popup_params = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            popup_params = WindowManager.LayoutParams.TYPE_PHONE;
        }
        setPopupSize(0,0);
        windowManager.addView(container, params);
        container.setVisibility(View.GONE);
        setDragListeners();

        Runnable r = new Runnable() {
            @Override
            public void run() {

                if (System.currentTimeMillis() - lastTouchTime > CONTROL_HIDE_TIMEOUT) {
                    hideControlContainer();
                }
                if(appPlayerManager != null){
                    if(isVideoPlaying != appPlayerManager.getPlayWhenReady()){
                        isVideoPlaying = appPlayerManager.getPlayWhenReady();
                        if (isVideoPlaying)
                            playPauseDrawable.transformToPause(false);
                        else playPauseDrawable.transformToPlay(false);
                    }
                }
                layout_drag.postDelayed(this, 1000);
            }
        };
        layout_drag.postDelayed(r,500);
    }

    private void showControl(){
        if(btnPausePlay != null) btnPausePlay.setVisibility(View.VISIBLE);
        if(relativeLayout != null) relativeLayout.setVisibility(View.VISIBLE);
    }

    private void hideControlContainer(){
        if(btnPausePlay != null) btnPausePlay.setVisibility(View.GONE);
        if(relativeLayout != null) relativeLayout.setVisibility(View.GONE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action!= null && action.equals(SERVICECMD))
            if(intent.getExtras() != null) action = intent.getExtras().getString(CMDNAME);
        handleAction(action);
        return START_NOT_STICKY;
    }

    public void handleAction(String action){
        if(action != null && appPlayerManager != null){
            if (action.equals(NEXT_ACTION)) {
                gotoNext();
            }else if(action.equals(PREVIOUS_ACTION)){
                prev();
            }else if(action.equals(TOGGLEPAUSE_ACTION)){
                if(appPlayerManager.getPlayWhenReady()) pause();
                else play();
                lastTouchTime = System.currentTimeMillis();
                if(container != null) {
                    if (appPlayerManager.getPlayWhenReady())
                        playPauseDrawable.transformToPause(false);
                    else playPauseDrawable.transformToPlay(false);
                }

            }else if(action.equals(CLOSE_ACTION)){
                releasePlayerView();
                mNotificationManager.cancel(notificationId);
                stopForeground(true);
                appPlayerManager.setPlayWhenReady(false);
                return;
            }else if(action.equals(PAUSE_ACTION)){
                pause();
                playPauseDrawable.transformToPlay(false);
            }else if(action.equals(PLAY_ACTION)){
                play();
                playPauseDrawable.transformToPause(false);
            }

            if(buildNotification() != null && preferencesUtility.isAllowBackgroundAudio())
                startForeground(notificationId, buildNotification());
        }

    }

    public SimpleExoPlayer getVideoPlayer(){
        return appPlayerManager.getCurrentPlayer();
    }
    public AppPlayerManager getAppPlayerManager(){
        return appPlayerManager;
    }
    @Override
    public IBinder onBind(Intent arg0){
        return new VideoBinder();
    }

    @Override
    public boolean onUnbind(Intent intent){
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSession.release();
        unregisterReceiver(becomingNoisyReceiver);
        appPlayerManager.releasePlayer();
        stopForeground(true);
        if(mAudioManager != null)
            mAudioManager.abandonAudioFocus(this);
        releasePlayerView();
    }


    public  class VideoBinder extends Binder {
        public VideoService getService(){
            return VideoService.this;
        }
    }

    private Notification buildNotification() {
        if(artWork == null) {
            loadImage(mAppGlobalVar.playingVideo);
            return null;
        }
        if (AppGlobalVar.getInstance().playingVideo == null) return null;
        VideoItemModel videoItemModel = AppGlobalVar.getInstance().playingVideo;
        final String content = videoItemModel.getPath();

        final boolean isPlaying = appPlayerManager.getPlayWhenReady();


        int playButtonResId = isPlaying
                ? R.drawable.btn_pause : R.drawable.btn_play;

        Intent nowPlayingIntent = new Intent(this, MainActivity.class);


        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (mNotificationPostTime == 0) {
            mNotificationPostTime = System.currentTimeMillis();
        }


        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, NOTIFICATIONCHANNELID)
                .setSmallIcon(R.drawable.btn_stat_name)
                .setLargeIcon(artWork)
                .setContentIntent(clickIntent)
                .setContentTitle(videoItemModel.getVideoTitle())
                .setContentText(content)
                .setWhen(mNotificationPostTime)
                .addAction(R.drawable.btn_skip_previous,
                        "",
                        retrievePlaybackAction(PREVIOUS_ACTION))
                .addAction(playButtonResId, "",
                        retrievePlaybackAction(TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.btn_skip_next,
                        "",
                        retrievePlaybackAction(NEXT_ACTION))
                .addAction(R.drawable.btn_delete, "",
                        retrievePlaybackAction(CLOSE_ACTION));

        builder.setShowWhen(false);


        builder.setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC);
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle()
                .setMediaSession(mSession.getSessionToken())
                .setShowActionsInCompactView(1, 2, 3, 0);
        builder.setStyle(style);

        if (artWork != null )
            builder.setColor(Palette.from(artWork).generate().getVibrantColor(Color.parseColor("#403f4d")));

        Notification n = builder.build();


        return n;
    }
    Bitmap artWork;

    private void loadImage(VideoItemModel videoItemModel){
        Glide.with(this).asBitmap()
                .load(videoItemModel.getPath())
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  //Toast.makeText(cxt, getResources().getString(R.string.unexpected_error_occurred_try_again), Toast.LENGTH_SHORT).show();
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                  artWork = resource;
                                  if(preferencesUtility.isAllowBackgroundAudio())
                                    startForeground(notificationId, buildNotification());
                                  return false;
                              }

                          }
                ).submit();
    }

    private void setUpMediaSession() {
        ComponentName mediaButtonReceiverComponentName = new ComponentName(getApplicationContext(), MediaButtonIntentReceiver.class);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiverComponentName);

        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);

        mSession = new MediaSessionCompat(this, "Music Player");
        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {
                pause();
                //mPausedByTransientLossOfFocus = false;
            }

            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onSkipToNext() {
                gotoNext();
            }

            @Override
            public void onSkipToPrevious() {
                prev();
            }

            @Override
            public void onStop() {
                releasePlayerView();
            }
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                return MediaButtonIntentReceiver.handleIntent(VideoService.this, mediaButtonEvent);
            }
        });
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS| MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);
    }

    private void pause() {
        if(appPlayerManager != null)
            appPlayerManager.setPlayWhenReady(false);
    }

    private void play(){
        if(appPlayerManager != null && reQuestAudioFocus())
            appPlayerManager.setPlayWhenReady(true);

    }

    private void prev(){
        VideoItemModel videoItemModel = getPreviousVideo();
        if(videoItemModel == null) return;
        mAppGlobalVar.playingVideo = videoItemModel;
        playVideo(0,false);
    }

    private VideoItemModel getPreviousVideo(){
        int prPos = getPrePosition();
        if(mAppGlobalVar.videoItemsPlaylistModel.size() == 0 || prPos == -1) return null;
        return mAppGlobalVar.videoItemsPlaylistModel.get(prPos);
    }

    private int getPrePosition(){
        int previousPosition = 0;
        if(appPlayerManager.getCurrentPlayer().getShuffleModeEnabled()){
            if(getCurrentShufflePosition() <= 0) return  shuffleArray[shuffleArray.length - 1];
            return shuffleArray[getCurrentShufflePosition() - 1];
        }else{
            previousPosition = getCurrentPosition() -1;
            if(previousPosition < 0) previousPosition = mAppGlobalVar.videoItemsPlaylistModel.size() - 1;
            //if(currentPosition < 0) currentPosition = (simpleExoPlayer.getRepeatMode() == Player.REPEAT_MODE_ONE)? -1: mGlobalVar.videoItemsPlaylist.size() - 1;
        }

        return previousPosition;
    }

    public int getCurrentPosition(){
        int currentPosition = 0;
        if(mAppGlobalVar.videoItemsPlaylistModel.size() == 0) return  -1;
        for(int i = 0; i < mAppGlobalVar.videoItemsPlaylistModel.size(); i++){
            if(mAppGlobalVar.playingVideo.getPath().equals(mAppGlobalVar.videoItemsPlaylistModel.get(i).getPath())){
                currentPosition = i ;
            }
        }
        return currentPosition;
    }

    private void gotoNext(){
        if(getNextVideo() == null) return;
        mAppGlobalVar.playingVideo = getNextVideo();
        playVideo(0,false);
    }

    private VideoItemModel getNextVideo(){
        if(mAppGlobalVar.videoItemsPlaylistModel.size() == 0 || getForceNextPosition() <= -1) return null;
        if(getForceNextPosition() >= mAppGlobalVar.videoItemsPlaylistModel.size()) return null;
        return mAppGlobalVar.videoItemsPlaylistModel.get(getForceNextPosition());
    }

    private int getForceNextPosition(){
        int nextPosition = 0;
        if(appPlayerManager.getCurrentPlayer().getShuffleModeEnabled()){
            if(getCurrentShufflePosition() >= shuffleArray.length - 1) return  shuffleArray[0];
            return shuffleArray[getCurrentShufflePosition() + 1];
        }else{
            nextPosition = getCurrentPosition() + 1;
            if(nextPosition >= mAppGlobalVar.videoItemsPlaylistModel.size()) nextPosition = 0;

        }

        return nextPosition;
    }

    private int getNextPosition(){
        int currentPosition = 0;
        if(appPlayerManager.getCurrentPlayer().getShuffleModeEnabled()){
            if(getCurrentShufflePosition() >= shuffleArray.length - 1) return  -1;
            return shuffleArray[getCurrentShufflePosition() + 1];
        }else{
            for(int i = 0; i < mAppGlobalVar.videoItemsPlaylistModel.size(); i++){
                if(mAppGlobalVar.playingVideo.getPath().equals(mAppGlobalVar.videoItemsPlaylistModel.get(i).getPath())){
                    currentPosition = i + 1;
                    if(currentPosition >= mAppGlobalVar.videoItemsPlaylistModel.size()) currentPosition = (appPlayerManager.getCurrentPlayer().getRepeatMode() == Player.REPEAT_MODE_ONE)? -1: 0;
                }
            }
        }

        return currentPosition;
    }

    private  PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(this, VideoService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
    }

    public boolean reQuestAudioFocus(){
        int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return status == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

    }

    public void playVideo(long seekPosition, boolean playAsPopup){

        if(!reQuestAudioFocus()) return;
        if(appPlayerManager == null)
            initExoPlayer();

        if(!currentVideoPlaying.getPath().contains(AppGlobalVar.getInstance().getPath())){
            currentVideoPlaying = AppGlobalVar.getInstance().playingVideo;

            appPlayerManager.prepare(true, false);
            //if(seekPosition >0) simpleExoPlayer.seekTo(seekPosition);
            appPlayerManager.setPlayWhenReady(AppGlobalVar.getInstance().isPlaying);

            if (appPlayerManager.getPlayWhenReady()) {
                playPauseDrawable.transformToPause(true);
            } else {
                playPauseDrawable.transformToPlay(true);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (AppUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    loadImage(AppGlobalVar.getInstance().playingVideo);
                } else artWork = null;
            } else loadImage(AppGlobalVar.getInstance().playingVideo);
        }
        if(playAsPopup && container == null) if (canDrawPopup()) {
            addPopupView();
            if(container != null) container.setVisibility(View.VISIBLE);
        }
    }

    private void setDragListeners() {
        layout_drag.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastTouchTime = System.currentTimeMillis();
                        showControl();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        if(container != null)
                            windowManager.updateViewLayout(container, params);
                        v.performClick();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
    private void changePopUpSize(){
        if(params != null)
            setPopupSize(params.x, params.y);
        else
            setPopupSize(0, 0);
        windowManager.updateViewLayout(container, params);
    }
    private void setPopupSize(int x, int  y){
        int baseSize  =  Resources.getSystem().getDisplayMetrics().widthPixels;
        if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            baseSize =  Resources.getSystem().getDisplayMetrics().heightPixels;
        if(popupsize == PopupSize.SMALL){
            popupsize = PopupSize.NORMAL;
            params = new WindowManager.LayoutParams(
                    (baseSize / 2),
                    (baseSize / (2*3/2)),
                    popup_params,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP | Gravity.START;
            params.x = x;
            params.y = y;


        }else if(popupsize == PopupSize.NORMAL){
            popupsize = PopupSize.LARGE;
            params = new WindowManager.LayoutParams(
                    (int) (baseSize / 1.6),
                    (int)(baseSize / (1.6*3/2)),
                    popup_params,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = x;
            params.y = y;

        }else if(popupsize == PopupSize.LARGE){
            popupsize = PopupSize.SMALL;
            params = new WindowManager.LayoutParams(
                    (int) (baseSize / 1.3),
                    (int)(baseSize / (1.3*1.5)),
                    popup_params,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = x;
            params.y = y;
        }

    }
    public void closeBackgroundMode(){
        if(appPlayerManager.getCurrentPlayer().getPlaybackState() != Player.STATE_READY) return;
        if(mNotificationManager != null){
            stopForeground(true);
            mNotificationManager.cancel(notificationId);
            appPlayerManager.getCurrentPlayer().setPlayWhenReady(false);
            play();
        }


    }
    public void openBackgroundMode(){
        if(appPlayerManager.getCurrentPlayer().getPlaybackState() != Player.STATE_READY) return;
        if(buildNotification() != null)
            startForeground(notificationId, buildNotification());
    }
    public void removePopup(){
        if(preferencesUtility.isAllowBackgroundAudio()){
            removePopupView();
        }else handleAction(CLOSE_ACTION);
    }
    private void removePopupView(){
        if(windowManager != null && container!= null) {
            windowManager.removeView(container);
            container = null;
        }
    }
    public boolean isPlayingAsPopup(){
        if(container != null) return true;
        return false;
    }
    public void releasePlayerView() {

        if(playerView != null){
            playerView.setPlayer(null);
            playerView = null;
        }
        removePopupView();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannelId(){
        String channelName = "Music Player Background Service";
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATIONCHANNELID,
                channelName, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(false);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert manager != null;
        manager.createNotificationChannel(notificationChannel);
    }
    public int[] shuffleArray = new int[10];


    public void createShuffleArray(){
        if(mAppGlobalVar.videoItemsPlaylistModel.size() <= 1) return;
        shuffleArray = new int[mAppGlobalVar.videoItemsPlaylistModel.size()];
        for(int i = 0; i< mAppGlobalVar.videoItemsPlaylistModel.size(); i++){
            shuffleArray[i] = i;
        }
        shuffleArray(shuffleArray);
    }

    private void shuffleArray(int[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rand = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rand.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
    private int getCurrentShufflePosition() {

        int currentPlayingPosition = -1;
        if(shuffleArray.length != mAppGlobalVar.videoItemsPlaylistModel.size()) createShuffleArray();
        if (mAppGlobalVar.videoItemsPlaylistModel.size() == 0)
            return -1;
        for (int i = 0; i < mAppGlobalVar.videoItemsPlaylistModel.size(); i++) {
            if (mAppGlobalVar.playingVideo.getPath().equals(mAppGlobalVar.videoItemsPlaylistModel.get(i).getPath()))
                currentPlayingPosition = i;

        }
        if (currentPlayingPosition == -1 || shuffleArray.length == 0)
            return -1;
        for (int j = 0; j < shuffleArray.length; j++)
            if (currentPlayingPosition == shuffleArray[j])
                return j;

        return -1;
    }

}
