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


public class RenewUserAnswer extends BasicTask {

    private final String renewUserAnswerURL;


    public RenewUserAnswer(String siteAddress, String cookie, String userId, FragmentManager fragmentManager) {
        setFragmentManager(fragmentManager);
        renewUserAnswerURL = siteAddress + "cyj@n3k/user/set_user_can_answer/?insecure=cool&cookie=" + cookie + "&user_id=" + userId;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        URL url;
        try {
            url = new URL(renewUserAnswerURL);
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
                } else{
                    setError("Błędne konto");
                    return false;
                }
            } catch (JSONException e) {
                setError("Błąd pobierania danych");
                e.printStackTrace();
            }
        } catch (IOException e) {
            setError("Brak połączenia");
            e.printStackTrace();
        }
        return true;
    }


    @Override
    protected void onSuccessExecute() {
        Information information = Information.newInstance("Teraz znów możesz odpowiadać");
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }
}


















