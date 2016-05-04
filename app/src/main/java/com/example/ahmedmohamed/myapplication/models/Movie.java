package com.example.ahmedmohamed.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private int id;

    private double vote_average;

    private String original_title, release_date, poster_path, synopsis;

    public Movie(int id, double vote_average, String original_title, String release_date, String poster_path, String synopsis) {

        this.id = id;

        this.vote_average = vote_average;

        this.original_title = original_title;

        this.release_date = release_date;

        this.poster_path = poster_path;

        this.synopsis = synopsis;
    }

    public int getId() {
        return id;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(id);

        out.writeDouble(vote_average);

        out.writeString(original_title);

        out.writeString(release_date);

        out.writeString(poster_path);

        out.writeString(synopsis);
    }

    public static final Creator<Movie> CREATOR
            = new Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {

        id = in.readInt();

        vote_average = in.readDouble();

        original_title = in.readString();

        release_date = in.readString();

        poster_path = in.readString();

        synopsis = in.readString();
    }
}
