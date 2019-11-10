package com.example.imusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.imusic.Adapter.MyAdapter;
import com.example.imusic.Entity.Song;
import com.example.imusic.Service.NetMusicService;
import com.example.imusic.R;
import com.example.imusic.Utils.NetEaseUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    MyAdapter adapter;
    SearchView search_bar;
    ArrayList<Song> myList = new ArrayList<>();
    String songName;
    public static final String START_SERVICE = "com.iMusic.action.NET_MUSIC_SERVICE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView me = findViewById(R.id.me);
        listView = findViewById(R.id.music_list);
        search_bar = findViewById(R.id.search_bar);
        listView.setVisibility(View.INVISIBLE);
//        new getMusicSourceTask().execute();
        getList();

        listView.setOnItemClickListener(this);


        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Me.class);
                startActivity(intent);
            }
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s)){
                    songName = s;
                    new getMusicSourceTask().execute();
                    return true;
                }else{
                    Toast invalid = Toast.makeText(getApplicationContext(), "Invalid Input",Toast.LENGTH_SHORT);
                    invalid.show();
                }
                return false;
            }
        });

    }

    public void getList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetEaseUtils jsonUtils = new NetEaseUtils();
                myList = jsonUtils.getSong(songName);
            }
        }).start();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent net_intent = new Intent();
        net_intent.setClass(MainActivity.this, NetMusicService.class);
        net_intent.setAction(START_SERVICE);
        stopService(net_intent);

        Intent intent = new Intent(MainActivity.this, Player.class);
        intent.setAction("NET_MUSIC_PLAYER");
        intent.putExtra("musicList",(Serializable)myList);
        intent.putExtra("musicItem",myList.get(position));
        intent.putExtra("position",position);
        startActivity(intent);

    }


    public class getMusicSourceTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            myList = NetEaseUtils.getSong(songName);
            return null;
        }

        @Override
        protected void onPreExecute(){
            myList = new ArrayList<>();

        }

        @Override
        protected void onPostExecute(Void aVoid){
            listView.setVisibility(View.VISIBLE);
            adapter = new MyAdapter(MainActivity.this, myList);
            listView.setAdapter(adapter);
        }
    }


}
