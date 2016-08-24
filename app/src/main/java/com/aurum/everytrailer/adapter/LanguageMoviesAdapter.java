package com.aurum.everytrailer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.LanguageBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/26/2015.
 */
public class LanguageMoviesAdapter extends ArrayAdapter<LanguageBean> {

    Context context;
    ArrayList<LanguageBean> list;
    LayoutInflater inflater;

    public LanguageMoviesAdapter(Context context, ArrayList<LanguageBean> list) {
        super(context, R.layout.feautred_list_item, list);

        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.feautred_list_item, parent, false);

            holder.movieName = (TextView) convertView.findViewById(R.id.movie_name);
            holder.moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster);
            holder.likePercentage = (TextView) convertView.findViewById(R.id.like_percentage);
            holder.views = (TextView) convertView.findViewById(R.id.views);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.movieName.setSelected(true);

        holder.likePercentage.setText(list.get(position).getLikePercentage()+"%");
        holder.movieName.setText(list.get(position).getMovieName());
        holder.views.setText(list.get(position).getViews());

        String imageUrl = list.get(position).getImage().trim().replace(" ", "%20");
        Picasso.with(context).load(imageUrl).resize(1280, 715).centerCrop().into(holder.moviePoster);

        return convertView;

    }

    class ViewHolder {
        TextView likePercentage, views, movieName;
        ImageView moviePoster;
    }


}
