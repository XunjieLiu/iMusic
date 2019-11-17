package com.example.imusic.Entity;

import java.io.Serializable;

public class Song implements Serializable {
    private String singer;
    private String song;
    private String path;
    private int duration;
    private long size;
    private String id;

    public Song(){
        super();
    }

    public Song(String singer, String song, String id, int duration){
        this.singer = singer;
        this.song  = song;
        this.id = id;
        this.duration = duration;
    }
    public  Song(String singer, String song, String path){
        this.singer = singer;
        this.song  = song;
        this.path = path;
    }

    public String getId(){
        return id;
    }


    public String getSinger(){
        return singer;
    }

    public String getSong(){
        return song;
    }

    public String getPath(){
        return path;
    }

    public int getDuration(){
        return duration;
    }

    public long getSize(){
        return size;
    }

    public void setSong(String song){
        this.song = song;
    }

    public void setSinger(String singer){
        this.singer = singer;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public void setSize (long size){
        this.size = size;
    }
}

