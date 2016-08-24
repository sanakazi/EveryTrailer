package com.aurum.everytrailer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

public class ChangePasswordActivity extends Activity {

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


    EditText mUserName, mOldPassword, mNewPassword, mConfirmPassword, mEmailId;
    TextView mSignIn, mTermsAndConditions;
    Button mChangePassword, mCancel;
    ForChangePAssword ChangepasswordUser;
    ProgressDialog mProgressDialog;
    String privateKey, changePasswordXML;
    ConnectionDetector connectionDetector;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String status, msg;
    String userName;

    String userId;
    SharedPreferences prefs;

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
        setContentView(R.layout.activity_change_password);

        // GET PRIVATE KEY
        privateKey = Constants.getPrivateKey();

        mUserName = (EditText) findViewById(R.id.change_password_user_name);
        mEmailId = (EditText) findViewById(R.id.change_password_email);
        mOldPassword = (EditText) findViewById(R.id.change_password_old_password);
        mNewPassword = (EditText) findViewById(R.id.change_password_new_password);
        mConfirmPassword = (EditText) findViewById(R.id.change_password_confirm_password);
        mChangePassword = (Button) findViewById(R.id.change_password_change_password_btn);
        mCancel = (Button) findViewById(R.id.change_password_cancel);

        prefs = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);

        userId = prefs.getString("User_ID", null);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mChangePassword.setOnClickListener(new View.OnClickListener() {
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
                    String oldpasswordStr = mOldPassword.getText().toString();
                    String newpasswordStr = mNewPassword.getText().toString();
                    String emailIdStr = mEmailId.getText().toString();
                    String confirmPasswordStr = mConfirmPassword.getText().toString();

                    if (userNameStr.isEmpty() || oldpasswordStr.isEmpty() || newpasswordStr.isEmpty() || emailIdStr.isEmpty() || confirmPasswordStr.isEmpty()) {
                        Toast.makeText(ChangePasswordActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isEmailValid(emailIdStr)) {
                            Toast.makeText(ChangePasswordActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!newpasswordStr.matches(confirmPasswordStr)) {
                                Toast.makeText(ChangePasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                            } else {

                                changePasswordXML = startTag + startEveryTrailler + startInputData +
                                        "<userID>" + userId + "</userID>"
                                        + "<oldPassword>" + oldpasswordStr + "</oldPassword>"
                                        + "<newPassword>" + newpasswordStr + "</newPassword>" + endInputData + endEveryTrailler;


                                ChangepasswordUser = new ForChangePAssword();
                                ChangepasswordUser.execute();
                            }
                        }
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
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
                msg = parser.getValue(everyTrailerElement, Constants.KEY_MESSAGE);
            } else {
                msg = parser.getValue(everyTrailerElement, Constants.KEY_MESSAGE);
            }
        }
    }

    class ForChangePAssword extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(ChangePasswordActivity.this);
            mProgressDialog.setMessage("Changing Password...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_CHANGE_USER_PASSWORD);

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
                        + "\"" + ",\"ChangePassword\":\"" + changePasswordXML + "\"" + "}";

                Log.d("HHHH", "HHHHH" + paramsStr);

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
                Toast.makeText(ChangePasswordActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChangePasswordActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }
}
