package com.aurum.everytrailer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.aurum.everytrailer.utils.XMLParser;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity {

    private static final String startUserType = "<userType>";
    private static final String startEmailID = "<emailID>";
    private static final String startDeviceID = "<deviceID>";
    private static final String startProfilePic = "<profilePic>";

    private static final String endUserType = "</userType>";
    private static final String endEmailID = "</emailID>";
    private static final String endDeviceID = "</deviceID>";
    private static final String endProfilePic = "</profilePic>";

    private static final int RC_SIGN_IN = 0;
    private static final String TWITTER_KEY = "1rympJnO8rLdQJv1lFiRkUlCt";
    private static final String TWITTER_SECRET = "KJnfZJyHn4Na2dAoz15HZNW8xAjucYmwyi4qjRo8PKNhGxzBbJ";
    public static GoogleApiClient googleApiClient;
    private final String startEveryTrailler = "<everyTrailler>";
    private final String startInputData = "<inputData>";
    private final String startUsername = "<username>";
    private final String startPassword = "<password>";
    private final String endEveryTrailler = "</everyTrailler>";
    private final String endInputData = "</inputData>";
    private final String endUsername = "</username>";
    private final String endPassword = "</password>";
    private final String startTag = "<?xml version='1.0' encoding='utf-8'?>";
    public URL imageURL;
    public Bitmap bitmap;
    String fbEmail, fbUserName, signUpXml, socialLoginResponseString;
    EditText mUserName, mPassword;
    Button mLogin;
    TextView mForgotPassword, mSignUp;
    ImageButton signInButton_custom_googlePlay;
    TextView userInfo;
    ImageView userImage;
    ForLogin loginUser;
    String privateKey, loginUserXml, status, msg;
    ProgressDialog mProgressDialog;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String profilePic, userId, userName;
    Profile profile;
    ImageButton fb;
    LoginButton facebook_login_button;
    ImageButton twitter_login_btn;
    ConnectionDetector connectionDetector;
    String profile_url;
    String imageUrl;
    private boolean mIntentInProgress;
    private CallbackManager callbackManager;
    private TwitterLoginButton twitterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GET PRIVATE KEY
        privateKey = Constants.getPrivateKey();

        //FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //TWITTER
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_login);


        mUserName = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        mSignUp = (TextView) findViewById(R.id.sign_up);
        fb = (ImageButton) findViewById(R.id.fb);
        twitter_login_btn = (ImageButton) findViewById(R.id.twitter_login_button);
        twitterButton = (TwitterLoginButton) findViewById(R.id.twitter_button);

        twitter_login_btn.setOnClickListener(new View.OnClickListener() {
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
                    twitterButton.performClick();
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        twitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                // Do something with result, which provides a TwitterSession for making API calls
                AccountService ac = Twitter.getApiClient(result.data).getAccountService();
                ac.verifyCredentials(true, true, new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> result) {

                        imageUrl = result.data.profileImageUrl.replace("_normal", "");
                        String email = result.data.email;
                        String userName = result.data.name;
                        final String userHandle = result.data.screenName;

                        signUpXml = startTag + startEveryTrailler + startInputData + startUserType
                                + "Social_Networking" + endUserType + "<mediaType>" + 2 + "</mediaType>" + startUsername + userHandle + endUsername
                                + startEmailID + "" + endEmailID
                                + startPassword + "" + endPassword + startDeviceID
                                + "4541" + endDeviceID + startProfilePic + imageUrl
                                + endProfilePic + endInputData + endEveryTrailler;

                        new ForSocialLogin().execute();

                    }

                    @Override
                    public void failure(TwitterException e) {
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
            }
        });

        facebook_login_button = (LoginButton) findViewById(R.id.facebook_login);
        facebook_login_button.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));
        facebook_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                profile = Profile.getCurrentProfile();
                try {

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    fbEmail = object.optString("email");
                                    fbUserName = object.optString("name");
                                    String Id = object.optString("id");

                                    Log.e("LoginActivity", response.toString());

                                    try {
                                        imageURL = new URL("https://graph.facebook.com/" + Id + "/picture?type=large");
                                        profile_url = imageURL.toString();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }

                                    String[] fbUserIdSplit = fbEmail.split("@");
                                    String fbUserId = fbUserIdSplit[0];

                                    signUpXml = startTag + startEveryTrailler + startInputData + startUserType
                                            + "Social_Networking" + endUserType + "<mediaType>" + 1 + "</mediaType>" + startUsername + fbUserId + endUsername
                                            + startEmailID + fbEmail + endEmailID
                                            + startPassword + "" + endPassword + startDeviceID
                                            + "4541" + endDeviceID + startProfilePic + imageURL
                                            + endProfilePic + endInputData + endEveryTrailler;

                                    new ForSocialLogin().execute();

                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                } catch (Exception e) {
                    Log.e(Constants.TAG, "LOGIN EXCEPTION: " + e);
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
//                info.setText("Login attempt failed.");
                Toast.makeText(LoginActivity.this, "" + e, Toast.LENGTH_SHORT).show();
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
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
                    facebook_login_button.performClick();
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
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
                    String userNameStr = mUserName.getText().toString();
                    String passwordStr = mPassword.getText().toString();

                    if (userNameStr.isEmpty() || passwordStr.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    } else {
                        loginUserXml = startTag + startEveryTrailler + startInputData
                                + startUsername + userNameStr + endUsername + startPassword
                                + passwordStr + endPassword + endInputData + endEveryTrailler;

                        loginUser = new ForLogin();
                        loginUser.execute();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String signUpText = "<font color = \"#ffffff\">Don’t have an account? </font><font color = \"#ff5040\" size=\"18\"><b>Sign Up</b></font>";
        mSignUp.setText(Html.fromHtml(signUpText));

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {

                            Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);

                            String personName = person.getDisplayName();
                            String personPhoto = person.getImage().getUrl();
                            String persion_email = Plus.AccountApi.getAccountName(googleApiClient);

                            personPhoto = personPhoto.substring(0, personPhoto.indexOf("sz=") + 3) + "200";

                            String[] googleUserIdSplit = persion_email.split("@");
                            String googleUserId = googleUserIdSplit[0];

                            signUpXml = startTag + startEveryTrailler + startInputData + startUserType
                                    + "Social_Networking" + endUserType + "<mediaType>" + 3 + "</mediaType>" + startUsername + googleUserId + endUsername
                                    + startEmailID + persion_email + endEmailID
                                    + startPassword + "" + endPassword + startDeviceID
                                    + "4541" + endDeviceID + startProfilePic + personPhoto
                                    + endProfilePic + endInputData + endEveryTrailler;

                            new ForSocialLogin().execute();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        //Here we are trying to reconnect
//                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        //Here we are trying to reconnect again

                        Log.e(Constants.TAG, "CONNECTION FAILED" + result);

                        if (!mIntentInProgress && result.hasResolution()) {
                            try {
                                mIntentInProgress = true;
                                startIntentSenderForResult(result.getResolution().getIntentSender(),
                                        RC_SIGN_IN, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                mIntentInProgress = false;
                                googleApiClient.connect();
                            }
                        }
                    }
                })
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();


        signInButton_custom_googlePlay = (ImageButton) findViewById(R.id.google_plus);

        signInButton_custom_googlePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleApiClient.connect();
            }
        });


        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private String getBase64StringFromBitmap(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String imgString = Base64.encodeToString(b, Base64.NO_WRAP);

        return imgString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 700: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Constants.MY_PERMISSIONS_REQUEST_GET_ACCOUNTS_GRANTED = true;

                } else {

                    Constants.MY_PERMISSIONS_REQUEST_GET_ACCOUNTS_GRANTED = false;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        twitterButton.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            //If the googleApiClient is not connecting then connect
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void parseLoginXML(String MovieListXml) {

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            status = parser.getValue(everyTrailerElement, Constants.KEY_STATUS);

            if (status.equalsIgnoreCase("1")) {
                profilePic = parser.getValue(everyTrailerElement, Constants.KEY_PROFILE_PIC);
                userId = parser.getValue(everyTrailerElement, Constants.KEY_USER_ID);
                userName = parser.getValue(everyTrailerElement, Constants.KEY_USERNAME);
            } else {
                msg = parser.getValue(everyTrailerElement, Constants.KEY_MESSAGE);
            }
        }
    }

    class ForSocialLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage("Logging in...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_SIGN_UP);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String paramsStr = "{\"pass\":\"" + privateKey
                        + "\"" + ",\"signUp\":\"" + signUpXml + "\"" + "}";

                Log.e(Constants.TAG, "SOCIAL LOGIN REQ: " + paramsStr);

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                socialLoginResponseString = Html.fromHtml(resultPlain).toString();

                Log.e(Constants.TAG, "SOCIAL LOGIN RESPONSE: " + socialLoginResponseString);

//                parseLoginXML(resultHtml);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String profilePic = null;
            String userId = null;
            String userName = null;

            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(socialLoginResponseString);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                status = parser.getValue(everyTrailerElement, Constants.KEY_STATUS);
                profilePic = parser.getValue(everyTrailerElement, "profilePic");
                userId = parser.getValue(everyTrailerElement, "userID");
                userName = parser.getValue(everyTrailerElement, "userName");
            }

            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

            editor = sharedpreferences.edit();
            editor.putString("User_name", userName);
            editor.putString("Profile_picture", profilePic);
            editor.putString("Profile_email", "");
            editor.putString("User_ID", userId);
            editor.putBoolean("isLocalSignedIn", false);
            editor.putBoolean("isSocialSignedIn", true);

            editor.commit();

            mProgressDialog.dismiss();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    class ForLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage("Logging in...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_LOGIN);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String paramsStr = "{\"pass\":\"" + privateKey
                        + "\"" + ",\"loginUser\":\"" + loginUserXml + "\"" + "}";

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                String resultHtml = Html.fromHtml(resultPlain).toString();

                parseLoginXML(resultHtml);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (status.equalsIgnoreCase("1")) {

                sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                editor = sharedpreferences.edit();
                editor.putString("User_name", userName);
                editor.putString("Profile_picture", profilePic);
                editor.putString("User_ID", userId);
                editor.putBoolean("isLocalSignedIn", true);
                editor.putBoolean("isSocialSignedIn", false);

                editor.commit();

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
        }
    }

}

