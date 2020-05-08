package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppGlobalVar;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppBaseActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.FastScroller;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.AppDetailedFolderActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.VideoPlayActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.SearchActivity;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.storageProcess.RenameVideo;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ItemHolder> implements FastScroller.BubbleTextGetter {


    Activity context;

    private ArrayList<VideoItemModel> videoItemModels = new ArrayList<>();
    public VideoAdapter(Activity context){
        this.context = context;
    }



    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView;
        if(context instanceof SearchActivity)
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_model, null);
        else if(PreferencesUtility.getInstance(context).isAlbumsInGrid())
             itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_grid_model, null);
        else
             itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_model, null);
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
        itemHolder.txtVideoPath.setText("/".concat(videoItemModel.getFolderName()).concat("     ").concat(videoItemModel.getFileSize()));
        itemHolder.container.setBackgroundColor(videoItemModel.isSelected() ? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);
        itemHolder.container.setOnLongClickListener(v -> {
            if(context instanceof AppDetailedFolderActivity) {
                AppGlobalVar.getInstance().isMutilSelectEnalble = true;
                videoItemModel.setLongClick(true);
                videoItemModel.setSelected(!videoItemModel.isSelected());
                itemHolder.container.setBackgroundColor(videoItemModel.isSelected() ? ContextCompat.getColor(context, R.color.multiselected) : Color.TRANSPARENT);
                if (context instanceof AppBaseActivity) {
                    ((AppBaseActivity) context).updateMultiSelectedState();
                }
            }
            return false;
        });
        itemHolder.container.setOnClickListener(v -> {
            AppGlobalVar.getInstance().videoItemsPlaylistModel = videoItemModels;
            AppGlobalVar.getInstance().playingVideo = videoItemModel;
            AppGlobalVar.getInstance().seekPosition = 0;
            if(AppGlobalVar.getInstance().getPlayer() == null){
                return;
            }else if(!AppGlobalVar.getInstance().isMutilSelectEnalble) {
                if(!AppGlobalVar.getInstance().isPlayingAsPopup()){
                    AppGlobalVar.getInstance().videoService.playVideo(AppGlobalVar.getInstance().seekPosition,false);
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    context.startActivity(intent);
                    if(AppGlobalVar.getInstance().videoService != null)
                        AppGlobalVar.getInstance().videoService.releasePlayerView();
                }else {
                    ((AppBaseActivity) context).showFloatingView(context,true);
                }
            }else if(checkError(videoItemModels,i) && !videoItemModels.get(i).isLongClick()) {
                videoItemModel.setSelected(!videoItemModel.isSelected());
                itemHolder.container.setBackgroundColor(videoItemModel.isSelected() ? ContextCompat.getColor(context,R.color.multiselected) : Color.TRANSPARENT);
                if(context instanceof AppBaseActivity){
                    ((AppBaseActivity) context).updateMultiSelectedState();
                }
            }
            try {
                videoItemModel.setLongClick(false);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }


        });
        itemHolder.imageViewOption.setOnClickListener(v -> {
            showBottomDialog(videoItemModel);
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

    public ArrayList<VideoItemModel> getVideoItemModels(){
        if(videoItemModels == null) return new ArrayList<>();
        return videoItemModels;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return null;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, duration,txtVideoPath;

        protected ImageView imageView,imageViewOption;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.txtVideoPath = view.findViewById(R.id.txtVideoPath);
            this.title = view.findViewById(R.id.mainVideoTitle);
            this.imageView = view.findViewById(R.id.imageView);
            this.duration = view.findViewById(R.id.txtVideoDuration);
            this.imageViewOption = view.findViewById(R.id.imageViewOption);

        }

    }
    private void showBottomDialog(VideoItemModel videoItemModel) {
        View view = context.getLayoutInflater().inflate(R.layout.video_option_bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        LinearLayout option_playPopup = view.findViewById(R.id.option_play_popup);
        option_playPopup.setOnClickListener(v -> {
            AppGlobalVar.getInstance().playingVideo = videoItemModel;
            AppGlobalVar.getInstance().videoItemsPlaylistModel = videoItemModels;
            if (context instanceof AppBaseActivity) {
                ((AppBaseActivity) context).showFloatingView(context, true);
            }
            dialog.dismiss();
        });
        LinearLayout option_play_audio = view.findViewById(R.id.option_play_audio);
        option_play_audio.setOnClickListener(v -> {
            PreferencesUtility.getInstance(context).setAllowBackgroundAudio(true);
            AppGlobalVar.getInstance().videoItemsPlaylistModel = videoItemModels;
            AppGlobalVar.getInstance().playingVideo = videoItemModel;
            AppGlobalVar.getInstance().videoService.playVideo(AppGlobalVar.getInstance().seekPosition, false);
            dialog.dismiss();
        });
        LinearLayout option_share = view.findViewById(R.id.option_share);
        option_share.setOnClickListener(v -> {
            context.startActivity(Intent.createChooser(AppUtils.shareVideo(context, videoItemModel),context.getString(R.string.share_video)));
            dialog.dismiss();
        });
        LinearLayout option_rename = view.findViewById(R.id.option_rename);
        option_rename.setOnClickListener(v -> {
            RenameVideo renamePlaylistDialog = RenameVideo.newInstance(context,this, videoItemModel);
            renamePlaylistDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),"");
            dialog.dismiss();
        });
        LinearLayout option_info = view.findViewById(R.id.option_info);
        option_info.setOnClickListener(v -> {
            dialog.dismiss();
            createDialog(videoItemModel);
        });
        LinearLayout option_delete = view.findViewById(R.id.option_delete);
        option_delete.setOnClickListener(v -> {
            dialog.dismiss();
            new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.delete_video))
                    .content(context.getString(R.string.confirm) +" " + videoItemModel.getVideoTitle() + " ?")
                    .positiveText(context.getString(R.string.confirm_delete))
                    .negativeText(context.getString(R.string.confirm_cancel))
                    .onPositive((dialog1, which) -> {
                        File deleteFile = new File(videoItemModel.getPath());
                        if(deleteFile.exists()){
                            if(deleteFile.delete()){
                                videoItemModels.remove(videoItemModel);
                                updateData(videoItemModels);
                                if(context instanceof AppBaseActivity){
                                    ((AppBaseActivity) context).updateListAfterDelete(videoItemModels);
                                }
                            }
                        }
                    })
                    .onNegative((dialog12, which) -> dialog12.dismiss())
                    .show();
        });
        dialog.setContentView(view);
        dialog.show();
    }
    private void createDialog(VideoItemModel videoItemModel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.content_video_info,null);

        TextView txtVideoTitle = view.findViewById(R.id.mainVideoTitle);
        txtVideoTitle.setText(videoItemModel.getVideoTitle());

        TextView txtLocation = view.findViewById(R.id.videoLocation_value);
        txtLocation.setText(videoItemModel.getPath());

        TextView txtVideoFormat = view.findViewById(R.id.videoFormat_value);
        txtVideoFormat.setText(AppUtils.getFileExtension(videoItemModel.getPath()));

        TextView txtDuration = view.findViewById(R.id.videoDuration_value);
        txtDuration.setText(videoItemModel.getDuration());

        TextView txtDateAdded = view.findViewById(R.id.videoDateAdded_value);
        txtDateAdded.setText(videoItemModel.getDate_added());

        TextView txtVideoSize = view.findViewById(R.id.videoFileSize_value);
        txtVideoSize.setText(videoItemModel.getFileSize());

        TextView txtVideoResolution = view.findViewById(R.id.videoResolution_value);
        txtVideoResolution.setText(videoItemModel.getResolution());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean checkError(List<VideoItemModel> songs, int position){
        if(songs.size() >= position && position >= 0) return true;
        return false;
    }
    public int getTotalVideoSelected(){
        int totalVideoSelected = 0;
        if(videoItemModels == null || videoItemModels.size() == 0) return 0;
        for (VideoItemModel videoItemModel : videoItemModels){
            if(videoItemModel.isSelected()) totalVideoSelected += 1;
        }
        return totalVideoSelected;
    }
    public ArrayList<VideoItemModel> getVideoItemsSelected(){
        ArrayList<VideoItemModel> resultList = new ArrayList<>();
        for (VideoItemModel videoItemModel : videoItemModels){
            if(videoItemModel.isSelected()) resultList.add(videoItemModel);
        }
        return resultList;
    }
    public void deleteListVideoSelected(){
        ArrayList<VideoItemModel> deletedList = getVideoItemsSelected();
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.delete_video))
                .content(context.getString(R.string.confirm) +" " + String.valueOf(deletedList.size()) + " " + context.getString(R.string.video) + " ?")
                .positiveText(context.getString(R.string.confirm_delete))
                .negativeText(context.getString(R.string.confirm_cancel))
                .onPositive((dialog1, which) -> {
                    for(VideoItemModel item :deletedList){
                            File deleteFile = new File(item.getPath());
                            if (deleteFile.exists()) {
                                if (deleteFile.delete()) {
                                    videoItemModels.remove(item);
                                }
                            }
                    }
                    updateData(videoItemModels);
                })
                .onNegative((dialog12, which) -> dialog12.dismiss())
                .show();
    }

}
