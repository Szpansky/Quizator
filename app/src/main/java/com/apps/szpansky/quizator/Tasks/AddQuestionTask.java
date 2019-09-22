package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.apps.szpansky.quizator.Constant;
import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.NewQuestion;
import com.apps.szpansky.quizator.SimpleData.UserData;
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

        UserData userData = new UserData();

        addQuestionURL = Constant.siteURL + Constant.siteApiUser + "add_question/" +
                "?question_category=" + question.getCategory() +
                "&question_text=" + question.getQuestionText() +
                "&question_a=" + question.getAnswerA() +
                "&question_b=" + question.getAnswerB() +
                "&question_c=" + question.getAnswerC() +
                "&question_d=" + question.getAnswerD() +
                "&question_correct_answer=" + question.getCorrectAnswer() +
                "&user_display_name="+ userData.getDisplayName();
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
