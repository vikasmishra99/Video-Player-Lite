package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.folder;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.util.ArrayList;

public class FolderItem {
    private String folderName;
    private String folderPath;

    private ArrayList<VideoItemModel> videoItemModels = new ArrayList<>();

    public FolderItem(String folderName,String folderPath){
        this.folderName = folderName;
        this.folderPath = folderPath;
    }
    public FolderItem(String folderName,String folderPath,ArrayList<VideoItemModel> videoItemModels){
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.videoItemModels = videoItemModels;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }


    public ArrayList<VideoItemModel> getVideoItemModels() {
        return videoItemModels;
    }

    public void setVideoItemModels(ArrayList<VideoItemModel> videoItemModels) {
        this.videoItemModels = videoItemModels;
    }
}
