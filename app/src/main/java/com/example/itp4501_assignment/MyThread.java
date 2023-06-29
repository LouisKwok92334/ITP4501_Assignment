package com.example.itp4501_assignment;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyThread {
    private String url;
    private String[] listItem;

    String data = "";

    public volatile boolean parsingComplete = true;

    public MyThread(String url) {
        this.url = url;
    }

    public String[] getListItem() {
        return listItem;
    }

    public void readJSON(String data) {
        try {
            JSONArray playerArray = new JSONArray(data);

            // Sort the JSONArray based on moves
            for (int i = 0; i < playerArray.length(); i++) {
                for (int j = i + 1; j < playerArray.length(); j++) {
                    JSONObject player1 = playerArray.getJSONObject(i);
                    JSONObject player2 = playerArray.getJSONObject(j);
                    if (player1.getInt("Moves") > player2.getInt("Moves")) {
                        playerArray.put(i, player2);
                        playerArray.put(j, player1);
                    }
                }
            }

            // Initialize the listItem with the length of the playerArray
            listItem = new String[playerArray.length()];
            for (int i = 0; i < playerArray.length(); i++) {
                JSONObject playerObject = playerArray.getJSONObject(i);
                String name = playerObject.getString("Name");
                int moves = playerObject.getInt("Moves");
                String player = "Rank " + (i + 1) + ", " + name + ", " + moves + " Moves";
                listItem[i] = player;
            }

            parsingComplete = false;

            Log.d("Test", data+"");
        } catch (Exception e) {
            // Add a log entry to provide more information when an exception occurs
            Log.e("MyThread", "Error in readJSON()", e);
        }
    }

    public void fetchJSON() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL urlString = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlString.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    //Log.d("Connect", String.valueOf(connection));

                    InputStream stream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(stream);
                    int inputStreamData = inputStreamReader.read();
                    while (inputStreamData != -1) {
                        char current = (char) inputStreamData;
                        inputStreamData = inputStreamReader.read();
                        data += current;
                    }
                    Log.d("Get Data", data + "");
                    readJSON(data);
                    stream.close();

                } catch (Exception e) {
                    Log.d("HI", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}