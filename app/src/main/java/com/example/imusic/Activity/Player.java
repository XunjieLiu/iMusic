package com.example.imusic.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imusic.Entity.Song;
import com.example.imusic.Service.NetMusicService;
import com.example.imusic.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class Player extends AppCompatActivity {

    ImageView pre_button,play_button,next_button;
    SeekBar seekBar;
    ImageView loop,shuffle;
    TextView duration_time,played_time;
    ImageView back;
    TextView singer,song;
    private Random random;

    private String TAG = "Player";
    private List<Song> songList;
    private int currentPosition = -1;
    private int currentTime;
    private int page = 1;
    private int play_style = 0;

    private boolean isPlaying;
    private boolean isPaused;
    private boolean flag;

    //    private Song currentSong;
    private PlayerReceiver playerReceiver;
    public static final String START_SERVICE = "com.iMusic.action.NET_MUSIC_SERVICE";
    public static final String UPDATE_ACTION = "com.iMusic.action.UPDATE_ACTION";
    public static final String MUSIC_CURRENT = "com.iMusic.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.iMusic.action.MUSIC_DURATION";
    public static final String MUSIC_COMPLETE="com.iMusic.action.MUSIC_COMPLETE";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Log.i("Service","Start");

        back = findViewById(R.id.player_back);
        loop = findViewById(R.id.loop);
        shuffle = findViewById(R.id.shuffle);
        seekBar = findViewById(R.id.seekBar);
        pre_button = findViewById(R.id.pre_button);
        play_button = findViewById(R.id.play_button);
        next_button = findViewById(R.id.next_button);
        duration_time = findViewById(R.id.player_duration);
        played_time = findViewById(R.id.played_time);
        song = findViewById(R.id.player_song);
        singer = findViewById(R.id.player_singer);

        songList = (ArrayList<Song>) getIntent().getSerializableExtra(("musicList"));
        int position = getIntent().getIntExtra("position",-1);
//        currentPosition = position;
//        currentSong = songList.get(currentPosition);
//        System.out.println(currentSong.getSong());
        System.out.println(songList.size());
        System.out.println(position);

        if(isPlaying) {
            if (position != currentPosition) {
                currentPosition = position;
                flag = false;
                isPaused = false;
                isPlaying = true;

            } else {
                flag = false;
                isPlaying = true;
                isPaused = false;
            }
        } else {
            if (position != currentPosition) {
                currentPosition = position;
                flag = true;
                isPlaying = true;
                isPaused = false;
            } else {
                flag = false;
                isPaused = true;
                isPlaying = false;
            }
        }

        setValues();
        setClick();
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        playerReceiver  = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        filter.addAction((MUSIC_COMPLETE));
        registerReceiver(playerReceiver,filter);
        if(flag){
            play(currentPosition);
            isPlaying = true;
            isPaused = false;
        }else{
            isPlaying = true;
            isPaused = false;
        }
        if(isPaused){
            play_button.setImageResource(R.drawable.ic_play_white_36dp);
        }else{
            play_button.setImageResource(R.drawable.ic_pause_white_36dp);
        }

    }
    public void setValues(){
        song.setText(songList.get(currentPosition).getSong());
        singer.setText(songList.get(currentPosition).getSinger());
        duration_time.setText(formatTime(songList.get(currentPosition).getDuration()));
        seekBar.setMax(songList.get(currentPosition).getDuration());

    }
    public void setClick(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Player.this, MainActivity.class);
                startActivity(intent);
            }
        });
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying&&!isPaused){
                    pause();
                    Log.v("clickpause", isPlaying+""+isPaused);
                }
                else if(isPaused&&!isPlaying) {
                    resume();
                    Log.v("clickresume", isPlaying + "" + isPaused);
                }
                else{
                    play(currentPosition);
                    Log.v("clickplay", isPlaying + "" + isPaused);
                }
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pre();
            }
        });
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loop();
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle();
            }
        });

    }

    public class PlayerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MUSIC_CURRENT)){
                currentTime = intent.getIntExtra("currentTime",-1);
                played_time.setText(formatTime(currentTime));
                seekBar.setProgress(currentTime);
                Log.i(songList.get(currentPosition).getSong()+","+songList.get(currentPosition).getSinger(),"progress received");
            }else if(action.equals(MUSIC_COMPLETE)){
                seekBar.setProgress(0);

                switch (play_style){
                    case 0:
                        play(currentPosition);
                        break;
                    case 1:
                    case 2:
                        next();
                        break;
                }
//                int position = intent.getIntExtra("pos",-1);
                Log.i(songList.get(currentPosition).getSong()+","+songList.get(currentPosition).getSinger(),"has been finished");
                Log.i(songList.get(currentPosition).getSong()+","+songList.get(currentPosition).getSinger(),"has been finished");
            }

        }
    }
    public void loop(){
        Toast loop = Toast.makeText(getApplicationContext(), "LOOP",Toast.LENGTH_SHORT);
        loop.show();
        play_style = 1;
    }

    public void shuffle(){
        Toast shuffle = Toast.makeText(getApplicationContext(), "SHUFFLE",Toast.LENGTH_SHORT);
        shuffle.show();
        play_style = 2;
    }

    public void play(int position){
        currentPosition = position;
        setValues();
        Intent intent = new Intent();
        intent.setAction(START_SERVICE);
        intent.setClass(this, NetMusicService.class);
        intent.putExtra("url",songList.get(currentPosition).getPath());
        intent.putExtra("position",currentPosition);
        intent.putExtra("MSG","PLAY_MSG");
        startService(intent);
        isPlaying = true;
        isPaused = false;
        play_button.setImageResource(R.drawable.ic_pause_white_36dp);
    }

    public void next(){
        switch (play_style) {
            case 0:
            case 1:
                currentPosition++;
                if(currentPosition>songList.size()-1){
                    currentPosition = 0;
                }
                play(currentPosition);
                break;
            case 2:
                currentPosition = 2 * currentPosition++ % songList.size();
                play(currentPosition);
                break;

        }

    }

    public void pre(){
        currentPosition--;
        if(currentPosition<0){
            currentPosition = songList.size()-1;
        }
        play(currentPosition);
    }

    public void pause(){
        Intent intent = new Intent();
        intent.setAction(START_SERVICE);
        intent.setClass(this,NetMusicService.class);
        intent.putExtra("MSG","PAUSE_MSG");
        startService(intent);
        isPlaying = false;
        isPaused = true;
        play_button.setImageResource(R.drawable.ic_play_white_36dp);
    }
    public void resume() {
        Intent intent = new Intent();
        intent.setAction(START_SERVICE);
        intent.setClass(this,NetMusicService.class);
        intent.putExtra("MSG","RESUME_MSG");
        startService(intent);
        isPaused = false;
        isPlaying = true;
        play_button.setImageResource(R.drawable.ic_pause_white_36dp);

    }
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            currentTime = i;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Intent intent = new Intent();
            intent.setClass(Player.this,NetMusicService.class);
            intent.setAction(START_SERVICE);
            intent.putExtra("MSG","PROGRESS_CHANGE");
            intent.putExtra("url",songList.get(currentPosition).getPath());
            intent.putExtra("position",currentPosition);
            intent.putExtra("progress",currentTime);
            startService(intent);
        }
    }
    public static String formatTime(int time) {
        if(time/1000 % 60 <10){
            return time / 1000 / 60 +":0"+ time / 1000 % 60;
        }
        else{
            return time / 1000 / 60 +":" + time / 1000 % 60;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(playerReceiver);
    }

}
