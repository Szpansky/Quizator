package com.apps.szpansky.quizator;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;
import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class ShowQuestionActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static boolean FINISH = false;

    private SendAnswer mAuthTask2 = null;
    boolean flag = false;

    FloatingActionButton sendAnswerButton;

    private RadioGroup radioGroup;
    private RadioButton answerA, answerB, answerC, answerD;
    public TextView questionTextArea;

    private UserData userData;
    private QuestionData questionData;

    Button startVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_question);

        getBundle();
        setViews();
        onButtonClick();
        setContent();
    }


    private void getBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userData = (UserData) bundle.getSerializable("userData");
            questionData = (QuestionData) bundle.getSerializable("questionData");
        }
    }

    private void setViews(){
        startVideo = findViewById(R.id.start_video);
        sendAnswerButton = findViewById(R.id.fab_question);
        questionTextArea = findViewById(R.id.question);
        radioGroup = findViewById(R.id.radioGroup);
        answerA = findViewById(R.id.a);
        answerB = findViewById(R.id.b);
        answerC = findViewById(R.id.c);
        answerD = findViewById(R.id.d);
    }

    private void onButtonClick(){
        startVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite(questionData.getLink());
            }
        });

        sendAnswerButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showProgress(true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAuthTask2 = new SendAnswer(userData.getCookie(), userData.getUserId(), getUserAnswer());
                        mAuthTask2.execute((Void) null);
                    }
                }, 300);
                flag = true;
                return false;
            }
        });

        sendAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag) {
                    Snackbar.make(view, R.string.hold_for_answer, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else flag = false;
            }
        });

    }

    private void setContent(){
        String[] mQuestion = questionData.getText().split("\n");
        ShowQuestionActivity.super.setTitle("Do wygrania: " + questionData.getPoints() + " punktów");

        if (!questionData.getLink().equals("-1")) {
            startVideo.setVisibility(View.VISIBLE);
        } else {
            startVideo.setVisibility(View.GONE);
        }

        for (int i = 0; i < mQuestion.length; i++) {
            if (mQuestion[i].contains("https://")) {
                int startId = mQuestion[i].indexOf("https://");
                mQuestion[i] = mQuestion[i].substring(0, startId);
            }
        }

        questionTextArea.setText(mQuestion[0]);
        answerA.setText(mQuestion[1]);
        answerB.setText(mQuestion[2]);
        answerC.setText(mQuestion[3]);
        answerD.setText(mQuestion[4]);

        FINISH = false;
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


    private void openSite(String siteUrl) {
        Uri uri = Uri.parse(siteUrl);
        Intent openSite = new Intent(Intent.ACTION_VIEW, uri);
        openSite.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(openSite);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(siteUrl)));
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


    @Override
    public void onDismiss(DialogInterface dialog) {
        if (FINISH) {
            finish();
        }
    }


    public class SendAnswer extends AsyncTask<Void, Void, Boolean> {

        private final String update_game_url;
        String questionResult;


        SendAnswer(String cookie, String userId, String userAnswer) {
            update_game_url = getString(R.string.site_address) + "cyj@n3k/user/send_answer/?insecure=cool&cookie=" + cookie + "&user_id=" + userId + "&user_answer=" + userAnswer;
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
                respond.body().close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask2 = null;
            showProgress(false);

            if (success) {

                Information information = Information.newInstance("Zaktualizowano punkty\n\n" + questionResult);
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

                FINISH = true;
            } else {

                Information information = Information.newInstance("Błąd dodawania punktów");
                getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

                FINISH = false;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask2 = null;
            showProgress(false);
        }
    }


}