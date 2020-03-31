package com.young.timber.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.young.appthemeengine.Config;
import com.young.timber.R;
import com.young.timber.models.Album;
import com.young.timber.utils.Helpers;
import com.young.timber.utils.NavigationUtils;
import com.young.timber.utils.PreferencesUtility;
import com.young.timber.utils.TimberUtils;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemHolder> {

    public List<Album> arrayList;
    public Activity mContext;
    private boolean isGrid;

    public AlbumAdapter(List<Album> arrayList, Activity mContext) {
        this.arrayList = arrayList;
        this.mContext = mContext;
        this.isGrid = PreferencesUtility.getInstance(mContext).isAlbumInGrid();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isGrid) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_album_grid, null);
            ItemHolder holder = new ItemHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_album_list, null);
            ItemHolder holder = new ItemHolder(view);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        Album album = arrayList.get(position);
        holder.title.setText(album.title);
        holder.artist.setText(album.artistName);
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(album.id).toString(), holder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnLoading(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (isGrid) {
                            new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@Nullable Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    if (swatch != null) {
                                        int color = swatch.getRgb();
                                        holder.footer.setBackgroundColor(color);
                                        int textColor = TimberUtils.getBlackWhiteColor(swatch.getTitleTextColor());
                                        holder.title.setTextColor(textColor);
                                        holder.artist.setTextColor(textColor);
                                    } else {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            int color = mutedSwatch.getRgb();
                                            holder.footer.setBackgroundColor(color);
                                            int textColor = TimberUtils.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                            holder.title.setTextColor(textColor);
                                            holder.artist.setTextColor(textColor);
                                        }

                                    }
                                }
                            });
                            if (TimberUtils.isLollipop()) {
                                holder.albumArt.setTransitionName("transition_album_art" + position);
                            }
                        }

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (!isGrid) {
                            holder.footer.setBackgroundColor(0);
                            if (mContext != null) {
                                int textColorPrimary = Config.textColorPrimary(mContext, Helpers.getATEKey(mContext));
                                holder.title.setTextColor(textColorPrimary);
                                holder.artist.setTextColor(textColorPrimary);
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return null != arrayList ? arrayList.size() : 0;
    }

    public void updateDataSet(List<Album> arrayList){
        this.arrayList = arrayList;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, artist;
        protected ImageView albumArt;
        protected View footer;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.album_title);
            artist = itemView.findViewById(R.id.album_artist);
            albumArt = itemView.findViewById(R.id.album_art);
            footer = itemView.findViewById(R.id.footer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtils.navigateToAlbum(mContext,arrayList.get(getAdapterPosition()).id,
                    new Pair<View, String>(albumArt,"transition_album_art" + getAdapterPosition()));
        }
    }

}
