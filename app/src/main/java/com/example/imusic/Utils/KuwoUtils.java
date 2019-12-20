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

public class KuwoUtils{
    /*
     * 我不确定一次能返回多少首歌曲，因为当一首歌没有file path的时候就不可以作为结果返回
     *
     * 增加返回歌曲有两个办法：
     * 1. 增加一次检索的limit
     * 2. 间隔一分钟后再去检索
     * */

    public static ArrayList<Song> getSong(String name) {
        JSONObject result = query("http://api.guaqb.cn/music/music/?input=" + name + "&filter=name&type=kugou");
        ArrayList<Song> songList = readJSON(result);

        if(songList.size() < 1){
            System.out.println("Nothing found");
            return null;
        }else{
            return songList;
        }

    }


    public static ArrayList<Song> readJSON(JSONObject jsonObject) {
        JSONArray songList = jsonObject.optJSONArray("data");
        ArrayList<Song> list = new ArrayList<Song>();

        try{
            for(int i = 0; i < songList.length(); i++){
                JSONObject single = songList.getJSONObject(i);
                Song s = getSingle(single);

                if(s == null){
                    continue;
                }else{
                    list.add(s);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public static Song getSingle(JSONObject single) {
        String art = single.optString("author");
        String name = single.optString("name");
        String path = null;
        String pic = null;

        /*
         * 搜索歌曲返回的JSON文件不一定会包含file路径，所以需要判断
         * limit设置的是20首歌，方法只会返回有地址的单曲
         * */

        try{
            path = single.optString("music");
            pic = single.optString("pic");
            System.out.println("This is path: " + path);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(path.length() < 5){
            return null;
        }else{
            Song s = new Song(art, name, path);
//            if(pic.length() > 5){
//                s.setPic(pic);
//            }

            return s;
        }
    }

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
