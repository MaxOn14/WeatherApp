package com.example.weatherapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String URL = "https://api.openweathermap.org/data/2.5/find?q=%s&type=like&APPID=2598ff8f3490a89cdbb86efc7b008460&lang=ru&units=metric";
    private EditText editTextCityName;
    private TextView textViewWeatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewWeatherInfo = findViewById(R.id.textViewWeatherInfo);
        editTextCityName = findViewById(R.id.editTextCityName);
    }

    public void getNewCity(View view) {
        String city = editTextCityName.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadInfoInJSON task = new DownloadInfoInJSON();
            String url = String.format(URL,city);
            task.execute(url);
        }
    }


    private class DownloadInfoInJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getJSONArray("list").getJSONObject(0).getString("name");
                String temp = jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("%s\nТемпература: %s\nНа улице: %s",city,temp,description);
                textViewWeatherInfo.setText(weather);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

}