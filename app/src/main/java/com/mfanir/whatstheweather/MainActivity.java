package com.mfanir.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText city;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.et_city);
        result = findViewById(R.id.tv_result);
    }

    public void getWeather(View view) {
        DownloadTask task = new DownloadTask();
        try {
            String encodedCityName = URLEncoder.encode(city.getText().toString(), "UTF-8");
            task.execute("https://samples.openweathermap.org/data/2.5/weather?q="+ encodedCityName + "&appid=b1b15e88fa797225412429c1c50c122a1");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result  = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";


                for (int i = 0; i < arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    //jika main & desc tidak null
                    if (!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    }

                    Log.i("main" , jsonPart.getString("main"));
                    Log.i("desc" , jsonPart.getString("description"));
                }

                if (!message.equals("")) {
                    result.setText(message);
                }

            } catch (JSONException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }
            //
        }
    }
}
