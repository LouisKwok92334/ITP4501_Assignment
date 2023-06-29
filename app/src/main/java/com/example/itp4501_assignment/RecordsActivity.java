package com.example.itp4501_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    ListView recordsListView;
    SQLiteDatabase gamesLogDB;
    ArrayList<String> recordsList;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recordsListView = findViewById(R.id.listRecords);
        recordsList = new ArrayList<>();

        // 加载 GIF
        ImageView loadingGif = findViewById(R.id.loading_gif);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.loading)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(loadingGif);

        // 在3秒后隐藏GIF
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
                loadingGif.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void loadData() {
        try {
            FrameLayout loadingLayout = findViewById(R.id.loading_layout);
            loadingLayout.setVisibility(View.GONE);
            gamesLogDB = SQLiteDatabase.openDatabase("/data/data/com.example.itp4501_assignment/GameLogDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            try (Cursor cursor = gamesLogDB.rawQuery("SELECT * FROM GamesLog ORDER BY gameID DESC", null)) {
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        String formattedRecord = String.format("Date: %s; Time: %s; Moves: %d",
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getInt(3));
                        recordsList.add(formattedRecord);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RecordsActivity.this, android.R.layout.simple_list_item_1, recordsList);
                    recordsListView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No records found", Toast.LENGTH_LONG).show();
                }
            }

            gamesLogDB.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void onBackClick(View view) {
        onBackPressed();
    }
}
