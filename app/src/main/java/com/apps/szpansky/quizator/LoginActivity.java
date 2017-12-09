package com.apps.szpansky.quizator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.Tasks.RetrievePassword;

import com.apps.szpansky.quizator.Tools.MySharedPreferences;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private RetrievePassword mPasswordTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    public final int RESULT_FROM_MAIN = 50;
    private CheckBox saveLoginData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        mEmailView.setText(MySharedPreferences.getLogin(this));
        mPasswordView.setText(MySharedPreferences.getPassword(this));

        saveLoginData = findViewById(R.id.save_login_data);
        saveLoginData.setChecked(MySharedPreferences.getSaveLoginDataIsSet(this));


        saveLoginData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MySharedPreferences.setSaveLoginData(getBaseContext(), isChecked);
                if (isChecked) {
                    MySharedPreferences.setLogin(getBaseContext(), "");
                    MySharedPreferences.setPassword(getBaseContext(), "");
                } else {
                    MySharedPreferences.setLogin(getBaseContext(), mEmailView.getText().toString());
                    MySharedPreferences.setPassword(getBaseContext(), mPasswordView.getText().toString());
                }
            }
        });


        if (MySharedPreferences.getSaveLoginDataIsSet(this)) {
            mEmailView.setText(MySharedPreferences.getLogin(this));
            mPasswordView.setText(MySharedPreferences.getPassword(this));
        }

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
            mPasswordTask = new RetrievePassword(getString(R.string.site_address), email, getSupportFragmentManager());
            mPasswordTask.execute();
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

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

            MySharedPreferences.setLogin(this, mEmailView.getText().toString().trim());
            MySharedPreferences.setPassword(this, mPasswordView.getText().toString().trim());

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAuthTask = new UserLoginTask(email, password);
                    mAuthTask.execute((Void) null);
                }
            }, 300);
        }
    }

    private boolean isEmailValid(String email) {
        return !(email.contains("\"") || email.contains(" ") || email.contains("?") || email.contains("&"));
    }

    private boolean isPasswordValid(String password) {
        return !(password.contains("\"") || password.contains(" ") || password.contains("?") || password.contains("&"));
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


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String sendLoginURL;
        private String error = "";
        private UserData userData = new UserData();

        UserLoginTask(String email, String password) {
            sendLoginURL = getString(R.string.site_address) + "cyj@n3k/user/generate_auth_cookie/?insecure=cool&username=" + email + "&password=" + password;
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
                        userData.setCookie(object.getString("cookie"));
                        userData.setUserId(object.getJSONObject("user").getString("id"));
                        userData.setUsername(object.getJSONObject("user").getString("username"));
                        userData.setNicename(object.getJSONObject("user").getString("nicename"));
                        userData.setEmail(object.getJSONObject("user").getString("email"));
                        userData.setRegistered(object.getJSONObject("user").getString("registered"));
                        userData.setNickname(object.getJSONObject("user").getString("nickname"));
                        userData.setUserPoints(object.getJSONObject("user").getString("points"));
                        userData.setUserPointsNext(object.getJSONObject("user").getString("points_next"));
                        userData.setPointsCurrentRank(object.getJSONObject("user").getString("points_current_rank"));
                        userData.setRankName(object.getJSONObject("user").getString("rank_name"));
                        userData.setRankPrev(object.getJSONObject("user").getString("rank_prev"));
                        userData.setRankNext(object.getJSONObject("user").getString("rank_next"));
                        userData.setUserAvatar(object.getJSONObject("user").getString("avatar"));

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
                startMain.putExtra("userData", userData);
                startActivityForResult(startMain, RESULT_FROM_MAIN);
            } else {

                Information information = Information.newInstance(getString(R.string.connection_error) + "\n\n" + error);
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

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

