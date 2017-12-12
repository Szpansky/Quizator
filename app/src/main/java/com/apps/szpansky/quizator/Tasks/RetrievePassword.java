package com.apps.szpansky.quizator.Tasks;


import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class RetrievePassword extends BasicTask {

    private final String sendRetrievePasswordURL;

    public RetrievePassword(String siteAddress, String email, FragmentManager fragmentManager) {
        super(fragmentManager);
        sendRetrievePasswordURL = siteAddress + "cyj@n3k/user/retrieve_password/?insecure=cool&user_login=" + email;
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
                } else {
                    setError("Konto nie istnieje");
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setError("Błąd połączenia");
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
        Information information = Information.newInstance("Hasło zostało wysłane na podany adres");
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }
}


















