package com.apps.szpansky.quizator.Tasks;

import android.support.v4.app.FragmentManager;

import android.os.AsyncTask;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Marcin on 2017-11-29.
 */

public class RetrievePassword extends AsyncTask {

    private final String sendRetrievePasswordURL;

    private final String mEmail;

    FragmentManager fragmentManager;

    public RetrievePassword(String email, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        mEmail = email;
        sendRetrievePasswordURL = "http://lukasz3.eradon.pl/g5/cyj@n3k/user/retrieve_password/?insecure=cool&user_login=" + email;
    }


    private void showProgress(final boolean show) {
        if (show){
            Loading loading = Loading.newInstance();
            if (fragmentManager.findFragmentByTag("Loading") == null)
                fragmentManager.beginTransaction().add(loading, "Loading").commit();
        }else {
            Loading loading = (Loading) fragmentManager.findFragmentByTag("Loading");
            if (loading != null && loading.isVisible()) loading.dismiss();
        }

    }


    @Override
    protected Object doInBackground(Object[] objects) {
        URL url;
        try {
            url = new URL(sendRetrievePasswordURL);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();
            Response respond = null;
            respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);

                if (object.getString("status").equals("ok")) {
                    return true;
                } else return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    protected void onPostExecute(Object o) {
        showProgress(false);
        if (o.equals(true)) {
            Information information = Information.newInstance("Twoje hasło zostało wysłane na podany adres email");
            fragmentManager.beginTransaction().add(information, "Information").commit();
        } else {
            Information information = Information.newInstance("Konto nie isnieje");
            fragmentManager.beginTransaction().add(information, "Information").commit();
        }

    }

}


















