package com.example.imusic.Utils;

import com.example.imusic.Entity.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetEaseUtils {

//    @Override
    public static ArrayList<Song> getSong(String name) {
        ArrayList<Song> songList = new ArrayList<>();
        try{
            songList = readJSON(query("http://118.89.196.158:3000/search?keywords=" + name + "&limit=10"));
            findURL(songList);
        }catch (Exception e){
            e.printStackTrace();
        }

        return songList;
    }

//    @Override
    public static void findURL(ArrayList<Song> songs) {
        try{
            for(Song s:songs){
                String id = s.getId();
                JSONObject jsonObject = query("http://118.89.196.158:3000/song/url?id=" + id);
                JSONArray data = jsonObject.optJSONArray("data");
                JSONObject info = data.getJSONObject(0);
                String url = info.optString("url");

                s.setPath(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    @Override
    public static ArrayList<Song> readJSON(JSONObject jsonObject) {
        ArrayList<Song> list = new ArrayList<Song>();
        try{
            JSONObject result = jsonObject.optJSONObject("result");
            JSONArray songs = result.optJSONArray("songs");

            for(int i = 0; i < songs.length(); i++){
                JSONObject single = songs.getJSONObject(i);
                Song s = getSingle(single);

                list.add(s);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

//    @Override
    public static Song getSingle(JSONObject single) {
        String id = single.optString("id");
        String name = single.optString("name");
        JSONArray artists = single.optJSONArray("artists");
        ArrayList<String> art_names = new ArrayList<String>();
        int duration = single.optInt("duration");

        // Get artists' names
        try{
            for(int i = 0; i < artists.length(); i++){
                String art_name = artists.getJSONObject(i).optString("name");
                art_names.add(art_name);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Song song = new Song(art_names.toString(), name, id, duration);

        return song;
    }

//    @Override
    public static JSONObject query(String address) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = null;
        JSONObject result = null;

        try{
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            InputStream in = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(in));
            response = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                response.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            if(connection != null){
                connection.disconnect();
            }

            try{
                result = new JSONObject(response.toString());
            }catch (Exception e){
                e.printStackTrace();

            }

            return result;
        }
    }
}
