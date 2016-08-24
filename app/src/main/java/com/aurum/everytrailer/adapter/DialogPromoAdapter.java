package com.aurum.everytrailer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.DialogPromoBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/24/2015.
 */
public class DialogPromoAdapter extends ArrayAdapter<DialogPromoBean> {

    ArrayList<DialogPromoBean> list;
    Context context;
    LayoutInflater inflater;

    public DialogPromoAdapter(Context context, ArrayList<DialogPromoBean> list) {
        super(context, R.layout.dialog_promo_list_item, list);
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.dialog_promo_list_item, parent, false);

            holder.dialogPromoImg = (ImageView) convertView.findViewById(R.id.dialog_promo_img);
            holder.dialogPromoLikes = (TextView) convertView.findViewById(R.id.dialog_promo_likes);
            holder.dialogPromoViews = (TextView) convertView.findViewById(R.id.dialog_promo_views);
            holder.dialogPromoTitle = (TextView) convertView.findViewById(R.id.dialog_promo_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dialogPromoViews.setText(list.get(position).getVideoViewCount());
        holder.dialogPromoLikes.setText(list.get(position).getTotalLikeCount() + "%");
        holder.dialogPromoTitle.setText(list.get(position).getTitle());

        String imageUrl = list.get(position).getVideoImageURL().replace(" ", "%20");

        Picasso.with(context).load(imageUrl).resize(1280, 715).centerCrop().into(holder.dialogPromoImg);

        return convertView;
    }

    class ViewHolder {
        ImageView dialogPromoImg;
        TextView dialogPromoLikes, dialogPromoViews, dialogPromoTitle;
    }
}
