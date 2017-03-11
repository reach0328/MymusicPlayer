package com.android.jh.mymusicplayer.Data.Domain;

import android.net.Uri;

import java.util.List;


public class Album extends Common {
    public int id;
    public String artist;
    public String album;
    public String album_art;
    public String album_key;
    public String numberofsong;
    public Uri album_img;
    public String yaer;
    public List<Music> musics;

    public String getYaer() {
        return yaer;
    }

    public void setYaer(String yaer) {
        this.yaer = yaer;
    }

    public Uri getAlbum_img() {
        return album_img;
    }

    public void setAlbum_img(Uri album_img) {
        this.album_img = album_img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getAlbum_key() {
        return album_key;
    }

    public void setAlbum_key(String album_key) {
        this.album_key = album_key;
    }

    public String getNumberofsong() {
        return numberofsong;
    }

    public void setNumberofsong(String numberofsong) {
        this.numberofsong = numberofsong;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    @Override
    public String getTitle() {
        return album;
    }

    @Override
    public String getArtist() {
        return numberofsong;
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
        return album_img;
    }

    @Override
    public List<Music> getList() {
        return musics;
    }
}
