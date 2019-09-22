package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentManager;

import com.apps.szpansky.quizator.Constant;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.ShowQuestionActivity;
import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class GetQuestion extends BasicTask {

    private QuestionData questionData;
    private UserData userData;

    private String questionURL;

    public GetQuestion(UserData userData, QuestionData questionData, FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        questionURL = Constant.siteURL + Constant.siteApiUser  + "get_question/?cookie=" + userData.getCookie() + "&user_id=" + userData.getUserId();
        this.questionData = questionData;
        this.userData = userData;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(questionURL);
            Request request = builder.url(url).build();
            Response respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);
                if (object.getString("status").equals("ok")) {

                    questionData.setId(object.getJSONObject("pytanie").getString("id"));
                    questionData.setText(object.getJSONObject("pytanie").getString("tekst"));
                    questionData.setLink(object.getJSONObject("pytanie").getString("link"));
                    questionData.setPoints(object.getJSONObject("pytanie").getString("punkty"));

                } else {
                    setError(getContext().getString(R.string.error_when_downloading));
                    return false;
                }
                if (questionData.getId().equals("-1")) {
                    setError(getContext().getString(R.string.question_error_daily_lock));
                    return false;
                }
                if (questionData.getId().equals("-2")) {
                    setError(getContext().getString(R.string.question_error_no_more_questions));
                    return false;
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                setError(getContext().getString(R.string.error_when_downloading));
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setError(getContext().getString(R.string.connection_error));
            return false;
        }

    }

    @Override
    protected void onSuccessExecute() {
        if (getContext() != null) {
            Intent startQuestion = new Intent(getContext(), ShowQuestionActivity.class);
            startQuestion.putExtra("questionData", questionData);
            startQuestion.putExtra("userData", userData);
            startQuestion.addFlags(FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(startQuestion);
        }
    }
}

