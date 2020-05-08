package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment.VideoListFrag;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.util.ArrayList;

public class AppDetailedFolderActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentFragment = new VideoListFrag();
        setContentView(R.layout.folder_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (mAppGlobalVar.folderItem != null && mAppGlobalVar.folderItem.getFolderName() != null)
            setActionBar(mAppGlobalVar.folderItem.getFolderName());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!mAppGlobalVar.isMutilSelectEnalble)
            finish();
        else releaseUI();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAppGlobalVar.isNeedRefreshFolder) {
            currentFragment = new VideoListFrag();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mAppGlobalVar.isMutilSelectEnalble)
            super.onBackPressed();
        else releaseUI();

    }

    @Override
    public void updateMultiSelectedState() {
        if (currentFragment == null) return;
        int totalVideoSelected = currentFragment.getTotalSelected();
        if (totalVideoSelected == 0) {
            releaseUI();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(String.valueOf(totalVideoSelected) + " " + getString(R.string.video));
            }
        }
    }

    @Override
    public void updateListAfterDelete(ArrayList<VideoItemModel> videoItemModels) {
        if (currentFragment == null) return;
        currentFragment.updateVideoList(videoItemModels);
    }

    private void releaseUI() {
        mAppGlobalVar.isMutilSelectEnalble = false;
        setActionBar(mAppGlobalVar.folderItem.getFolderName());
        if (currentFragment != null) currentFragment.releaseUI();
    }

    public void setActionBar(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!mAppGlobalVar.isMutilSelectEnalble) {
            if (PreferencesUtility.getInstance(AppDetailedFolderActivity.this).isAlbumsInGrid())
                getMenuInflater().inflate(R.menu.first, menu);
            else
                getMenuInflater().inflate(R.menu.first1, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_multiselected_option, menu);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mAppGlobalVar.isMutilSelectEnalble) {
            if (PreferencesUtility.getInstance(AppDetailedFolderActivity.this).isAlbumsInGrid())
                getMenuInflater().inflate(R.menu.first, menu);
            else
                getMenuInflater().inflate(R.menu.first1, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_multiselected_option, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Handler handler = new Handler();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_list) {
            PreferencesUtility.getInstance(AppDetailedFolderActivity.this).setAlbumsInGrid(false);
            currentFragment = new VideoListFrag();
            handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit(), 500);

        }
        if (id == R.id.action_view_grid) {
            PreferencesUtility.getInstance(AppDetailedFolderActivity.this).setAlbumsInGrid(true);
            currentFragment = new VideoListFrag();
            handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit(), 500);

        }
        if (id == R.id.action_play) {
            currentFragment.playItemSelected();
            releaseUI();

        } else if (id == R.id.action_rate_app) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }

        } else if (id == R.id.action_share_app) {
            String shareBody = getString(R.string.share_desc) + " \n " + " \n " + "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Select App to Share Video :)"));

        } else if (id == R.id.action_share) {
            currentFragment.shareSelected();
        } else if (id == R.id.action_delete) {
            currentFragment.deleteSelected();
        } else if (id == R.id.menu_sort_by_az) {
            currentFragment.sortAZ();
        } else if (id == R.id.menu_sort_by_za) {
            currentFragment.sortZA();
        } else if (id == R.id.menu_sort_by_size) {
            currentFragment.sortSize();
        } else if (id == R.id.action_go_to_playing) {
            if (mAppGlobalVar.videoService == null || mAppGlobalVar.playingVideo == null) {
                Toast.makeText(this, getString(R.string.no_video_playing), Toast.LENGTH_SHORT).show();
                return false;
            }
            final Intent intent = new Intent(AppDetailedFolderActivity.this, VideoPlayActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_stay_x);

        }
        return false;
    }
}
