package com.aurum.everytrailer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurum.everytrailer.adapter.DialogPromoAdapter;
import com.aurum.everytrailer.adapter.LinkAdapter;
import com.aurum.everytrailer.adapter.RelatedPromoAdapter;
import com.aurum.everytrailer.bean.DialogPromoBean;
import com.aurum.everytrailer.bean.LinkBean;
import com.aurum.everytrailer.bean.PollBean;
import com.aurum.everytrailer.bean.RelatedPromoBean;
import com.aurum.everytrailer.bean.ReviewBean;
import com.aurum.everytrailer.bean.VideoAnalytics;
import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.aurum.everytrailer.utils.XMLParser;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONStringer;
import org.lucasr.twowayview.TwoWayView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by NiravShrimali on 11/2/2015.
 */
public class Youtube_Activity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, AppCompatCallback {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    public static ArrayList<ReviewBean> lisreviewbean;
    String Countview;
    String Videoid;
    String VideoSynopsis;
    String userRating;
    TextView Synopsistxt;
    TextView videocounttv;
    AppCompatDelegate delegate;
    ArrayList<String> poll1;
    ArrayList<String> poll2;
    ArrayList<String> poll3;
    ArrayList<String> poll4;
    ArrayList<PollBean> pollArrayList;
    ArrayList<String> ArrayListproviderName;
    ArrayList<String> ArrayListlink;
    ArrayList<String> ArrayListtitle;
    ArrayList<String> ArrayListdescription;
    ArrayList<String> ArrayListuserLike;
    ArrayList<String> ArrayListtotalLikeCount;
    ArrayList<String> ArrayListdate;
    ArrayList<String> rating;
    ProgressDialog pd;
    TextView textViewwillgo, textViewmaybe, textViewwontgo;
    TextView textViewwillgocount, textViewmaybecount, textViewwontgocount, videoliketxt;
    TwoWayView mDialogPromo, mRelatedPromo;
    DialogPromoAdapter dialogAdapter;
    RelatedPromoAdapter relatedAdapter;
    ArrayList<DialogPromoBean> dialogPromoList;
    ArrayList<RelatedPromoBean> relatedPromoList;
    ArrayList<VideoAnalytics> videoAnalyticsList;
    String movieName, movieId, trailerType, movieIdDetail, trailerIdDetail;
    YouTubePlayer youTubePlayer;
    FrameLayout imagelikeup, imagemaybe, imagewillnot;
    boolean isliketotal = false;
    LinearLayout linerbook;
    ArrayList<LinkBean> linkbean;
    LinkAdapter linkadpter;
    String resultratnig;
    String pollwillgo;
    String userLikedornot;
    //    ImageView imgliketotal;
    String Videovideocount;
    String Likevid;
    // VIDEO ANALYTICS
    ImageView videoAnalyticsImgHigh, videoAnalyticsImgLow;
    TextView videoAnalyticsLikesHigh, videoAnalyticsLikesLow, videoAnalyticsViewsHigh, videoAnalyticsViewsLow, videoAnalyticsTitleHigh, videoAnalyticsTitleLow;
    ProgressDialog mProgressDialog;
    ConnectionDetector connectionDetector;
    boolean isReviewsVisible = true, isDialogPromoVisible = true, isRelatedPromoVisible = true, isVideoAnalyticsHighVisible = true, isVideoAnalyticsLowVisible = true;
    //    RelativeLayout review_container;
    LinearLayout dialog_promo_container, related_promo_container, video_analytics_high_container, video_analytics_low_container, video_analytics_container;
    ScrollView container;
    SharedPreferences prefs;
    String userId;
    LinearLayout linearLayout;
    View rootLinearView;
    android.support.design.widget.AppBarLayout appbarlay;
    String mainStatus;
    LinearLayout linReview;
    TextView description, likecount, name, date;
    ImageView likeimg, review_profile_pic;
    RatingBar ratingBar;
    TextView morereview;
    String statusreview = null;
    String messagereview = null;
    CircleImageView comments_profile_pic;
    EditText comments_add_comments;
    ImageButton comments_send;
    String commentsString;
    ProgressBar progressBar_willgo, progressBar_maybe, progressBar_wontgo;
    int progressBarWillGoStatus, progressBarMaybeStatus, progressBarWontgoStatus;
    float totalPollPercent;
    ImageView wont_go_image, maybe_image, will_go_image;
    String privateKey;
    // YouTube player view
    private YouTubePlayerView youTubeView;
    private boolean fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GET PRIVATE KEY
        privateKey = Constants.getPrivateKey();

        prefs = PreferenceManager.getDefaultSharedPreferences(Youtube_Activity.this);

        delegate = AppCompatDelegate.create(this, this);

        //call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);

        //use the delegate to inflate the layout
        delegate.setContentView(R.layout.youtubeactivity_layout);

        movieName = getIntent().getStringExtra("Movie Name");
        movieId = getIntent().getStringExtra("Movie ID");
        trailerType = getIntent().getStringExtra("Trailer Type");
        userId = prefs.getString("User_ID", null);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Youtube_Activity.this);

        String profile_image = prefs.getString("Profile_picture", "");

        // ROOT CONTAINER
        container = (ScrollView) findViewById(R.id.container);

        //add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaryoutubeactivity);

        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setTitle(movieName);
        toolbar.setTitle(movieName);
        delegate.getSupportActionBar().setHomeButtonEnabled(true);
        delegate.getSupportActionBar().hide();

        appbarlay = (android.support.design.widget.AppBarLayout) findViewById(R.id.appbarlay);
        linearLayout = (LinearLayout) findViewById(R.id.linear11);
        rootLinearView = findViewById(R.id.liner123);

        // COMMENTS
        comments_profile_pic = (CircleImageView) findViewById(R.id.comments_profile_pic);
        comments_add_comments = (EditText) findViewById(R.id.comments_add_comments);
        comments_send = (ImageButton) findViewById(R.id.comments_send);
        comments_send.setVisibility(View.GONE);

        Picasso.with(Youtube_Activity.this).load(profile_image).placeholder(R.drawable.actionbar_bg).into(comments_profile_pic);

        comments_add_comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (comments_add_comments.getText().toString().isEmpty()) {
                    comments_send.setVisibility(View.GONE);
                } else {
                    comments_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        comments_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentsString = comments_add_comments.getText().toString();

                if (commentsString.isEmpty()) {
                    Toast.makeText(Youtube_Activity.this, "Please write a comment.", Toast.LENGTH_SHORT).show();
                } else {
                    new AddComments().execute();
                }
            }
        });

        // REVIEW
        linReview = (LinearLayout) findViewById(R.id.linrev);

