package com.aurum.everytrailer.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by NiravShrimali on 11/2/2015.
 */
public class Constants {
    public static final String MY_PREFS_NAME = "MyPrefs";
    public static String TAG = "EveryTrailer";
    public static String youtubedeveloperkey = "AIzaSyAd9s8qVu2pqaaKdKC-fCrggTfmO1GaBQo";
    public static int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 700;
    public static boolean MY_PERMISSIONS_REQUEST_GET_ACCOUNTS_GRANTED = false;

//    public static String URL_MOVIE_LIST = "http://192.168.168.5:8096/landingScreen.svc/moviesList";
//    public static String URL_LOGIN = "http://192.168.168.5:8096/validateUser.svc/authenticateUser";
//    public static String URL_SIGN_UP = "http://192.168.168.5:8096/validateUser.svc/registrationProcess";
//    public static String URL_FORGOT_PASSWORD = "http://192.168.168.5:8096/validateUser.svc/forgotPassword";
//    public static String URL_CHANGE_USER_PASSWORD = "http://192.168.168.5:8096/validateUser.svc/ChangeUserPassword";
//    public static String URL_MOVIE_DETAIL = "http://192.168.168.5:8096/videoData.svc/getVideoDetailsByID";
//    public static String URL_MOVIE_RATING = "http://192.168.168.5:8096/videoData.svc/userRating";
//    public static String URL_MOVIE_REVIEWPOST = "http://192.168.168.5:8096/videoData.svc/userReview";
//    public static String URL_LANGUAGE_WISE_MOVIES = "http://192.168.168.5:8096/landingScreen.svc/languageWiseMovies";
//    public static String URL_NEWS_LIST = "http://192.168.168.5:8096/landingScreen.svc/newsList";
//    public static String URL_MOVIE_SEARCH = "http://192.168.168.5:8096/landingScreen.svc/moviewSearch";
//    public static String URL_MOVIE_LIKEBTN = "http://192.168.168.5:8096/videoData.svc/reviewLikeDisLike";
//    public static String URL_MOVIE_POLLCOUNT = "http://192.168.168.5:8096/videoData.svc/userPoll";
//    public static String URL_MOVIE_VIDEOLIKE = "http://192.168.168.5:8096/videoData.svc/videoLikeDisLike";
//    public static String URL_TERMS_AND_CONDITIONS = "http://192.168.168.5:8096/getData.svc/getTermAndCondition";
//    public static String URL_NOTIFICATIONS = "http://192.168.168.5:8096/landingScreen.svc/Notification";
//    public static String URL_UPDATE_PROFILE_PIC = "http://192.168.168.5:8096/getData.svc/updateProfilePic";

    //LIVE
    public static String URL_MOVIE_LIST = "http://boxofficecapsule.com:8087/landingScreen.svc/moviesList";
    public static String URL_LOGIN = "http://boxofficecapsule.com:8087/validateUser.svc/authenticateUser";
    public static String URL_SIGN_UP = "http://boxofficecapsule.com:8087/validateUser.svc/registrationProcess";
    public static String URL_FORGOT_PASSWORD = "http://boxofficecapsule.com:8087/validateUser.svc/forgotPassword";
    public static String URL_CHANGE_USER_PASSWORD = "http://boxofficecapsule.com:8087/validateUser.svc/ChangeUserPassword";
    public static String URL_MOVIE_DETAIL = "http://boxofficecapsule.com:8087/videoData.svc/getVideoDetailsByID";
    public static String URL_MOVIE_RATING = "http://boxofficecapsule.com:8087/videoData.svc/userRating";
    public static String URL_MOVIE_REVIEWPOST = "http://boxofficecapsule.com:8087/videoData.svc/userReview";
    public static String URL_LANGUAGE_WISE_MOVIES = "http://boxofficecapsule.com:8087/landingScreen.svc/languageWiseMovies";
    public static String URL_NEWS_LIST = "http://boxofficecapsule.com:8087/landingScreen.svc/newsList";
    public static String URL_MOVIE_SEARCH = "http://boxofficecapsule.com:8087/landingScreen.svc/moviewSearch";
    public static String URL_MOVIE_LIKEBTN = "http://boxofficecapsule.com:8087/videoData.svc/reviewLikeDisLike";
    public static String URL_MOVIE_POLLCOUNT = "http://boxofficecapsule.com:8087/videoData.svc/userPoll";
    public static String URL_MOVIE_VIDEOLIKE = "http://boxofficecapsule.com:8087/videoData.svc/videoLikeDisLike";
    public static String URL_TERMS_AND_CONDITIONS = "http://boxofficecapsule.com:8087/getData.svc/getTermAndCondition";
    public static String URL_NOTIFICATIONS = "http://boxofficecapsule.com:8087/landingScreen.svc/Notification";
    public static String URL_UPDATE_PROFILE_PIC = "http://boxofficecapsule.com:8087/getData.svc/updateProfilePic";


    // XML PARSE KEY'S
    public static String KEY_LANDING_SCREEN = "landingScreen";
    public static String KEY_STATUS = "status";
    public static String KEY_FEATURED = "featured";
    public static String KEY_MOVIE_LIST = "movieList";
    public static String KEY_TRAILER_ID = "trailerID";
    public static String KEY_LANDING_IMG_PATH = "landingImgPath";
    public static String KEY_VIEWS_COUNT = "viewsCount";
    public static String KEY_LIKES_PER = "likesPer";
    public static String KEY_TRAILER_TYPE = "trailerType";
    public static String KEY_TOP_VIEWS = "topViews";
    public static String KEY_RECENT = "recent";
    public static String KEY_NOW_SHOWING = "nowShowing";
    public static String KEY_COMMING_SOON = "commingSoon";
    public static String KEY_EVERY_TRAILER = "everyTrailler";
    public static String KEY_MOVIE_NAME = "movieName";
    public static String KEY_TRAILER_TITLE = "trailerTitle";
    public static String KEY_MESSAGE = "msz";
    public static String KEY_PROFILE_PIC = "profilePic";
    public static String KEY_USERNAME = "userName";
    public static String KEY_USER_ID = "userID";
    public static String KEY_ALL_MOVIES = "Allmovies";
    public static String KEY_LANGUAGE_LIST = "languageList";
    public static String KEY_LANGUAGE_ID = "languageID";
    public static String KEY_LANGUAGE = "language";
    public static String KEY_LANGUAGE_MOVIES = "langMovies";
    public static String KEY_NEWS = "news";
    public static String KEY_NEWS_LIST = "newsList";
    public static String KEY_READ_MORE = "readMore";
    public static String KEY_NEWS_DATE = "newsDate";
    public static String KEY_NEWS_DESC = "desc";
    public static String KEY_NEWS_TITLE = "title";
    public static String TERM_AND_CONDITION = "termAndCondition";
    public static String PRIVACY_POLICY = "privacyPolicy";

    private static Calendar cal;
    private static String format, day, year, month;

    // Private Key
    public static String getPrivateKey() {

        // CONNECTION DETECTOR
        AsyncTask<Void, String[], String[]> connectionDetectorTask = new GetPassKey()
                .execute();

        String[] result = {};
        try {
            result = connectionDetectorTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result[0].equalsIgnoreCase("1")) {
            return result[1];
        } else {
            return null;
        }
    }

    public static String readStream(InputStream in) {
        StringBuilder sb = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));

            sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                // Log.e("LINE: ", "LINE: " + line);
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();

            return null;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
