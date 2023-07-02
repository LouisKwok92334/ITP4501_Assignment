package com.example.itp4501_assignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TextView TextMoves;
    private int moves = 0;
    private long startTime = 0;
    private long elapsedTime = 0;
    private CountDownTimer timer;
    private ImageButton[] imageButtons = new ImageButton[8];
    private int[] imageResources = new int[8];
    private ImageButton lastClickedButton = null;
    private int lastClickedIndex = -1;
    private boolean isWaiting = false;
    private Button btnContinue, btnQuit;
    SQLiteDatabase db;
    private MediaPlayer finishMediaPlayer;
    private MediaPlayer gameMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameMediaPlayer = MediaPlayer.create(this, R.raw.game);
        gameMediaPlayer.setLooping(true); // 如果需要循环播放 game.mp3
        gameMediaPlayer.start();

        // Initialize the database
        initializeDatabase();

        TextMoves = findViewById(R.id.tvMoves);
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeGame();
            }
        });

        btnQuit = findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });

        initializeGame();
    }

    private void initializeGame() {
        initializeImageResources();
        shuffleArray(imageResources);
        startTimer();

        for(int i = 0; i < imageButtons.length; i++) {
            String buttonID = "imageButton" + (i + 1);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            imageButtons[i] = findViewById(resID);
            imageButtons[i].setImageResource(R.drawable.card);
            imageButtons[i].setTag(false);
            imageButtons[i].setVisibility(View.VISIBLE);
            imageButtons[i].setRotationY(0f);  //Set the rotation state of the image buttons

            imageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageButton clickedButton = (ImageButton) view;
                    if ((Boolean) clickedButton.getTag() || isWaiting) {
                        return;
                    }

                    int clickedIndex = Arrays.asList(imageButtons).indexOf(clickedButton);
                    performFlipAnimation(clickedButton, clickedIndex, imageButtons, imageResources);

                    if(lastClickedButton != null && lastClickedButton != clickedButton) {
                        TextMoves.setText("Moves: " + ++moves);
                        checkMatch(clickedIndex, clickedButton);
                        lastClickedButton = null;
                    } else {
                        lastClickedButton = clickedButton;
                        lastClickedIndex = clickedIndex;
                    }
                }
            });
        }
    }

    private void flipAnimation(ImageButton button, float startAngle, float endAngle, int newImageResource) {
        ObjectAnimator flip = ObjectAnimator.ofFloat(button, "rotationY", startAngle, endAngle);
        flip.setDuration(500);
        flip.setInterpolator(new AccelerateDecelerateInterpolator());

        flip.addUpdateListener(animation -> {
            float angle = (Float) animation.getAnimatedValue();
            if (startAngle < endAngle && angle > 90f || startAngle > endAngle && angle < 90f) {
                button.setImageResource(newImageResource);
            }
        });

        flip.start();
    }

    private void performFlipAnimation(ImageButton clickedButton, int clickedIndex, ImageButton[] imageButtons, int[] imageResources) {
        clickedButton.setTag(true);

        // create the flip animation
        flipAnimation(clickedButton, 0f, 180f, imageResources[clickedIndex]);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            isWaiting = false;
        }, 2000);
    }

    public void checkMatch(int clickedIndex, ImageButton clickedButton) {
        if (imageResources[clickedIndex] != imageResources[lastClickedIndex]) {
            // delay 1 second and turn back the images
            final ImageButton button1 = clickedButton;
            final ImageButton button2 = lastClickedButton;
            Handler handler = new Handler();
            isWaiting = true;
            handler.postDelayed(() -> {
                // flip the buttons back
                flipAnimation(button1, 180f, 0f, R.drawable.card);
                flipAnimation(button2, 180f, 0f, R.drawable.card);
                button1.setTag(false);
                button2.setTag(false);

                // if not match, vibrate the device after the second card is flipped
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] vibrationPattern = {0, 100, 50, 100};  // delay, vibrate, pause, vibrate
                vibrator.vibrate(vibrationPattern, -1);  // pass -1 to vibrate the pattern once

                // if not match, play the wrong sound
                MediaPlayer wrongSound = MediaPlayer.create(this, R.raw.wrong);
                wrongSound.start();
            }, 1000);
        } else {
            // if match, play the correct sound
            MediaPlayer correctSound = MediaPlayer.create(this, R.raw.correct);
            correctSound.start();

            // delay 1 second and make the images disappear
            final ImageButton button1 = clickedButton;
            final ImageButton button2 = lastClickedButton;
            Handler handler = new Handler();
            isWaiting = true;
            handler.postDelayed(() -> {
                button1.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.INVISIBLE);

                // Check if all image buttons are invisible (i.e., all pairs are found)
                boolean allPairsFound = true;
                for (ImageButton button : imageButtons) {
                    if (button.getVisibility() == View.VISIBLE) {
                        allPairsFound = false;
                        break;
                    }
                }

                // If all pairs are found, show the continue button
                if (allPairsFound) {
                    endGame();
                }
            }, 1000);
        }
    }
    private void initializeImageResources() {
        moves = 0;
        TextMoves.setText("Moves: " + moves);
        lastClickedButton = null;
        lastClickedIndex = -1;
        isWaiting = false;

        imageResources[0] = R.drawable.megumin1;
        imageResources[1] = R.drawable.megumin1;
        imageResources[2] = R.drawable.megumin2;
        imageResources[3] = R.drawable.megumin2;
        imageResources[4] = R.drawable.megumin3;
        imageResources[5] = R.drawable.megumin3;
        imageResources[6] = R.drawable.megumin4;
        imageResources[7] = R.drawable.megumin4;

        btnContinue.setVisibility(View.GONE);
        btnQuit.setVisibility(View.GONE);
    }

    // Implementing Fisher–Yates shuffle
    public void shuffleArray(int[] array)
    {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            public void onTick(long millisUntilFinished) {
                elapsedTime = System.currentTimeMillis() - startTime;
            }

            public void onFinish() {

            }
        }.start();
    }

    private void endGame() {
        elapsedTime = System.currentTimeMillis() - startTime;
        timer.cancel();

        // Stop and release the gameMediaPlayer
        if (gameMediaPlayer != null) {
            gameMediaPlayer.stop();
            gameMediaPlayer.release();
            gameMediaPlayer = null;
        }

        // Show the button when the game ends
        btnContinue.setVisibility(View.VISIBLE);
        btnQuit.setVisibility(View.VISIBLE);
        // Insert the record of the game just played
        insertGameRecord();
        playWinSound();
    }

    private void playWinSound() {
        MediaPlayer winMediaPlayer = MediaPlayer.create(this, R.raw.win);
        winMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                playFinishSound();
            }
        });
        winMediaPlayer.start();
    }

    private void playFinishSound() {
        finishMediaPlayer = MediaPlayer.create(this, R.raw.finish);
        finishMediaPlayer.setLooping(true);
        finishMediaPlayer.start();
    }

    private void initializeDatabase() {
        try {
            // Open the database or create if it does not exist
            db = SQLiteDatabase.openDatabase("/data/data/com.example.itp4501_assignment/GameLogDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            // Create table if not exists
            db.execSQL("CREATE TABLE IF NOT EXISTS GamesLog (gameID INTEGER PRIMARY KEY AUTOINCREMENT, playDate text, playTime text, moves int);");

        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void insertGameRecord() {
        try {
            String playDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String playTime = String.format(Locale.getDefault(), "%02d:%02d", elapsedTime/1000/60, (elapsedTime/1000) % 60);

            // Insert the record into the database
            db.execSQL("INSERT INTO GamesLog(playDate, playTime, moves) VALUES ( ?, ?, ?)",
                    new Object[]{playDate, playTime, moves});
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exit, null);
        ImageView imageView = dialogView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.cry);

        TextView textView = new TextView(this);
        textView.setText("Do you really want to leave game?");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, 0, 0, 20);

        LinearLayout linearLayout = dialogView.findViewById(R.id.dialogLayout);
        linearLayout.addView(textView, 0);

        builder.setView(dialogView)
                .setPositiveButton("Exit！", new DialogInterface.OnClickListener() {
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

    @Override
    protected void onStart() {
        super.onStart();
        if (gameMediaPlayer != null && !gameMediaPlayer.isPlaying()) {
            gameMediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameMediaPlayer != null && gameMediaPlayer.isPlaying()) {
            gameMediaPlayer.pause();
        }
        if (finishMediaPlayer != null && finishMediaPlayer.isPlaying()) {
            finishMediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameMediaPlayer != null && !gameMediaPlayer.isPlaying()) {
            gameMediaPlayer.start();
        }
        if (finishMediaPlayer != null && !finishMediaPlayer.isPlaying()) {
            finishMediaPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gameMediaPlayer != null && gameMediaPlayer.isPlaying()) {
            gameMediaPlayer.pause();
        }
        if (finishMediaPlayer != null && finishMediaPlayer.isPlaying()) {
            finishMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (finishMediaPlayer != null) {
            finishMediaPlayer.release();
            finishMediaPlayer = null;
        }

        if (gameMediaPlayer != null) {
            gameMediaPlayer.release();
            gameMediaPlayer = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (db != null) {
            db.close();
        }
    }
}