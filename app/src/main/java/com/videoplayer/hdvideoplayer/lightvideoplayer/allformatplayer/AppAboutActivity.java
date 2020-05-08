package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.dailogs.Change_Log;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.dailogs.Privacy_Policy;

import de.psdev.licensesdialog.LicensesDialog;

public class AppAboutActivity extends AppBaseActivity implements View.OnClickListener {
    private static String GITHUB = "https://github.com/vikasmishara/Video-Player-Light";
    private static String RATE_ON_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer";
    private static String TWITTER = "https://twitter.com/GoldsMineApps";
    private static String LIKE_FB = "https://www.facebook.com/lite.videoplayer";
    TextView appVersion;
    LinearLayout changelog, forkOnGitHub, licenses, privacyPolicy, sendUSFeedback, followOnTwitter, likeonFB, rateOnGooglePlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        appVersion = (TextView) findViewById(R.id.app_version_name);
        changelog = (LinearLayout) findViewById(R.id.videoPlayer_changelog);
        forkOnGitHub = (LinearLayout) findViewById(R.id.openSource_applink);
        licenses = (LinearLayout) findViewById(R.id.licenses);
        privacyPolicy = (LinearLayout) findViewById(R.id.privacyPolicy);
        sendUSFeedback = (LinearLayout) findViewById(R.id.send_feedback);
        likeonFB = (LinearLayout) findViewById(R.id.like_onFB);
        rateOnGooglePlay = (LinearLayout) findViewById(R.id.rate_on_google_play);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setUpViews();
    }

    private void setUpViews() {
        setUpAppVersion();
        setUpOnClickListeners();
    }

    private void setUpAppVersion() {
        appVersion.setText(getCurrentVersionName(this));
    }

    private void setUpOnClickListeners() {
        changelog.setOnClickListener(this);
        forkOnGitHub.setOnClickListener(this);
        licenses.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        sendUSFeedback.setOnClickListener(this);
        likeonFB.setOnClickListener(this);
        rateOnGooglePlay.setOnClickListener(this);

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
    public void onClick(View v) {
        if (v == changelog) {
            Change_Log.create().show(getSupportFragmentManager(), "CHANGELOG_DIALOG");
        } else if (v == forkOnGitHub) {
            openUrl(GITHUB);
        } else if (v == licenses) {
            licenseDialog();
        } else if (v == privacyPolicy) {
            Privacy_Policy.create().show(getSupportFragmentManager(), "PRIVACY_POLICY");
        } else if (v == sendUSFeedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:goldsmineapps@gmail.com"));
            intent.putExtra(Intent.EXTRA_EMAIL, "goldsmineapps@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Video Player App");
            startActivity(Intent.createChooser(intent, "E-Mail"));
        } else if (v == likeonFB) {
            openUrl(LIKE_FB);
        } else if (v == rateOnGooglePlay) {
            openUrl(RATE_ON_GOOGLE_PLAY);
        }
    }
private void licenseDialog(){
       new LicensesDialog.Builder(this)
               .setNotices(R.raw.notices)
               .setTitle(R.string.licenses)
               .setNoticesCssStyle(getString(R.string.license_dialog_style)
               .replace("{bg-color}", colorToHex(Color.parseColor("#FFFFFF")))
               .replace("{text-color}",colorToHex(Color.parseColor("#000000")))
               .replace("{license-bg-color}",colorToHex(Color.parseColor("#FFFFFF"))))
               .setIncludeOwnLicense(true)
               .build()
               .showAppCompat();

}
    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    private static String colorToHex(int color) {
        return Integer.toHexString(color).substring(2);
    }
}
