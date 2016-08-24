package com.aurum.everytrailer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.AllMoviesBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/25/2015.
 */
public class AllMoviesAdapter extends ArrayAdapter<AllMoviesBean> {

    Context context;
    ArrayList<AllMoviesBean> list;
    LayoutInflater inflater;

    public AllMoviesAdapter(Context context, ArrayList<AllMoviesBean> list) {
        super(context, R.layout.allmovies_list_item, list);
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.allmovies_list_item, parent, false);

            holder.allmovies_poster = (ImageView) convertView.findViewById(R.id.allmovie_poster);
            holder.language = (TextView) convertView.findViewById(R.id.allmovies_language);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = list.get(position).getImage().trim().replace(" ", "%20");
        Picasso.with(context).load(imageUrl).resize(1280, 715).centerCrop().into(holder.allmovies_poster);

        holder.language.setText(list.get(position).getLanguage());

        return convertView;
    }

    class ViewHolder {
        ImageView allmovies_poster;
        TextView language;
    }
}
