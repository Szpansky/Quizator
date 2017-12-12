package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.ShowQuestionActivity;
import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class GetQuestion extends BasicTask {

    QuestionData questionData;
    UserData userData;

    private final WeakReference<Context> context;

    private String questionURL;

    public GetQuestion(String siteAddress, UserData userData, QuestionData questionData, FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        questionURL = siteAddress + "cyj@n3k/user/get_question/?insecure=cool&cookie=" + userData.getCookie() + "&user_id=" + userData.getUserId();
        this.questionData = questionData;
        this.userData = userData;
        this.context = new WeakReference<>(context);
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

                    questionData.setId(object.getJSONObject("pytanie").getString("id"));
                    questionData.setText(object.getJSONObject("pytanie").getString("tekst"));
                    questionData.setLink(object.getJSONObject("pytanie").getString("link"));
                    questionData.setPoints(object.getJSONObject("pytanie").getString("punkty"));

                } else {
                    setError("Problem podczas pobierania danych");
                    return false;
                }
                if (questionData.getId().equals("-1")) {
                    setError("Dziś już odpowiadałeś, wróć jutro lub kliknij przycisk \"Omiń blokadę\"");
                    return false;
                }
                if (questionData.getId().equals("-2")) {
                    setError("Brak pytań");
                    return false;
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                setError("Problem podczas pobierania danych");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setError("Brak połączenia");
            return false;
        }

    }

    @Override
    protected void onSuccessExecute() {
        if(context.get()!=null){
            Intent startQuestion = new Intent(context.get(), ShowQuestionActivity.class);
            startQuestion.putExtra("questionData", questionData);
            startQuestion.putExtra("userData", userData);
            startQuestion.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.get().startActivity(startQuestion);
        }
    }
}

