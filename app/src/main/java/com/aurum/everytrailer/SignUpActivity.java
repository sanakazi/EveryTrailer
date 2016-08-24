package com.aurum.everytrailer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.aurum.everytrailer.utils.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity {

    private static final String startTag = "<?xml version='1.0' encoding='utf-8'?>";
    private static final String startEveryTrailler = "<everyTrailler>";
    private static final String startInputData = "<inputData>";
    private static final String startUserType = "<userType>";
    private static final String startUsername = "<username>";
    private static final String startEmailID = "<emailID>";
    private static final String startPassword = "<password>";
    private static final String startDeviceID = "<deviceID>";
    private static final String startProfilePic = "<profilePic>";

    private static final String endEveryTrailler = "</everyTrailler>";
    private static final String endInputData = "</inputData>";
    private static final String endUserType = "</userType>";
    private static final String endUsername = "</username>";
    private static final String endEmailID = "</emailID>";
    private static final String endPassword = "</password>";
    private static final String endDeviceID = "</deviceID>";
    private static final String endProfilePic = "</profilePic>";

    EditText mUserName, mPassword, mConfirmPassword, mEmailId;
    TextView mSignIn, mTermsAndConditions;
    Button mSignUp;
    ForSignUp signUpUser;
    ProgressDialog mProgressDialog;
    String privateKey, signUpXml;
    ConnectionDetector connectionDetector;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String status, msg;
    String userName;

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUserName = (EditText) findViewById(R.id.sign_up_user_name);
        mEmailId = (EditText) findViewById(R.id.sign_up_email);
        mPassword = (EditText) findViewById(R.id.sign_up_password);
        mConfirmPassword = (EditText) findViewById(R.id.sign_up_confirm_password);
        mSignUp = (Button) findViewById(R.id.sign_up_btn);
        mTermsAndConditions = (TextView) findViewById(R.id.sign_up_terms_and_cond);
        mSignIn = (TextView) findViewById(R.id.sign_up_sign_in);

        String text = "By registering, you confirm that you accept our <font color=#ff6e61>Terms Of Use</font> and <font color=#ff6e61>Privacy Policy.</font>";
        mTermsAndConditions.setText(Html.fromHtml(text));

        mTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
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
                    String emailIdStr = mEmailId.getText().toString();
                    String confirmPasswordStr = mConfirmPassword.getText().toString();

                    if (userNameStr.isEmpty() || passwordStr.isEmpty() || emailIdStr.isEmpty() || confirmPasswordStr.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isEmailValid(emailIdStr)) {
                            Toast.makeText(SignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!passwordStr.matches(confirmPasswordStr)) {
                                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                            } else {
                                privateKey = Constants.getPrivateKey();

                                signUpXml = startTag + startEveryTrailler + startInputData + startUserType
                                        + "CMS" + endUserType + "<mediaType>" + "" + "</mediaType>" + startUsername + userNameStr + endUsername
                                        + startEmailID + emailIdStr + endEmailID
                                        + startPassword + passwordStr + endPassword + startDeviceID
                                        + "4541" + endDeviceID + startProfilePic + "sda"
                                        + endProfilePic + endInputData + endEveryTrailler;

                                signUpUser = new ForSignUp();
                                signUpUser.execute();
                            }
                        }
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void parseLoginXML(String MovieListXml) {

        String profilePic = null;
        String userId = null;

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

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);

        editor = sharedpreferences.edit();
        editor.putString("User_name", userName);
        editor.putString("Profile_picture", profilePic);
        editor.putString("Profile_email", "");
        editor.putString("User_ID", userId);
        editor.putBoolean("isSignedIn", true);

        editor.commit();
    }

    class ForSignUp extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SignUpActivity.this);
            mProgressDialog.setMessage("Signing up...");
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
            mProgressDialog.dismiss();

            if (status.equalsIgnoreCase("0")) {
                Toast.makeText(SignUpActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
            } else {

                final Dialog query_alert = new Dialog(
                        SignUpActivity.this);
                query_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                query_alert.setContentView(R.layout.customlayoutok);
                query_alert.setCancelable(false);
                TextView txtTextView = (TextView) query_alert
                        .findViewById(R.id.customalert);
                txtTextView.setText("Sign up complete. Please Login with the Username:" + userName);
                Button track_ok = (Button) query_alert.findViewById(R.id.ok_id);

                track_ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        query_alert.dismiss();
                    }
                });
                query_alert.show();
            }
        }
    }
}
