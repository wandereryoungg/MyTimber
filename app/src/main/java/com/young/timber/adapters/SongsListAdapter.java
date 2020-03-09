package com.young.timber.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.timber.R;
import com.young.timber.models.Song;
import com.young.timber.utils.Helpers;
import com.young.timber.utils.TimberUtils;
import com.young.timber.widgets.BubbleTextGetter;
import com.young.timber.widgets.MusicVisualizer;

import java.util.List;

public class SongsListAdapter extends BaseSongAdapter<SongsListAdapter.ItemHolder> implements BubbleTextGetter {

    public int currentlyPlayingPosition;
    private List<Song> arrayList;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private String ateKey;
    private int playlistId;

    public SongsListAdapter(List<Song> arrayList, AppCompatActivity mContext, boolean isPlaylist, boolean animate) {
        this.arrayList = arrayList;
        this.mContext = mContext;
        this.isPlaylist = isPlaylist;
        this.animate = animate;
        this.songIDs = getSongIds();
        this.ateKey = Helpers.getATEKey(mContext);
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isPlaylist) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_song_playlist, parent, false);
            return new ItemHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Song localItem = arrayList.get(position);
        holder.title.setText(localItem.title);
        holder.artist.setText(localItem.artistName);

        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(),holder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true).
                showImageOnLoading(R.drawable.ic_empty_music2).
                resetViewBeforeLoading(true).
                build());

    }

    private long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arrayList.get(i).id;
        }
        return ret;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, artist;
        protected ImageView albumArt, popupMenu;
        private MusicVisualizer visualizer;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.song_title);
            artist = itemView.findViewById(R.id.song_artist);
            albumArt = itemView.findViewById(R.id.album_art);
            popupMenu = itemView.findViewById(R.id.popup_menu);
            visualizer = itemView.findViewById(R.id.visualizer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAll(mContext, songIDs, getAdapterPosition(), -1, TimberUtils.IdType.NA, false, arrayList.get(getAdapterPosition()), false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(currentlyPlayingPosition);
                            notifyItemChanged(getAdapterPosition());
                        }
                    }, 50);
                }
            }, 100);

        }
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return null;
    }
}
