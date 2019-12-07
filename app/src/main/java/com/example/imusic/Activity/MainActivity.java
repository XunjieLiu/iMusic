package com.example.imusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.imusic.Utils.XiaMiUtils;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    MyAdapter adapter;
    SearchView search_bar;
    ArrayList<Song> netEaseList = new ArrayList<>();
    ArrayList<Song> xiamiList = new ArrayList<>();
    String songName;
    TabLayout tabLayout;
    private int showNum = -1;
//    ViewPager pager;
    String ApiName;
    public static final String START_SERVICE = "com.iMusic.action.NET_MUSIC_SERVICE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView me = findViewById(R.id.me);
        listView = findViewById(R.id.music_list);
        search_bar = findViewById(R.id.search_bar);
        listView.setVisibility(View.INVISIBLE);
        tabLayout = findViewById(R.id.tabs);

//        pager = findViewById(R.id.viewPager);
//        new getMusicSourceTask().execute();
//        getList();
//        tabLayout.setupWithViewPager(pager);
        listView.setOnItemClickListener(this);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("AAA","onTabSelected:"+tab.getText());
                System.out.println(tab.getPosition());
                showNum = tab.getPosition();
                if(tab.getPosition() == 0){
                    listView.setVisibility(View.VISIBLE);
                    adapter = new MyAdapter(MainActivity.this, netEaseList,"N");
                    listView.setAdapter(adapter);
                    System.out.println(netEaseList.size());
                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

                }
                else if(tab.getPosition() == 1){
                    listView.setVisibility(View.VISIBLE);
                    adapter = new MyAdapter(MainActivity.this, xiamiList,"M");
                    listView.setAdapter(adapter);
                    System.out.println(xiamiList.size());
                    System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("NetEase"));
        tabLayout.addTab(tabLayout.newTab().setText("XiaMi"));
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
//
//    public void getList(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                NetEaseUtils jsonUtils = new NetEaseUtils();
//                myList = jsonUtils.getSong(songName);
//            }
//        }).start();
//
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent net_intent = new Intent();
        net_intent.setClass(MainActivity.this, NetMusicService.class);
        net_intent.setAction(START_SERVICE);
        stopService(net_intent);

        Intent intent = new Intent(MainActivity.this, Player.class);
        intent.setAction("NET_MUSIC_PLAYER");
        intent.putExtra("musicList",(Serializable)netEaseList);
        intent.putExtra("musicItem",netEaseList.get(position));
        intent.putExtra("position",position);
        startActivity(intent);

    }


    public class getMusicSourceTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            netEaseList = NetEaseUtils.getSong(songName);
            xiamiList = XiaMiUtils.getSong(songName);
            return null;
        }

        @Override
        protected void onPreExecute(){
            netEaseList = new ArrayList<>();
            xiamiList = new ArrayList<>();

        }

        @Override
        protected void onPostExecute(Void aVoid){
            if (showNum == 1){
                listView.setVisibility(View.VISIBLE);
                adapter = new MyAdapter(MainActivity.this, xiamiList,"M");
                listView.setAdapter(adapter);
            }else{
                listView.setVisibility(View.VISIBLE);
                adapter = new MyAdapter(MainActivity.this, netEaseList,"N");
                listView.setAdapter(adapter);
            }

        }
    }


}
