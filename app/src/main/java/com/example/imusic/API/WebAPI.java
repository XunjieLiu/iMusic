package com.example.imusic.API;

import com.example.imusic.Entity.Song;

import org.json.JSONObject;

import java.util.ArrayList;

public interface WebAPI {

    /*
    * Input: song name like "Dance monkey", and instantiate song objects for every result
    *
    * Return: songs array, but without mp3 url, need findURL to fill up
    * */
    public ArrayList<Song> getSong(String name);

    /*
    * Input: songs array. without mp3 url
    *
    * Void: modify every song object in this array, fill them with URL according to their song id
    * */
    public void findURL(ArrayList<Song> songs);

    public ArrayList<Song> readJSON(JSONObject jsonObject);

    /*
    * According to one single song JSONObject, get all information such as singer name, album pic url
    * */
    public Song getSingle(JSONObject single);

    public JSONObject query(String url);
}
