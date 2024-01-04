package com.example.vault.gallery;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;

public class GalleryFileSort implements Comparator<GalleryEnt> {
    int _SortBy = 0;

    public GalleryFileSort(int i) {
        this._SortBy = i;
    }

    public int compare(GalleryEnt galleryEnt, GalleryEnt galleryEnt2) {
        int i = this._SortBy;
        if (i == 0) {
            return galleryEnt2.get_galleryfileName().toUpperCase(Locale.getDefault()).compareTo(galleryEnt.get_galleryfileName().toUpperCase(Locale.getDefault()));
        } else if (i == 1) {
            return galleryEnt.get_modifiedDateTime().compareTo(galleryEnt2.get_modifiedDateTime());
        } else {
            return Integer.valueOf((int) new File(galleryEnt.get_folderLockgalleryfileLocation()).length()).compareTo(Integer.valueOf((int) new File(galleryEnt2.get_folderLockgalleryfileLocation()).length()));
        }
    }
}
