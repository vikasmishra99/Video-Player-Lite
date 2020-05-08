package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoPlayingListAdapter extends RecyclerView.Adapter<VideoPlayingListAdapter.ItemHolder> {

    Activity context;
    private ArrayList<VideoItemModel> videoItemModels = new ArrayList<>();
    public VideoPlayingListAdapter(Activity context){
        this.context = context;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_playing_model, null);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        VideoItemModel videoItemModel = videoItemModels.get(i);
        itemHolder.title.setText(videoItemModel.getVideoTitle());
        itemHolder.duration.setText(videoItemModel.getDuration());
        Glide.with(context.getApplicationContext())
                .load(videoItemModel.getPath())
                .into(itemHolder.imageView);
        itemHolder.txtVideoPath.setText(videoItemModel.getPath());
        itemHolder.container.setOnClickListener(v -> {
            AppGlobalVar.getInstance().playingVideo = videoItemModel;
            AppGlobalVar.getInstance().videoService.playVideo(0,false);
        });

        itemHolder.btnRemove.setOnClickListener(v -> {
            videoItemModels.remove(i);
            AppGlobalVar.getInstance().videoItemsPlaylistModel = videoItemModels;
            updateData(videoItemModels);
        });


    }
    public void updateData(ArrayList<VideoItemModel> items){
        if(items == null) items = new ArrayList<>();
        ArrayList<VideoItemModel> r = new ArrayList<>(items);
        int currentSize = videoItemModels.size();
        if(currentSize != 0) {
            this.videoItemModels.clear();
            this.videoItemModels.addAll(r);
            notifyItemRangeRemoved(0, currentSize);
            //tell the recycler view how many new items we added
            notifyItemRangeInserted(0, r.size());
        }
        else {
            this.videoItemModels.addAll(r);
            notifyItemRangeInserted(0, r.size());
        }

    }
    @Override
    public int getItemCount() {
        return videoItemModels.size();
    }



    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, duration,txtVideoPath;
        protected ImageView imageView;
        protected MaterialIconView btnRemove;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.txtVideoPath = view.findViewById(R.id.txtVideoPath);
            this.title = view.findViewById(R.id.mainVideoTitle);
            this.imageView = view.findViewById(R.id.imageView);
            this.duration = view.findViewById(R.id.txtVideoDuration);
            this.btnRemove = view.findViewById(R.id.btn_remove_to_playingList);
        }

    }
    public void deleteVideo(VideoItemModel videoItemModel){
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.delete_video))
                .content(context.getString(R.string.confirm) +" " + videoItemModel.getVideoTitle() + " ?")
                .positiveText(context.getString(R.string.confirm_delete))
                .negativeText(context.getString(R.string.confirm_cancel))
                .onPositive((dialog1, which) -> {
                    File deleteFile = new File(videoItemModel.getPath());
                    if(deleteFile.exists()){
                        if(deleteFile.delete()){
                            AppGlobalVar.getInstance().isNeedRefreshFolder = true;
                            if(removeIfPossible(videoItemModel))
                                context.finish();
                            else {
                                updateData(videoItemModels);
                            }

                        }
                    }
                })
                .onNegative((dialog12, which) -> dialog12.dismiss())
                .show();
    }
    private boolean removeIfPossible(VideoItemModel videoItemModel){
        for(VideoItemModel video: videoItemModels) {
            if (videoItemModel.getPath().equals(video.getPath())){
                videoItemModels.remove(videoItemModel);
                break;
            }

        }
        AppGlobalVar.getInstance().videoItemsPlaylistModel = new ArrayList<>(videoItemModels);
        if(videoItemModels.size() == 0){
            return true;
        }else {
            AppGlobalVar.getInstance().playNext();
        }
        return false;
    }
}
