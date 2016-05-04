package com.example.ahmedmohamed.myapplication.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.ahmedmohamed.myapplication.BuildConfig;
import com.example.ahmedmohamed.myapplication.R;
import com.example.ahmedmohamed.myapplication.adapters.PostersAdapter;
import com.example.ahmedmohamed.myapplication.data.MoviesContract;
import com.example.ahmedmohamed.myapplication.data.MoviesDbHelper;
import com.example.ahmedmohamed.myapplication.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MoviesFragment extends Fragment {

    private  PostersAdapter adpater;

    private GridView gridView;

    private ProgressDialog progressDialog;

    private ArrayList<Movie> MoviesList;

    private String[] moviesPosters;

    private SQLiteDatabase db;

    SharedPreferences sh;

    private static final String MOVIES_KEY = "movies";
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {


            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                MoviesList = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                if(MoviesList == null )
                    adpater.addAll(MoviesList);



            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movies_fragment, container, false);

        if(!isNetworkConnected()) {
            sh = getActivity().getSharedPreferences("myShared", getActivity().MODE_PRIVATE);
            Toast.makeText(getActivity(), "There is a problem with your internet connection", Toast.LENGTH_LONG).show();
        }
        else {

            MoviesDbHelper mOpenHelper = new MoviesDbHelper(getActivity());

            db = mOpenHelper.getWritableDatabase();

            gridView = (GridView) rootView.findViewById(R.id.gridView);


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Movie movie = MoviesList.get(position);
                    ((Callback) getActivity()).onItemSelected(movie);
                }
            });

            sh = getActivity().getSharedPreferences("myShared", getActivity().MODE_PRIVATE);

            setHasOptionsMenu(true);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main_menu, menu);

        MenuItem item1 = menu.findItem(R.id.btn1);

        MenuItem item2 = menu.findItem(R.id.btn2);

        MenuItem item3 = menu.findItem(R.id.btn3);


        String s = sh.getString("selected", getString(R.string.pref_sort_by_popularity_desc));

        switch (s) {

            case "popularity.desc":
                item1.setChecked(true);
                break;

            case "vote_average.desc":
                item2.setChecked(true);
                break;

            case "favorite":
                item3.setChecked(true);
                break;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        SharedPreferences.Editor editor = sh.edit();

        switch (item.getItemId()) {

            case R.id.btn1:
                editor.putString("selected", getString(R.string.pref_sort_by_popularity_desc));
                editor.apply();
                item.setChecked(true);
                updateThumbnails();
                break;

            case R.id.btn2:
                editor.putString("selected", getString(R.string.pref_sort_by_vote_average_desc));
                editor.apply();
                item.setChecked(true);
                updateThumbnails();
                break;

            case R.id.btn3:
                editor.putString("selected", getString(R.string.pref_sort_by_favorite));
                editor.apply();
                item.setChecked(true);
                updateThumbnails();
                break;
        }

        return false;
    }

    @Override
    public void onStart() {

        super.onStart();

        updateThumbnails();
    }


    public void updateThumbnails() {

        String tempSettings = sh.getString("selected",
                getString(R.string.pref_sort_by_popularity_desc));

        if (tempSettings.equals("favorite")) {

            Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,

                    new String[]{MoviesContract.MovieEntry.COLUMN_MOVIE_ID,

                            MoviesContract.MovieEntry.COLUMN_TITLE,

                            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,

                            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,

                            MoviesContract.MovieEntry.COLUMN_SYNOPSIS,

                            MoviesContract.MovieEntry.COLUMN_POSTER_PATH},

                    null, null, null, null, null);

            if (cursor != null) {

                if (cursor.moveToFirst()) {

                    moviesPosters = new String[cursor.getCount()];

                    MoviesList = new ArrayList<>();
                    do {
                        int mId = cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));

                        String mTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));

                        String mDate = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));

                        double mVote = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));

                        String mSynopsis = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SYNOPSIS));

                        String mPoster = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));

                        MoviesList.add(new Movie(mId, mVote, mTitle, mDate, mPoster, mSynopsis));

                    } while (cursor.moveToNext());

                    cursor.close();

                    for (int i = 0; i < MoviesList.size(); i++) {
                        Movie m = MoviesList.get(i);
                        moviesPosters[i] = m.getPoster_path();
                    }
                    gridView.setAdapter(new PostersAdapter(getActivity(), moviesPosters));

                    if (getActivity().findViewById(R.id.movie_detail_container) != null) {

                        Movie movie = MoviesList.get(0);

                        Bundle args = new Bundle();

                        args.putParcelable("movie", movie);

                        DetailFragment df = new DetailFragment();

                        df.setArguments(args);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                                df).commit();
                    }

                } else {
                    gridView.setAdapter(null);
                    Toast.makeText(getActivity(), "There is no favorite movies !", Toast.LENGTH_LONG).show();
                }
            }

        } else {

            progressDialog = new ProgressDialog(getActivity());

            progressDialog.setMessage("Please wait ...");

            progressDialog.show();

            try {

                new FetchMoviesTask().execute(sh.getString("selected", getString(R.string.pref_sort_by_popularity_desc)));
            } catch (Exception e) {
                progressDialog.dismiss();

                Toast.makeText(getActivity(), "There is a problem with your internet connection", Toast.LENGTH_LONG).show();
            }
        }


    }
    public void onSaveInstanceState(Bundle outState) {

        if (MoviesList != null) {
            outState.putParcelableArrayList(MOVIES_KEY, MoviesList);
        }
        super.onSaveInstanceState(outState);
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String movies_url = "http://api.themoviedb.org/3/discover/movie?";
            String SORT_BY = "sort_by", API_KEY = "api_key";

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String moviesJsonStr = null;
            try {
                Uri uri = Uri.parse(movies_url)
                        .buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY)
                        .build();

                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                if (stringBuilder.length() != 0) {
                    moviesJsonStr = stringBuilder.toString();
                }else if(moviesJsonStr==null){return null;}
            } catch (Exception ex) {
                Log.e("MoviesFragment ", "Error in json: ", ex);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("MoviesFragment ", "Error in closing stream : ", e);
                    }
                }
                else {return null;}
            }

            return getMoviesFromJson(moviesJsonStr);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] posters) {
            super.onPostExecute(posters);

            progressDialog.dismiss();
            if (posters != null) {

                gridView.setAdapter(new PostersAdapter(getActivity(), posters));

                if (getActivity().findViewById(R.id.movie_detail_container) != null) {
                    Movie movie = MoviesList.get(0);
                    Bundle args = new Bundle();
                    args.putParcelable("movie", movie);
                    DetailFragment df = new DetailFragment();
                    df.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                            df).commit();
                }

            }

            else {

                Toast.makeText(getActivity(), "There is an error during loading !!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public String[] getMoviesFromJson(String jsonStr) {

        try {
            MoviesList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            moviesPosters = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movie = jsonArray.getJSONObject(i);
                MoviesList.add(new Movie(movie.getInt("id"),
                        movie.getDouble("vote_average"),
                        movie.getString("original_title"),
                        movie.getString("release_date"),
                        "http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path"),
                        movie.getString("overview")));


                if (!movie.getString("poster_path").contains("null")) {
                    moviesPosters[i] = "http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path");
                } else {
                    moviesPosters[i] = "NOT AVAILABLE IMAGE !!";
                }
            }
            if(jsonStr==null){return null;}
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesPosters;
    }

    public interface Callback {
        void onItemSelected(Movie movie);
    }

}