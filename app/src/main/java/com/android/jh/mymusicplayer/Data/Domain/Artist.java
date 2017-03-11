package com.android.jh.mymusicplayer.Data.Domain;

import android.net.Uri;

import java.util.List;

/**
 * Created by pc on 2/28/2017.
 */

public class Artist extends Common{
    public int id;
    public String artist;
    public String artist_key;
    public int album_id;
    public Uri album_image_uri;
    public int number_of_tracks;
    public int number_of_albums;
    public List<Music> musics;

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    @Override
    public String getTitle() {
        return artist;
    }

    @Override
    public String getArtist() {
        return "Tracks : " + number_of_tracks;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public String getDurationText() {
        return null;
    }

    @Override
    public Uri getImageUri() {
        return album_image_uri;
    }

    @Override
    public List<Music> getList() {
        return musics;
    }
}
