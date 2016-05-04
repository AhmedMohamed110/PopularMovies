package com.example.ahmedmohamed.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.ahmedmohamed.myapplication.fragments.DetailFragment;
import com.example.ahmedmohamed.myapplication.fragments.MoviesFragment;
import com.example.ahmedmohamed.myapplication.models.Movie;

public class Movies extends AppCompatActivity implements MoviesFragment.Callback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movies);


        makeRunTimeFragment(savedInstanceState);
    }

    private void makeRunTimeFragment(Bundle savedInstanceState) {

        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,
                        new DetailFragment())
                        .commit();
            }
        }
        else {

            mTwoPane = false;

            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.movies_content, new MoviesFragment())
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Movie movie) {

        if (mTwoPane) {

            DetailFragment df = new DetailFragment();

            Bundle args = new Bundle();

            args.putParcelable("movie", movie);

            df.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, df).commit();

        } else {
            startActivity(new Intent(this, DetailActivity.class).putExtra("movie", movie));
        }
    }
}
