package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.material.navigation.NavigationView;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.AppUtils;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.fragment.FolderListFrag;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.video.VideoItemModel;

import java.util.ArrayList;

public class MainActivity extends AppBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    //private FirebaseAnalytics mFirebaseAnalytics;
    //private AdView adView;
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int theme = PreferencesUtility.getInstance(this).getThemeSettings();
        // Obtain the FirebaseAnalytics instance.
        // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        currentFragment = new FolderListFrag();
        setSupportActionBar(toolbar);
        //setupCast();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (AppUtils.isMarshmallow()) checkPermissionAndThenLoad();
        else loadEverything();


        //navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        if (theme < 0 || theme > 20) theme = 0;
        if (theme == 0) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme0));


        } else if (theme == 1) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme1));

        }   if (theme == 2) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme2));

        }if (theme == 3) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme3));

        }
        if (theme == 4) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme4));

        }
        if (theme == 5) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme5));

        }
        if (theme == 6) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme6));

        }
        if (theme == 7) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme7));

        }
        if (theme == 8) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme8));

        }
        if (theme == 9) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme9));

        }
        if (theme == 10) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme10));
        }
        if (theme == 11) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme11));

        }
        if (theme == 12) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme12));

        }
        if (theme == 13) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme13));

        }
        if (theme == 14) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme14));

        }
        if (theme == 15) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme15));

        }
        if (theme == 16) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme16));

        }
        if (theme == 17) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme17));

        }
        if (theme == 18) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme18));

        }
        if (theme == 19) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme19));

        }
        if (theme == 20) {
            navigationView.setBackgroundColor(getResources().getColor(R.color.theme20));

        }

        //version name
        Menu menu = navigationView.getMenu();
        MenuItem version = menu.findItem(R.id.nav_version);
        version.setTitle(getCurrentVersionName(this));

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {

            Intent receivedIntent = getIntent();
            Uri receivedUri = receivedIntent.getData();

            assert receivedUri != null;
            String _file = receivedUri.toString();
            mAppGlobalVar.playingVideo = new VideoItemModel();
            mAppGlobalVar.playingVideo.setPath(_file);
            mAppGlobalVar.playingVideo.setVideoTitle(AppUtils.getFileNameFromPath(_file));
            mAppGlobalVar.videoItemsPlaylistModel = new ArrayList<>();
            mAppGlobalVar.videoItemsPlaylistModel.add(mAppGlobalVar.playingVideo);
            if (mAppGlobalVar.videoService == null) {
                mAppGlobalVar.isOpenFromIntent = true;
            } else {
                mAppGlobalVar.videoService.playVideo(0, false);
                showFloatingView(MainActivity.this, true);
                finish();
            }
        }
        showRateDialog();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void loadEverything() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    public void reloadData() {
        currentFragment = new FolderListFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAppGlobalVar.isNeedRefreshFolder && currentFragment != null) {
            mAppGlobalVar.isNeedRefreshFolder = false;
            reloadData();
        }
//        if(castContext == null){
//             return;
//        }
//        if(mGlobalVar.videoService == null || mGlobalVar.videoService.getPlayerManager() == null){
//            return;
//        }
//        mGlobalVar.videoService.getPlayerManager().updateCast(castContext);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;

        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.secound, menu);
//        if(currentFragment instanceof FragmentVideoList){
//            if(PreferencesUtility.getInstance(FirstActivity.this).isAlbumsInGrid())
//                getMenuInflater().inflate(R.menu.first, menu);
//            else
//                getMenuInflater().inflate(R.menu.first1, menu);
//        }
//        else if (currentFragment instanceof FragmentFolderList)
//        {
//            getMenuInflater().inflate(R.menu.secound, menu);
//        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.secound, menu);
//        if(currentFragment instanceof FragmentVideoList){
//            if(PreferencesUtility.getInstance(FirstActivity.this).isAlbumsInGrid())
//                getMenuInflater().inflate(R.menu.first, menu);
//            else
//                getMenuInflater().inflate(R.menu.first1, menu);
//        }
//        else if (currentFragment instanceof FragmentFolderList)
//        {
//            getMenuInflater().inflate(R.menu.secound, menu);
//        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Handler handler = new Handler();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if (AppUtils.isMarshmallow()) {
                if (AppUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    navigateToSearch(this);
                }
            } else navigateToSearch(this);
        }
        if (id == R.id.menu_sort_by_az) {
            currentFragment.sortAZ();
        } else if (id == R.id.menu_sort_by_za) {
            currentFragment.sortZA();
        } else if (id == R.id.menu_sort_by_total_videos) {
            currentFragment.sortSize();
        } else if (id == R.id.action_go_to_playing) {
            if (mAppGlobalVar.videoService == null || mAppGlobalVar.playingVideo == null) {
                Toast.makeText(this, getString(R.string.no_video_playing), Toast.LENGTH_SHORT).show();
                return false;
            }
            final Intent intent = new Intent(MainActivity.this, VideoPlayActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_stay_x);

        } else if (id == R.id.action_rate_app) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }

        }
        else if (id == R.id.action_share_app) {
            String shareBody = getString(R.string.share_desc) + " \n " + " \n " + "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Select App to Share Video :)"));

        }


        return false;
    }

    public static void navigateToSearch(Activity context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    AlertDialog dialog;
    Button btnDownload;
    TextView textView1;


    public void showRateDialog() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(8)
                .positiveButtonText("Maybe later")
                .negativeButtonText("Never")
                .negativeButtonTextColor(R.color.grey_500)
                .ratingBarColor(R.color.nice_yellow)
                .playstoreUrl("https://play.google.com/store/apps/details?id=" + getPackageName())
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendEmail();
                    }
                }).build();

        ratingDialog.show();
    }

    private void sendEmail() {
        String[] TO = {"labcompact@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Video Player Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please type your message here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getCurrentVersionName(@NonNull final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Unkown";
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Checking if the item is in checked state or not, if not make it in checked state
        if (item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_themes:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_music_player:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.musicplayer.musicapps.mp3player.audioplayer")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.musicplayer.musicapps.mp3player.audioplayer")));
                }
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AppAboutActivity.class));
                break;
            case R.id.nav_join_beta:
                Uri uri = Uri.parse("https://play.google.com/apps/testing/" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/apps/testing/" + getPackageName())));
                }
                break;
            case R.id.nav_rate_app:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.nav_share_app:
                String shareBody = getString(R.string.share_desc) + " \n " + " \n " + "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Select App to Share Video :)"));
            default:
                break;
        }
        return true;
    }
}
