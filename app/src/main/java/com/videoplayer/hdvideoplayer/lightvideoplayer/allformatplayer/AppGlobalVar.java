package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.folder.FolderItem;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.videoService.VideoService;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.util.ArrayList;

public class AppGlobalVar {
    private static final AppGlobalVar ourInstance = new AppGlobalVar();

    public static AppGlobalVar getInstance() {
        return ourInstance;
    }

    private AppGlobalVar() {
    }


    public VideoItemModel playingVideo;
    public VideoService videoService;
    public boolean isMutilSelectEnalble = false;
    public long seekPosition = 0;
    public boolean isPlaying = true;
    public boolean isNeedRefreshFolder = false;
    public boolean isOpenFromIntent = false;

    public ArrayList<VideoItemModel> videoItemsPlaylistModel = new ArrayList<>();
    public ArrayList<VideoItemModel> allVideoItemModels = new ArrayList<>();
    public FolderItem folderItem;


    public boolean isSeekBarProcessing = false;

    public String getPath(){
        if(playingVideo != null)
            return playingVideo.getPath();
        return "77777777777";
    }

    public SimpleExoPlayer getPlayer(){
        if(videoService == null)
            return  null;
        return videoService.getVideoPlayer();
    }
    public boolean isPlayingAsPopup(){
        if(videoService == null) return false;
        return videoService.isPlayingAsPopup();
    }
    public void playNext(){
        if(videoService == null) return;
        videoService.handleAction(VideoService.NEXT_ACTION);
    }
    public void playPrevious(){
        if(videoService == null) return;
        videoService.handleAction(VideoService.PREVIOUS_ACTION);
    }
    public void closeNotification(){
        if(videoService == null) return;
        videoService.closeBackgroundMode();
    }
    public void openNotification(){

        if(videoService == null) return;
        videoService.openBackgroundMode();
    }
    public void pausePlay(){
        if(videoService == null) return;
        videoService.handleAction(VideoService.TOGGLEPAUSE_ACTION);
    }
    public void createShuffle(){
        if(videoService == null) return;
        videoService.createShuffleArray();
    }
    public int getCurrentPosition(){
        if(videoService == null) return  - 1;
        return videoService.getCurrentPosition();
    }

}
