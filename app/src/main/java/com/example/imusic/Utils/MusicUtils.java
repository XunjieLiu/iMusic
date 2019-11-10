package com.example.imusic.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.imusic.Entity.Song;

import java.util.ArrayList;
import java.util.List;


public class MusicUtils {
    static List<Song> list = new ArrayList<>();

    public static List<Song> getMusicData(Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if(cursor != null){
            while (cursor.moveToNext()){
                Song song = new Song();
                song.setSong( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if (song.getSize()>1000*800){
                    if(song.getSong().contains("-")){
                        String[] str = song.getSong().split("-");
                        song.setSinger(str[0]);
                        song.setSong(str[1]);
                    }
                    list.add(song);
                }else{
                    list.add(song);
                }

            }
            cursor.close();
        }
        return list;
    }

    public static String formatTime(int time) {
        if(time/1000 % 60 <10){
            return time / 1000 / 60 +":0"+ time / 1000 % 60;
        }
        else{
            return time / 1000 / 60 +":" + time / 1000 % 60;
        }
    }
}