//        review_container = (RelativeLayout) findViewById(R.id.review_container);
        dialog_promo_container = (LinearLayout) findViewById(R.id.dialog_promo_container);
        related_promo_container = (LinearLayout) findViewById(R.id.related_promo_container);
        video_analytics_high_container = (LinearLayout) findViewById(R.id.video_analytics_high_container);
        video_analytics_low_container = (LinearLayout) findViewById(R.id.video_analytics_low_container);
        video_analytics_container = (LinearLayout) findViewById(R.id.video_analytics_container);

        //POLL (WILL GO, MAYBE, WONT GO)
        poll1 = new ArrayList<String>();
        poll2 = new ArrayList<String>();
        poll3 = new ArrayList<String>();
        poll4 = new ArrayList<String>();
        pollArrayList = new ArrayList<PollBean>();
        isliketotal = false;

        imagelikeup = (FrameLayout) findViewById(R.id.imagelikeup); // WILL GO
        imagemaybe = (FrameLayout) findViewById(R.id.imagemaybe); // MAYBE
        imagewillnot = (FrameLayout) findViewById(R.id.imagewillnot); // WONT GO
        linerbook = (LinearLayout) findViewById(R.id.linerbook); // BUY TICKETS

        textViewwillgo = (TextView) findViewById(R.id.willgotxt);
        textViewmaybe = (TextView) findViewById(R.id.maybetxt);
        textViewwontgo = (TextView) findViewById(R.id.wontgotxt);

        textViewwillgocount = (TextView) findViewById(R.id.willgotxtcount);
        textViewmaybecount = (TextView) findViewById(R.id.maybetxtcount);
        textViewwontgocount = (TextView) findViewById(R.id.wontgotxtcount);

        progressBar_willgo = (ProgressBar) findViewById(R.id.progressBar_willgo);
        progressBar_maybe = (ProgressBar) findViewById(R.id.progressBar_maybe);
        progressBar_wontgo = (ProgressBar) findViewById(R.id.progressBar_wontgo);

        wont_go_image = (ImageView) findViewById(R.id.wont_go_image);
        maybe_image = (ImageView) findViewById(R.id.maybe_image);
        will_go_image = (ImageView) findViewById(R.id.will_go_image);

        ArrayListproviderName = new ArrayList<String>();
        ArrayListlink = new ArrayList<String>();

        // SYNOPSIS
        ArrayListtitle = new ArrayList<String>();
        ArrayListdescription = new ArrayList<String>();
        ArrayListuserLike = new ArrayList<String>();
        ArrayListtotalLikeCount = new ArrayList<String>();
        ArrayListdate = new ArrayList<String>();
        rating = new ArrayList<String>();

        Synopsistxt = (TextView) findViewById(R.id.Synopsistxt);
        videocounttv = (TextView) findViewById(R.id.textView2);
        videoliketxt = (TextView) findViewById(R.id.videoliketxt);

//        imgliketotal = (ImageView) findViewById(R.id.imagelikecount);

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
            new GetMovieDetail().execute();
        } else {
            Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }


        // VIDEO ANALYTICS
        videoAnalyticsImgHigh = (ImageView) findViewById(R.id.video_analytics_img_high);
        videoAnalyticsImgLow = (ImageView) findViewById(R.id.video_analytics_img_low);
        videoAnalyticsLikesHigh = (TextView) findViewById(R.id.video_analytics_likes_high);
        videoAnalyticsLikesLow = (TextView) findViewById(R.id.video_analytics_likes_low);
        videoAnalyticsViewsHigh = (TextView) findViewById(R.id.video_analytics_views_high);
        videoAnalyticsViewsLow = (TextView) findViewById(R.id.video_analytics_views_low);
        videoAnalyticsTitleHigh = (TextView) findViewById(R.id.video_analytics_title_high);
        videoAnalyticsTitleLow = (TextView) findViewById(R.id.video_analytics_title_low);

        // DIALOG PROMO
        mDialogPromo = (TwoWayView) findViewById(R.id.dialog_promo);

        // RELATED PROMO
        mRelatedPromo = (TwoWayView) findViewById(R.id.related_promo);

        dialogPromoList = new ArrayList<DialogPromoBean>();
        relatedPromoList = new ArrayList<RelatedPromoBean>();
        videoAnalyticsList = new ArrayList<VideoAnalytics>();

        morereview = (TextView) findViewById(R.id.morereviewlink);
        morereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(Youtube_Activity.this, ReviewActvity.class);
                intnt.putExtra("Movie_Name", movieName);
                startActivity(intnt);
            }
        });

        // YOUTUBE VIDEO PLAYER
        Youtube_Activity.this.delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        linerbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getlinklistdialog();
            }
        });

