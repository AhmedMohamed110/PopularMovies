package com.example.ahmedmohamed.myapplication.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ahmedmohamed.myapplication.BuildConfig;
import com.example.ahmedmohamed.myapplication.data.MoviesDbHelper;
import com.example.ahmedmohamed.myapplication.data.MoviesContract.MovieEntry;
import com.example.ahmedmohamed.myapplication.R;
import com.example.ahmedmohamed.myapplication.adapters.TrailersAdapter;
import com.example.ahmedmohamed.myapplication.models.Author;
import com.example.ahmedmohamed.myapplication.models.Movie;
import com.squareup.picasso.Picasso;
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

public class DetailFragment extends Fragment {

    private ImageView poster;

    private TextView original_title, release_date, vote_average, overview, show_reviews;

    private Movie movie;

    String[] trailerKeys;

    private ListView trailer_list;

    private SQLiteDatabase db;

    SharedPreferences sh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        Bundle args = getArguments();

        if (args != null) {
            movie = args.getParcelable("movie");
        }

        MoviesDbHelper mOpenHelper = new MoviesDbHelper(getActivity());

        db = mOpenHelper.getWritableDatabase();

        poster = (ImageView) rootView.findViewById(R.id.poster);

        original_title = (TextView) rootView.findViewById(R.id.original_title);

        release_date = (TextView) rootView.findViewById(R.id.release_date);

        vote_average = (TextView) rootView.findViewById(R.id.vote_average);

        overview = (TextView) rootView.findViewById(R.id.synopsis);

        trailer_list = (ListView) rootView.findViewById(R.id.trailer_list);

        show_reviews = (TextView) rootView.findViewById(R.id.show_reviews);

        final CheckBox favorite = (CheckBox) rootView.findViewById(R.id.favorite);


