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


public class RetrievePassword extends BasicTask {

    private final String sendRetrievePasswordURL;

    public RetrievePassword(String email, FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        sendRetrievePasswordURL = Constant.siteURL + Constant.siteApiUser  + "retrieve_password/?user_login=" + email;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        URL url;
        try {
            url = new URL(sendRetrievePasswordURL);
            Request request = builder.url(url).build();
            Response respond;
            respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);

                if (object.getString("status").equals("ok")) {
                    return true;
                } else {
                    setError(getContext().getString(R.string.info_account_doesnt_exist));
                    return false;
                }
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
        Information information = Information.newInstance(getContext().getString(R.string.info_password_was_send));
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }
}
