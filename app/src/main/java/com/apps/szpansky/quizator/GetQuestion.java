package com.apps.szpansky.quizator;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetQuestion extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static boolean FINISH = false;
    private DownloadQuestion mAuthTask = null;
    private SendAnswer mAuthTask2 = null;
    boolean flag = false;

    private String question;

    private String points;
    private String userPoints;


    private RadioGroup radioGroup;
    public TextView questionTextArea;


    private UserData userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioGroup = findViewById(R.id.radioGroup);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userData = (UserData) bundle.getSerializable("userData");
        }

        questionTextArea = findViewById(R.id.question);


        showProgress(true);
        mAuthTask = new DownloadQuestion(userData.getCookie(), userData.getUserId());
        mAuthTask.execute((Void) null);


        FloatingActionButton fab = findViewById(R.id.fab_question);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag) {
                    Snackbar.make(view, "Przytrzymaj aby odpowiedzieć", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else flag = false;
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showProgress(true);
                mAuthTask2 = new SendAnswer(userData.getCookie(), points, userData.getUserId(), getUserAnswer());
                mAuthTask2.execute((Void) null);
                flag = true;
                return false;
            }
        });

    }




    private String getUserAnswer() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.a:
                return ("a");
            case R.id.b:
                return ("b");
            case R.id.c:
                return ("c");
            case R.id.d:
                return ("d");
            default:
                return "x";
        }
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


    public void setTextQuestion(String question) {
        this.question = question;
    }


    public void setPoints(String points) {
        this.points = points;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        if(FINISH){
            finish();
        }
    }


    public class DownloadQuestion extends AsyncTask<Void, Void, Boolean> {

        private final String questionURL;
        private String error = "";
        String questionId;

        DownloadQuestion(String cookie, String userId) {
            questionURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/get_question/?insecure=cool&cookie=" + cookie + "&user_id=" + userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(questionURL);
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).build();
                Response respond = client.newCall(request).execute();
                String json = respond.body().string();
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getString("status").equals("ok")) {

                        questionId = object.getJSONObject("pytanie").getString("id");
                        setTextQuestion(object.getJSONObject("pytanie").getString("tekst"));

                        setPoints(object.getJSONObject("pytanie").getString("punkty"));

                    } else return false;
                    if (questionId.equals("-1")) {
                        error = "Dziś już odpowiadałeś na pytanie.\n\nNie chcesz  czekać ?\nKliknij w przycisk (Omiń blokadę) i obejrzyj krótki filmik";
                        return false;
                    }
                    if (questionId.equals("-2")) {
                        error = "Brak pytań.";
                        return false;
                    }
                    return true;
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
                showProgress(false);
                questionTextArea.setText(question);
                GetQuestion.super.setTitle("Do wygrania: " + points + " punktów");

                FINISH = false;

            } else {

                Information information = Information.newInstance("Błąd podczas pobierania pytania.\n\n" + error);
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

                FINISH = true;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public class SendAnswer extends AsyncTask<Void, Void, Boolean> {

        private final String logURL;
        private final String update_game_url;
        String questionResult;


        SendAnswer(String cookie, String points, String userId, String userAnswer) {

            @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            logURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/put_log/?insecure=cool&cookie=" + cookie + "&user_id=" + userId + "&points=" + points + "&date=" + date;
            update_game_url = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/send_answer/?insecure=cool&cookie=" + cookie + "&user_id=" + userId + "&user_answer=" + userAnswer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(update_game_url);
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).build();
                Response respond = client.newCall(request).execute();

                String json = respond.body().string();
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getString("status").equals("ok")) {
                        questionResult = (object.getString("informacja"));
                    } else return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                url = new URL(logURL);
                client = new OkHttpClient();
                builder = new Request.Builder();
                request = builder.url(url).build();
                respond = client.newCall(request).execute();

                respond.body().close();
                return true;
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

                Information information = Information.newInstance("Zaktualizowano punkty\n\n"+questionResult);
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

                Bundle conData = new Bundle();
                conData.putString("userPoints", userPoints);
                Intent intent = new Intent();
                intent.putExtras(conData);
                setResult(RESULT_OK, intent);

                FINISH = true;
            } else {

                Information information = Information.newInstance("Błąd dodawania punktów");
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