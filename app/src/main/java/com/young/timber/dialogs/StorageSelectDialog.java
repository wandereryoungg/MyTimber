package com.young.timber.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;

import com.young.timber.R;

import java.io.File;
import java.io.FileFilter;

public class StorageSelectDialog implements DialogInterface.OnClickListener {

    private final AlertDialog mDialog;
    private final File[] mStorages;
    private OnDirSelectListener mDirSelectListener;

    public StorageSelectDialog(final Context context) {
        mStorages = getAvailableStorages(context);
        String[] names = new String[mStorages.length];
        for(int i=0;i<names.length;i++){
            names[i] = mStorages[i].getName();
        }
        mDialog = new AlertDialog.Builder(context)
                .setItems(names,this)
                .setNegativeButton(android.R.string.cancel,null)
                .setNeutralButton(R.string.menu_show_as_entry_default, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDirSelectListener.onDirSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
                    }
                })
                .setCancelable(true)
                .setTitle(R.string.select_storage)
                .create();
    }

    public StorageSelectDialog setDirSelectListener(OnDirSelectListener onDirSelectListener){
        this.mDirSelectListener = onDirSelectListener;
        return this;
    }

    public void show(){
        mDialog.show();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        File dir = mStorages[which];
        mDirSelectListener.onDirSelected(dir);
    }

    public interface OnDirSelectListener {
        void onDirSelected(File dir);
    }

    private static File[] getAvailableStorages(Context context) {
        File storageRoot = new File("/storage");
        return storageRoot.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.canRead();
            }
        });
    }
}
