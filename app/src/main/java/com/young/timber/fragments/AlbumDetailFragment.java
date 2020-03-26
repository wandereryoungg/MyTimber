package com.young.timber.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.young.appthemeengine.ATE;
import com.young.appthemeengine.Config;
import com.young.timber.MusicPlayer;
import com.young.timber.R;
import com.young.timber.adapters.AlbumSongAdapter;
import com.young.timber.dataloaders.AlbumLoader;
import com.young.timber.dataloaders.AlbumSongLoader;
import com.young.timber.dialogs.AddPlaylistDialog;
import com.young.timber.models.Album;
import com.young.timber.models.Song;
import com.young.timber.utils.ATEUtils;
import com.young.timber.utils.Constants;
import com.young.timber.utils.Helpers;
import com.young.timber.utils.ImageUtils;
import com.young.timber.utils.NavigationUtils;
import com.young.timber.utils.PreferencesUtility;
import com.young.timber.utils.SortOrder;
import com.young.timber.utils.TimberUtils;
import com.young.timber.widgets.DividerItemDecoration;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

public class AlbumDetailFragment extends Fragment {

    private long albumId = -1;
    private ImageView albumArt;
    private TextView albumTitle, albumDetail;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private AlbumSongAdapter mAdapter;
    private AppCompatActivity mContext;
    private Context context;
    private PreferencesUtility mPreferences;
    private Album album;
    private boolean loadFailed = false;
    private int primaryColor = -1;

    public static AlbumDetailFragment newInstance(long id, boolean useTransition, String transitionName) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ALBUM_ID, id);
        bundle.putBoolean("transition", useTransition);
        if (useTransition) {
            bundle.putString("transition_name", transitionName);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getLong(Constants.ALBUM_ID);
        }
        context = getActivity();
        mContext = (AppCompatActivity) context;
        mPreferences = PreferencesUtility.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_detail, container, false);
        albumArt = view.findViewById(R.id.album_art);
        albumTitle = view.findViewById(R.id.album_title);
        albumDetail = view.findViewById(R.id.album_details);
        toolbar = view.findViewById(R.id.toolbar);
        fab = view.findViewById(R.id.fab);
        if (getArguments().getBoolean("transition")) {
            albumArt.setTransitionName(getArguments().getString("transition_name"));
        }
        appBarLayout = view.findViewById(R.id.app_bar);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        album = AlbumLoader.getAlbum(context, albumId);
        setAlbumart();
        setUpEverything();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlbumSongAdapter adapter = (AlbumSongAdapter) recyclerView.getAdapter();
                        MusicPlayer.playAll(getActivity(), adapter.getSongIds(), 0, albumId, TimberUtils.IdType.Album, true);
                        NavigationUtils.navigateToNowplaying(getActivity(), false);
                    }
                }, 150);
            }
        });
        return view;
    }

    private void setUpEverything() {
        setupToolbar();
        setAlbumDetails();
        setUpAlbumSongs();
    }

    private void setUpAlbumSongs() {
        List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumId);
        mAdapter = new AlbumSongAdapter(songList, getActivity(), albumId);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
    }

    private void setAlbumDetails() {
        String songCount = TimberUtils.makeLabel(context, R.plurals.Nsongs, album.songCount);
        String year = (album.year != 0) ? ("-" + String.valueOf(album.year)) : "";
        albumTitle.setText(album.title);
        albumDetail.setText(album.artistName + "-" + songCount + year);
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(album.title);
    }

    private void setAlbumart() {
        ImageUtils.loadAlbumArtIntoView(album.id, albumArt, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                loadFailed = true;
                MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(context)
                        .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                        .setColor(TimberUtils.getBlackWhiteColor(Config.accentColor(context, Helpers.getATEKey(context))));
                ATEUtils.setFabBackgroundTint(fab, Config.accentColor(context, Helpers.getATEKey(context)));
                fab.setImageDrawable(builder.build());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (swatch != null) {
                            primaryColor = swatch.getRgb();
                            collapsingToolbarLayout.setContentScrimColor(primaryColor);
                            if (getActivity() != null) {
                                ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
                            }
                        } else {
                            Palette.Swatch swatchMuted = palette.getMutedSwatch();
                            if (swatchMuted != null) {
                                primaryColor = swatchMuted.getRgb();
                                collapsingToolbarLayout.setContentScrimColor(primaryColor);
                                if (getActivity() != null) {
                                    ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
                                }
                            }
                        }
                        if (getActivity() != null) {
                            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                                    .setSizeDp(30);
                            if (primaryColor != -1) {
                                builder.setColor(TimberUtils.getBlackWhiteColor(primaryColor));
                                ATEUtils.setFabBackgroundTint(fab, primaryColor);
                                fab.setImageDrawable(builder.build());
                            } else {
                                if (context != null) {
                                    ATEUtils.setFabBackgroundTint(fab, Config.accentColor(context, Helpers.getATEKey(context)));
                                    builder.setColor(TimberUtils.getBlackWhiteColor(Config.accentColor(context, Helpers.getATEKey(context))));
                                    fab.setImageDrawable(builder.build());
                                }
                            }
                        }

                    }
                });

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.album_detail, menu);
        if (getActivity() != null) {
            ATE.applyMenu(getActivity(), "dark_theme", menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_go_to_artist:
                NavigationUtils.goToArtist(getActivity(), album.artistId);
                break;
            case R.id.popup_song_addto_queue:
                MusicPlayer.addToQueue(getActivity(), mAdapter.getSongIds(), -1, TimberUtils.IdType.NA);
                break;
            case R.id.popup_song_addto_playlist:
                AddPlaylistDialog.newInstance(mAdapter.getSongIds()).show(getActivity().getSupportFragmentManager(), "ADD_PLAYLIST");
                break;
            case R.id.menu_sort_by_az:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_YEAR);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_duration:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_DURATION);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_track_number:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
                reloadAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String ateKey = Helpers.getATEKey(getActivity());
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        if (primaryColor != -1 && getActivity() != null) {
            collapsingToolbarLayout.setContentScrimColor(primaryColor);
            ATEUtils.setFabBackgroundTint(fab, primaryColor);
            ATEUtils.setStatusBarColor(getActivity(), ateKey, primaryColor);
        }
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumId);
                mAdapter.updateDataSet(songList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
