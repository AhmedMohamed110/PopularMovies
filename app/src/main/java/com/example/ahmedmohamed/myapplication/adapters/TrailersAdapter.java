package com.example.ahmedmohamed.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ahmedmohamed.myapplication.R;

public class TrailersAdapter extends BaseAdapter {

    private String[] trailers;

    private Context context;

    public TrailersAdapter(Context context, String[] trailers) {

        this.trailers = trailers;

        this.context = context;
    }

    @Override
    public int getCount() {
        return trailers.length;
    }

    @Override
    public Object getItem(int position) {
        return trailers[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);

            convertView = inflater.inflate(R.layout.trailer, parent, false);

            holder = new ViewHolder();

            convertView.setTag(holder);

        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv = (TextView) convertView.findViewById(R.id.trailer_order);
        holder.iv = (ImageView) convertView.findViewById(R.id.trailer_icon);


        if (getItem(position).equals("There is no trailers !")) {

            holder.tv.setText(trailers[position]);

            holder.tv.setTextSize(16);

            holder.iv.setVisibility(View.GONE);
        }

        else {

            holder.tv.append((position + 1) + "");
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv;
        ImageView iv;
    }
}
