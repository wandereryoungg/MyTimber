package com.young.timber.adapters;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.cast.framework.CastSession;
import com.young.timber.MusicPlayer;
import com.young.timber.activities.BaseActivity;
import com.young.timber.cast.TimberCastHelper;
import com.young.timber.models.Song;
import com.young.timber.utils.NavigationUtils;
import com.young.timber.utils.TimberUtils;

import java.util.List;

public class BaseSongAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final TimberUtils.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {
        if (context instanceof BaseActivity) {
            CastSession castSession = ((BaseActivity) context).getCastSession();
            if (castSession != null) {
                navigateNowPlaying = false;
                TimberCastHelper.startCasting(castSession, currentSong);
            } else {
                MusicPlayer.playAll(context, list, position, -1, TimberUtils.IdType.NA, false);
            }
        } else {
            MusicPlayer.playAll(context, list, position, -1, TimberUtils.IdType.NA, false);
        }
        if (navigateNowPlaying) {
            NavigationUtils.navigateToNowplaying(context, true);
        }

    }

    public void removeSongAt(int i){
    }

    public void updateDataSet(List<Song> arraylist){

    }


}
