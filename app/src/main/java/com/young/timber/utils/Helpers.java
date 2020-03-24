package com.young.timber.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.young.timber.R;

import static com.young.timber.utils.Constants.PREFERENCES_NAME;

public class Helpers {

    public static void showAbout(AppCompatActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag("dialog_about");
        if (prev != null) {
            transaction.remove(prev);
        }
        transaction.addToBackStack(null);
        new AboutDialog().show(manager, "dialog_about");
    }

    public static String getATEKey(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getBoolean("dark_theme", false) ? "dark_theme" : "light_theme";
    }

    public static class AboutDialog extends DialogFragment {

        String urlgithub = "https://github.com/wandereryoungg/Timber";
        String urlSource = "https://github.com/wandereryoungg/MyTimber/issues";

        public AboutDialog() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_about_dialog, null);
            TextView appversion = linearLayout.findViewById(R.id.app_version_name);
            TextView googleplus = linearLayout.findViewById(R.id.googleplus);
            TextView twitter = linearLayout.findViewById(R.id.twitter);
            TextView github = linearLayout.findViewById(R.id.github);
            TextView source = linearLayout.findViewById(R.id.source);
            TextView community = linearLayout.findViewById(R.id.feature_request);
            final TextView dismiss = linearLayout.findViewById(R.id.dismiss_dialog);

            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            googleplus.setPaintFlags(googleplus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            twitter.setPaintFlags(twitter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            github.setPaintFlags(github.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            github.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urlgithub));
                    startActivity(intent);
                }
            });
            source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urlSource));
                    startActivity(intent);
                }
            });
            try {
                PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                String version = info.versionName;
                appversion.setText("Timber " + version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return new AlertDialog.Builder(getActivity()).setView(linearLayout).create();
        }
    }
}
