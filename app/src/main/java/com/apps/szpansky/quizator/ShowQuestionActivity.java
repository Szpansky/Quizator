package com.apps.szpansky.quizator;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.Tasks.SendAnswer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class ShowQuestionActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    SendAnswer sendAnswer = null;

    boolean flag = false;

    FloatingActionButton sendAnswerButton,
            startVideo;

    private RadioGroup radioGroup;
    private RadioButton answerA, answerB, answerC, answerD;
    public TextView questionTextArea;

    private UserData userData;
    private QuestionData questionData;

    private InterstitialAd interstitialAd;


    @Override
    protected void onPause() {
        super.onPause();
        if (sendAnswer != null) sendAnswer.cancel(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_question);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getBundle();
        setViews();
        setContent();
        setListeners();
        setAd();
    }


    private void setAd() {
        //ca-app-pub-3940256099942544/1033173712  getString(R.string.ads_reward_full_screen)
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.ads_reward_full_screen));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                interstitialAd.show();
            }
        }, 3000);
    }


    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userData = (UserData) bundle.getSerializable("userData");
            questionData = (QuestionData) bundle.getSerializable("questionData");
        }
    }

    private void setViews() {
        startVideo = findViewById(R.id.start_video);
        sendAnswerButton = findViewById(R.id.fab_question);
        questionTextArea = findViewById(R.id.question);
        radioGroup = findViewById(R.id.radioGroup);
        answerA = findViewById(R.id.a);
        answerB = findViewById(R.id.b);
        answerC = findViewById(R.id.c);
        answerD = findViewById(R.id.d);
    }

    private void setListeners() {
        startVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite(questionData.getLink());
            }
        });

        sendAnswerButton.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public boolean onLongClick(View v) {
                sendAnswer = new SendAnswer(userData.getCookie(), userData.getUserId(), getUserAnswer(), getSupportFragmentManager(), getApplicationContext());
                sendAnswer.execute((Void) null);
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

    private void setContent() {
        String[] mQuestion = questionData.getText().split("\n");
        ShowQuestionActivity.super.setTitle(getString(R.string.to_win) + ": " + questionData.getPoints() + " " + getString(R.string.points_shortcut));

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


    @Override
    public void onDismiss(DialogInterface dialog) {
        finish();
    }

}