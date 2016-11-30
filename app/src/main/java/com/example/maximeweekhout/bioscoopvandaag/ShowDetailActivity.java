package com.example.maximeweekhout.bioscoopvandaag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowDetailActivity extends Activity {

    StorableShow show;

    TextView title, actors, plot, time;
    ImageView poster;

    Button removeButton, ticketButton;

    MovieStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);

        // Set Storage
        storage = new MovieStorage(this);

        // Load views reference
        title = (TextView) findViewById(R.id.vTitle);
        actors = (TextView) findViewById(R.id.vActors);
        plot = (TextView) findViewById(R.id.vPlot);
        poster = (ImageView) findViewById(R.id.vPoster);
        time = (TextView) findViewById(R.id.vTime);

        ticketButton = (Button) findViewById(R.id.ticketButton);
        removeButton = (Button) findViewById(R.id.removeButton);

        // Get Json from result to parse
        Intent intent = getIntent();
        String result = intent.getStringExtra("showJson");

        try {
            // Load show and parse information
            show = new StorableShow(result);
            title.setText(show.getTitle());
            actors.setText(show.getImdb().getString("Actors"));
            plot.setText(show.getImdb().getString("Plot"));

            // Check if there is a ticket to buy
            if (!show.getShow().getDate().equals("Vandaag")) {
                ticketButton.setVisibility(View.INVISIBLE);
            }

            // Update timelabel
            time.setText(show.getShow().getDate() + " om " + show.getShow().getStart());

            // Start loading image aSync
            new ImageFromUrl().execute(show.getImdb().getString("Poster"));

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error loading details...", Toast.LENGTH_LONG).show();
        }

        // Button handling
        removeButton.setOnClickListener(new View.OnClickListener(){

            /**
             * Remove show and go back to previous view
             * @param view v
             */
            @Override
            public void onClick(View view) {
                storage.remove(show);
                Toast.makeText(getApplicationContext(), "Film verwijderd", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ticketButton.setOnClickListener(new View.OnClickListener(){

            /**
             * Open ticket URL in default browser
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(show.getShow().getTicketUrl()));
                startActivity(browserIntent);
            }
        });
    }

    /**
     * Set loaded image in placeholder
     * @param image The loaded image
     */
    void setImageDrawable(Drawable image) {
        poster.setImageDrawable(image);
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
            ShowDetailActivity.this.poster.setImageDrawable(image);
        }
    }
}
