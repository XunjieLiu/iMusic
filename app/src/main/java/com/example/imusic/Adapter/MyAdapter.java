package com.example.imusic.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imusic.Entity.Song;
import com.example.imusic.Activity.MainActivity;
import com.example.imusic.R;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    private String ApiName;

    public MyAdapter(MainActivity mainActivity, List<Song> list, String ApiName) {
        this.context = mainActivity;
        if(list == null){
            list = new ArrayList<Song>(0);
        }
        this.list = list;
        this.ApiName = ApiName;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null) {
            holder = new ViewHolder();
            // layout
            view = View.inflate(context, R.layout.item_music,null);
            // Initialization
            holder.song = (TextView) view.findViewById(R.id.title);
            holder.singer = (TextView) view.findViewById(R.id.author);
            holder.duration = (TextView) view.findViewById(R.id.duration);
            holder.image = (ImageView) view.findViewById(R.id.source);
            if(ApiName.equals("N")) {
                holder.duration.setVisibility(View.VISIBLE);
                holder.image.setImageResource(R.drawable.net_ease);
            }
            else if(ApiName.equals("M")){
                holder.duration.setVisibility(View.INVISIBLE);
                holder.image.setImageResource(R.drawable.xia_mi);
            }else{
                holder.duration.setVisibility(View.INVISIBLE);
                holder.image.setImageResource(R.drawable.ku_wo);
            }
//            holder.position = (TextView) view.findViewById(R.id.item_mymusic_position);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }
        String string_song = list.get(i).getSong();
        holder.song.setText(string_song.trim());

        holder.singer.setText(list.get(i).getSinger().trim());
        int duration = list.get(i).getDuration();
        String time = formatTime(duration);
        holder.duration.setText(time);
        return view;
    }
    class ViewHolder {
        ImageView image;
        TextView song;
        TextView singer;
        TextView duration;
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
