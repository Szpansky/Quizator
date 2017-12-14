package com.apps.szpansky.quizator;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;
import com.apps.szpansky.quizator.Tasks.CreateNewAccount;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class NewAccountActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private CreateNewAccount mAuthTask = null;

    EditText mEmailView,
            mDisplayNameView,
            mPasswordView,
            mRePasswordView,
            mUserNameView;

    Button mEmailSignInButton;


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthTask != null) mAuthTask.cancel(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        setViews();
        setListeners();
    }


    private void setListeners() {
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    private void setViews() {
        mEmailView = findViewById(R.id.emailRegister);
        mDisplayNameView = findViewById(R.id.userDisplayName);
        mUserNameView = findViewById(R.id.userNameRegister);
        mPasswordView = findViewById(R.id.passwordRegister);
        mRePasswordView = findViewById(R.id.repasswordRegister);
        mEmailSignInButton = findViewById(R.id.create_account_button_registe);
    }


    private void attemptLogin() {
        mEmailView.setError(null);
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);
        mDisplayNameView.setError(null);


        final String username = mUserNameView.getText().toString();
        final String displayName = mDisplayNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
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
        if (!isUsernameValid(displayName)) {
            mDisplayNameView.setError(getString(R.string.error_invalid_username));
            focusView = mDisplayNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new CreateNewAccount(email, password, username,displayName, getSupportFragmentManager(), getApplicationContext());
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


    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mAuthTask.WAS_CREATED) {
            this.setResult(RESULT_OK);
            finish();
        }
    }
}

