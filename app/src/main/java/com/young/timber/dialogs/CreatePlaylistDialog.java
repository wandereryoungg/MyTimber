package com.young.timber.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.young.timber.models.Song;

public class CreatePlaylistDialog extends DialogFragment {

    public static CreatePlaylistDialog newInstance(){
        return newInstance((Song) null);
    }

    public static CreatePlaylistDialog newInstance(Song song){
        long[] songs;
        if(song ==null){
            songs = new long[0];
        }else{
            songs = new long[1];
            songs[0] = song.id;
        }
        return newInstance(songs);
    }

    public static CreatePlaylistDialog newInstance(long[] songs){
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs",songs);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
