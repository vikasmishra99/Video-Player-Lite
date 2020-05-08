package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.ThemeChoiceModel;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeChoiceModelAdapter extends RecyclerView.Adapter<ThemeChoiceModelAdapter.ItemHolder> {


    Activity context;
    ArrayList<ThemeChoiceModel> themeChoiceModels = new ArrayList<>();
    int choiceId = 0;
    PreferencesUtility preferencesUtility;

    public ThemeChoiceModelAdapter(Activity context){
        this.context = context;
        preferencesUtility = new PreferencesUtility(context);
        choiceId = preferencesUtility.getThemeSettings();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.theme_choice_model, null);

        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        ThemeChoiceModel themeChoiceModel = themeChoiceModels.get(i);
        itemHolder.layoutContainer.setBackgroundColor(ContextCompat.getColor(context, themeChoiceModel.getColor()));
        itemHolder.imageView.setColor(themeChoiceModel.getId() == choiceId ? Color.GREEN : Color.WHITE);
        itemHolder.imageView.setVisibility(themeChoiceModel.getId() == choiceId ? View.VISIBLE:View.INVISIBLE);

        itemHolder.container.setOnClickListener(v -> {
            choiceId = themeChoiceModel.getId();
            updateData(themeChoiceModels);
            preferencesUtility.setThemSettings(themeChoiceModel.getId());
        });

    }
    public void updateData(ArrayList<ThemeChoiceModel> items){

        if(items == null) items = new ArrayList<>();
        ArrayList<ThemeChoiceModel> r = new ArrayList<>(items);
        int currentSize = themeChoiceModels.size();
        if(currentSize != 0) {
            this.themeChoiceModels.clear();
            this.themeChoiceModels.addAll(r);
            notifyDataSetChanged();
        }
        else {
            this.themeChoiceModels.addAll(r);
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemCount() {
        return themeChoiceModels.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout layoutContainer;

        protected MaterialIconView imageView;

        View container;

        public ItemHolder(View view) {
            super(view);
            container = view;
            this.layoutContainer = view.findViewById(R.id.layout_root);
            this.imageView = view.findViewById(R.id.btn_choice);

        }
    }

}
