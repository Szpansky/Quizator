package com.apps.szpansky.quizator;


import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class NewAccountActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static boolean FINISH = false;
    private UserCreateAccountTask mAuthTask = null;


    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private EditText mUserNameView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        mEmailView = findViewById(R.id.emailRegister);
        mUserNameView = findViewById(R.id.userNameRegister);


        mPasswordView = findViewById(R.id.passwordRegister);
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

        mRePasswordView = findViewById(R.id.repasswordRegister);
        mRePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.create_account_button_registe);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);


        String username = mUserNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(rePassword)) {
            mRePasswordView.setError(getString(R.string.error_field_required));
            focusView = mRePasswordView;
            cancel = true;
        } else if (!isPasswordValid(rePassword)) {
            mRePasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mRePasswordView;
            cancel = true;
        } else if (!rePassword.equals(password)) {
            mRePasswordView.setError(getString(R.string.password_dont_match));
            focusView = mRePasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
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
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUserNameView.setError(getString(R.string.error_invalid_username));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserCreateAccountTask(email, password, username, username, mLoginFormView);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return !(email.contains("\"") || email.contains(" ") || !email.contains("@") || !email.contains(".") || email.contains("?") || email.contains("&"));
    }

    private boolean isPasswordValid(String password) {
        return !(password.contains("\"") || password.contains(" ") || password.contains("?") || password.contains("&"));
    }

    private boolean isUsernameValid(String username) {
        return !(username.contains("\"") || username.contains(" ") || username.contains("?") || username.contains("&"));
    }


    private void showProgress(final boolean show) {
        if (show) {
            Loading loading = Loading.newInstance();
            if (getSupportFragmentManager().findFragmentByTag("Loading") == null)
                getSupportFragmentManager().beginTransaction().add(loading, "Loading").commit();
        } else {
            Loading loading = (Loading) getSupportFragmentManager().findFragmentByTag("Loading");
            if (loading != null && loading.isVisible()) loading.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
    if(FINISH){
        finish();
    }
    }


    public class UserCreateAccountTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername;
        private String nonceValue;
        private String error;


        private final String nonceURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/get_nonce/?controller=user&method=register&insecure=cool";
        String registerURL;

        UserCreateAccountTask(String email, String password, String username, String displayName, View view) {
            mEmail = email;
            mPassword = password;
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(nonceURL);
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).build();
                Response respond = client.newCall(request).execute();
                String json = respond.body().string();
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getString("status").equals("ok")) {
                        nonceValue = object.getString("nonce");
                        registerURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/register/?insecure=cool&notify=no&username=" + mUsername + "&email=" + mEmail + "&nonce=" + nonceValue + "&display_name=" + mUsername + "&user_pass=" + mPassword;

                        url = new URL(registerURL);
                        client = new OkHttpClient();
                        builder = new Request.Builder();
                        request = builder.url(url).build();
                        respond = client.newCall(request).execute();
                        String json2 = respond.body().string();

                        try {
                            JSONObject object2 = new JSONObject(json2);
                            if (object2.getString("status").equals("error")) {
                                error = object2.getString("error");
                                return false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return false;
                        }

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
                Information information = Information.newInstance("Konto zostało utworzone, teraz możesz się logować");
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();
                FINISH = true;
            } else {
                Information information = Information.newInstance("Błąd:\n" + error);
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();
                FINISH = false;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


}

