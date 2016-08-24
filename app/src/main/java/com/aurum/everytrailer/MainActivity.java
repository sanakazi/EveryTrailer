package com.aurum.everytrailer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aurum.everytrailer.bean.AllMoviesBean;
import com.aurum.everytrailer.bean.ComingSoonBean;
import com.aurum.everytrailer.bean.FeaturedBean;
import com.aurum.everytrailer.bean.NowShowingBean;
import com.aurum.everytrailer.bean.RecentBean;
import com.aurum.everytrailer.bean.TopBean;
import com.aurum.everytrailer.fragments.Viewpager;
import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.aurum.everytrailer.utils.XMLParser;
import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<FeaturedBean> featuredList;
    public static ArrayList<RecentBean> recentList;
    public static ArrayList<TopBean> topViewsList;
    public static ArrayList<ComingSoonBean> comingSoonList;
    public static ArrayList<NowShowingBean> nowShowingList;
    public static ArrayList<AllMoviesBean> allMoviesList;

    FrameLayout framecontainer;
    CircleImageView profileimage;
    int RESULT_LOAD_IMG = 1500, RESULT_CAMERA = 17;
    String imgDecodableString;
    GetMovieList getMovieList;
    ProgressDialog mProgressDialog;

    TextView username_header;
    TextView username_email;

    ImageButton camera_btn_profile, gallery_btn_profile;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    boolean isLocalSignedIn = false, isSocialSignedIn = false;
    String updateProfilePicRequestXML, profilePicBase64;

    ConnectionDetector connectionDetector;

    String userId, profilePicResponse;
    Bitmap profilePicBitmap;
    String privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        privateKey = Constants.getPrivateKey();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setBackgroundResource(R.color.colorbg);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.inflateHeaderView(R.layout.nav_header_main);
        profileimage = (CircleImageView) v.findViewById(R.id.profileimage);
        framecontainer = (FrameLayout) findViewById(R.id.framecontainer);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        edit = prefs.edit();

        isLocalSignedIn = prefs.getBoolean("isLocalSignedIn", false);
        isSocialSignedIn = prefs.getBoolean("isSocialSignedIn", false);
        userId = prefs.getString("User_ID", null);

        String user_name = prefs.getString("User_name", "");
        String profile_image = prefs.getString("Profile_picture", "");
        String profile_email = prefs.getString("Profile_email", "");

        username_header = (TextView) v.findViewById(R.id.user_name_nav);
        username_email = (TextView) v.findViewById(R.id.user_email_nav);
        camera_btn_profile = (ImageButton) v.findViewById(R.id.camera_btn_profile);
        gallery_btn_profile = (ImageButton) v.findViewById(R.id.gallery_btn_profile);

        username_email.setText("Welcome");
        username_header.setText(user_name);

        Log.e(Constants.TAG, "PATH MUST NOT BE EMPTY: " + profile_image);

        Picasso.with(MainActivity.this).load(profile_image).placeholder(getResources().getDrawable(R.drawable.actionbar_bg)).into(profileimage);

        camera_btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                // Start the Intent
                startActivityForResult(cameraIntent, RESULT_CAMERA);
            }
        });

        gallery_btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


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
            getMovieList = new GetMovieList();
            getMovieList.execute();
        } else {
            Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (connectionDetector.isConnectingToInternet()) {
//            getMovieList = new GetMovieList();
//            getMovieList.execute();
//        } else {
//            Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                profilePicBitmap = BitmapFactory
                        .decodeFile(imgDecodableString);

                Bitmap bitmap = Bitmap.createScaledBitmap(profilePicBitmap, 50, 50, false);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                profilePicBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                updateProfilePicRequestXML = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler><merchandDetail><merchandID>mobileapp</merchandID><password>" + Constants.getPrivateKey() + "</password></merchandDetail><inputData><userID>" + userId + "</userID><profilePic>" + profilePicBase64.trim() + "</profilePic></inputData></everyTrailler>";

                new UpdateProfilePic().execute();

            } else if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Bitmap bp = (Bitmap) data.getExtras().get("data");

                Log.e(Constants.TAG, "BITMAP:" + bp);

                profilePicBitmap = (Bitmap) data.getExtras().get("data");

                Bitmap bitmap = Bitmap.createScaledBitmap(profilePicBitmap, 50, 50, false);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                profilePicBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                updateProfilePicRequestXML = "<?xml version='1.0' encoding='utf-8' ?><everyTrailler><merchandDetail><merchandID>mobileapp</merchandID><password>" + Constants.getPrivateKey() + "</password></merchandDetail><inputData><userID>" + userId + "</userID><profilePic>" + profilePicBase64.trim() + "</profilePic></inputData></everyTrailler>";

                new UpdateProfilePic().execute();

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

            Log.e(Constants.TAG, "CAMERA EXCEPTION: " + e);

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

        } else if (id == R.id.notification_drawer) {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.news_drawer) {
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.signout_drawer) {
            try {
                // GOOGLE
                if (LoginActivity.googleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(LoginActivity.googleApiClient);
                    //Finally disconnect the
                    LoginActivity.googleApiClient.disconnect();
                } else {
//                    Toast.makeText(MainActivity.this, "Google Didnt disconnect", Toast.LENGTH_SHORT).show();
                }

                // FACEBOOK
                LoginManager.getInstance().logOut();

                //TWITTER
                Twitter.getInstance();
                Twitter.logOut();
            } catch (Exception e) {
                Log.e(Constants.TAG, "SIGN OUT ERROR:" + e);
            }

            if (isLocalSignedIn == true) {
                edit.putBoolean("isLocalSignedIn", false);
            } else if (isSocialSignedIn == true) {
                edit.putBoolean("isSocialSignedIn", false);
            }
            edit.commit();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.settings_drawer) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void parseMovieListXML(String MovieListXml) {

        featuredList = new ArrayList<FeaturedBean>();
        recentList = new ArrayList<RecentBean>();
        comingSoonList = new ArrayList<ComingSoonBean>();
        topViewsList = new ArrayList<TopBean>();
        nowShowingList = new ArrayList<NowShowingBean>();
        allMoviesList = new ArrayList<AllMoviesBean>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            if (parser.getValue(everyTrailerElement, Constants.KEY_STATUS).equalsIgnoreCase("1")) {

                // FEATURED LIST
                NodeList featuredNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_FEATURED);
                for (int j = 0; j < featuredNodeList.getLength(); j++) {
                    Element featuredElement = (Element) featuredNodeList.item(j);

                    NodeList featuredMovieListNodeList = featuredElement.getElementsByTagName(Constants.KEY_MOVIE_LIST);
                    for (int k = 0; k < featuredMovieListNodeList.getLength(); k++) {
                        Element featuredMovieListElement = (Element) featuredMovieListNodeList.item(k);

                        String featuredtrailerId = parser.getValue(featuredMovieListElement, Constants.KEY_TRAILER_ID);
                        String featuredLandingImgPath = parser.getValue(featuredMovieListElement, Constants.KEY_LANDING_IMG_PATH);
                        String featuredViewCounts = parser.getValue(featuredMovieListElement, Constants.KEY_VIEWS_COUNT);
                        String featuredLikesPer = parser.getValue(featuredMovieListElement, Constants.KEY_LIKES_PER);
                        String featuredTrailerType = parser.getValue(featuredMovieListElement, Constants.KEY_TRAILER_TYPE);
                        String featuredTrailerTitle = parser.getValue(featuredMovieListElement, Constants.KEY_TRAILER_TITLE);
                        String featuredMovieName = parser.getValue(featuredMovieListElement, Constants.KEY_MOVIE_NAME);

                        featuredList.add(new FeaturedBean(featuredtrailerId, featuredViewCounts, featuredLikesPer, featuredLandingImgPath, featuredTrailerTitle, featuredMovieName, featuredTrailerType));
                    }
                }

                // TOP VIEWS LIST
                NodeList topViewsNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_TOP_VIEWS);
                for (int j = 0; j < topViewsNodeList.getLength(); j++) {
                    Element topViewsElement = (Element) topViewsNodeList.item(j);

                    NodeList topViewsMovieListNodeList = topViewsElement.getElementsByTagName(Constants.KEY_MOVIE_LIST);
                    for (int k = 0; k < topViewsMovieListNodeList.getLength(); k++) {
                        Element topViewsMovieListElement = (Element) topViewsMovieListNodeList.item(k);

                        String topViewstrailerId = parser.getValue(topViewsMovieListElement, Constants.KEY_TRAILER_ID);
                        String topViewsLandingImgPath = parser.getValue(topViewsMovieListElement, Constants.KEY_LANDING_IMG_PATH);
                        String topViewsViewCounts = parser.getValue(topViewsMovieListElement, Constants.KEY_VIEWS_COUNT);
                        String topViewsLikesPer = parser.getValue(topViewsMovieListElement, Constants.KEY_LIKES_PER);
                        String topViewsTrailerType = parser.getValue(topViewsMovieListElement, Constants.KEY_TRAILER_TYPE);
                        String topViewsTrailerTitle = parser.getValue(topViewsMovieListElement, Constants.KEY_TRAILER_TITLE);
                        String topViewsMovieName = parser.getValue(topViewsMovieListElement, Constants.KEY_MOVIE_NAME);

                        topViewsList.add(new TopBean(topViewstrailerId, topViewsViewCounts, topViewsLikesPer, topViewsLandingImgPath, topViewsTrailerTitle, topViewsMovieName, topViewsTrailerType));
                    }
                }

                // RECENT LIST
                NodeList recentNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_RECENT);
                for (int j = 0; j < recentNodeList.getLength(); j++) {
                    Element recentElement = (Element) recentNodeList.item(j);

                    NodeList recentMovieListNodeList = recentElement.getElementsByTagName(Constants.KEY_MOVIE_LIST);
                    for (int k = 0; k < recentMovieListNodeList.getLength(); k++) {
                        Element recentMovieListElement = (Element) recentMovieListNodeList.item(k);

                        String recenttrailerId = parser.getValue(recentMovieListElement, Constants.KEY_TRAILER_ID);
                        String recentLandingImgPath = parser.getValue(recentMovieListElement, Constants.KEY_LANDING_IMG_PATH);
                        String recentViewCounts = parser.getValue(recentMovieListElement, Constants.KEY_VIEWS_COUNT);
                        String recentLikesPer = parser.getValue(recentMovieListElement, Constants.KEY_LIKES_PER);
                        String recentTrailerType = parser.getValue(recentMovieListElement, Constants.KEY_TRAILER_TYPE);
                        String recentTrailerTitle = parser.getValue(recentMovieListElement, Constants.KEY_TRAILER_TITLE);
                        String recentMovieName = parser.getValue(recentMovieListElement, Constants.KEY_MOVIE_NAME);

                        recentList.add(new RecentBean(recenttrailerId, recentViewCounts, recentLikesPer, recentLandingImgPath, recentTrailerTitle, recentMovieName, recentTrailerType));
                    }
                }

                // NOW SHOWING LIST
                NodeList nowShowingNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_NOW_SHOWING);
                for (int j = 0; j < nowShowingNodeList.getLength(); j++) {
                    Element nowShowingElement = (Element) nowShowingNodeList.item(j);

                    NodeList nowShowingMovieListNodeList = nowShowingElement.getElementsByTagName(Constants.KEY_MOVIE_LIST);
                    for (int k = 0; k < nowShowingMovieListNodeList.getLength(); k++) {
                        Element nowShowingMovieListElement = (Element) nowShowingMovieListNodeList.item(k);

                        String nowShowingtrailerId = parser.getValue(nowShowingMovieListElement, Constants.KEY_TRAILER_ID);
                        String nowShowingLandingImgPath = parser.getValue(nowShowingMovieListElement, Constants.KEY_LANDING_IMG_PATH);
                        String nowShowingViewCounts = parser.getValue(nowShowingMovieListElement, Constants.KEY_VIEWS_COUNT);
                        String nowShowingLikesPer = parser.getValue(nowShowingMovieListElement, Constants.KEY_LIKES_PER);
                        String nowShowingTrailerType = parser.getValue(nowShowingMovieListElement, Constants.KEY_TRAILER_TYPE);
                        String nowShowingTrailerTitle = parser.getValue(nowShowingMovieListElement, Constants.KEY_TRAILER_TITLE);
                        String nowShowingMovieName = parser.getValue(nowShowingMovieListElement, Constants.KEY_MOVIE_NAME);

                        nowShowingList.add(new NowShowingBean(nowShowingtrailerId, nowShowingViewCounts, nowShowingLikesPer, nowShowingLandingImgPath, nowShowingTrailerTitle, nowShowingMovieName, nowShowingTrailerType));
                    }
                }

                // COMING SOON LIST
                NodeList commingSoonNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_COMMING_SOON);
                for (int j = 0; j < commingSoonNodeList.getLength(); j++) {
                    Element commingSoonElement = (Element) commingSoonNodeList.item(j);

                    NodeList commingSoonMovieListNodeList = commingSoonElement.getElementsByTagName(Constants.KEY_MOVIE_LIST);
                    for (int k = 0; k < commingSoonMovieListNodeList.getLength(); k++) {
                        Element commingSoonMovieListElement = (Element) commingSoonMovieListNodeList.item(k);

                        String commingSoontrailerId = parser.getValue(commingSoonMovieListElement, Constants.KEY_TRAILER_ID);
                        String commingSoonLandingImgPath = parser.getValue(commingSoonMovieListElement, Constants.KEY_LANDING_IMG_PATH);
                        String commingSoonViewCounts = parser.getValue(commingSoonMovieListElement, Constants.KEY_VIEWS_COUNT);
                        String commingSoonLikesPer = parser.getValue(commingSoonMovieListElement, Constants.KEY_LIKES_PER);
                        String commingSoonTrailerType = parser.getValue(commingSoonMovieListElement, Constants.KEY_TRAILER_TYPE);
                        String commingSoonTrailerTitle = parser.getValue(commingSoonMovieListElement, Constants.KEY_TRAILER_TITLE);
                        String commingSoonMovieName = parser.getValue(commingSoonMovieListElement, Constants.KEY_MOVIE_NAME);

                        comingSoonList.add(new ComingSoonBean(commingSoontrailerId, commingSoonViewCounts, commingSoonLikesPer, commingSoonLandingImgPath, commingSoonTrailerTitle, commingSoonMovieName, commingSoonTrailerType));
                    }
                }

                // ALL MOVIES LIST
                NodeList allMoviesNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_ALL_MOVIES);
                for (int j = 0; j < allMoviesNodeList.getLength(); j++) {
                    Element allMoviesElement = (Element) allMoviesNodeList.item(j);

                    NodeList allMoviesMovieListNodeList = allMoviesElement.getElementsByTagName(Constants.KEY_LANGUAGE_LIST);
                    for (int k = 0; k < allMoviesMovieListNodeList.getLength(); k++) {
                        Element allMoviesMovieListElement = (Element) allMoviesMovieListNodeList.item(k);

                        String allMoviesLanguageId = parser.getValue(allMoviesMovieListElement, Constants.KEY_LANGUAGE_ID);
                        String allMoviesLanguage = parser.getValue(allMoviesMovieListElement, Constants.KEY_LANGUAGE);
                        String allMoviesImage = parser.getValue(allMoviesMovieListElement, Constants.KEY_LANDING_IMG_PATH);

                        allMoviesList.add(new AllMoviesBean(allMoviesLanguageId, allMoviesLanguage, allMoviesImage));
                    }
                }
            } else {
            }
        }
    }


    class UpdateProfilePic extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Uploading image");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_UPDATE_PROFILE_PIC);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

