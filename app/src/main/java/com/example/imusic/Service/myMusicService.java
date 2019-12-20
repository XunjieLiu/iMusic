package com.example.imusic.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.imusic.Activity.Player;
import com.example.imusic.R;

import java.io.IOException;

public class myMusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public MediaPlayer mediaPlayer;
    private int currentTime;
    private int duration;
    private int currentPosition;
    private boolean isPaused;
    private String msg;
    private String url;
    private int percent;
    public static final String MUSIC_CURRENT = "com.iMusic.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.iMusic.action.nMUSIC_DURATION";
    public static final String MUSIC_PERCENT="com.iMusic.action.NET_MUSIC_PERCENT";
    public static final String MUSIC_COMPLETE ="com.iMusic.action.MUSIC_COMPLETE";
    public static final String GET_DURATION = "com.iMusic.action.GET_DURATION";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            if(msg.what == 0){
                if(mediaPlayer !=null){
                    currentTime = mediaPlayer.getCurrentPosition();
                    Intent intent = new Intent();
                    intent.setAction(MUSIC_CURRENT);
                    intent.putExtra("currentTime",currentTime);
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(0,1000);
                    Log.i("HandleMessage","Progress message has been sent");
                }
            }
            if(msg.what == 1){
//                duration = mediaPlayer.getDuration();
                if(duration >0){
                    Intent intent = new Intent();
                    intent.setAction(MUSIC_DURATION);
                    intent.putExtra("pos",currentPosition);
                    intent.putExtra("duration",duration);
                    sendBroadcast(intent);
                    Log.i("HandleMessage","current position message has been sent");
                }
            }
            if(msg.what ==2){
                Intent intent = new Intent();
                intent.setAction(MUSIC_PERCENT);
                intent.putExtra("percent",percent);
                sendBroadcast(intent);
            }
            if(msg.what == 3){
                Intent intent = new Intent();
                intent.setAction(MUSIC_COMPLETE);
                intent.putExtra("pos",currentPosition);
                sendBroadcast(intent);
                Log.i("HandleMessage","completion message has been sent");

            }
            if(msg.what == 4){
                removeMessages(0);
                Log.i("HandleMessage","completion message has been sent");
            }
            if(msg.what == 5){
                duration = mediaPlayer.getDuration();
                Intent intent = new Intent();
                intent.setAction(GET_DURATION);
                intent.putExtra("duration",duration);
                sendBroadcast(intent);
                Log.i("HandleMessage","duration message has been sent");
            }
        }
    };
    @Override
    public void onCreate(){
        super.onCreate();
        try{
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    public void onStart(Intent intent, int flags){
        if(intent == null){
            stopSelf();
        }
        url = intent.getStringExtra("url");
        msg = intent.getStringExtra("MSG");
        currentPosition = intent.getIntExtra("position",-1);
        Log.e(url,msg+currentPosition);
        switch (msg) {
            case "PLAY_MSG":
                play(0);
                Log.e("PLAY_MSG","HAS BEEN PROCESSED");
                break;
            case "PROGRESS_CHANGE":
                currentTime = intent.getIntExtra("progress", -1);
                mediaPlayer.seekTo(currentTime);
                handler.sendEmptyMessage(1);
                Log.e("PROGRESS_MSG","HAS BEEN PROCESSED");
                break;
            case "PLAYING_MSG":
                handler.sendEmptyMessage(1);
                Log.e("PLAYING_MSG","HAS BEEN PROCESSED");
                break;
            case "PAUSE_MSG":
                pause();
                Log.e("PAUSE_MSG","HAS BEEN PROCESSED");
                break;
            case "STOP_MSG":
                stop();
                Log.e("STOP_MSG","HAS BEEN PROCESSED");
                break;
            case "RESUME_MSG":
                resume();
                Log.e("RESUME_MSG","HAS BEEN PROCESSED");
                break;
        }
        return;
    }
    public void play(int currentTime){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            handler.removeMessages(0);
            handler.sendEmptyMessage(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    public void resume(){
        if(isPaused){
            mediaPlayer.start();
            isPaused = false;
        }
    }

    public void stop(){
        if(mediaPlayer !=null){
            mediaPlayer.stop();
            handler.sendEmptyMessage(4);
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        handler.sendEmptyMessage(3);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        handler.sendEmptyMessage(5);
        mediaPlayer.start();
    }
}
