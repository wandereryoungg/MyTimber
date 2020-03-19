package com.young.timber.adapters;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.young.timber.MusicPlayer;
import com.young.timber.R;
import com.young.timber.dialogs.AddPlaylistDialog;
import com.young.timber.models.Song;
import com.young.timber.utils.NavigationUtils;
import com.young.timber.utils.TimberUtils;

import java.util.List;

public class AlbumSongAdapter extends BaseSongAdapter<AlbumSongAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    private long albumId;
    private long[] songIds;

    public AlbumSongAdapter(List<Song> arraylist, Activity context, long albumId) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.albumId = albumId;
        songIds = getSongIds();
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }
        return ret;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_song, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Song localItem = arraylist.get(position);
        holder.title.setText(localItem.title);
        holder.duration.setText(TimberUtils.makeShortTimeString(mContext, (localItem.duration) / 1000));
        int trackNumber = localItem.trackNumber;
        if (trackNumber == 0) {
            holder.trackNumber.setText("-");
        } else {
            holder.trackNumber.setText(String.valueOf(trackNumber));
        }
        setOnPopupMenuListener(holder, position);
        super.onBindViewHolder(holder, position);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
        itemHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext, songIds, position, -1, TimberUtils.IdType.NA, false);
                                break;
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arraylist.get(position).id;
                                MusicPlayer.playNext(mContext, ids, -1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arraylist.get(position).id;
                                MusicPlayer.addToQueue(mContext, id, -1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:
                                AddPlaylistDialog.newInstance(arraylist.get(position)).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ADD_PLAYLIST");
                                break;
                            case R.id.popup_song_goto_album:
                                NavigationUtils.goToAlbum(mContext, arraylist.get(position).albumId);
                                break;
                            case R.id.popup_song_goto_artist:
                                NavigationUtils.goToArtist(mContext, arraylist.get(position).artistId);
                                break;
                            case R.id.popup_song_share:
                                TimberUtils.shareTrack(mContext, arraylist.get(position).id);
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteId = {arraylist.get(position).id};
                                TimberUtils.showDeleteDialog(mContext, arraylist.get(position).title, deleteId, AlbumSongAdapter.this, position);
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != arraylist ? arraylist.size() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, duration, trackNumber;
        protected ImageView menu;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.song_title);
            duration = itemView.findViewById(R.id.song_duration);
            trackNumber = itemView.findViewById(R.id.trackNumber);
            menu = itemView.findViewById(R.id.popup_menu);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAll(mContext, songIds, getAdapterPosition(), albumId, TimberUtils.IdType.Album, false, arraylist.get(getAdapterPosition()), true);
                }
            }, 100);

        }
    }

    @Override
    public void updateDataSet(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIds = getSongIds();
    }

    @Override
    public void removeSongAt(int i) {
        arraylist.remove(i);
    }
}
