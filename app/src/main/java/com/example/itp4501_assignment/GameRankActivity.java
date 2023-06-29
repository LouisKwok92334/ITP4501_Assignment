package com.example.itp4501_assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GameRankActivity extends AppCompatActivity {
    private RecyclerView rV;
    private MyAdapter adapter;
    String[] dataArray;
    private MyThread myThread;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rank);

        // 加载 GIF
        ImageView loadingGif = findViewById(R.id.loading_gif);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(loadingGif);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingGif.setVisibility(View.GONE);

                FrameLayout loadingLayout = findViewById(R.id.loading_layout);
                loadingLayout.setVisibility(View.GONE);

                rV = findViewById(R.id.rV);
                String url = getResources().getString(R.string.url);
                myThread = new MyThread(url);
                myThread.fetchJSON();

                while (myThread.parsingComplete)
                    dataArray = myThread.getListItem();

                rV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new MyAdapter(getApplicationContext(), dataArray);
                rV.setAdapter(adapter);
            }
        }, 2000);
    }

    public void onBackClick(View view) {
        onBackPressed();
    }
}