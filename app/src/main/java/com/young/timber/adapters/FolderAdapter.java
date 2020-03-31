package com.young.timber.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.timber.R;
import com.young.timber.dataloaders.FolderLoader;
import com.young.timber.dataloaders.SongLoader;
import com.young.timber.models.Song;
import com.young.timber.utils.PreferencesUtility;
import com.young.timber.utils.TimberUtils;
import com.young.timber.widgets.BubbleTextGetter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends BaseSongAdapter<FolderAdapter.ItemHolder> implements BubbleTextGetter {

    private List<File> mFileSet;
    private List<Song> mSongs;
    private File mRoot;
    private Activity mContext;
    private final Drawable[] mIcons;
    private boolean mBusy = false;

    public FolderAdapter(Activity mContext, File root) {
        this.mContext = mContext;
        mIcons = new Drawable[]{
                ContextCompat.getDrawable(mContext, R.drawable.ic_folder_open_black_24dp),
                ContextCompat.getDrawable(mContext, R.drawable.ic_folder_parent_dark),
                ContextCompat.getDrawable(mContext, R.drawable.ic_file_music_dark),
                ContextCompat.getDrawable(mContext, R.drawable.ic_timer_wait)
        };
        mSongs = new ArrayList<>();
        updateDataSet(root);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_folder_list, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        File localFile = mFileSet.get(position);
        Song song = mSongs.get(position);
        holder.title.setText(localFile.getName());
        if (localFile.isDirectory()) {
            holder.albumArt.setImageDrawable("".equals(localFile.getName()) ? mIcons[1] : mIcons[0]);
        } else {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(song.albumId).toString(), holder.albumArt, new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .showImageOnFail(mIcons[2])
                    .resetViewBeforeLoading(true)
                    .build());
        }
    }

    @Override
    public int getItemCount() {
        return mFileSet.size();
    }

    public void updateDataSet(File root) {
        if (mBusy) {
            return;
        }
        if ("..".equals(root.getName())) {
            goUp();
            return;
        }
        mRoot = root;
        mFileSet = FolderLoader.getMediaFiles(mRoot, true);
        getSongsForFiles(mFileSet);


    }

    public boolean updateDataSetAsync(File newRoot) {
        if (mBusy) {
            return false;
        }
        if ("".equals(newRoot.getName())) {
            goUpAsync();
            return false;
        }
        mRoot = newRoot;
        new NavigateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRoot);
        return true;
    }

    private class NavigateTask extends AsyncTask<File, Void, List<File>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBusy = true;
        }

        @Override
        protected List<File> doInBackground(File... params) {
            List<File> files = FolderLoader.getMediaFiles(params[0], true);
            getSongsForFiles(files);
            return files;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            mFileSet = files;
            notifyDataSetChanged();
            mBusy = false;
            PreferencesUtility.getInstance(mContext).storeLastFolder(mRoot.getPath());
        }
    }

    private void getSongsForFiles(List<File> files) {
        mSongs.clear();
        for (File file : files) {
            mSongs.add(SongLoader.getSongFromPath(file.getAbsolutePath(), mContext));
        }
    }

    public boolean goUp() {
        if (mBusy || mRoot == null) {
            return false;
        }
        File parent = mRoot.getParentFile();
        if (parent != null && parent.canRead()) {
            updateDataSet(parent);
            return true;
        } else {
            return false;
        }

    }

    public boolean goUpAsync() {
        if (mBusy || mRoot == null) {
            return false;
        }
        File parent = mRoot.getParentFile();
        if (parent != null && parent.canRead()) {
            return updateDataSetAsync(parent);
        } else {
            return false;
        }
    }

    public void applyTheme(boolean Dark) {
        ColorFilter colorFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        for (Drawable drawable : mIcons) {
            if (Dark) {
                drawable.setColorFilter(colorFilter);
            } else {
                drawable.clearColorFilter();
            }
        }
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (mBusy || mFileSet.size() == 0) {
            return "";
        }
        try {
            File f = mFileSet.get(pos);
            if (f.isDirectory()) {
                return String.valueOf(f.getName().charAt(0));
            } else {
                return Character.toString(f.getName().charAt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView albumArt;
        protected TextView title;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.album_art);
            title = itemView.findViewById(R.id.folder_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mBusy) {
                return;
            }
            final File f = mFileSet.get(getAdapterPosition());
            if (f.isDirectory() && updateDataSetAsync(f)) {
                albumArt.setImageDrawable(mIcons[3]);
            } else if (f.isFile()) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int current = -1;
                        long songId = SongLoader.getSongFromPath(mFileSet.get(getAdapterPosition()).getAbsolutePath(), mContext).id;
                        int count = 0;
                        for (Song song : mSongs) {
                            if (songId != -1) {
                                count++;
                            }
                        }
                        long[] ret = new long[count];
                        int j = 0;
                        for (int i = 0; i < getItemCount(); i++) {
                            if (mSongs.get(i).id != -1) {
                                ret[j] = mSongs.get(i).id;
                                if (mSongs.get(i).id == songId) {
                                    current = j;
                                }
                                j++;
                            }

                        }
                        playAll(mContext, ret, current, -1, TimberUtils.IdType.NA, false, mSongs.get(getAdapterPosition()), false);
                    }
                }, 100);
            }
        }
    }

}
