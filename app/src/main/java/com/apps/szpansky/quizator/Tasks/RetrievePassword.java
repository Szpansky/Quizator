package com.apps.szpansky.quizator.Tasks;

import android.support.v4.app.FragmentManager;

import android.os.AsyncTask;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;

import com.apps.szpansky.quizator.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class RetrievePassword extends AsyncTask<Void, Void, Boolean> {

    private final String sendRetrievePasswordURL;

    private FragmentManager fragmentManager;

    public RetrievePassword(String siteAddress, String email, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        sendRetrievePasswordURL = siteAddress + "cyj@n3k/user/retrieve_password/?insecure=cool&user_login=" + email;
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
    protected Boolean doInBackground(Void... voids) {
        URL url;
        try {
            url = new URL(sendRetrievePasswordURL);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();
            Response respond;
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
    protected void onPostExecute(Boolean aBoolean) {
        showProgress(false);
        if (aBoolean) {
            Information information = Information.newInstance("Twoje hasło zostało wysłane na podany adres email");
            fragmentManager.beginTransaction().add(information, "Information").commit();
        } else {
            Information information = Information.newInstance("Konto nie isnieje");
            fragmentManager.beginTransaction().add(information, "Information").commit();
        }

    }

}


















