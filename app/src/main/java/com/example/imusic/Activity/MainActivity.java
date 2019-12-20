package com.example.imusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imusic.Adapter.MyAdapter;
import com.example.imusic.Entity.Song;
import com.example.imusic.R;
import com.example.imusic.Service.myMusicService;
import com.example.imusic.Utils.KuwoUtils;
import com.example.imusic.Utils.NetEaseUtils;
import com.example.imusic.Utils.XiaMiUtils;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    MyAdapter adapter;
    SearchView search_bar;
    ArrayList<Song> netEaseList = new ArrayList<>();
    ArrayList<Song> xiamiList = new ArrayList<>();
    ArrayList<Song> kuwoList = new ArrayList<>();
    String songName;
    TabLayout tabLayout;
    private int showNum = -1;
    ImageView me, player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        me = findViewById(R.id.me);
        player =findViewById(R.id.player);
        listView = findViewById(R.id.music_list);
        search_bar = findViewById(R.id.search_bar);
        listView.setVisibility(View.INVISIBLE);
        tabLayout = findViewById(R.id.tabs);
        listView.setOnItemClickListener(this);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("Current Music API Service","onTabSelected:"+tab.getText());
                System.out.println(tab.getPosition());
                showNum = tab.getPosition();
                if(tab.getPosition() == 0){
                    listView.setVisibility(View.VISIBLE);
                    adapter = new MyAdapter(MainActivity.this, netEaseList,"N");
                    listView.setAdapter(adapter);
                }
                else if(tab.getPosition() == 1){
                    listView.setVisibility(View.VISIBLE);
                    adapter = new MyAdapter(MainActivity.this, xiamiList,"M");
                    listView.setAdapter(adapter);
                }else if(tab.getPosition() == 2){
                    listView.setVisibility(View.VISIBLE);
                    adapter = new MyAdapter(MainActivity.this, kuwoList,"K");
                    listView.setAdapter(adapter);
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
        tabLayout.addTab(tabLayout.newTab().setText("Kuwo"));

        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, User.class);
                startActivity(intent);
            }
        });

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netEaseList.size()!=0) {
                    if (showNum == 1) {
                        Intent intent = new Intent(MainActivity.this, Player.class);
                        intent.putExtra("musicList", (Serializable) xiamiList);
                        intent.putExtra("position", 0);
                        intent.putExtra("songName", songName);
                        startActivity(intent);
                    } else if (showNum == 2) {
                        Intent intent = new Intent(MainActivity.this, Player.class);
                        intent.putExtra("musicList", (Serializable) kuwoList);
                        intent.putExtra("position", 0);
                        intent.putExtra("songName", songName);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, Player.class);
                        intent.putExtra("musicList", (Serializable) netEaseList);
                        intent.putExtra("position", 0);
                        intent.putExtra("songName", songName);
                        startActivity(intent);
                    }
                }else{
                    Toast invalid = Toast.makeText(getApplicationContext(), "Please Search First",Toast.LENGTH_SHORT);
                    invalid.show();
                }
            }
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s)){
                    if(songName == null)
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent serviceStop = new Intent(MainActivity.this, myMusicService.class);
        serviceStop.setAction("MUSIC_SERVICE");
        stopService(serviceStop);
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                builder.setChannelId("a");
            }
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Play Music");
            builder.setContentText("Come and play some music");

            Intent intent2 = new Intent(MainActivity.this,Player.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent2,0);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1,notification);

        if(showNum == 2) {
            Intent intent = new Intent(MainActivity.this, Player.class);
            intent.putExtra("musicList", (Serializable) kuwoList);
            intent.putExtra("position", position);
            intent.putExtra("songName",songName);
            startActivity(intent);
        }
        else if(showNum == 1){
            Intent intent = new Intent(MainActivity.this, Player.class);
            intent.putExtra("musicList", (Serializable) xiamiList);
            intent.putExtra("position", position);
            intent.putExtra("songName",songName);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(MainActivity.this, Player.class);
            intent.putExtra("musicList", (Serializable) netEaseList);
            intent.putExtra("position", position);
            intent.putExtra("songName",songName);
            startActivity(intent);
        }
    }

    public class getMusicSourceTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            netEaseList = NetEaseUtils.getSong(songName);
            xiamiList = XiaMiUtils.getSong(songName);
            kuwoList = KuwoUtils.getSong(songName);
            return null;
        }

        @Override
        protected void onPreExecute(){
            netEaseList = new ArrayList<>();
            xiamiList = new ArrayList<>();
            kuwoList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid){
            if (showNum == 1){
                listView.setVisibility(View.VISIBLE);
                adapter = new MyAdapter(MainActivity.this, xiamiList,"M");
                listView.setAdapter(adapter);
            }else if (showNum == 2){
                listView.setVisibility(View.VISIBLE);
                adapter = new MyAdapter(MainActivity.this, kuwoList,"K");
                listView.setAdapter(adapter);
            }
            else{
                listView.setVisibility(View.VISIBLE);
                adapter = new MyAdapter(MainActivity.this, netEaseList,"N");
                listView.setAdapter(adapter);
            }

        }
    }


}
