package com.example.maximeweekhout.bioscoopvandaag;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    MovieStorage storage;
    ProgressDialog pd;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView.Adapter movieAdapter;

    private String theaterName, theaterUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recList.setLayoutManager(llm);

        theaterName = getIntent().getStringExtra("theaterName");
        theaterUrl = getIntent().getStringExtra("theaterUrl");

        movieAdapter = new MoviesAdapter(movieList);
        recList.setAdapter(movieAdapter);

        updateList();
    }

    /**
     * Show ProgrssDialog and start ASyncTask for loading details
     * Needs to have theaterURL not to be null
     */
    void updateList() {

        pd = ProgressDialog.show(ListActivity.this, "Even geduld...", "Films worden geladen", true);

        System.out.print("URL: ");
        System.out.println(theaterUrl);

        if (theaterUrl != null) {
            System.out.println("Execute");
            new MovieAPI().execute(theaterUrl);
        }
    }

    /**
     * Loads items in list and update MovieAdapter
     * @param result JSON result from request
     */
    void loadItemsInList(String result) {
        pd.dismiss();

        try {
            JSONObject object = new JSONObject(result);
            JSONArray results = object.getJSONArray("result");
            for (int i = 0; i < results.length(); i++) {
                movieList.add(new Movie(results.getJSONObject(i)));
            }

            if (results.length() == 0) {
                Toast.makeText(this, "Vandaag geen films meer!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        movieAdapter.notifyDataSetChanged();
    }

    /**
     * Load items from MovieAPI
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
            ListActivity.this.loadItemsInList(result);
        }
    }
}

