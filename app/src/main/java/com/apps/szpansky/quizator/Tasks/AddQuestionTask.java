package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.NewQuestion;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class AddQuestionTask extends BasicTask {

    private String addQuestionURL;

    public AddQuestionTask(NewQuestion question, FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        addQuestionURL = getContext().getString(R.string.site_address) + "cyj@n3k/user/add_question/?insecure=cool" +
                "&question_category=" + question.getCategory() +
                "&question_text=" + question.getQuestionText() +
                "&question_a=" + question.getAnswerA() +
                "&question_b=" + question.getAnswerB() +
                "&question_c=" + question.getAnswerC() +
                "&question_d=" + question.getAnswerD() +
                "&question_correct_answer=" + question.getCorrectAnswer();
    }

    @Override
    protected void onSuccessExecute() {
        Information information = Information.newInstance("Dodano pomyślnie");
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(addQuestionURL);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();
            Response respond = client.newCall(request).execute();
            String json = respond.body().string();
            JSONObject object = new JSONObject(json);
            if (object.getString("status").equals("ok")) {
                return true;
            } else {
                setError(getContext().getString(R.string.error_when_downloading));
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            setError(getContext().getString(R.string.connection_error));
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            setError(getContext().getString(R.string.error_when_downloading));
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            setError(getContext().getString(R.string.connection_error));
            return false;
        }
    }
}
