package com.aurum.everytrailer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.LinkBean;

import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/30/2015.
 */
public class LinkAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<LinkBean> list;

    public LinkAdapter(Context context, ArrayList<LinkBean> list) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.adapterlayoutlink, parent, false);
            holder.title1 = (TextView) convertView.findViewById(R.id.linkname);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title1.setText(list.get(position).getLinkname());


        return convertView;
    }

    class ViewHolder {
        TextView title1;

    }
}

