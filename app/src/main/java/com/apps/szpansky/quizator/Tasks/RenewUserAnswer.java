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


public class RenewUserAnswer extends BasicTask {

    private final String renewUserAnswerURL;


    public RenewUserAnswer(String cookie, String userId, FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        renewUserAnswerURL = Constant.siteURL + Constant.siteApiUser  + "set_user_can_answer/?cookie=" + cookie + "&user_id=" + userId;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        URL url;
        try {
            url = new URL(renewUserAnswerURL);
            Request request = builder.url(url).build();
            Response respond;
            respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);

                if (object.getString("status").equals("ok")) {
                    return true;
                } else {
                    setError(getContext().getString(R.string.info_incorrect_account));
                    return false;
                }
            } catch (JSONException e) {
                setError(getContext().getString(R.string.error_when_downloading));
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            setError(getContext().getString(R.string.connection_error));
            e.printStackTrace();
            return false;
        }
    }


    @Override
    protected void onSuccessExecute() {
        Information information = Information.newInstance(getContext().getString(R.string.info_now_you_can_answer));
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }
}