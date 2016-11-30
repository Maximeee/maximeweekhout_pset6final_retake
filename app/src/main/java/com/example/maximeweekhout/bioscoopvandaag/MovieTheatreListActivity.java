package com.example.maximeweekhout.bioscoopvandaag;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieTheatreListActivity extends AppCompatActivity {

    private List<Theater> theaterlist = new ArrayList<>();
    private ArrayAdapter<Theater> theaterAdapter;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_theatre_list);

        // Define list
        ListView list = (ListView) findViewById(R.id.theatrelist);

        // Load list
        theaterAdapter = new TheaterAdapter(getApplicationContext(), theaterlist);
        list.setAdapter(theaterAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MovieTheatreListActivity.this, ListActivity.class);
                Theater theater = theaterlist.get(position);
                intent.putExtra("theaterName", theater.getName());
                intent.putExtra("theaterUrl", theater.getUrl());
                startActivity(intent);
            }
        });
        updateList();
    }

    /**
     * Load theaters from API
     */
    void updateList() {
        pd = ProgressDialog.show(MovieTheatreListActivity.this, "Even geduld...", "Bioscopen worden geladen", true);
        new MovieAPI().execute("http://movieapi.ducosebel.nl/theaters.php");
    }

    /**
     * Load result into list
     * @param result JSON Result from request
     */
    void loadItemsInList(String result) {

        pd.dismiss();

        try {
            JSONObject object = new JSONObject(result);
            JSONArray results = object.getJSONArray("result");
            for (int i = 0; i < results.length(); i++) {
                Theater theater = new Theater( results.getJSONObject(i));

                theaterlist.add(theater);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        theaterAdapter.notifyDataSetChanged();
    }

    /**
     * MovieAPI AsyncTask
     */
    public class MovieAPI extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MovieTheatreListActivity.this.loadItemsInList(result);

        }
    }
}
