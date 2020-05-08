package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter.FolderModelAdapter;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.WrapContentLinearLayoutManager;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.folder.FolderItem;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.folder.FolderLoader;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoLoadListener;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoLoader;

import java.util.ArrayList;
import java.util.Collections;

public class FolderListFrag extends MyFragment {
    private final static String TAG = "FolderListFragment";

    RecyclerView recyclerView;
    FolderModelAdapter folderModelAdapter;
    ArrayList<FolderItem> folderItems;

    public FolderListFrag() {

    }

    public static FolderListFrag newInstance() {
        FolderListFrag fragment = new FolderListFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.folder_list_fragment, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        setLayoutManager();
        folderModelAdapter = new FolderModelAdapter(getActivity());
        recyclerView.setAdapter(folderModelAdapter);
        loadEveryThing();
        return rootView;
    }

    private void setLayoutManager() {
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }

    private void loadEveryThing() {
        VideoLoader videoLoader = new VideoLoader(getActivity());
        videoLoader.loadDeviceVideos(new VideoLoadListener() {
            @Override
            public void onVideoLoaded(final ArrayList<VideoItemModel> items) {
                AppGlobalVar.getInstance().allVideoItemModels = items;
                folderItems = FolderLoader.getFolderList(items);
                folderModelAdapter.updateData(folderItems);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void sortAZ() {
        if (folderItems != null && folderItems.size() > 0) {
            folderItems = sortFolderAZ(folderItems);
            folderModelAdapter.updateData(folderItems);
        }

    }

    @Override
    public void sortZA() {
        if (folderItems != null && folderItems.size() > 0) {
            folderItems = sortFolderZA(folderItems);
            folderModelAdapter.updateData(folderItems);
        }

    }

    @Override
    public void sortSize() {
        if (folderItems != null && folderItems.size() > 0) {
            folderItems = sortFolderNumberSong();
            folderModelAdapter.updateData(folderItems);
        }

    }

    private ArrayList<FolderItem> sortFolderAZ(ArrayList<FolderItem> folders) {
        ArrayList<FolderItem> m_folders = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < folders.size(); i++) {
            names.add(folders.get(i).getFolderName() + "_" + folders.get(i).getFolderPath());
        }
        Collections.sort(names, String::compareToIgnoreCase);

        for (int i = 0; i < names.size(); i++) {
            String path = names.get(i);
            for (int j = 0; j < folders.size(); j++) {
                if (path.equals(folders.get(j).getFolderName() + "_" + folders.get(j).getFolderPath())) {
                    m_folders.add(folders.get(j));
                }
            }
        }


        return m_folders;
    }

    private ArrayList<FolderItem> sortFolderZA(ArrayList<FolderItem> folders) {
        ArrayList<FolderItem> m_folders = sortFolderAZ(folders);
        Collections.reverse(m_folders);

        return m_folders;

    }

    private ArrayList<FolderItem> sortFolderNumberSong() {
        ArrayList<FolderItem> m_folders = folderItems;
        for (int i = 0; i < m_folders.size() - 1; i++) {
            for (int j = 0; j < m_folders.size() - 1 - i; j++) {
                if (m_folders.get(j).getVideoItemModels().size() < m_folders.get(j + 1).getVideoItemModels().size()) {
                    Collections.swap(m_folders, j, j + 1);
                }
            }
        }

        return m_folders;

    }


}