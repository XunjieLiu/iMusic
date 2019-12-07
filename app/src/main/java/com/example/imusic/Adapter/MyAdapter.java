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

import java.util.List;


public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    private int position_flag = 0;
    private String ApiName;
//    private int myBlue = Color.argb(0xff, 0x00, 0xBF, 0xFF);

    public MyAdapter(MainActivity mainActivity, List<Song> list, String ApiName) {
        this.context = mainActivity;
        this.list = list;
        this.ApiName = ApiName;
    }
    public void setFlag(int flag){
        this.position_flag = flag;
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
            else {
                holder.duration.setVisibility(View.INVISIBLE);
                holder.image.setImageResource(R.drawable.xia_mi);
            }
//            holder.position = (TextView) view.findViewById(R.id.item_mymusic_position);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }
        String string_song = list.get(i).getSong();
//        if(string_song.length() >= 5 && string_song.substring(string_song.length()-4).equals(".mp3")) {
//            holder.song.setText(string_song.substring(0,string_song.length() - 4).trim());
//        }else{
//            holder.song.setText(string_song.trim());
//        }
        holder.song.setText(string_song.trim());

        holder.singer.setText(list.get(i).getSinger().trim());
        int duration = list.get(i).getDuration();
        String time = formatTime(duration);
        holder.duration.setText(time);
//        String listNum = String.valueOf(i+1);
//        holder.position.setText(listNum);
//        if (i == position_flag){
//            holder.song.setTextColor(myBlue);
//            holder.singer.setTextColor(myBlue);
//            holder.duration.setTextColor(myBlue);
//            holder.position.setText("");
//            holder.position.setBackgroundResource(R.mipmap.play_small);
//        }else{
//            holder.song.setTextColor(Color.BLACK);
//            holder.singer.setTextColor(Color.BLACK);
//            holder.duration.setTextColor(Color.BLACK);
//            holder.position.setText(String.valueOf(i+1));
//            holder.position.setBackground(null);
//        }
        return view;
    }
    class ViewHolder {
        ImageView image;
        TextView song;
        TextView singer;
        TextView duration;
        TextView position;
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
