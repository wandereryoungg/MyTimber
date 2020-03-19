package com.young.timber.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.young.timber.dataloaders.AlbumLoader;
import com.young.timber.models.Album;

public class ImageUtils {


    public static void loadAlbumArtIntoView(final long albumId, final ImageView view, final ImageLoadingListener listener){
        if(PreferencesUtility.getInstance(view.getContext()).alwaysLoadAlbumImagesFromLastfm()){

        }
    }

    private static void loadAlbumArtFromLastfm(long albumId, final ImageView albumArt, final ImageLoadingListener listener){
        Album album = AlbumLoader.getAlbum(albumArt.getContext(),albumId);
    }


}