//        imgliketotal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectionDetector.isConnectingToInternet()) {
//                    new Likevideo().execute();
//                } else {
//                    Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        videoliketxt.setOnClickListener(new View.OnClickListener() {
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
                    new Likevideo().execute();
                } else {
                    Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imagelikeup.setOnClickListener(new View.OnClickListener() {
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
                    new pollwillgo().execute();
                    pollselection(Youtube_Activity.this, true, true, false);
                } else {
                    Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imagemaybe.setOnClickListener(new View.OnClickListener() {
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
                    new pollmaybe().execute();
                    pollselection(Youtube_Activity.this, false, true, false);
                } else {
                    Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imagewillnot.setOnClickListener(new View.OnClickListener() {
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
                    new pollwontgo().execute();
                    pollselection(Youtube_Activity.this, false, false, true);
                } else {
                    Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // RELATED PROMO
        mRelatedPromo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Youtube_Activity.this, Youtube_Activity.class);
                intent.putExtra("Movie Name", relatedPromoList.get(position).getTitle());
                intent.putExtra("Movie ID", relatedPromoList.get(position).getTrailerId());
                intent.putExtra("Trailer Type", trailerType);
                startActivity(intent);
                finish();
            }
        });

        // DIALOG PROMO
        mDialogPromo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Youtube_Activity.this, Youtube_Activity.class);
                intent.putExtra("Movie Name", dialogPromoList.get(position).getTitle());
                intent.putExtra("Movie ID", dialogPromoList.get(position).getTrailerId());
                intent.putExtra("Trailer Type", trailerType);
                startActivity(intent);
                finish();
            }
        });

        video_analytics_high_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Youtube_Activity.this, Youtube_Activity.class);
                intent.putExtra("Movie Name", videoAnalyticsList.get(0).getHigherTitle());
                intent.putExtra("Movie ID", videoAnalyticsList.get(0).getHigherTrailerId());
                intent.putExtra("Trailer Type", trailerType);
                startActivity(intent);
                finish();
            }
        });

        video_analytics_low_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Youtube_Activity.this, Youtube_Activity.class);
                intent.putExtra("Movie Name", videoAnalyticsList.get(0).getLowerTitle());
                intent.putExtra("Movie ID", videoAnalyticsList.get(0).getLowerTrailerId());
                intent.putExtra("Trailer Type", trailerType);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (connectionDetector.isConnectingToInternet()) {
//            new GetMovieDetail().execute();
//        } else {
//            Toast.makeText(Youtube_Activity.this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void getlinklistdialog() {

        ListView listvieww = null;
        try {

            if (linkbean.size() == 0) {
                Toast.makeText(Youtube_Activity.this, "No links found", Toast.LENGTH_SHORT).show();
            } else {
                final Dialog query_alert1 = new Dialog(
                        Youtube_Activity.this);
                query_alert1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                query_alert1.setContentView(R.layout.alert_listview_layout);
                listvieww = (ListView) query_alert1
                        .findViewById(R.id.listviewalertlink);
                linkadpter = new LinkAdapter(Youtube_Activity.this, linkbean);
                listvieww.setAdapter(linkadpter);
                query_alert1.show();

                listvieww.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final String urlstr1 = linkbean.get(position).getLink();
                        Intent url = new Intent(Intent.ACTION_VIEW);
                        url.setData(Uri.parse(urlstr1.trim()));
                        startActivity(url);
                        query_alert1.dismiss();

                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error error12", "error error12" + e);

        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        youTubePlayer.setOnFullscreenListener(this);
        if (!b) {
            youTubePlayer.cueVideo(Videoid);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider
                                                provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YouTubePlayer (%1$s)", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constants.youtubedeveloperkey, this);
        }

    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        doLayout();
    }

    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void NotFullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) youTubeView.getLayoutParams();
        if (fullscreen) {
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            FullScreencall();
            rootLinearView.setVisibility(View.GONE);
            appbarlay.setVisibility(View.GONE);
        } else {
            rootLinearView.setVisibility(View.VISIBLE);
            appbarlay.setVisibility(View.VISIBLE);
            NotFullScreencall();
            ViewGroup.LayoutParams otherViewsParams = rootLinearView.getLayoutParams();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                playerParams.width = otherViewsParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                playerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                playerParams.weight = 0;
                otherViewsParams.height = 0;
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
            }
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);


    }

    private void parseMovieListXML(String MovieListXml) {

        lisreviewbean = new ArrayList<ReviewBean>();
        linkbean = new ArrayList<LinkBean>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            Countview = parser.getValue(everyTrailerElement, "videoViewCount");
            Videoid = parser.getValue(everyTrailerElement, "videoURL");
            VideoSynopsis = parser.getValue(everyTrailerElement, "synopsis");
            Videovideocount = parser.getValue(everyTrailerElement, "totalLikeCount");
            userLikedornot = parser.getValue(everyTrailerElement, "userLike");
            userRating = parser.getValue(everyTrailerElement, "userReating");
            movieIdDetail = parser.getValue(everyTrailerElement, "movieID");
            trailerIdDetail = parser.getValue(everyTrailerElement, "trailerID");
            mainStatus = parser.getValue(everyTrailerElement, "status");

            // POLLS
            NodeList videoPoll = everyTrailerElement.getElementsByTagName("videoPoll");
            for (int j = 0; j < videoPoll.getLength(); j++) {
                Element poll = (Element) videoPoll.item(j);
                NodeList pollnode = poll.getElementsByTagName("poll");

                for (int k = 0; k < pollnode.getLength(); k++) {
                    Element polle = (Element) pollnode.item(k);

                    String pollName = parser.getValue(polle, "pollName");
                    String pollID = parser.getValue(polle, "pollID");
                    String pollCount = parser.getValue(polle, "pollCount");
                    String userPoll = parser.getValue(polle, "userPoll");

                    poll1.add(pollName);
                    poll2.add(pollID);
                    poll3.add(pollCount);
                    poll4.add(userPoll);

                    pollArrayList.add(new PollBean(pollName, pollID, pollCount, userPoll));
                }
            }

            // BUY TICKETS
            NodeList buyTicket = everyTrailerElement.getElementsByTagName("buyTicket");
            for (int j = 0; j < buyTicket.getLength(); j++) {
                Element buyTicketnode = (Element) buyTicket.item(j);
                NodeList provider = buyTicketnode.getElementsByTagName("provider");

                for (int k = 0; k < provider.getLength(); k++) {
                    Element providersublist = (Element) provider.item(k);

                    String providerName = parser.getValue(providersublist, "providerName");
                    String link = parser.getValue(providersublist, "link");

                    linkbean.add(new LinkBean(link, providerName));

                }
            }


            // REVIEW DETAILS
            NodeList reviewDetails = everyTrailerElement.getElementsByTagName("reviewDetails");
            for (int j = 0; j < reviewDetails.getLength(); j++) {
                Element reviewDetailsnode = (Element) reviewDetails.item(j);
                NodeList review = reviewDetailsnode.getElementsByTagName("review");

                if (review.getLength() == 0) {
                    Log.e(Constants.TAG, "REVIEW NODE LENGTH: " + review.getLength());
                    lisreviewbean = new ArrayList<ReviewBean>();
                    isReviewsVisible = false;
                } else {
                    for (int k = 0; k < review.getLength(); k++) {
                        Element reviewsublist = (Element) review.item(k);

                        String title = parser.getValue(reviewsublist, "title");

                        String description = parser.getValue(reviewsublist, "description");
                        String userLike = parser.getValue(reviewsublist, "userLike");
                        String totalLikeCount = parser.getValue(reviewsublist, "totalLikeCount");
                        String date = parser.getValue(reviewsublist, "date");
                        String userName = parser.getValue(reviewsublist, "userName");
                        String ratingtxt = parser.getValue(reviewsublist, "rating");
                        String ratingId = parser.getValue(reviewsublist, "reviewID");
                        String profilePic = parser.getValue(reviewsublist, "profilePic");

                        lisreviewbean.add(new ReviewBean(title, description, totalLikeCount, userName, date, ratingtxt, ratingId, userLike, profilePic));

                        Collections.sort(lisreviewbean, new Comparator<ReviewBean>() {
                            @Override
                            public int compare(ReviewBean lhs, ReviewBean rhs) {
                                return rhs.getDate().compareTo(lhs.getDate());
                            }
                        });
                    }
                }
            }

            // DIALOG PROMO
            NodeList dialogPromoDetails = everyTrailerElement.getElementsByTagName("dialougePromos");

            if (dialogPromoDetails.getLength() == 0) {
                Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH" + dialogPromoDetails.getLength());
                dialogPromoList = new ArrayList<>();
                isDialogPromoVisible = false;
            } else {
                for (int j = 0; j < dialogPromoDetails.getLength(); j++) {
                    Element dialogPromoDetailsnode = (Element) dialogPromoDetails.item(j);
                    NodeList dialogPromo = dialogPromoDetailsnode.getElementsByTagName("promo");

                    if (dialogPromo.getLength() == 0) {
                        Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + dialogPromo.getLength());
                        dialogPromoList = new ArrayList<DialogPromoBean>();
                        isDialogPromoVisible = false;
                    } else {
                        for (int k = 0; k < dialogPromo.getLength(); k++) {
                            Element dialogPromoSubList = (Element) dialogPromo.item(k);

                            String title = parser.getValue(dialogPromoSubList, "title");
                            String trailerID = parser.getValue(dialogPromoSubList, "trailerID");
                            String movieID = parser.getValue(dialogPromoSubList, "movieID");
                            String videoImageURL = parser.getValue(dialogPromoSubList, "videoImageURL");
                            String videoURL = parser.getValue(dialogPromoSubList, "videoURL");
                            String videoViewCount = parser.getValue(dialogPromoSubList, "videoViewCount");
                            String userLike = parser.getValue(dialogPromoSubList, "userLike");
                            String totalLikeCount = parser.getValue(dialogPromoSubList, "totalLikeCount");

                            dialogPromoList.add(new DialogPromoBean(title, trailerID, movieID, videoImageURL, videoURL, videoViewCount, userLike, totalLikeCount));
                        }
                    }
                }
            }


            // RELATED PROMO
            NodeList relatedPromoDetails = everyTrailerElement.getElementsByTagName("relatedPromos");
            for (int j = 0; j < relatedPromoDetails.getLength(); j++) {
                Element relatedPromoDetailsnode = (Element) relatedPromoDetails.item(j);
                NodeList relatedPromo = relatedPromoDetailsnode.getElementsByTagName("promo");

                if (relatedPromo.getLength() == 0) {
                    Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + relatedPromo.getLength());
                    relatedPromoList = new ArrayList<RelatedPromoBean>();
                    isRelatedPromoVisible = false;
                } else {
                    for (int k = 0; k < relatedPromo.getLength(); k++) {
                        Element relatedPromoSubList = (Element) relatedPromo.item(k);

                        String title = parser.getValue(relatedPromoSubList, "title");
                        String trailerID = parser.getValue(relatedPromoSubList, "trailerID");
                        String movieID = parser.getValue(relatedPromoSubList, "movieID");
                        String videoImageURL = parser.getValue(relatedPromoSubList, "videoImageURL");
                        String videoURL = parser.getValue(relatedPromoSubList, "videoURL");
                        String videoViewCount = parser.getValue(relatedPromoSubList, "videoViewCount");
                        String userLike = parser.getValue(relatedPromoSubList, "userLike");
                        String totalLikeCount = parser.getValue(relatedPromoSubList, "totalLikeCount");

                        relatedPromoList.add(new RelatedPromoBean(title, trailerID, movieID, videoImageURL, videoURL, videoViewCount, userLike, totalLikeCount));
                    }
                }
            }

            // VIDEO ANALYTICS
            NodeList videoAnalyticsDetails = everyTrailerElement.getElementsByTagName("videoAnalysis");
            for (int j = 0; j < videoAnalyticsDetails.getLength(); j++) {
                VideoAnalytics videoAnalyticsBean = new VideoAnalytics();

                Element videoAnalyticsDetailsnode = (Element) videoAnalyticsDetails.item(j);

                // HIGHER
                NodeList videoAnalyticsHigher = videoAnalyticsDetailsnode.getElementsByTagName("higher");

                if (videoAnalyticsHigher.getLength() == 0) {
                    Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + videoAnalyticsHigher.getLength());
                    videoAnalyticsList = new ArrayList<VideoAnalytics>();
                    isVideoAnalyticsHighVisible = false;
                } else {
                    for (int k = 0; k < videoAnalyticsHigher.getLength(); k++) {
                        Element videoAnalyticsSubList = (Element) videoAnalyticsHigher.item(k);

                        String highertitle = parser.getValue(videoAnalyticsSubList, "title");
                        String highertrailerID = parser.getValue(videoAnalyticsSubList, "trailerID");
                        String highermovieID = parser.getValue(videoAnalyticsSubList, "movieID");
                        String highervideoImageURL = parser.getValue(videoAnalyticsSubList, "videoImageURL");
                        String highervideoURL = parser.getValue(videoAnalyticsSubList, "videoURL");
                        String highervideoViewCount = parser.getValue(videoAnalyticsSubList, "videoViewCount");
                        String higheruserLike = parser.getValue(videoAnalyticsSubList, "userLike");
                        String highertotalLikeCount = parser.getValue(videoAnalyticsSubList, "totalLikeCount");

                        videoAnalyticsBean.setHigherTitle(highertitle);
                        videoAnalyticsBean.setHigherTrailerId(highertrailerID);
                        videoAnalyticsBean.setHigherMovieId(highermovieID);
                        videoAnalyticsBean.setHigherVideoImageURL(highervideoImageURL);
                        videoAnalyticsBean.setHigherVideoURL(highervideoURL);
                        videoAnalyticsBean.setHigherVideoViewCount(highervideoViewCount);
                        videoAnalyticsBean.setHigherUserLike(higheruserLike);
                        videoAnalyticsBean.setHigherTotalLikeCount(highertotalLikeCount);
                    }
                }

                // LOWER
                NodeList videoAnalyticsLower = videoAnalyticsDetailsnode.getElementsByTagName("lower");

                if (videoAnalyticsLower.getLength() == 0) {
                    Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + videoAnalyticsLower.getLength());
                    videoAnalyticsList = new ArrayList<VideoAnalytics>();
                    isVideoAnalyticsLowVisible = false;
                } else {
                    isVideoAnalyticsLowVisible = true;
                    for (int k = 0; k < videoAnalyticsLower.getLength(); k++) {
                        Element videoAnalyticsSubList = (Element) videoAnalyticsLower.item(k);

                        String lowertitle = parser.getValue(videoAnalyticsSubList, "title");
                        String lowertrailerID = parser.getValue(videoAnalyticsSubList, "trailerID");
                        String lowermovieID = parser.getValue(videoAnalyticsSubList, "movieID");
                        String lowervideoImageURL = parser.getValue(videoAnalyticsSubList, "videoImageURL");
                        String lowervideoURL = parser.getValue(videoAnalyticsSubList, "videoURL");
                        String lowervideoViewCount = parser.getValue(videoAnalyticsSubList, "videoViewCount");
                        String loweruserLike = parser.getValue(videoAnalyticsSubList, "userLike");
                        String lowertotalLikeCount = parser.getValue(videoAnalyticsSubList, "totalLikeCount");


                        videoAnalyticsBean.setLowerTitle(lowertitle);
                        videoAnalyticsBean.setLowerTrailerId(lowertrailerID);
                        videoAnalyticsBean.setLowerMovieId(lowermovieID);
                        videoAnalyticsBean.setLowerVideoImageURL(lowervideoImageURL);
                        videoAnalyticsBean.setLowerVideoURL(lowervideoURL);
                        videoAnalyticsBean.setLowerVideoViewCount(lowervideoViewCount);
                        videoAnalyticsBean.setLowerUserLike(loweruserLike);
                        videoAnalyticsBean.setLowerTotalLikeCount(lowertotalLikeCount);
                    }
                }
                videoAnalyticsList.add(videoAnalyticsBean);
            }
        }
    }

    public void pollselection(Context context, boolean iswillgo, boolean ismaybe, boolean iswontgo) {
        if (iswillgo) {
            imagelikeup.setClickable(false);
            imagemaybe.setClickable(true);
            imagewillnot.setClickable(true);

            // IMAGE CHANGE
            will_go_image.setImageResource(R.drawable.will_go);
            maybe_image.setImageResource(R.drawable.may_be_unselected);
            wont_go_image.setImageResource(R.drawable.wont_go_unselected);

            //TEXT COLOR CHANGE
            textViewwillgo.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybe.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgo.setTextColor(Color.parseColor("#ff4545"));

            // COUNT COLOR CHANGE
            textViewwillgocount.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybecount.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgocount.setTextColor(Color.parseColor("#ff4545"));

        } else if (ismaybe) {
            imagelikeup.setClickable(true);
            imagemaybe.setClickable(false);
            imagewillnot.setClickable(true);

            // IMAGE CHANGE
            will_go_image.setImageResource(R.drawable.will_go_unselected);
            maybe_image.setImageResource(R.drawable.maybe_icon);
            wont_go_image.setImageResource(R.drawable.wont_go_unselected);

            //TEXT COLOR CHANGE
            textViewwillgo.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybe.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgo.setTextColor(Color.parseColor("#ff4545"));

            // COUNT COLOR CHANGE
            textViewwillgocount.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybecount.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgocount.setTextColor(Color.parseColor("#ff4545"));

        } else if (iswontgo) {
            imagelikeup.setClickable(true);
            imagemaybe.setClickable(true);
            imagewillnot.setClickable(false);

            // IMAGE CHANGE
            will_go_image.setImageResource(R.drawable.will_go_unselected);
            maybe_image.setImageResource(R.drawable.may_be_unselected);
            wont_go_image.setImageResource(R.drawable.wont_go);

            //TEXT COLOR CHANGE
            textViewwillgo.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybe.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgo.setTextColor(Color.parseColor("#ff4545"));

            // COUNT COLOR CHANGE
            textViewwillgocount.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybecount.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgocount.setTextColor(Color.parseColor("#ff4545"));

        } else {
            imagelikeup.setClickable(true);
            imagemaybe.setClickable(true);
            imagewillnot.setClickable(true);

            // IMAGE CHANGE
            will_go_image.setImageResource(R.drawable.will_go_unselected);
            maybe_image.setImageResource(R.drawable.may_be_unselected);
            wont_go_image.setImageResource(R.drawable.wont_go_unselected);

            //TEXT COLOR CHANGE
            textViewwillgo.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybe.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgo.setTextColor(Color.parseColor("#ff4545"));

            // COUNT COLOR CHANGE
            textViewwillgocount.setTextColor(Color.parseColor("#01b36a"));
            textViewmaybecount.setTextColor(Color.parseColor("#fd8a2d"));
            textViewwontgocount.setTextColor(Color.parseColor("#ff4545"));
        }
    }

    @Override
    public void onBackPressed() {
        if (fullscreen) {
            youTubePlayer.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    void updateReview(ArrayList<ReviewBean> lisreviewbean, int listSize) {
        linReview.removeAllViews();

        LayoutInflater reviewInfalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < listSize; i++) {

            View reviewView = reviewInfalter.inflate(R.layout.review_layout_new, null);

            likecount = (TextView) reviewView.findViewById(R.id.countlike);
            name = (TextView) reviewView.findViewById(R.id.nameadp);
            date = (TextView) reviewView.findViewById(R.id.dateadp);
            description = (TextView) reviewView.findViewById(R.id.despadp);
            likeimg = (ImageView) reviewView.findViewById(R.id.likeimg);
            ratingBar = (RatingBar) reviewView.findViewById(R.id.ratingbaradp);
            review_profile_pic = (ImageView) reviewView.findViewById(R.id.review_profile_pic);

            description.setText(lisreviewbean.get(i).getDescription());
            likecount.setText(lisreviewbean.get(i).getLikecount());
            name.setText(lisreviewbean.get(i).getName());
            date.setText(lisreviewbean.get(i).getDate());

            if (lisreviewbean.get(i).getRatingsount().equalsIgnoreCase("")) {
                ratingBar.setRating(Float.parseFloat("0"));
            } else {
                ratingBar.setRating(Float.parseFloat(lisreviewbean.get(i).getRatingsount()));
            }

            if (lisreviewbean.get(i).getUserlike().equalsIgnoreCase("0")) {
                likeimg.setImageResource(R.drawable.likereview);
            } else {
                likeimg.setImageResource(R.drawable.likereview_colour);
            }

            String imageUrlProfilePic = lisreviewbean.get(i).getProfilePic().replace(" ", "%20");

            Picasso.with(Youtube_Activity.this).load(imageUrlProfilePic).placeholder(R.drawable.actionbar_bg).into(review_profile_pic);

            linReview.addView(reviewView, i);
        }
    }

    private void parseReviewXML(String MovieReviewXml) {

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieReviewXml);
        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            statusreview = parser.getValue(everyTrailerElement, "status");
            messagereview = parser.getValue(everyTrailerElement, "msz");

            Log.e("statusreview", "featuredtrailerId " + statusreview);
            Log.e("messagereview", "featuredtrailerId " + messagereview);


            // REVIEW DETAILS
            NodeList reviewDetails = everyTrailerElement.getElementsByTagName("reviewDetails");
            for (int j = 0; j < reviewDetails.getLength(); j++) {

                Element reviewDetailsnode = (Element) reviewDetails.item(j);
                NodeList review = reviewDetailsnode.getElementsByTagName("review");

                if (review.getLength() == 0) {
                    Log.e(Constants.TAG, "REVIEW NODE LENGTH: " + review.getLength());
                    lisreviewbean = new ArrayList<ReviewBean>();
                    isReviewsVisible = false;

                } else {
                    lisreviewbean.clear();
                    for (int k = 0; k < review.getLength(); k++) {
                        Element reviewsublist = (Element) review.item(k);

                        String title = parser.getValue(reviewsublist, "title");

                        String description = parser.getValue(reviewsublist, "description");
                        String userLike = parser.getValue(reviewsublist, "userLike");
                        String totalLikeCount = parser.getValue(reviewsublist, "totalLikeCount");
                        String date = parser.getValue(reviewsublist, "date");
                        String userName = parser.getValue(reviewsublist, "userName");
                        String ratingtxt = parser.getValue(reviewsublist, "rating");
                        String ratingId = parser.getValue(reviewsublist, "reviewID");
                        String profilePic = parser.getValue(reviewsublist, "profilePic");

                        lisreviewbean.add(new ReviewBean(title, description, totalLikeCount, userName, date, ratingtxt, ratingId, userLike, profilePic));
                    }

                    Collections.sort(lisreviewbean, new Comparator<ReviewBean>() {
                        @Override
                        public int compare(ReviewBean lhs, ReviewBean rhs) {
                            return rhs.getDate().compareTo(lhs.getDate());
                        }
                    });
                }
            }
        }
    }

    class GetMovieDetail extends AsyncTask<Void, Void, Void> {

        JSONStringer stringer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(Youtube_Activity.this);
            mProgressDialog.setMessage("Loading Movie Lists");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_DETAIL);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String stri = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler><merchandDetail><merchandID>      mobileapp    </merchandID><password>" + privateKey + "</password></merchandDetail><inputData><userID>" + userId + "</userID><videoType>" + trailerType + "</videoType><trailerID>" + movieId + "</trailerID></inputData></everyTrailler>";
                stringer = new JSONStringer().object().key("xmlString").value(stri);

                osw.write(stringer.toString());
                osw.flush();
                osw.close();

                String result1 = Constants.readStream(con.getInputStream());

                result1 = "" + Html.fromHtml(result1);

                String result = result1.replace("&", "and");

                parseMovieListXML(result);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mainStatus.equalsIgnoreCase("0")) {
                onBackPressed();
            } else {

                Synopsistxt.setText(VideoSynopsis);
                videocounttv.setText(Countview);
                videoliketxt.setText(Videovideocount);

                //POLL
//                textViewwillgocount.setText(poll3.get(0));
//                textViewmaybecount.setText(poll3.get(1));
//                textViewwontgocount.setText(poll3.get(2));

                float willGoCount = Float.valueOf(pollArrayList.get(0).getPollCount());
                float maybeCount = Float.valueOf(pollArrayList.get(1).getPollCount());
                float wontgoCount = Float.valueOf(pollArrayList.get(2).getPollCount());

                totalPollPercent = willGoCount + wontgoCount + maybeCount;

                final int willgoPercent = (int) ((willGoCount / totalPollPercent) * 100);
                final int maybePercent = (int) ((maybeCount / totalPollPercent) * 100);
                final int wontgoPercent = (int) ((wontgoCount / totalPollPercent) * 100);

                for (int i = 0; i < pollArrayList.size(); i++) {
                    if (pollArrayList.get(i).getPollID().equalsIgnoreCase("1")) {

                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_willgo, 0, willgoPercent);
                        anim.setDuration(1000);
                        progressBar_willgo.startAnimation(anim);

                        textViewwillgocount.setText("" + willgoPercent + "%");
                    } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("2")) {

                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_maybe, 0, maybePercent);
                        anim.setDuration(1000);
                        progressBar_maybe.startAnimation(anim);

                        textViewmaybecount.setText("" + maybePercent + "%");
                    } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("3")) {

                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_wontgo, 0, wontgoPercent);
                        anim.setDuration(1000);
                        progressBar_wontgo.startAnimation(anim);

                        textViewwontgocount.setText("" + wontgoPercent + "%");
                    }
                }

                Log.e(Constants.TAG, "REVIEW VISIBLE: " + isReviewsVisible);
                Log.e(Constants.TAG, "DIALOG PROMO VISIBLE: " + isDialogPromoVisible);
                Log.e(Constants.TAG, "RELATED PROMO VISIBLE: " + isRelatedPromoVisible);
                Log.e(Constants.TAG, "VIDEO ANALYTICS HIGH VISIBLE: " + isVideoAnalyticsHighVisible);
                Log.e(Constants.TAG, "VIDEO ANALYTICS LOW VISIBLE: " + isVideoAnalyticsLowVisible);

                if (!isReviewsVisible) {
                    morereview.setVisibility(View.GONE);
                } else {
                    morereview.setVisibility(View.VISIBLE);
                }

                if (!isDialogPromoVisible) {
                    dialog_promo_container.setVisibility(View.GONE);
                } else {
                    dialog_promo_container.setVisibility(View.VISIBLE);
                }

                if (!isRelatedPromoVisible) {
                    related_promo_container.setVisibility(View.GONE);
                } else {
                    related_promo_container.setVisibility(View.VISIBLE);
                }

                if (!isVideoAnalyticsHighVisible) {
                    video_analytics_high_container.setVisibility(View.GONE);
                } else {
                    video_analytics_high_container.setVisibility(View.VISIBLE);
                }

                if (!isVideoAnalyticsLowVisible) {
                    video_analytics_low_container.setVisibility(View.GONE);
                } else {
                    video_analytics_low_container.setVisibility(View.VISIBLE);
                }

                if (!isVideoAnalyticsHighVisible && !isVideoAnalyticsLowVisible) {
                    video_analytics_container.setVisibility(View.GONE);
                } else {
                    video_analytics_container.setVisibility(View.VISIBLE);
                }

                // VIDEO LIKE
                if (userLikedornot.equalsIgnoreCase("0")) {
//                    imgliketotal.setImageResource(R.drawable.likereview);
                    videoliketxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp_unselected, 0, 0, 0);
                    isliketotal = false;
                } else {
//                    imgliketotal.setImageResource(R.drawable.likereview_colour);
                    videoliketxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp_selected, 0, 0, 0);
                    isliketotal = true;
                }

                // REVIEW
                if (lisreviewbean.size() == 0) {
                    morereview.setVisibility(View.GONE);
                } else if (lisreviewbean.size() == 1) {
                    updateReview(lisreviewbean, 1);
                    morereview.setVisibility(View.GONE);
                } else if (lisreviewbean.size() == 2) {
                    updateReview(lisreviewbean, 2);
                    morereview.setVisibility(View.GONE);
                } else {
                    updateReview(lisreviewbean, 2);
                    morereview.setVisibility(View.VISIBLE);
                }

                // POLL
                if (poll4.get(0).equalsIgnoreCase("1")) {
                    pollselection(Youtube_Activity.this, true, false, false);
                } else if (poll4.get(1).equalsIgnoreCase("1")) {
                    pollselection(Youtube_Activity.this, false, true, false);
                } else if (poll4.get(2).equalsIgnoreCase("1")) {
                    pollselection(Youtube_Activity.this, false, false, true);
                } else {
                    pollselection(Youtube_Activity.this, false, false, false);
                }

                // YOUTUBE INIT
                youTubeView.initialize(Constants.youtubedeveloperkey, Youtube_Activity.this);

                //DIALOG PROMO
                dialogAdapter = new DialogPromoAdapter(Youtube_Activity.this, dialogPromoList);
                mDialogPromo.setAdapter(dialogAdapter);

                // RELATED PROMO
                relatedAdapter = new RelatedPromoAdapter(Youtube_Activity.this, relatedPromoList);
                mRelatedPromo.setAdapter(relatedAdapter);

                // VIDEO ANALYTICS HIGH
                try {
                    String videoAnalyticsImgHighImageUrl = videoAnalyticsList.get(0).getHigherVideoImageURL().replace(" ", "%20");
                    Picasso.with(Youtube_Activity.this).load(videoAnalyticsImgHighImageUrl).resize(1920, 1080).centerCrop().into(videoAnalyticsImgHigh);
                } catch (Exception e) {

                }

                videoAnalyticsLikesHigh.setText(videoAnalyticsList.get(0).getHigherTotalLikeCount());
                videoAnalyticsViewsHigh.setText(videoAnalyticsList.get(0).getHigherVideoViewCount());
                videoAnalyticsTitleHigh.setText(videoAnalyticsList.get(0).getHigherTitle());

                // LOW
                try {
                    String videoAnalyticsImgLowImageUrl = videoAnalyticsList.get(0).getLowerVideoImageURL().replace(" ", "%20");
                    Picasso.with(Youtube_Activity.this).load(videoAnalyticsImgLowImageUrl).resize(1920, 1080).centerCrop().into(videoAnalyticsImgLow);
                } catch (Exception e) {

                }
                videoAnalyticsLikesLow.setText(videoAnalyticsList.get(0).getLowerTotalLikeCount());
                videoAnalyticsViewsLow.setText(videoAnalyticsList.get(0).getLowerVideoViewCount());
                videoAnalyticsTitleLow.setText(videoAnalyticsList.get(0).getLowerTitle());

                container.smoothScrollTo(0, 0);

                doLayout();
                mProgressDialog.dismiss();
            }
        }
    }

    public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

    class AddComments extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(Youtube_Activity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Loading, please wait...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_REVIEWPOST);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String paramsStr = "{\"xmlString\":\"<?xml version='1.0' encoding='utf-8' ?> <everyTrailler><merchandDetail><merchandID>mobileapp</merchandID><password>" + privateKey + "</password></merchandDetail><inputData><userID>" + userId + "</userID><trailerID>" + movieId + "</trailerID><title>" + "" + "</title><review>" + commentsString + "</review></inputData></everyTrailler>\"}";

                Log.e("everytrailer", "everytrailer " + paramsStr);

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String result = Constants.readStream(con.getInputStream());

                resultratnig = "" + Html.fromHtml(result);
                Log.e("everytrailer", "everytrailerafterwebreview " + resultratnig);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            parseReviewXML(resultratnig);

            pd.dismiss();
            // REVIEW
            if (lisreviewbean.size() == 0) {
                morereview.setVisibility(View.GONE);
            } else if (lisreviewbean.size() == 1) {
                morereview.setVisibility(View.GONE);
                updateReview(lisreviewbean, 1);
            } else if (lisreviewbean.size() == 2) {
                morereview.setVisibility(View.GONE);
                updateReview(lisreviewbean, 2);
            } else {
                morereview.setVisibility(View.VISIBLE);
                updateReview(lisreviewbean, 2);
            }

            comments_add_comments.setText("");
            View view = Youtube_Activity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public class pollwillgo extends AsyncTask<Void, Void, Void> {

        JSONStringer stringer;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(Youtube_Activity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Loading, please wait...");
//            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_POLLCOUNT);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String stri = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler>  <merchandDetail>    <merchandID>      mobileapp    </merchandID>    <password>   " + privateKey + "  </password>  </merchandDetail>  <inputData>    <userID>" + userId + "</userID>    <movieID>" + movieIdDetail + "</movieID>    <pollID>1</pollID>  </inputData></everyTrailler>";
                stringer = new JSONStringer().object().key("xmlString").value(stri);
                Log.e("everytrailer", "everytrailer " + stringer.toString());
                osw.write(stringer.toString());
                osw.flush();
                osw.close();

                String result = Constants.readStream(con.getInputStream());

                pollwillgo = "" + Html.fromHtml(result);

                Log.e("everytrailer", "everytrailerafterwebreview " + pollwillgo);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pollArrayList.clear();

            String status = null;
            String message = null;

            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(pollwillgo);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, "status");
                message = parser.getValue(everyTrailerElement, "msz");

                if (status.equalsIgnoreCase("1")) {

                    NodeList videoPollNodeList = everyTrailerElement.getElementsByTagName("videoPoll");
                    for (int j = 0; j < videoPollNodeList.getLength(); j++) {
                        Element pollElement = (Element) videoPollNodeList.item(j);
                        NodeList relatedPromo = pollElement.getElementsByTagName("poll");

                        if (relatedPromo.getLength() == 0) {
                            Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + relatedPromo.getLength());
                            relatedPromoList = new ArrayList<RelatedPromoBean>();
                        } else {
                            for (int k = 0; k < relatedPromo.getLength(); k++) {
                                Element relatedPromoSubList = (Element) relatedPromo.item(k);

                                String pollName = parser.getValue(relatedPromoSubList, "pollName");
                                String pollID = parser.getValue(relatedPromoSubList, "pollID");
                                String pollCount = parser.getValue(relatedPromoSubList, "pollCount");
                                String userPoll = parser.getValue(relatedPromoSubList, "userPoll");

                                pollArrayList.add(new PollBean(pollName, pollID, pollCount, userPoll));
                            }
                        }
                    }
                } else {
                    Toast.makeText(Youtube_Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                }
            }

            float willGoCount = Float.valueOf(pollArrayList.get(0).getPollCount());
            float maybeCount = Float.valueOf(pollArrayList.get(1).getPollCount());
            float wontgoCount = Float.valueOf(pollArrayList.get(2).getPollCount());

            totalPollPercent = willGoCount + wontgoCount + maybeCount;

            final int willgoPercent = (int) ((willGoCount / totalPollPercent) * 100);
            final int maybePercent = (int) ((maybeCount / totalPollPercent) * 100);
            final int wontgoPercent = (int) ((wontgoCount / totalPollPercent) * 100);

            for (int i = 0; i < pollArrayList.size(); i++) {
                if (pollArrayList.get(i).getPollID().equalsIgnoreCase("1")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_willgo, 0, willgoPercent);
                    anim.setDuration(1000);
                    progressBar_willgo.startAnimation(anim);

                    textViewwillgocount.setText("" + willgoPercent + "%");
                } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("2")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_maybe, 0, maybePercent);
                    anim.setDuration(1000);
                    progressBar_maybe.startAnimation(anim);

                    textViewmaybecount.setText("" + maybePercent + "%");
