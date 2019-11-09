package com.example.fifthone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Xiami_JSONUtils implements WebAPI {

    /*
     * 我不确定一次能返回多少首歌曲，因为当一首歌没有file path的时候就不可以作为结果返回
     *
     * 增加返回歌曲有两个办法：
     * 1. 增加一次检索的limit
     * 2. 间隔一分钟后再去检索
     * */
    @Override
    public ArrayList<Song> getSong(String name) {
        JSONObject result = query("https://music-api-jwzcyzizya.now.sh/api/search/song/xiami?key= " + name + "&limit=10&page=1");
        System.out.println(result);
        ArrayList<Song> songList = readJSON(result);

        if(songList.size() < 1){
            System.out.println("Nothing found");
            return null;
        }else{
            return songList;
        }

    }

    @Override
    public void findURL(ArrayList<Song> songs) {

    }

    /*
     * 搜索歌曲返回的JSON文件不一定会包含file路径，所以需要判断
     * limit设置的是20首歌，方法只会返回有地址的单曲
     * */
    @Override
    public ArrayList<Song> readJSON(JSONObject jsonObject) {
        JSONArray songList = jsonObject.optJSONArray("songList");
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

    @Override
    public Song getSingle(JSONObject single) {
        JSONArray art = single.optJSONArray("artists");
        String name = single.optString("name");
        String path = null;
        String art_name = null;

        /*
        * 搜索歌曲返回的JSON文件不一定会包含file路径，所以需要判断
        * limit设置的是20首歌，方法只会返回有地址的单曲
        * */

        try{
            art_name = art.getJSONObject(0).optString("name");
            path = single.optString("file");
            System.out.println("This is path: " + path);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(path.length() < 5){
            return null;
        }else{
            Song s = new Song(art_name, name, path);

            return s;
        }
    }

    @Override
    public JSONObject query(String address) {
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
