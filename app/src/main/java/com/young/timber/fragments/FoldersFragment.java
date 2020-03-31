package com.young.timber.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.young.appthemeengine.ATE;
import com.young.timber.R;
import com.young.timber.adapters.FolderAdapter;
import com.young.timber.dialogs.StorageSelectDialog;
import com.young.timber.utils.PreferencesUtility;
import com.young.timber.widgets.DividerItemDecoration;

import java.io.File;

import static com.young.timber.utils.Constants.PREFERENCES_NAME;

public class FoldersFragment extends Fragment implements StorageSelectDialog.OnDirSelectListener {

    private RecyclerView recyclerView;
    private ProgressBar mProgressbar;
    private FolderAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setTitle(R.string.folders);

        recyclerView = view.findViewById(R.id.recyclerview);
        mProgressbar = view.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getActivity() != null) {
            new loadFolders().execute("");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean dark = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("dark_theme", false);
        if (dark) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
        if (mAdapter != null) {
            mAdapter.applyTheme(dark);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_folders, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_storages:
                new StorageSelectDialog(getActivity())
                        .setDirSelectListener(this)
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDirSelected(File dir) {
        mAdapter.updateDataSetAsync(dir);
    }

    private class loadFolders extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            mAdapter = new FolderAdapter(getActivity(), new File(PreferencesUtility.getInstance(getActivity()).getLastFolder()));
            updateTheme();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(mAdapter);
            if (getActivity() != null) {
                setItemDecoration();
            }
            mAdapter.notifyDataSetChanged();
            mProgressbar.setVisibility(View.GONE);
        }
    }

    private void setItemDecoration() {
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    public void updateTheme() {
        Context context = getActivity();
        if (context != null) {
            boolean dark = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("dark_theme", false);
            mAdapter.applyTheme(dark);
        }
    }
}
