package com.young.timber.dataloaders;

import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FolderLoader {
    private static final String[] SUPPORTED_EXT = new String[]{
            "mp3",
            "mp4",
            "m4a",
            "aac",
            "ogg",
            "wav"
    };

    public static List<File> getMediaFiles(File dir, final boolean accepDirs) {
        ArrayList<File> list = new ArrayList<>();
        list.add(new File(dir, ".."));
        if (dir.isDirectory()) {
            List<File> files = Arrays.asList(dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isFile()) {
                        String name = file.getName();
                        return !".nomedia".equals(name) && checkFileExt(name);
                    } else if (file.isDirectory()) {
                        return accepDirs && checkDir(file);
                    } else {
                        return false;
                    }
                }
            }));
            Collections.sort(files, new FilenameComparator());
            Collections.sort(files, new DirFirstComparator());
            list.addAll(files);
        }
        return list;
    }

    private static class DirFirstComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() == o2.isDirectory()) {
                return 0;
            } else if (o1.isDirectory() && !o2.isDirectory()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private static class FilenameComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private static boolean checkDir(File dir) {
        return dir.exists() && dir.canRead() && !".".equals(dir.getName()) && dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return !".".equals(name) && !"..".equals(name) && file.canRead() && (file.isDirectory() || (file.isFile() && checkFileExt(name)));
            }
        }).length != 0;
    }

    private static boolean checkFileExt(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        int p = name.lastIndexOf(".") + 1;
        if (p < 1) {
            return false;
        }
        String ext = name.substring(p).toLowerCase();
        for (String string : SUPPORTED_EXT) {
            if (string.equals(ext)) {
                return true;
            }
        }
        return false;
    }

}
