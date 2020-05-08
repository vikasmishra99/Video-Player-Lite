package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.adapter.ThemeChoiceModelAdapter;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.ThemeChoiceModel;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.customizeUI.WrapContentGridLayoutManager;

import java.util.ArrayList;

public class SettingsActivity extends AppBaseActivity {
    //Settings

    Switch backgroundAudioSwitch ;
    PreferencesUtility preferencesUtility;
    AppGlobalVar mAppGlobalVar = AppGlobalVar.getInstance();
    ThemeChoiceModelAdapter themeChoiceModelAdapter;
    RecyclerView recyclerView;
    int currentTheme ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferencesUtility = PreferencesUtility.getInstance(this);
        currentTheme = preferencesUtility.getThemeSettings();

        backgroundAudioSwitch = findViewById(R.id.backgroundAudioSwitch);
        backgroundAudioSwitch.setChecked(preferencesUtility.isAllowBackgroundAudio());
        backgroundAudioSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) mAppGlobalVar.openNotification();
            else mAppGlobalVar.closeNotification();
            preferencesUtility.setAllowBackgroundAudio(isChecked);

        });
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        themeChoiceModelAdapter = new ThemeChoiceModelAdapter(this);
        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(this, 5));
        recyclerView.setAdapter(themeChoiceModelAdapter);
        themeChoiceModelAdapter.updateData(setupListData());

    }
    @Override
    public boolean onSupportNavigateUp() {
        if(preferencesUtility.getThemeSettings() == currentTheme)
            finish();
        else {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
            finish();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if(preferencesUtility.getThemeSettings() == currentTheme)
            super.onBackPressed();
        else {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
            finish();
        }
    }
    private ArrayList<ThemeChoiceModel> setupListData(){
        ArrayList<ThemeChoiceModel> themeChoiceModels = new ArrayList<>();
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme0,0));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme1,1));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme2,2));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme3,3));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme4,4));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme5,5));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme6,6));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme7,7));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme8,8));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme9,9));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme10,10));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme11,11));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme12,12));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme13,13));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme14,14));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme15,15));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme16,16));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme17,17));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme18,18));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme19,19));
        themeChoiceModels.add(new ThemeChoiceModel(R.color.theme20,20));


        return themeChoiceModels;
    }
}
