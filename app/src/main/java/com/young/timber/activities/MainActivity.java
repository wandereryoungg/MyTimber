package com.young.timber.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.young.timber.R;
import com.young.timber.fragments.FoldersFragment;
import com.young.timber.fragments.MainFragment;
import com.young.timber.fragments.PlaylistFragment;
import com.young.timber.fragments.QueueFragment;
import com.young.timber.permissions.PermissionCallback;
import com.young.timber.permissions.Young;
import com.young.timber.slidinguppanel.SlidingUpPanelLayout;
import com.young.timber.utils.Helpers;
import com.young.timber.utils.NavigationUtils;
import com.young.timber.utils.TimberUtils;

import java.util.HashMap;
import java.util.Map;

import static com.young.timber.utils.Constants.NAVIGATE_LIBRARY;
import static com.young.timber.utils.Constants.NAVIGATE_NOWPLAYING;
import static com.young.timber.utils.Constants.NAVIGATE_PLAYLIST;
import static com.young.timber.utils.Constants.NAVIGATE_QUEUE;
import static com.young.timber.utils.Constants.PREFERENCES_NAME;

public class MainActivity extends BaseActivity {

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
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        }
    };

    private Runnable navigatePlaylist = new Runnable() {
        @Override
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_playlists).setChecked(true);
            Fragment fragment = new PlaylistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateFolder = new Runnable() {
        @Override
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_folders).setChecked(true);
            Fragment fragment = new FoldersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateQueue = new Runnable() {
        @Override
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
            Fragment fragment = new QueueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment);
        }
    };
    //TODO 专辑,艺术家和歌词详情Fragment

    private Runnable navigateNowplaying = new Runnable() {
        @Override
        public void run() {
            navigateLibray.run();
            startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
        }
    };

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if(navigation!=null){
            navigation.run();
        }else{
            navigateLibray.run();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        action = getIntent().getAction();
        isDarkTheme = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).getBoolean("dark_theme", false);

        navigationMap.put(NAVIGATE_LIBRARY, navigateLibray);
        navigationMap.put(NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(NAVIGATE_NOWPLAYING, navigateNowplaying);
        //TODO 专辑,艺术家和歌词详情Fragment

        mDrawerLayout = findViewById(R.id.drawer_layout);
        panelLayout = findViewById(R.id.sliding_layout);
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        albumArt = header.findViewById(R.id.album_art);
        songTitle = header.findViewById(R.id.song_title);
        songArtist = header.findViewById(R.id.song_artist);

        setPanelSlideListeners(panelLayout);
        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
                setupNavigationIcons(navigationView);
            }
        }, 700);

        if (TimberUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            loadEverything();
        }

        addBackstackListener();
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }

    private void checkPermissionAndThenLoad() {
        if (Young.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && Young.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Young.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "Timber will need to read external storage to display songs on your device.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Young.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        })
                        .show();
            } else {
                Young.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }

    private void setupNavigationIcons(NavigationView navigationView) {
        if (!isDarkTheme) {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
            navigationView.getMenu().findItem(R.id.nav_donate).setIcon(R.drawable.payment_black);
        } else {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music_white);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music_white);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information_white);
            navigationView.getMenu().findItem(R.id.nav_donate).setIcon(R.drawable.payment_white);
        }
        try {
            if (!BillingProcessor.isIabServiceAvailable(this)) {
                navigationView.getMenu().removeItem(R.id.nav_donate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                updatePosition(menuItem);
                return true;
            }
        });
    }

    private void updatePosition(MenuItem menuItem) {
        runnable = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateLibray;
                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;
                break;
            case R.id.nav_folders:
                runnable = navigateFolder;
                break;
            case R.id.nav_nowplaying:
                if (getCastSession() != null) {
                    startActivity(new Intent(this, ExpandedControlsActivity.class));
                } else {
                    NavigationUtils.navigateToNowplaying(this, false);
                }
                break;
            case R.id.nav_queue:
                runnable = navigateQueue;
                break;
            case R.id.nav_settings:
                NavigationUtils.navigateToSettings(this);
                break;
            case R.id.nav_about:
                mDrawerLayout.closeDrawers();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Helpers.showAbout(MainActivity.this);
                    }
                }, 300);
                break;
            case R.id.nav_donate:
                startActivity(new Intent(MainActivity.this, DonateActivity.class));
                break;
        }
        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

}
