package com.example.maximeweekhout.bioscoopvandaag;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maximeweekhout.bioscoopvandaag.Movie;
import com.example.maximeweekhout.bioscoopvandaag.MovieStorage;
import com.example.maximeweekhout.bioscoopvandaag.R;

import org.json.JSONException;
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

public class DetailActivity extends AppCompatActivity {

    TextView title, actors, plot;
    JSONObject imdbResult;
    ImageView poster;
    ListView timetable;

    Movie movie;

    MovieStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        movie = (Movie) bundle.getSerializable("movie");

        storage = new MovieStorage(this);

        title = (TextView) findViewById(R.id.vTitle);
        actors = (TextView) findViewById(R.id.vActors);
        plot = (TextView) findViewById(R.id.vPlot);
        poster = (ImageView) findViewById(R.id.vPoster);
        timetable = (ListView) findViewById(R.id.vTimetable);
        poster.setBackgroundColor(Color.rgb(230, 230, 230));

        title.setText(movie.getTitle());

        /**
         * Load items
         */
        if (!movie.getImdbId().equals("")) {
            new Omdb().execute("http://www.omdbapi.com/?i=" + movie.getImdbId());
        }

        if (!movie.getPoster().equals("")) {
            new ImageFromUrl().execute(movie.getPoster());
        }

        timetable.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialog(movie.getShows().get(position));
            }
        });

        updateTimeTable();
    }

    /**
     * Open dialog and ask for what the user wants to do
     * @param show the selected show
     */
    void openDialog(final Show show) {

        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);


        builder.setTitle("Maak een keuze");
        builder.setItems(new CharSequence[]
                        {"Opslaan", "Koop tickets", "Annuleer"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                storeShow(show);
                                break;
                            case 1:
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(show.getTicketUrl()));
                                startActivity(browserIntent);
                                break;
                            case 2:

                                break;
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * Store show in permanent storage
     * @param show the show to be stored
     */
    void storeShow(Show show) {
        StorableShow storableShow = new StorableShow(movie.getTitle(), movie.getTheatre(), show, imdbResult);
        storage.add(storableShow);
        Toast.makeText(this, "Voorstelling opgeslagen!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update detail page with imdb results
     */
    void updateDetailPage (String result) {

        try {
            JSONObject o = new JSONObject(result);
            actors.setText(o.getString("Actors"));
            plot.setText(o.getString("Plot"));
            imdbResult = o;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the timetable list with the new times a movie plays
     */
    void updateTimeTable() {
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < movie.getShows().size(); i++) {
            Show show = movie.getShows().get(i);
            list.add("Vandaag" + " - Van " + show.getStart() + " tot " + show.getEnd());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list );

        timetable.setAdapter(arrayAdapter);
    }

    // https://developer.android.com/reference/android/os/AsyncTask.html
    public class Omdb extends AsyncTask<String, String, String> {

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

            DetailActivity.this.updateDetailPage(result);

        }
    }

    // https://developer.android.com/reference/android/os/AsyncTask.html
    class ImageFromUrl extends AsyncTask<String, Void, Drawable> {

        protected Drawable doInBackground(String... url) {

            try {
                InputStream is = (InputStream) new URL(url[0]).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                return d;
            } catch (Exception e) {
                return null;
            }

        }

        protected void onPostExecute(Drawable image) {
            DetailActivity.this.poster.setImageDrawable(image);
        }
    }
}
