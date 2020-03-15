package com.young.timber.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.young.timber.MusicPlayer;
import com.young.timber.fragments.PlaylistFragment;
import com.young.timber.models.Song;

public class CreatePlaylistDialog extends DialogFragment {

    public static CreatePlaylistDialog newInstance() {
        return newInstance((Song) null);
    }

    public static CreatePlaylistDialog newInstance(Song song) {
        long[] songs;
        if (song == null) {
            songs = new long[0];
        } else {
            songs = new long[1];
            songs[0] = song.id;
        }
        return newInstance(songs);
    }

    public static CreatePlaylistDialog newInstance(long[] songs) {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs", songs);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity()).positiveText("Create").negativeText("Cancel")
                .input("Enter playlist name", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        long[] songs = getArguments().getLongArray("songs");
                        long playlistId = MusicPlayer.createPlaylist(getActivity(), input.toString());
                        if (playlistId != -1) {
                            if (songs != null && songs.length != 0) {
                                MusicPlayer.addToPlaylist(getActivity(), songs, playlistId);
                            } else {
                                Toast.makeText(getActivity(), "Created playlist", Toast.LENGTH_SHORT).show();
                            }
                            if(getParentFragment() instanceof PlaylistFragment){
                                getParentFragment()
                            }
                        }
                    }
                }).build();
    }
}