        trailer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailerKeys[position]));
                startActivity(intent);
            }
        });


        if (movie != null) {

            String query = "SELECT * FROM " + MovieEntry.TABLE_NAME + " WHERE movie_id= '" + movie.getId() + "'";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() <= 0) {
                favorite.setChecked(false);
            }

            else {
                favorite.setChecked(true);
            }
            cursor.close();
        }


        favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (movie != null) {

                        ContentValues values = new ContentValues();

                        values.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());

                        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());

                        values.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());

                        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

                        values.put(MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());

                        values.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());

                        long rowId = db.insert(MovieEntry.TABLE_NAME, null, values);

                        if (rowId != -1) {

                            Toast.makeText(getActivity(), "Marked as favorite", Toast.LENGTH_SHORT).show();
                        }

                    }

                    else {

                        favorite.setChecked(false);
                        Toast.makeText(getActivity(), "Select a movie first !", Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    int id = movie.getId();

                    String[] where = {String.valueOf(id)};

                    int rowId = db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MOVIE_ID + "= ?", where);

                    if (rowId > -1) {

                        Toast.makeText(getActivity(), "Done..", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        if (getActivity().findViewById(R.id.movie_detail_container) == null) {

            sh = getActivity().getSharedPreferences("myShared",getActivity().MODE_PRIVATE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        SharedPreferences.Editor editor = sh.edit();

        switch (item.getItemId()) {

            case R.id.btn1:
                editor.putString("selected", getString(R.string.pref_sort_by_popularity_desc));
                editor.apply();
                item.setChecked(true);
                break;

            case R.id.btn2:
                editor.putString("selected", getString(R.string.pref_sort_by_vote_average_desc));
                editor.apply();
                item.setChecked(true);
                break;

            case R.id.btn3:
                editor.putString("selected", getString(R.string.pref_sort_by_favorite));
                editor.apply();
                item.setChecked(true);
                break;
        }

        return false;
    }

    @Override
    public void onStart() {

        super.onStart();

        if (movie != null) {
            updateMovieDetail();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (movie != null) {
            new FetchTrailersTask().execute(movie.getId());
            new FetchReviewsTask().execute(movie.getId());
        }
    }

    private void updateMovieDetail() {


        if (movie.getPoster_path().length() != 0) {

            Picasso
                    .with(getActivity())
                    .load(movie.getPoster_path())
                    .fit()
                    .into(poster);
        }

        else {
            poster.setImageResource(R.drawable.unavailable);
        }


        if (movie.getTitle().length() != 0)
            original_title.setText(movie.getTitle());
        else
            original_title.setText("The movie has not name");


        if (movie.getRelease_date().length() != 0) {

            String[] subDate = movie.getRelease_date().split("-");
            release_date.setText(subDate[0]);
        } else
            release_date.setText("The movie has no release date");


        if (String.valueOf(movie.getVote_average()).length() != 0) {
            String vote_average_txt = String.valueOf(movie.getVote_average()) + "/10";
            vote_average.setText(vote_average_txt);
        } else
            vote_average.setText("The movie has no vote average");


        if (movie.getSynopsis().length() != 0)
            overview.setText(movie.getSynopsis());
        else
            overview.setText("The movie has no synopsis");

    }


    public class FetchTrailersTask extends AsyncTask<Integer, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Integer... params) {

            String movies_url = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";
            String API_KEY = "api_key";

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String moviesJsonStr = null;
            try {
                Uri uri = Uri.parse(movies_url)
                        .buildUpon()
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
                }else if(moviesJsonStr==null) {return null;}
            } catch (Exception ex) {
                Log.e("DetailFragment ", "Error in json: ", ex);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("DetailFragment ", "Error in closing stream : ", e);
                    }
                }
            }

            return getTrailersFromJson(moviesJsonStr);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] trailers) {
            super.onPostExecute(trailers);

            if (trailers.length == 0) {
                trailers = new String[1];
                trailers[0] = "There is no trailers !";
            }
            trailer_list.setAdapter(new TrailersAdapter(getActivity(), trailers));
            setListViewHeightBasedOnChildren(trailer_list);

        }
    }

    public String[] getTrailersFromJson(String jsonStr) {
        try {
            if(jsonStr == null ){
//                Toast.makeText(getActivity(), "There is an error during loading !!", Toast.LENGTH_LONG).show();
                return null;
            }
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            trailerKeys = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trailer = jsonArray.getJSONObject(i);
                trailerKeys[i] = trailer.getString("key");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerKeys;
    }


    public class FetchReviewsTask extends AsyncTask<Integer, Void, ArrayList<Author>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Author> doInBackground(Integer... params) {
            String movies_url = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";
            String API_KEY = "api_key";

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String moviesJsonStr = null;
            try {
                Uri uri = Uri.parse(movies_url)
                        .buildUpon()
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
                Log.e("DetailFragment ", "Error in json: ", ex);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("DetailFragment ", "Error in closing stream : ", e);
                    }
                }
            }

            return getReviewsFromJson(moviesJsonStr);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Author> authors) {
            super.onPostExecute(authors);

            Log.d("onPostExecute: ", authors.size() + "");

            if (authors.size() > 0) {
                for (Author author : authors) {
                    show_reviews.append("- Author:\n" + author.getName() + "\n" +
                            "- Content:\n" + author.getContent() +
                            "\n \n" + "_________________" + "\n \n \n");
                }
            } else {
                show_reviews.setText("There is no reviews ! \n \n");
            }

        }
    }

    public ArrayList<Author> getReviewsFromJson(String jsonStr) {
        ArrayList<Author> authorArrayList = new ArrayList<>();
        try {
            if(jsonStr == null ){
//                Toast.makeText(getActivity(), "There is an error during loading !!", Toast.LENGTH_LONG).show();
                return null;
            }
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject JsonObj = jsonArray.getJSONObject(i);
                authorArrayList.add(new Author(JsonObj.getString("author"), JsonObj.getString("content")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorArrayList;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {

            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
