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

public class SendAnswer extends BasicTask {


    private final String update_game_url;
    String questionResult;

    protected SendAnswer(String siteAddress, String cookie, String userId, String userAnswer, FragmentManager fragmentManager) {
        update_game_url = siteAddress + "cyj@n3k/user/send_answer/?insecure=cool&cookie=" + cookie + "&user_id=" + userId + "&user_answer=" + userAnswer;
        setFragmentManager(fragmentManager);
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(update_game_url);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();
            Response respond = client.newCall(request).execute();

            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);
                if (object.getString("status").equals("ok")) {
                    questionResult = (object.getString("informacja"));
                } else {
                    setError("Błąd odczytu danych");
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setError("Błąd pobierania danych");
                return false;
            }
            respond.body().close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            setError("Brak połączenia");
            return false;
        }
    }


    @Override
    protected void onSuccessExecute() {
        Information information = Information.newInstance("Zaktualizowano punkty\n" + questionResult);
        getFragmentManager().beginTransaction().add(information, "Information").commit();
    }


}
