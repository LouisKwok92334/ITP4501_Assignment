package com.example.itp4501_assignment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class OpenActivity extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);

        videoView = findViewById(R.id.videoView);

        // 设置视频资源文件
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.open);
        videoView.setVideoURI(videoUri);
        // 当视频播放完毕后，自动循环播放
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 在这里添加您希望在视频播放完毕后执行的操作
                videoView.setVisibility(View.GONE);
                openGame();
            }
        });

        videoView.start();
    }
    
    public void openGame() {
        Intent intent = new Intent(OpenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