//                String paramsStr = "{\"xmlString\":\"" + updateProfilePicRequestXML + "\"}";

                String paramsStr = "{\"xmlString\":\"<?xml version='1.0' encoding='utf-8' ?><everyTrailler><merchandDetail><merchandID>mobileapp</merchandID><password>" + privateKey + "</password></merchandDetail><inputData><userID>" + userId + "</userID><profilePic>" + profilePicBase64.trim() + "</profilePic></inputData></everyTrailler>\"}";

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                String resultHtml = Html.fromHtml(resultPlain).toString();

                profilePicResponse = resultHtml.replace("&", "and");

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String status = null, message = null;

            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(profilePicResponse);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, "status");
                message = parser.getValue(everyTrailerElement, "msz");
            }

            if (status.equalsIgnoreCase("1")) {

                Bitmap bp = Bitmap.createScaledBitmap(profilePicBitmap, 150, 150, false);

                profileimage.setImageBitmap(bp);
            } else {
                Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
        }
    }

    class GetMovieList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Loading Movie Lists");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_LIST);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String paramsStr = "{\"pass\":\"" + privateKey + "\"}";
                Log.d("myprivate key = " ,  privateKey);

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                String resultHtml = Html.fromHtml(resultPlain).toString();

                String result = resultHtml.replace("&", "and");

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
            mProgressDialog.dismiss();

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Viewpager fragment = new Viewpager();
            fragmentTransaction.replace(R.id.framecontainer, fragment);
            fragmentTransaction.commit();

        }
    }

}
