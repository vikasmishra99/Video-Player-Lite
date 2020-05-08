package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.folder;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.io.File;
import java.util.ArrayList;

public class FolderLoader {
    public static ArrayList<FolderItem>  getFolderList(ArrayList<VideoItemModel> videoItemModels){
        File file;
        ArrayList<FolderItem> folderItems = new ArrayList<>();
        if(videoItemModels != null && videoItemModels.size() > 0){
            for(int i = 0; i < videoItemModels.size(); i ++){
                file = new File(videoItemModels.get(i).getPath());

                String filePath = file.getParent();
                String fileName = "Unknow Folder";
                File _parentFile = file.getParentFile();
                if (_parentFile.exists()) {
                    fileName = _parentFile.getName();
                }
                if(!isFileExits(folderItems,filePath)){
                    folderItems.add(new FolderItem(fileName,filePath));
                }
                for (FolderItem item :folderItems) {
                    if(item.getFolderPath().contains(filePath)){
                        item.getVideoItemModels().add(videoItemModels.get(i));
                    }

                }

            }
        }

        return folderItems;
    }
    private static boolean isFileExits(ArrayList<FolderItem> folderItems, String path){
        for (FolderItem item :folderItems) {
            if(item.getFolderPath().contains(path))
                return true;
        }
        return false;
    }


}