//                    progressBar_maybe.setProgress(maybePercent);
                } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("3")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_wontgo, 0, wontgoPercent);
                    anim.setDuration(1000);
                    progressBar_wontgo.startAnimation(anim);

                    textViewwontgocount.setText("" + wontgoPercent + "%");
//                    progressBar_wontgo.setProgress(wontgoPercent);
                }
            }
            pd.dismiss();
        }
    }

    public class pollmaybe extends AsyncTask<Void, Void, Void> {

        JSONStringer stringer;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(Youtube_Activity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Loading, please wait...");
//            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_POLLCOUNT);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String stri = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler>  <merchandDetail>    <merchandID>      mobileapp    </merchandID>    <password>   " + privateKey + "  </password>  </merchandDetail>  <inputData>    <userID>" + userId + "</userID>    <movieID>" + movieIdDetail + "</movieID>    <pollID>2</pollID>  </inputData></everyTrailler>";
//                String paramsStr = "{\"pass\":\"" + Constants.getPrivateKey() + "\"}";
                stringer = new JSONStringer().object().key("xmlString").value(stri);
                Log.e("everytrailer", "everytrailer " + stringer.toString());
                osw.write(stringer.toString());
                osw.flush();
                osw.close();

                String result = Constants.readStream(con.getInputStream());


                pollwillgo = "" + Html.fromHtml(result);
//                Log.e(Constants.TAG, "MOVIE LIST: " + Html.fromHtml(result));

//                result = result.replaceAll("&lt;", "<");
//
                Log.e("everytrailer", "everytrailerafterwebreview " + pollwillgo);
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

            pollArrayList.clear();

            String status = null;
            String message = null;
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(pollwillgo);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, "status");
                message = parser.getValue(everyTrailerElement, "msz");
                if (status.equalsIgnoreCase("1")) {

                    NodeList videoPollNodeList = everyTrailerElement.getElementsByTagName("videoPoll");
                    for (int j = 0; j < videoPollNodeList.getLength(); j++) {
                        Element pollElement = (Element) videoPollNodeList.item(j);
                        NodeList relatedPromo = pollElement.getElementsByTagName("poll");

                        if (relatedPromo.getLength() == 0) {
                            Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + relatedPromo.getLength());
                            relatedPromoList = new ArrayList<RelatedPromoBean>();
                        } else {
                            for (int k = 0; k < relatedPromo.getLength(); k++) {
                                Element relatedPromoSubList = (Element) relatedPromo.item(k);

                                String pollName = parser.getValue(relatedPromoSubList, "pollName");
                                String pollID = parser.getValue(relatedPromoSubList, "pollID");
                                String pollCount = parser.getValue(relatedPromoSubList, "pollCount");
                                String userPoll = parser.getValue(relatedPromoSubList, "userPoll");

                                pollArrayList.add(new PollBean(pollName, pollID, pollCount, userPoll));
                            }
                        }
                    }
                } else {
                    Toast.makeText(Youtube_Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                }
            }

            float willGoCount = Float.valueOf(pollArrayList.get(0).getPollCount());
            float maybeCount = Float.valueOf(pollArrayList.get(1).getPollCount());
            float wontgoCount = Float.valueOf(pollArrayList.get(2).getPollCount());

            totalPollPercent = willGoCount + wontgoCount + maybeCount;

            final int willgoPercent = (int) ((willGoCount / totalPollPercent) * 100);
            final int maybePercent = (int) ((maybeCount / totalPollPercent) * 100);
            final int wontgoPercent = (int) ((wontgoCount / totalPollPercent) * 100);

            for (int i = 0; i < pollArrayList.size(); i++) {
                if (pollArrayList.get(i).getPollID().equalsIgnoreCase("1")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_willgo, 0, willgoPercent);
                    anim.setDuration(1000);
                    progressBar_willgo.startAnimation(anim);

                    textViewwillgocount.setText("" + willgoPercent + "%");
//                    progressBar_willgo.setProgress(willgoPercent);
                } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("2")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_maybe, 0, maybePercent);
                    anim.setDuration(1000);
                    progressBar_maybe.startAnimation(anim);

                    textViewmaybecount.setText("" + maybePercent + "%");
                    progressBar_maybe.setProgress(maybePercent);
                } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("3")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_wontgo, 0, wontgoPercent);
                    anim.setDuration(1000);
                    progressBar_wontgo.startAnimation(anim);

                    textViewwontgocount.setText("" + wontgoPercent + "%");
