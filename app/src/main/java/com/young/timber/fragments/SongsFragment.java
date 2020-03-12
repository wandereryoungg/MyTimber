package com.young.timber.fragments;

import androidx.fragment.app.Fragment;

import com.young.timber.adapters.SongsListAdapter;
import com.young.timber.utils.PreferencesUtility;
import com.young.timber.widgets.BaseRecyclerView;

public class SongsFragment extends Fragment {

    private SongsListAdapter mAdapter;
    private BaseRecyclerView recyclerView;
    private PreferencesUtility mPreferences;

}
