package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video;

import java.util.ArrayList;


public interface VideoLoadListener {

    void onVideoLoaded(ArrayList<VideoItemModel> videoItemModels);

    void onFailed(Exception e);
}
