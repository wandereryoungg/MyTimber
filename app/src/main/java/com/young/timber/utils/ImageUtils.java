package com.young.timber.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.young.timber.R;
import com.young.timber.dataloaders.AlbumLoader;
import com.young.timber.lastfmapi.LastFmClient;
import com.young.timber.lastfmapi.callbacks.AlbumInfoListener;
import com.young.timber.lastfmapi.models.AlbumQuery;
import com.young.timber.lastfmapi.models.LastfmAlbum;
import com.young.timber.models.Album;

public class ImageUtils {

    private static final DisplayImageOptions lastfmDisplayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageOnFail(R.drawable.ic_empty_music2)
            .build();

    private static final DisplayImageOptions diskDisplayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .build();


    public static void loadAlbumArtIntoView(final long albumId, final ImageView view, final ImageLoadingListener listener) {
        if (PreferencesUtility.getInstance(view.getContext()).alwaysLoadAlbumImagesFromLastfm()) {
            loadAlbumArtFromLastfm(albumId, view, listener);
        } else {
            loadAlbumArtFromDiskWithLastfmFallback(albumId, view, listener);
        }
    }

    private static void loadAlbumArtFromDiskWithLastfmFallback(final long albumId, final ImageView albumArt, final ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(albumId).toString(), albumArt, diskDisplayImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                loadAlbumArtFromLastfm(albumId, albumArt, listener);
                listener.onLoadingFailed(imageUri, view, failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                listener.onLoadingComplete(imageUri, view, loadedImage);
            }
        });
    }

    private static void loadAlbumArtFromLastfm(long albumId, final ImageView albumArt, final ImageLoadingListener listener) {
        Album album = AlbumLoader.getAlbum(albumArt.getContext(), albumId);
        LastFmClient.getInstance(albumArt.getContext()).getAlbumInfo(new AlbumQuery(album.title, album.artistName), new AlbumInfoListener() {
            @Override
            public void albumInfoSuccess(LastfmAlbum album) {
                Log.e("young","album==null"+album);
                if (album != null) {
                    ImageLoader.getInstance().displayImage(album.getmArtwork().get(4).mUrl, albumArt, lastfmDisplayImageOptions, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            listener.onLoadingStarted(imageUri, view);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            listener.onLoadingFailed(imageUri, view, failReason);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            listener.onLoadingComplete(imageUri, view, loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            listener.onLoadingCancelled(imageUri, view);
                        }
                    });
                }
            }

            @Override
            public void albumInfoFailed() {

            }
        });

    }


}
