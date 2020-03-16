package com.young.timber.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.young.timber.MusicPlayer;
import com.young.timber.dataloaders.PlaylistLoader;
import com.young.timber.models.Playlist;
import com.young.timber.models.Song;

import java.util.List;

public class AddPlaylistDialog extends DialogFragment {

    public static AddPlaylistDialog newInstance(Song song) {
        long[] songs = new long[1];
        songs[0] = song.id;
        return newInstance(songs);
    }

    public static AddPlaylistDialog newInstance(long[] songList) {
        AddPlaylistDialog dialog = new AddPlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs", songList);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), false);
        CharSequence[] chars = new CharSequence[playlists.size() + 1];
        chars[0] = "Create new playlist";
        for (int i = 0; i < playlists.size(); i++) {
            chars[i+1] = playlists.get(i).name;
        }
        return new MaterialDialog.Builder(getActivity())
                .title("add to playlist")
                .items(chars)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        long[] songs = getArguments().getLongArray("songs");
                        if(position == 0){
                            CreatePlaylistDialog.newInstance(songs).show(getActivity().getSupportFragmentManager(),"CREATE_PLAYLIST");
                            return;
                        }
                        MusicPlayer.addToPlaylist(getActivity(),songs,playlists.get(position-1).id);
                        dialog.dismiss();
                    }
                })
                .build();
    }
}
