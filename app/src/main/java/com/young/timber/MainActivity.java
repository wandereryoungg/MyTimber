package com.young.timber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.young.timber.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SlidingUpPanelLayout panelLayout;
    private NavigationView navigationView;
    private TextView songTitle, songArtist;
    private ImageView albumArt;
    private String action;
    private Map<String, Runnable> navigationMap = new HashMap<>();
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;
    private DrawerLayout mDrawerLayout;
    private boolean isDarkTheme;

    private Runnable navigateLibray = new Runnable() {
        @Override
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
