package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppDetailedFolderActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.VideoPlayActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.folder.FolderItem;

import java.util.ArrayList;

public class FolderModelAdapter extends RecyclerView.Adapter<FolderModelAdapter.ItemHolder> {

    Activity context;
    private ArrayList<FolderItem> folderItems = new ArrayList<>();

    public FolderModelAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView
                = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folder_model, null);

        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        FolderItem folderItem = folderItems.get(i);
        itemHolder.folderTitle.setText(folderItem.getFolderName());
        itemHolder.folderPath.setText(folderItem.getFolderPath());
        itemHolder.folderSize.setText(String.valueOf(folderItem.getVideoItemModels().size()).concat(" ").concat(context.getString(R.string.video)));

        itemHolder.container.setOnClickListener(v -> {
            AppGlobalVar.getInstance().folderItem = folderItem;
            Intent intent = new Intent(context, AppDetailedFolderActivity.class);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_stay_x);
        });
        itemHolder.imageViewOption.setOnClickListener(v -> showBottomDialog(folderItem));

    }

    public void updateData(ArrayList<FolderItem> items) {
        if (items == null) items = new ArrayList<>();

        int currentSize = folderItems.size();
        if(currentSize != 0) {
            this.folderItems.clear();
            this.folderItems.addAll(items);
            notifyItemRangeRemoved(0, currentSize);
            //tell the recycler view how many new items we added
            notifyItemRangeInserted(0, items.size());
        }
        else {
            this.folderItems.addAll(items);
            notifyItemRangeInserted(0, items.size());
        }
    }

    @Override
    public int getItemCount() {
        return folderItems.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView folderTitle, folderPath, folderSize;
        protected ImageView imageViewOption;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.folderTitle = view.findViewById(R.id.txtFolderName);
            this.folderPath = view.findViewById(R.id.txtFolderPath);
            this.folderSize = view.findViewById(R.id.txtFolderSize);
            this.imageViewOption = view.findViewById(R.id.imageViewOption);

        }

    }

    private void showBottomDialog(FolderItem folderItem) {
        View view = context.getLayoutInflater().inflate(R.layout.folder_option_bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        LinearLayout option_play = view.findViewById(R.id.option_play);
        option_play.setOnClickListener(v -> {
            if (folderItem == null || folderItem.getVideoItemModels() == null || folderItem.getVideoItemModels().size() == 0)
                return;
            AppGlobalVar.getInstance().videoItemsPlaylistModel = folderItem.getVideoItemModels();
            AppGlobalVar.getInstance().folderItem = folderItem;
            AppGlobalVar.getInstance().playingVideo = folderItem.getVideoItemModels().get(0);
            AppGlobalVar.getInstance().videoService.playVideo(AppGlobalVar.getInstance().seekPosition, false);
            Intent intent = new Intent(context, VideoPlayActivity.class);
            context.startActivity(intent);
            if (AppGlobalVar.getInstance().videoService != null)
                AppGlobalVar.getInstance().videoService.releasePlayerView();
            dialog.dismiss();
        });

        LinearLayout option_play_audio = view.findViewById(R.id.option_play_audio);
        option_play_audio.setOnClickListener(v -> {
            if (folderItem == null || folderItem.getVideoItemModels() == null || folderItem.getVideoItemModels().size() == 0)
                return;
            PreferencesUtility.getInstance(context).setAllowBackgroundAudio(true);
            AppGlobalVar.getInstance().folderItem = folderItem;
            AppGlobalVar.getInstance().videoItemsPlaylistModel = folderItem.getVideoItemModels();
            AppGlobalVar.getInstance().playingVideo = folderItem.getVideoItemModels().get(0);
            AppGlobalVar.getInstance().videoService.playVideo(AppGlobalVar.getInstance().seekPosition, false);
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
