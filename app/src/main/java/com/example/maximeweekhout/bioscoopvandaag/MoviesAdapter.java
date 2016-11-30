package com.example.maximeweekhout.bioscoopvandaag;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime Weekhout on 18-10-2016.
 */

// https://developer.android.com/training/material/lists-cards.html
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<Movie>();

    public MoviesAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    /**
     * Returns total of items in current list
     * @return total items
     */
    @Override
    public int getItemCount() {
        try {
            return movieList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Load details in items
     * @param movieViewHolder view
     * @param i item
     */
    @Override
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int i) {
        Movie ci = movieList.get(i);
        movieViewHolder.vTitle.setText(ci.getTitle());
        movieViewHolder.vTheater.setText(ci.getTheatre());
        movieViewHolder.vPoster.setBackgroundColor(Color.rgb(230,230,230));
        movieViewHolder.movie = ci;

        if (!ci.getPoster().equals("")) {
            movieViewHolder.setImage(ci.getPoster());
        }
    }

    /**
     * Create viewholder
     * @param viewGroup the viewgroup
     * @param i element of viewgroup
     * @return
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview, viewGroup, false);

        return new MovieViewHolder(itemView);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        protected TextView vTitle, vTheater;
        protected ImageView vPoster;
        protected TextView vBioscoop;
        protected Movie movie;

        public MovieViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);

            vTitle =  (TextView) v.findViewById(R.id.title);
            vTheater = (TextView)  v.findViewById(R.id.theater);
            vPoster = (ImageView)  v.findViewById(R.id.poster);
        }

        /**
         * Open new activity onclick
         * @param clickedView the clicked element
         */
        @Override
        public void onClick(View clickedView) {

            Intent intent = new Intent(clickedView.getContext(), DetailActivity.class);
            intent.putExtra("movie", MovieViewHolder.this.movie);
            clickedView.getContext().startActivity(intent);

        }

        /**
         * Start loading image
         * @param url the image URL
         */
        void setImage(String url) {
            new ImageFromUrl().execute(url);
        }

        // AsyncTask to get drawable
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
                MovieViewHolder.this.vPoster.setImageDrawable(image);
            }
        }
    }
}
