package com.example.itp4501_assignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btnPlay, btnGameRank, btnRecords, btnClose;
    private MediaPlayer mediaPlayer;
    private View whiteOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        whiteOverlay = findViewById(R.id.whiteOverlay);

        mediaPlayer = MediaPlayer.create(this, R.raw.main);
        mediaPlayer.setLooping(true);

        btnPlay = findViewById(R.id.btnPlay);
        animateButton(btnPlay, 0);

        btnGameRank = findViewById(R.id.btnGameRank);
        animateButton(btnGameRank, 150);

        btnRecords = findViewById(R.id.btnRecords);
        animateButton(btnRecords, 300);

        btnClose = findViewById(R.id.btnClose);
        animateButton(btnClose, 450);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartDialog();
            }
        });
        btnGameRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameRankActivity.class);
                startActivity(intent);
            }
        });
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });
    }

    private void showStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_start_adventure, null);
        ImageView imageView = dialogView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.story_img3);

        TextView textView = new TextView(this);
        textView.setText("Are you ready to start the adventure?");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, 0, 0, 20);

        LinearLayout linearLayout = dialogView.findViewById(R.id.dialogLayout);
        linearLayout.addView(textView, 0);

        builder.setView(dialogView)
                .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Animate the white overlay
                        whiteOverlay.setVisibility(View.VISIBLE);
                        ValueAnimator animator = ValueAnimator.ofInt(0, 255);
                        whiteOverlay.setBackgroundColor(Color.argb(0, 255, 255, 255));
                        whiteOverlay.setVisibility(View.VISIBLE);
                        animator.setDuration(2000); // 1000ms = 1s
                        animator.setInterpolator(new AccelerateInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int alpha = (int) animation.getAnimatedValue();
                                whiteOverlay.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
                            }
                        });
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                startActivity(intent);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        whiteOverlay.setBackgroundColor(Color.argb(0, 255, 255, 255));
                                        whiteOverlay.setVisibility(View.INVISIBLE);
                                    }
                                }, 500);
                            }
                        });

                        MediaPlayer adventure = MediaPlayer.create(MainActivity.this, R.raw.adventure);
                        adventure.start();
                        animator.start();
                    }
                })
                .setNegativeButton("Wait...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.web_popup_02);
        alertDialog.show();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exit, null);
        ImageView imageView = dialogView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.quit);

        TextView textView = new TextView(this);
        textView.setText("Do you really want to leave Megamin？");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, 0, 0, 20);

        LinearLayout linearLayout = dialogView.findViewById(R.id.dialogLayout);
        linearLayout.addView(textView, 0);

        builder.setView(dialogView)
                .setPositiveButton("Exit！！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Continue...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.web_popup_02);
        alertDialog.show();
    }

    private void animateButton(Button button, long startOffset) {
        Animation slideInFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        slideInFromRight.setStartOffset(startOffset);
        slideInFromRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playButtonClickSound();
                    }
                }, startOffset);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        button.startAnimation(slideInFromRight);
    }

    private void playButtonClickSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.click2);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}