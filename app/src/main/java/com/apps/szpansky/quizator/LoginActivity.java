package com.apps.szpansky.quizator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private UserRetrievePassword mPasswordTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    public final int RESULT_FROM_MAIN = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        Button retrievePassword = findViewById(R.id.retrieve_password_button);
        Button createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccount = new Intent(getBaseContext(), NewAccountActivity.class);
                startActivity(createAccount);
            }
        });
        retrievePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResetPassword();
            }
        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptResetPassword() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        final String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mPasswordTask = new UserRetrievePassword(email);
            mPasswordTask.execute((Void) null);
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return !(email.contains("\"") || email.contains(" ") || email.contains("?") || email.contains("&"));
    }

    private boolean isPasswordValid(String password) {
        return !(password.contains("\"") || password.contains(" ") || password.contains("?") || password.contains("&"));
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    public class UserRetrievePassword extends AsyncTask<Void, Void, Boolean> {

        private final String sendRetrievePasswordURL;

        private final String mEmail;

        UserRetrievePassword(String email) {
            mEmail = email;
            sendRetrievePasswordURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/retrieve_password/?insecure=cool&user_login=" + mEmail;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            try {
                url = new URL(sendRetrievePasswordURL);
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).build();
                Response respond = null;
                respond = client.newCall(request).execute();
                String json = respond.body().string();
                try {
                    JSONObject object = new JSONObject(json);

                    if (object.getString("status").equals("ok")) {
                        return true;
                    } else return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Restart hasła");
                dialog.setMessage("Twoje hasło zostało wysłane na podany adres email");
                dialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgress(false);
                    }
                });
                final AlertDialog alert = dialog.create();
                alert.show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Restart hasła");
                dialog.setMessage("Konto nie istnieje");
                dialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgress(false);
                    }
                });
                final AlertDialog alert = dialog.create();
                alert.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String sendLoginURL;

        String cookie;

        private final String mEmail;
        private final String mPassword;
        private String username;
        private String nickname;
        private String nicename;
        private String email;
        private String registered;
        private String userId;
        private String userPoints;
        private String userAvatar;
        private String error = "";
        private String userPointsNext;
        private String rankNext;
        private String rankName;


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            sendLoginURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/generate_auth_cookie/?insecure=cool&username=" + mEmail + "&password=" + mPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(sendLoginURL);
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).build();
                Response respond = client.newCall(request).execute();
                String json = respond.body().string();

                try {
                    JSONObject object = new JSONObject(json);

                    if (object.getString("status").equals("ok")) {
                        cookie = object.getString("cookie");
                        userId = object.getJSONObject("user").getString("id");
                        username = object.getJSONObject("user").getString("username");
                        nicename = object.getJSONObject("user").getString("nicename");
                        email = object.getJSONObject("user").getString("email");
                        registered = object.getJSONObject("user").getString("registered");
                        nickname = object.getJSONObject("user").getString("nickname");
                        userPoints = object.getJSONObject("user").getString("points");
                        userPointsNext = object.getJSONObject("user").getString("points_next");
                        rankName = object.getJSONObject("user").getString("rank_name");
                        rankNext = object.getJSONObject("user").getString("rank_next");
                        userAvatar = object.getJSONObject("user").getString("avatar");

                        return true;
                    } else {
                        error = object.getString("error");
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent startMain = new Intent(getBaseContext(), MainActivity.class);
                startMain.putExtra("cookie", cookie);
                startMain.putExtra("userId", userId);
                startMain.putExtra("username", username);
                startMain.putExtra("nicename", nicename);
                startMain.putExtra("email", email);
                startMain.putExtra("registered", registered);
                startMain.putExtra("nickname", nickname);
                startMain.putExtra("points", userPoints);
                startMain.putExtra("points_next", userPointsNext);
                startMain.putExtra("rank_name", rankName);
                startMain.putExtra("rank_next", rankNext);
                startMain.putExtra("userAvatar", userAvatar);
                startActivityForResult(startMain, RESULT_FROM_MAIN);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Uwaga");
                dialog.setMessage("Błąd połączenia. \n\n" + error);
                dialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgress(false);
                    }
                });
                final AlertDialog alert = dialog.create();
                alert.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_FROM_MAIN:
                if (resultCode == RESULT_OK) {

                    String email = mEmailView.getText().toString();
                    String password = mPasswordView.getText().toString();

                    showProgress(true);
                    mAuthTask = new UserLoginTask(email, password);
                    mAuthTask.execute((Void) null);

                }
                break;
        }


    }
}

