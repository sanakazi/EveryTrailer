package com.aurum.everytrailer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.NewsBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/23/2015.
 */
public class NewsAdapter extends ArrayAdapter<NewsBean> {

    ArrayList<NewsBean> list;
    Context context;
    LayoutInflater inflater;

    public NewsAdapter(Context context, ArrayList<NewsBean> list) {
        super(context, R.layout.news_list_item, list);
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.news_list_item, parent, false);

            holder.newsImage = (ImageView) convertView.findViewById(R.id.news_img);
            holder.newsAuthorAndTime = (TextView) convertView.findViewById(R.id.news_author);
            holder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            holder.newsDesc = (TextView) convertView.findViewById(R.id.news_desc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.newsTitle.setText(list.get(position).getNews());
        holder.newsAuthorAndTime.setText(list.get(position).getTime() + " hr ago");
        holder.newsDesc.setText(list.get(position).getAuthor());

        Picasso.with(context).load(list.get(position).getImage()).resize(150, 150).centerCrop().into(holder.newsImage);

        return convertView;
    }

    class ViewHolder {
        ImageView newsImage;
        TextView newsAuthorAndTime, newsTitle, newsDesc;
    }
}
