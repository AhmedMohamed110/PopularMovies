package com.example.ahmedmohamed.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.ahmedmohamed.myapplication.R;
import com.squareup.picasso.Picasso;

public class PostersAdapter extends ArrayAdapter {

    private Context context;

    private LayoutInflater inflater;

    private String[] images;


    public PostersAdapter(Context context, String[] images) {

        super(context, R.layout.grid_poster, images);

        this.context = context;

        this.images = images;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.grid_poster, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv = (ImageView) convertView.findViewById(R.id.grid_poster_img);

        if (!images[position].equals("NOT AVAILABLE IMAGE !!")){

            Picasso
                    .with(context)
                    .load(images[position])
                    .fit()
                    .into(holder.iv);
        } else {

            holder.iv.setImageResource(R.drawable.unavailable);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView iv;
    }
}
