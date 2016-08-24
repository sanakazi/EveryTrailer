package com.aurum.everytrailer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.NotificationBean;

import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/23/2015.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationBean> {

    ArrayList<NotificationBean> list;
    Context context;
    LayoutInflater inflater;

    public NotificationAdapter(Context context, ArrayList<NotificationBean> list) {
        super(context, R.layout.notification_list_item, list);
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.notification_list_item, parent, false);

            holder.notificationTime = (TextView) convertView.findViewById(R.id.notification_time);
            holder.notificationTitle = (TextView) convertView.findViewById(R.id.notification_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.notificationTitle.setText(list.get(position).getTitle());
        holder.notificationTime.setText(list.get(position).getTime() + " hr ago");

        return convertView;
    }

    class ViewHolder {
        TextView notificationTime, notificationTitle;
    }

}
