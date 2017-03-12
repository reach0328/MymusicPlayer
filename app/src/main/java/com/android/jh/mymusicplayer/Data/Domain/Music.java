package com.android.jh.mymusicplayer.Data.Domain;

import android.net.Uri;

import com.android.jh.mymusicplayer.util.TimeUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "music")
public class Music extends Common {
    @DatabaseField(generatedId = true)
    public int id;
    // music info.
    @DatabaseField
    public int _id;
    @DatabaseField
    public String music_uri_string;
    public Uri music_uri;
    @DatabaseField
    public String title;
    @DatabaseField
    public int artist_id;
    @DatabaseField
    public String artist;
    @DatabaseField
    public String artist_key;
    @DatabaseField
    public String album;
    @DatabaseField
    public int album_id;
    @DatabaseField
    public String album_image_uri_string;
    public Uri album_image_uri;
    @DatabaseField
    public int duration;
    @DatabaseField
    public String is_music;
    @DatabaseField
    public String composer;
    @DatabaseField
    public String year;


    public Music() {

    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    // add info.
    public int order;
    public boolean favor;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getDurationText() {
        return TimeUtil.convertMiliToTime(duration);
    }

    @Override
    public Uri getImageUri() {
        return album_image_uri;
    }

    @Override
    public List<Music> getList() {
        return null;
    }
}
