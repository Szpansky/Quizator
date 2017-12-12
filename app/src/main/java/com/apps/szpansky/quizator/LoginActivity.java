package com.apps.szpansky.quizator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.Tasks.RetrievePassword;

import com.apps.szpansky.quizator.Tasks.UserLogin;
import com.apps.szpansky.quizator.Tools.MySharedPreferences;

public class LoginActivity extends AppCompatActivity {

    RetrievePassword mPasswordTask = null;
    UserLogin userLogin = null;

    UserData userData;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    public final int RESULT_FROM_MAIN = 50;
    private CheckBox saveLoginData;


    @Override
    protected void onPause() {
        super.onPause();
        if (userLogin != null) userLogin.cancel(true);
        if (mPasswordTask != null) mPasswordTask.cancel(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        //mEmailView.setText(MySharedPreferences.getLogin(this));
        //mPasswordView.setText(MySharedPreferences.getPassword(this));

        saveLoginData = findViewById(R.id.save_login_data);
        saveLoginData.setChecked(MySharedPreferences.getSaveLoginDataIsSet(this));


        saveLoginData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MySharedPreferences.setSaveLoginData(getBaseContext(), isChecked);
                if (!isChecked) {
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
            MySharedPreferences.setLogin(this, mEmailView.getText().toString().trim());
            MySharedPreferences.setPassword(this, mPasswordView.getText().toString().trim());
            newLoginTask();
        }
    }

    private boolean isEmailValid(String email) {
        return !(email.contains("\"") || email.contains(" ") || email.contains("?") || email.contains("&"));
    }

    private boolean isPasswordValid(String password) {
        return !(password.contains("\"") || password.contains(" ") || password.contains("?") || password.contains("&"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_FROM_MAIN:
                if (resultCode == RESULT_OK) {
                    newLoginTask();
                }
                break;
        }
    }


    private void newLoginTask() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        userData = new UserData();
        userLogin = new UserLogin(getString(R.string.site_address), email, password, userData, getSupportFragmentManager(), getBaseContext());
        userLogin.execute((Void) null);
    }
}

