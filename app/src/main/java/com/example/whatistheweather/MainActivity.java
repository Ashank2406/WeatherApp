package com.example.whatistheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityEditText;
    Button enterButton;
    TextView displayTextView;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                reader.read();
                int data = reader.read();
                while(data!=-1){
                    char current = (char) data;
                    result = result + current;
                    data=reader.read();
                }
                String result1 = result;
                return result1;
            } catch (Exception e) {
                e.printStackTrace();
                displayTextView.setText("Invalid City Name");
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String s1 = "{" + s + "}";
                JSONObject jsonObject = new JSONObject(s1);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                String weather = "";

                for(int i = 0; i < arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if(!main.equals("")&&!description.equals("")) {
                        weather = main + ": " + description + "\n";
                    }
                    if(!weather.equals("")) {
                        displayTextView.setText(weather);
                    }else{
                        displayTextView.setText("Could not find weather :(");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void enter (View view){
        try{
            //displayTextView.setText(null);
            DownloadTask task = new DownloadTask();
            String city = cityEditText.getText().toString();
            String encodedCityName = URLEncoder.encode(city,"UTF-8");
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=38a7cc8507f040fd772fd4288b00ad87";
            task.execute(url).get();
        }catch (Exception e){
            e.printStackTrace();
            displayTextView.setText("Could not find weather :(");
        }
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0 );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityEditText = findViewById(R.id.cityEditText);
        enterButton = findViewById(R.id.enterButton);
        displayTextView = findViewById(R.id.displayTextView);
    }
}
