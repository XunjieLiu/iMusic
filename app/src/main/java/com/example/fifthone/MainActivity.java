package com.example.fifthone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, WebAPI{
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendRequest = findViewById(R.id.send_request);
        responseText = findViewById(R.id.response_text);

        sendRequest.setOnClickListener(this);
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

    @Override
    public ArrayList<Song> getSong(final String name) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ArrayList<Song> songList = readJSON(query("http://118.89.196.158:3000/search?keywords=" + name + "&limit=5"));
                    findURL(songList);

                    for(Song s:songList){
                        System.out.println(s.getPath());
                    }

                    showResponse(songList.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        return null;
    }

    @Override
    public void findURL(ArrayList<Song> songs) {
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

    @Override
    public ArrayList<Song> readJSON(JSONObject jsonObject) {
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

    @Override
    public Song getSingle(JSONObject single) {
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

        Song song = new Song(art_names, name, id, duration);

        return song;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_request){
            getSong("Dance Monkey");
        }
    }

//    private void sendRequestWithURLConnection(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                BufferedReader reader = null;
//
//                try{
//                    URL url = new URL("https://s.music.163.com/search/get/?s=黄昏&type=1");
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//
//                    InputStream in = connection.getInputStream();
//
//                    reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//
//                    while((line = reader.readLine()) != null){
//                        response.append(line);
//                    }
//
//                    showResponse(response.toString());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }finally {
//                    if(reader != null){
//                        try{
//                            reader.close();
//                        }catch(IOException e){
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if(connection != null){
//                        connection.disconnect();
//                    }
//                }
//            }
//        }).start();
//    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }
}
