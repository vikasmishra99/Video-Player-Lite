package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppBaseActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.WrapContentGridLayoutManager;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.WrapContentLinearLayoutManager;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.MainActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppDetailedFolderActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.VideoPlayActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.SearchActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter.VideoAdapter;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoLoadListener;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoLoader;

import java.util.ArrayList;
import java.util.Collections;


public class VideoListFrag extends MyFragment {
    private final static String TAG = "VideoListFragment";
    RecyclerView recyclerView;
    VideoAdapter videoAdapter;
    VideoLoader videoLoader;
    ArrayList<VideoItemModel> videoItemModels = new ArrayList<>();


    public VideoListFrag() {
        // Required empty public constructor
    }

    public static VideoListFrag newInstance(String param1, String param2) {
        VideoListFrag fragment = new VideoListFrag();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.video_list_fragment, container, false);


        recyclerView = rootView.findViewById(R.id.recyclerView);
        setLayoutManager(getCurrentOrientation());
        videoAdapter = new VideoAdapter(getActivity());
        recyclerView.setAdapter(videoAdapter);
        loadEveryThing();
        return rootView;
    }


    @Override
    public void reloadFragment(int orientation) {
        doLayoutChange(orientation);

    }

    @Override
    public int getTotalSelected() {
        if (videoAdapter == null)
            return 0;
        return videoAdapter.getTotalVideoSelected();
    }

    @Override
    public void playItemSelected() {
        ArrayList<VideoItemModel> videoItemModels = videoAdapter.getVideoItemsSelected();
        if (videoItemModels.size() > 0 && getActivity() != null) {
            AppGlobalVar.getInstance().videoItemsPlaylistModel = videoItemModels;
            AppGlobalVar.getInstance().playingVideo = videoItemModels.get(0);
            if (!AppGlobalVar.getInstance().isPlayingAsPopup()) {
                AppGlobalVar.getInstance().videoService.playVideo(AppGlobalVar.getInstance().seekPosition, false);
                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                getActivity().startActivity(intent);
                if (AppGlobalVar.getInstance().videoService != null)
                    AppGlobalVar.getInstance().videoService.releasePlayerView();
            } else {
                ((AppBaseActivity) getActivity()).showFloatingView(getActivity(), true);
            }
        }
    }

    @Override
    public void sortAZ() {
        if (videoItemModels != null && videoItemModels.size() > 0) {
            videoItemModels = sortVideoAZ(videoItemModels);
            videoAdapter.updateData(videoItemModels);
        }


    }

    @Override
    public void sortZA() {
        if (videoItemModels != null && videoItemModels.size() > 0) {
            videoItemModels = sortVideoZA(videoItemModels);
            videoAdapter.updateData(videoItemModels);
        }
    }

    @Override
    public void sortSize() {
        if (videoItemModels != null && videoItemModels.size() > 0) {
            videoItemModels = sortSongSize();
            videoAdapter.updateData(videoItemModels);
        }
    }

    @Override
    public void shareSelected() {
        if (videoAdapter == null || getActivity() == null) return;
        ArrayList<VideoItemModel> videoItemModels = videoAdapter.getVideoItemsSelected();
        AppUtils.shareMultiVideo(getActivity(), videoItemModels);
    }

    @Override
    public void deleteSelected() {
        videoAdapter.deleteListVideoSelected();
    }

    @Override
    public void updateVideoList(ArrayList<VideoItemModel> videoItemModels) {
        if (videoItemModels == null) return;
        this.videoItemModels = videoItemModels;
        AppGlobalVar.getInstance().folderItem.setVideoItemModels(videoItemModels);
        AppGlobalVar.getInstance().isNeedRefreshFolder = true;
    }

    @Override
    public void releaseUI() {
        for (VideoItemModel videoItemModel : videoItemModels) {
            videoItemModel.setSelected(false);
        }
        videoAdapter.updateData(videoItemModels);
    }

    private void doLayoutChange(int orientation) {
        if (getActivity() instanceof MainActivity || getActivity() instanceof AppDetailedFolderActivity) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 4));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 2));

                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            }
            videoAdapter.updateData(videoItemModels);
        }
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;

    }

    private void setLayoutManager(int orientation) {
        if (getActivity() instanceof MainActivity || getActivity() instanceof AppDetailedFolderActivity) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 4));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (PreferencesUtility.getInstance(getActivity()).isAlbumsInGrid()) {
                    recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 2));
                } else {
                    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                }
            }
        }

    }

    private void loadEveryThing() {
        if (getActivity() instanceof MainActivity) {
            videoLoader = new VideoLoader(getActivity());
            videoLoader.loadDeviceVideos(new VideoLoadListener() {
                @Override
                public void onVideoLoaded(final ArrayList<VideoItemModel> items) {
                    videoItemModels = items;
                    AppGlobalVar.getInstance().allVideoItemModels = videoItemModels;
                    videoAdapter.updateData(items);

                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (getActivity() instanceof AppDetailedFolderActivity) {
            videoItemModels = AppGlobalVar.getInstance().folderItem.getVideoItemModels();
            videoAdapter.updateData(videoItemModels);
        } else if (getActivity() instanceof SearchActivity) {

        }
    }

    private ArrayList<VideoItemModel> sortVideoAZ(ArrayList<VideoItemModel> videoItemModels) {
        ArrayList<VideoItemModel> m_videos = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < videoItemModels.size(); i++) {
            names.add(videoItemModels.get(i).getFolderName() + "_" + videoItemModels.get(i).getPath());
        }
        Collections.sort(names, String::compareToIgnoreCase);

        for (int i = 0; i < names.size(); i++) {
            String path = names.get(i);
            for (int j = 0; j < videoItemModels.size(); j++) {
                if (path.equals(videoItemModels.get(j).getFolderName() + "_" + videoItemModels.get(j).getPath())) {
                    m_videos.add(videoItemModels.get(j));
                }
            }
        }


        return m_videos;
    }

    private ArrayList<VideoItemModel> sortVideoZA(ArrayList<VideoItemModel> videoItemModels) {
        ArrayList<VideoItemModel> m_videos = sortVideoAZ(videoItemModels);
        Collections.reverse(m_videos);

        return m_videos;

    }

    private ArrayList<VideoItemModel> sortSongSize() throws NumberFormatException {
        ArrayList<VideoItemModel> m_videos = videoItemModels;
        for (int i = 0; i < m_videos.size() - 1; i++) {
            for (int j = 0; j < m_videos.size() - 1 - i; j++) {

                if (m_videos.get(j).getFileSizeAsFloat() < m_videos.get(j + 1).getFileSizeAsFloat()) {
                    Collections.swap(m_videos, j, j + 1);
                }
            }
        }

        return m_videos;

    }

}
