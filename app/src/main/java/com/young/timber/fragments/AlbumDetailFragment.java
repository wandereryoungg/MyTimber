package com.young.timber.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.young.timber.R;
import com.young.timber.adapters.AlbumSongAdapter;
import com.young.timber.dataloaders.AlbumLoader;
import com.young.timber.models.Album;
import com.young.timber.utils.Constants;
import com.young.timber.utils.PreferencesUtility;

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

        return view;
    }

    private void setAlbumart(){

    }
}
