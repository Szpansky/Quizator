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
import java.util.ArrayList;

public class GetTopUsers extends BasicTask {

    String getTopTenURL;
    ArrayList<UserRank> topTen = new ArrayList<>();



    class UserRank {
        String userName;
        String userPoints;

        @Override
        public String toString() {
            return userName +
                    "\t Pkt= " + userPoints + "\n";
        }
    }


    public GetTopUsers(String siteAddress, FragmentManager fragmentManager) {
        super(fragmentManager);
        getTopTenURL = siteAddress + "JSON/user/get_top_ten/?insecure=cool";
    }


    @Override
    protected void onSuccessExecute() {
        String ranks = "";
        for (UserRank userRank :
                topTen) {
            ranks = ranks.concat(userRank.toString());
        }

        Information information = Information.newInstance(ranks);
        getFragmentManager().beginTransaction().add(information, "Information").commit();

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        URL url;
        try {
            url = new URL(getTopTenURL);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();
            Response respond;
            respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);


                if (object.getString("status").equals("ok")) {


                    for (int i = 0; i < object.getJSONArray("array").length(); i++) {
                        UserRank userRank = new UserRank();
                        userRank.userName = object.getJSONArray("array").getJSONObject(i).get("user_nicename").toString();
                        userRank.userPoints = object.getJSONArray("array").getJSONObject(i).get("meta_value").toString();
                        topTen.add(userRank);
                    }

                    return true;
                } else {
                    setError("Błędne konto");
                    return false;
                }
            } catch (JSONException e) {
                setError("Błąd pobierania danych");
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            setError("Brak połączenia");
            e.printStackTrace();
            return false;
        }
    }
}
