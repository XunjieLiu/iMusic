package com.example.imusic.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.imusic.R;

import androidx.appcompat.app.AppCompatActivity;

public class User extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my);
        ImageView back = findViewById(R.id.me_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
