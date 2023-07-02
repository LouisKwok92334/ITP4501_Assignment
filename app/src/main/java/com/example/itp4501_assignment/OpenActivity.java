package com.example.itp4501_assignment;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;

public class OpenActivity extends AppCompatActivity {
    private VideoView videoView;
    private boolean isOpeningGame = false;
    private MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open);

        videoView = findViewById(R.id.videoView);
        openVideo();
    }

    private void startBlinkingAnimation(final TextView textView) {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(750);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        textView.startAnimation(animation);
    }

    public void openVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.open);
        videoView.setVideoURI(videoUri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playAudio();
                videoView.setVisibility(View.GONE);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setAlpha(0f);
                imageView.setVisibility(View.VISIBLE);
                imageView.animate().alpha(1f).setDuration(3000); // 淡入动画，3秒内透明度从0变为1

                TextView tvEnterGame = findViewById(R.id.tv_enter_game);
                tvEnterGame.setVisibility(View.VISIBLE);
                startBlinkingAnimation(tvEnterGame); // 开始闪烁动画

                ConstraintLayout rootLayout = findViewById(R.id.open_root_layout);
                rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openGame();
                    }
                });
            }
        });
        videoView.start();
    }

    private void playAudio() {
        Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.happy);

        try {
            mp.setDataSource(this, audioUri);
            mp.prepare();

            FallingStarView fallingStarView = findViewById(R.id.falling_star_view);
            fallingStarView.setVisibility(View.VISIBLE);
            fallingStarView.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
        });

        mp.start();
    }

    private void stopAudio() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void openGame() {
        if (isOpeningGame) {
            return;
        }
        isOpeningGame = true;

        final TextView tvEnterGame = findViewById(R.id.tv_enter_game);
        final ImageView imageView = findViewById(R.id.imageView);
        tvEnterGame.clearAnimation();

        // 使用 ObjectAnimator 创建淡出动画
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(tvEnterGame, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(1000);
        fadeOutAnimator.start();

        // ImageView 淡出动画
        ObjectAnimator fadeOutAnimatorImageView = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
        fadeOutAnimatorImageView.setDuration(1000);
        fadeOutAnimatorImageView.start();
        fadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.INVISIBLE);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAudio();
                Intent intent = new Intent(OpenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mp != null && !mp.isPlaying()) {
            mp.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }
}
