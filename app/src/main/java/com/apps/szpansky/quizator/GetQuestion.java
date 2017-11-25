package com.apps.szpansky.quizator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetQuestion extends AppCompatActivity {

    private DownloadQuestion mAuthTask = null;
    private UpdatePoints mAuthTask2 = null;
    boolean flag = false;

    private String text;
    private String correctAnswer;
    private String points;
    private String userPoints;
    private String userId;
    private String cookie;
    private RadioGroup radioGroup;
    public TextView textView;
    private String questionId;


    private View mProgressView;
    private View mQuestionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mQuestionView = findViewById(R.id.question_view);
        mProgressView = findViewById(R.id.progressBar);

        radioGroup = findViewById(R.id.radioGroup);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cookie = bundle.getString("cookie");
            userPoints = bundle.getString("userPoints");
            userId = bundle.getString("userId");
        }

        textView = findViewById(R.id.question);


        showProgress(true);
        mAuthTask = new DownloadQuestion(cookie);
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

                if (getUserAnswer().equals(correctAnswer)) {
                    Snackbar.make(v, "Brawo, odpowiedź poprawna", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    AnswerIsCorrect();
                } else {
                    Snackbar.make(v, "Hmmm, niestety błędna odpowiedź", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    AnswerIsUncorrected();
                }
                flag = true;
                return false;
            }
        });

    }


    private void AnswerIsCorrect() {
        showProgress(true);
        Integer currency = Integer.parseInt(userPoints) + Integer.parseInt(points);
        userPoints = currency.toString();
        mAuthTask2 = new UpdatePoints(cookie, userPoints, points, userId);
        mAuthTask2.execute((Void) null);
    }

    private void AnswerIsUncorrected() {
        showProgress(true);
        Integer currency = Integer.parseInt(userPoints) - Integer.parseInt(points);
        userPoints = currency.toString();
        mAuthTask2 = new UpdatePoints(cookie, userPoints, "-" + points, userId);
        mAuthTask2.execute((Void) null);
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mQuestionView.setVisibility(show ? View.GONE : View.VISIBLE);
        mQuestionView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mQuestionView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    public void setText(String text) {
        this.text = text;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer.toLowerCase();
    }

    public void setPoints(String points) {
        this.points = points;
    }


    public class DownloadQuestion extends AsyncTask<Void, Void, Boolean> {

        private final String questionURL;
        private String information = "";


        DownloadQuestion(String cookie) {
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
                        setText(object.getJSONObject("pytanie").getString("tekst"));
                        setCorrectAnswer(object.getJSONObject("pytanie").getString("odpowiedz"));
                        setPoints(object.getJSONObject("pytanie").getString("punkty"));

                    } else return false;
                    if (questionId.equals("-1")) {
                        information = "Dziś już odpowiadałeś na pytanie.";
                        return false;
                    }
                    if (questionId.equals("-2")) {
                        information = "Brak pytań.";
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
                textView.setText(text);
                GetQuestion.super.setTitle("Do wygrania: " + points + " punktów");

            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GetQuestion.this);
                dialog.setCancelable(false);
                dialog.setTitle("Uwaga");
                dialog.setMessage("Błąd podczas pobierania pytania.\n\n" + information);
                dialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgress(false);
                        finish();
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


    public class UpdatePoints extends AsyncTask<Void, Void, Boolean> {

        private final String logURL;
        private final String update_game_url;


        UpdatePoints(String cookie, String userPoints, String points, String userId) {

            @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            logURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/put_log/?insecure=cool&cookie=" + cookie + "&user_id=" + userId + "&points=" + points + "&date=" + date;
            update_game_url = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/update_user_game/?insecure=cool&cookie=" + cookie + "&user_id=" + userId + "&question_id=" + questionId + "&user_points=" + userPoints;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(update_game_url);
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(url).build();
                Response respond = client.newCall(request).execute();

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(GetQuestion.this);
                dialog.setCancelable(false);
                dialog.setTitle("Punkty");
                dialog.setMessage("Dodano / odjęto punkty");
                dialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgress(false);

                        Bundle conData = new Bundle();
                        conData.putString("userPoints", userPoints);
                        Intent intent = new Intent();
                        intent.putExtras(conData);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                });
                final AlertDialog alert = dialog.create();
                alert.show();

            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GetQuestion.this);
                dialog.setCancelable(false);
                dialog.setTitle("Punkty");
                dialog.setMessage("Błąd");
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


}