//                    progressBar_wontgo.setProgress(wontgoPercent);
                }
            }

            pd.dismiss();
        }
    }

    public class pollwontgo extends AsyncTask<Void, Void, Void> {

        JSONStringer stringer;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(Youtube_Activity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Loading, please wait...");
//            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_POLLCOUNT);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String stri = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler>  <merchandDetail>    <merchandID>      mobileapp    </merchandID>    <password>   " + privateKey + "  </password>  </merchandDetail>  <inputData>    <userID>" + userId + "</userID>    <movieID>" + movieIdDetail + "</movieID>    <pollID>3</pollID>  </inputData></everyTrailler>";
                stringer = new JSONStringer().object().key("xmlString").value(stri);
                Log.e("everytrailer", "everytrailer " + stringer.toString());
                osw.write(stringer.toString());
                osw.flush();
                osw.close();

                String result = Constants.readStream(con.getInputStream());


                pollwillgo = "" + Html.fromHtml(result);
                Log.e("everytrailer", "everytrailerafterwebreview " + pollwillgo);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pollArrayList.clear();

            String status = null;
            String message = null;
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(pollwillgo);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, "status");
                message = parser.getValue(everyTrailerElement, "msz");

                if (status.equalsIgnoreCase("1")) {

                    NodeList videoPollNodeList = everyTrailerElement.getElementsByTagName("videoPoll");
                    for (int j = 0; j < videoPollNodeList.getLength(); j++) {
                        Element pollElement = (Element) videoPollNodeList.item(j);
                        NodeList relatedPromo = pollElement.getElementsByTagName("poll");

                        if (relatedPromo.getLength() == 0) {
                            Log.e(Constants.TAG, "DIALOG PROMO NODE LENGTH: " + relatedPromo.getLength());
                            relatedPromoList = new ArrayList<RelatedPromoBean>();
                        } else {
                            for (int k = 0; k < relatedPromo.getLength(); k++) {
                                Element relatedPromoSubList = (Element) relatedPromo.item(k);

                                String pollName = parser.getValue(relatedPromoSubList, "pollName");
                                String pollID = parser.getValue(relatedPromoSubList, "pollID");
                                String pollCount = parser.getValue(relatedPromoSubList, "pollCount");
                                String userPoll = parser.getValue(relatedPromoSubList, "userPoll");

                                pollArrayList.add(new PollBean(pollName, pollID, pollCount, userPoll));

                            }
                        }
                    }
                } else {
                    Toast.makeText(Youtube_Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                }
            }

            float willGoCount = Float.valueOf(pollArrayList.get(0).getPollCount());
            float maybeCount = Float.valueOf(pollArrayList.get(1).getPollCount());
            float wontgoCount = Float.valueOf(pollArrayList.get(2).getPollCount());

            totalPollPercent = willGoCount + wontgoCount + maybeCount;

            final int willgoPercent = (int) ((willGoCount / totalPollPercent) * 100);
            final int maybePercent = (int) ((maybeCount / totalPollPercent) * 100);
            final int wontgoPercent = (int) ((wontgoCount / totalPollPercent) * 100);

            for (int i = 0; i < pollArrayList.size(); i++) {
                if (pollArrayList.get(i).getPollID().equalsIgnoreCase("1")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_willgo, 0, willgoPercent);
                    anim.setDuration(1000);
                    progressBar_willgo.startAnimation(anim);

                    textViewwillgocount.setText("" + willgoPercent + "%");
//                    progressBar_willgo.setProgress(willgoPercent);
                } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("2")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_maybe, 0, maybePercent);
                    anim.setDuration(1000);
                    progressBar_maybe.startAnimation(anim);

                    textViewmaybecount.setText("" + maybePercent + "%");
