package com.young.timber.adapters;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.appthemeengine.Config;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.timber.MusicPlayer;
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

        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(), holder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true).
                        showImageOnLoading(R.drawable.ic_empty_music2).
                        resetViewBeforeLoading(true).
                        build());
        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            holder.title.setTextColor(Config.accentColor(mContext, ateKey));
            if (MusicPlayer.isPlaying()) {
                holder.visualizer.setColor(Config.accentColor(mContext, ateKey));
                holder.visualizer.setVisibility(View.VISIBLE);
            } else {
                holder.visualizer.setVisibility(View.GONE);
            }
        } else {
            holder.visualizer.setVisibility(View.GONE);
            if (isPlaylist) {
                holder.title.setTextColor(Color.WHITE);
            } else {
                holder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
            }
        }
        if (animate && isPlaylist) {
            if (TimberUtils.isLollipop()) {
                setAnimation(holder.itemView, position);
            } else {
                if (position > 10) {
                    setAnimation(holder.itemView, position);
                }
            }
        }


    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, androidx.appcompat.R.anim.abc_slide_in_bottom);
        viewToAnimate.setAnimation(animation);
        lastPosition = position;
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position){
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext,v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext,songIDs,position,-1, TimberUtils.IdType.NA,false);
                                break;
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arrayList.get(position).id;
                                MusicPlayer.playNext(mContext,ids,-1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arrayList.get(position).id;
                                MusicPlayer.addToQueue(mContext,id,-1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:

                                break;
                        }
                        return false;
                    }
                });

            }
        });
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
