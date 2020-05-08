package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.util.ArrayList;

public abstract class MyFragment extends AppBaseFragment {
    public void reloadFragment(int orientation){}
    public int getTotalSelected(){
        return 0;
    }
    public void releaseUI(){ }
    public void playItemSelected(){}
    public void shareSelected(){}
    public void deleteSelected(){}
    public void updateVideoList(ArrayList<VideoItemModel> videoItemModels){}

    public void sortAZ(){ }
    public void sortZA(){}
    public void sortSize(){}
}
