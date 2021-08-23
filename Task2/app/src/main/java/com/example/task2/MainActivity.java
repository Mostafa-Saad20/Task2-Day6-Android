package com.example.task2;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView userId_data, id_data, title_data, body_data;
    Button btnNext, btnPrev;
    Handler handler;
    String jsonString;
    List<Integer> userIdList, idList;
    List<String> titleList, bodyList;
    private static final String DATA = "DATA";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get layout references ..
        initComponent();

        // create handler ..

        handler = new Handler(Looper.getMainLooper());
        // put handler on a thread ..
        new Thread(() -> {
            jsonString = getJSON();
            // check if data get from server ..
            Log.i(DATA, jsonString);

            // getting JSON data ..
            if (jsonString != null)
            {
                try {
                    userIdList  = new ArrayList<>();
                    idList      = new ArrayList<>();
                    titleList   = new ArrayList<>();
                    bodyList    = new ArrayList<>();

                    // create jsonObject ..
                    JSONObject jsonObject = new JSONObject(jsonString);
                    // create jsonArray ..
                    JSONArray jsonArray = jsonObject.getJSONArray(jsonString);
                    // loop in jsonArray ..
                    for (int i = 0; i < jsonObject.length(); i++)
                    {
                        JSONObject userData = jsonArray.getJSONObject(i);
                        // get data to lists ..
                        userIdList.add(userData.getInt("userID"));
                        idList.add(userData.getInt("id"));
                        titleList.add(userData.getString("title"));
                        bodyList.add(userData.getString("body"));
                    }

                    // update UI in handler ..
                    handler.post(() -> {
                        userId_data.setText(userIdList.get(0));
                        id_data.setText(idList.get(0));
                        title_data.setText(titleList.get(0));
                        body_data.setText(bodyList.get(0));

                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }// end of onCreate() ..


    private void initComponent()
    {
        userId_data = findViewById(R.id.userID_data);
        id_data     = findViewById(R.id.ID_data);
        title_data  = findViewById(R.id.title_data);
        body_data   = findViewById(R.id.body_data);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
    }

// make connection to get json file & parse it ..
    private String getJSON()
    {
        HttpsURLConnection connection = null;
        InputStream is = null;
        String result  = null;

        try {
            // create url to get json url ..
            URL url = new URL("https://jsonplaceholder.typicode.com/posts");
            // make connection ..
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // get data in input stream ..
            is = new BufferedInputStream(connection.getInputStream());
            // convert json data to string ..
            result = convertJSONtoString(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        { // close connection ..
            try {
                assert is != null;
                is.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

// convert json to string ..
    private String convertJSONtoString(InputStream is)
    {
        // read data in buffered reader ..
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb  = new StringBuilder();
        String line;

        try {
            // read stream line by line ..
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

}