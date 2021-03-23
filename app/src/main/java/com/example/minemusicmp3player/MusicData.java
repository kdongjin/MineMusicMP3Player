package com.example.minemusicmp3player;

import java.util.Objects;

public class MusicData {
    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private String duration;
    private int playCount;
    private int liked;

    public MusicData() {
    }

    public MusicData(String id, String artist, String title, String albumArt, String duration, int playCount, int liked) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        this.playCount = playCount;
        this.liked = liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    //MusicData 7가지 멤버가 들어갔는데. id 값이 같으면 true, 다르면 false
    @Override
    public boolean equals(Object object) {
        boolean equal = false;
        if(object instanceof MusicData){
            MusicData musicData=(MusicData)object;
            equal=this.id.equals(musicData.getId());
        }
        return equal;
    }

}