//                    progressBar_maybe.setProgress(maybePercent);
                } else if (pollArrayList.get(i).getPollID().equalsIgnoreCase("3")) {

                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar_wontgo, 0, wontgoPercent);
                    anim.setDuration(1000);
                    progressBar_wontgo.startAnimation(anim);

                    textViewwontgocount.setText("" + wontgoPercent + "%");
//                        progressBar_wontgo.setProgress(wontgoPercent);
                }
            }

            pd.dismiss();
        }
    }

    public class Likevideo extends AsyncTask<Void, Void, Void> {

        JSONStringer stringer;

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(Youtube_Activity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Loading, please wait...");
//            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_VIDEOLIKE);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                String stri = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler>  <merchandDetail>    <merchandID>      mobileapp    </merchandID>    <password>" + privateKey + "</password>  </merchandDetail>  <inputData>    <userID>" + userId + "</userID>    <trailerID>" + trailerIdDetail + "</trailerID>   </inputData></everyTrailler>";
//                String paramsStr = "{\"pass\":\"" + Constants.getPrivateKey() + "\"}";
                stringer = new JSONStringer().object().key("xmlString").value(stri);
                Log.e("everytrailer", "everytrailer " + stringer.toString());
                osw.write(stringer.toString());
                osw.flush();
                osw.close();

                String result = Constants.readStream(con.getInputStream());


                Likevid = "" + Html.fromHtml(result);
//                Log.e(Constants.TAG, "MOVIE LIST: " + Html.fromHtml(result));

//                result = result.replaceAll("&lt;", "<");
//
                Log.e("everytrailer", "everytrailerafterwebreview " + Likevid);
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
            String likeCount = null;
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(Likevid);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, "status");
                message = parser.getValue(everyTrailerElement, "msz");
                likeCount = parser.getValue(everyTrailerElement, "likeCount");
                videoliketxt.setText(likeCount);
            }
            pd.dismiss();
            if (status.equalsIgnoreCase("0")) {

            } else {
                if (isliketotal == false) {
                    isliketotal = true;
//                    imgliketotal.setImageResource(R.drawable.likereview_colour);
                    videoliketxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp_selected, 0, 0, 0);
                } else {
                    isliketotal = false;
//                    imgliketotal.setImageResource(R.drawable.likereview);
                    videoliketxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp_unselected, 0, 0, 0);
                }
            }
        }
    }
}


