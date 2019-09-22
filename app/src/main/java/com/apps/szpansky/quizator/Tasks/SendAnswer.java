package com.apps.szpansky.quizator.Tasks;


import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.apps.szpansky.quizator.Constant;
import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class SendAnswer extends BasicTask {


    private final String update_game_url;
    private String questionResult;

    public SendAnswer(String cookie, String userId, String userAnswer, FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        update_game_url = Constant.siteURL + Constant.siteApiUser  + "send_answer/?cookie=" + cookie + "&user_id=" + userId + "&user_answer=" + userAnswer;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(update_game_url);
            Request request = builder.url(url).build();
            Response respond = client.newCall(request).execute();

            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);
                if (object.getString("status").equals("ok")) {
                    questionResult = (object.getString("informacja"));
                } else {
                    setError(getContext().getString(R.string.error_when_downloading));
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setError(getContext().getString(R.string.error_when_downloading));
                return false;
            }
            respond.body().close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            setError(getContext().getString(R.string.connection_error));
            return false;
        }
    }


    @Override
    protected void onSuccessExecute() {
        Information information = Information.newInstance(questionResult);
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }


}
