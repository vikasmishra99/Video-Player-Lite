package com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.dailogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.R;
import com.videoplayer.hdvideoplayer.lightvideoplayer.allformatplayer.Util.PreferencesUtility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Privacy_Policy extends DialogFragment {
    public static Privacy_Policy create() {
        return new Privacy_Policy();
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getContext()).inflate(R.layout.web_view_dialog, null);
        } catch (InflateException e) {
            e.printStackTrace();
            return new MaterialDialog.Builder(getContext())
                    .title(R.string.dialog_privacy_title)
                    .content("This device doesn't support web view, which is necessary to view the change log. It is missing a system component.")
                    .positiveText(R.string.ok)
                    .build();
        }
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.privacy_policy)
                .customView(customView, false)
                .positiveText(android.R.string.ok)
                .showListener(dialog1 -> {
                    if (getContext() != null)
                        setChangelogRead(getContext());
                }).build();
        final WebView webView = customView.findViewById(R.id.web_view);
        try {
// Load from change_log.html in the assets folder
            StringBuilder buf = new StringBuilder();
            InputStream json = getContext().getApplicationContext().getAssets().open("privacy_policy.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();
            // Inject color values for WebView body background and links
            final String backgroundColor = colorToHex(Color.parseColor("#FFFFFF"));
            final String contentColor = colorToHex(Color.parseColor("#454545"));
            webView.loadData(buf.toString()
                    .replace("{style-placeholder}", String.format("body { background-color: %s; color: %s; }", backgroundColor, contentColor))
                    .replace("{link-color}", colorToHex(Color.parseColor("#454545")))
                    .replace("{link-color-active}", colorToHex(Color.parseColor("#454545"))), "text/html", "UTF-8"
            );
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }
        return dialog;
    }

    public static void setChangelogRead(@NonNull Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int currentVersion = pInfo.versionCode;
            PreferencesUtility.getInstance(context).setLastChangeLogVersion(currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String colorToHex(int color) {
        return Integer.toHexString(color).substring(2);
    }
}
