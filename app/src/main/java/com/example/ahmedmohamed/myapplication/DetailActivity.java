package com.example.ahmedmohamed.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.ahmedmohamed.myapplication.fragments.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();

            args.putParcelable("movie",getIntent().getExtras().getParcelable("movie"));

            DetailFragment df = new DetailFragment();

            df.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_fragment, df).commit();
        }
    }

}
