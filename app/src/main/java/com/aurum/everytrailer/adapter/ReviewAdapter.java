package com.aurum.everytrailer.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.bean.ReviewBean;
import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.aurum.everytrailer.utils.XMLParser;

import org.json.JSONStringer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by VarunBarve on 11/26/2015.
 */
public class ReviewAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<ReviewBean> list;
    ProgressDialog pd;
    String resultreview;
    ConnectionDetector connectionDetector;
    int pos;
    String reviewid;
    ViewHolder holder;
    String userId;
    String privateKey;

    public ReviewAdapter(Context context, ArrayList<ReviewBean> list, String userId) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userId = userId;
        this.privateKey = Constants.getPrivateKey();
    }

    @Override
    public int getCount() {
        if (list.size() == 0 || list.size() == 1) {
            return list.size();
        } else {
            return 2;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {

//        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.review_layout_new, parent, false);

//            holder.title = (TextView) convertView.findViewById(R.id.titleadp);
            holder.likecount = (TextView) convertView.findViewById(R.id.countlike);
            holder.name = (TextView) convertView.findViewById(R.id.nameadp);
            holder.date = (TextView) convertView.findViewById(R.id.dateadp);
            holder.description = (TextView) convertView.findViewById(R.id.despadp);
//            holder.shareimg = (ImageView) convertView.findViewById(R.id.shareimg);
            holder.likeimg = (ImageView) convertView.findViewById(R.id.likeimg);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbaradp);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.title.setText(list.get(position).getTitle());
        holder.description.setText(list.get(position).getDescription());
        holder.likecount.setText(list.get(position).getLikecount());
        holder.name.setText(list.get(position).getName());
        holder.date.setText(list.get(position).getDate());
        if (list.get(position).getRatingsount().equalsIgnoreCase("")) {
            holder.ratingBar.setRating(Float.parseFloat("0"));

        } else {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).getRatingsount()));
        }

        if (list.get(position).getUserlike().equalsIgnoreCase("0")) {
            holder.likeimg.setImageResource(R.drawable.likereview);
        } else {
            holder.likeimg.setImageResource(R.drawable.likereview_colour);
        }

        holder.likeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CONNECTION DETECTOR
                AsyncTask<Void, Boolean, Boolean> connectionDetectorTask = new ConnectionDetector()
                        .execute();

                boolean result = false;
                try {
                    result = connectionDetectorTask.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (result) {
                    pos = position;
                    reviewid = list.get(position).getRatingId();
                    new Likesend().execute();
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        holder.shareimg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent sharingIntent = new Intent(
//                        android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//                        "EveryTrailer App");
//                sharingIntent
//                        .putExtra(
//                                android.content.Intent.EXTRA_TEXT,
//                                list.get(position).getDescription()+" - Review on EveryTrailer "+"https://play.google.com/store/apps/details?id=com.aurum.everytrailer");
//                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
//            }
//        });

        return convertView;
    }

    class ViewHolder {
        TextView title, description, likecount, name, date;
        ImageView shareimg, likeimg;
        RatingBar ratingBar;
    }

    public class Likesend extends AsyncTask<Void, Void, Void> {

        JSONStringer stringer;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(context);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Loading, please wait...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_LIKEBTN);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String stri = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler>  <merchandDetail>    <merchandID>      mobileapp    </merchandID>    <password>" + privateKey + "</password>  </merchandDetail>  <inputData>    <userID>" + userId + "</userID>    <reviewID>" + reviewid + "</reviewID>  </inputData></everyTrailler>";
//                String paramsStr = "{\"pass\":\"" + Constants.getPrivateKey() + "\"}";
                stringer = new JSONStringer().object().key("xmlString").value(stri);
                Log.e("everytrailer", "everytrailer " + stringer.toString());
                osw.write(stringer.toString());
                osw.flush();
                osw.close();

                String result = Constants.readStream(con.getInputStream());


                resultreview = "" + Html.fromHtml(result);
//                Log.e(Constants.TAG, "MOVIE LIST: " + Html.fromHtml(result));

//                result = result.replaceAll("&lt;", "<");
//
//                Log.e("everytrailer", "everytrailerafterwebreview " + resultreview);
//                parseMovieListXML(result);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String status = null;
            String message = null;
            String likecount = null;

            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(resultreview);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, "status");
                message = parser.getValue(everyTrailerElement, "msz");
                likecount = parser.getValue(everyTrailerElement, "likeCount");
            }
            pd.dismiss();

            if (status.equalsIgnoreCase("0")) {
                Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();

            } else {
                if (list.get(pos).getUserlike().equalsIgnoreCase("0")) { // LIKED
                    list.get(pos).setUserlike("1");
                    list.get(pos).setLikecount(likecount);
                    holder.likeimg.setImageResource(R.drawable.likereview_colour);
                } else {// NOT LIKED
                    list.get(pos).setUserlike("0");
                    list.get(pos).setLikecount(likecount);
                    holder.likeimg.setImageResource(R.drawable.likereview);
                }
            }
            notifyDataSetChanged();


//            final Dialog query_alert = new Dialog(
//                    context);
//            query_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            query_alert.setContentView(R.layout.customlayoutok);
//            query_alert.setCancelable(false);
//            TextView txtTextView = (TextView) query_alert
//                    .findViewById(R.id.customalert);
//            txtTextView.setText(message);
//            Button track_ok = (Button) query_alert.findViewById(R.id.ok_id);
//
//            track_ok.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    query_alert.dismiss();
//                }
//            });
//            query_alert.show();


        }
    }
}

