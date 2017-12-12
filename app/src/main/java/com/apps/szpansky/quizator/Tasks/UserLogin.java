package com.apps.szpansky.quizator.Tasks;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.MainActivity;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class UserLogin extends BasicTask {

    private final String sendLoginURL;
    private UserData userData;
    private final WeakReference<Context> context;

    public UserLogin(String siteAddress, String email, String password, UserData userData, FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        sendLoginURL = siteAddress + "cyj@n3k/user/generate_auth_cookie/?insecure=cool&username=" + email + "&password=" + password;
        this.userData = userData;
        this.context= new WeakReference<> (context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(sendLoginURL);
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url).build();
            Response respond = client.newCall(request).execute();
            String json = respond.body().string();

            try {
                JSONObject object = new JSONObject(json);

                if (object.getString("status").equals("ok")) {
                    userData.setCookie(object.getString("cookie"));
                    userData.setUserId(object.getJSONObject("user").getString("id"));
                    userData.setUsername(object.getJSONObject("user").getString("username"));
                    userData.setNicename(object.getJSONObject("user").getString("nicename"));
                    userData.setEmail(object.getJSONObject("user").getString("email"));
                    userData.setRegistered(object.getJSONObject("user").getString("registered"));
                    userData.setNickname(object.getJSONObject("user").getString("nickname"));
                    userData.setUserPoints(object.getJSONObject("user").getString("points"));
                    userData.setUserPointsNext(object.getJSONObject("user").getString("points_next"));
                    userData.setPointsCurrentRank(object.getJSONObject("user").getString("points_current_rank"));
                    userData.setRankName(object.getJSONObject("user").getString("rank_name"));
                    userData.setRankPrev(object.getJSONObject("user").getString("rank_prev"));
                    userData.setRankNext(object.getJSONObject("user").getString("rank_next"));
                    userData.setUserAvatar(object.getJSONObject("user").getString("avatar"));
                    return true;
                } else {
                    setError(object.getString("error"));
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setError("Problem podczas pobierania danych");
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
        if (context.get()!= null) {
            Intent startMain = new Intent(context.get(), MainActivity.class);
            startMain.putExtra("userData", userData);
            startMain.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.get().startActivity(startMain);
        }
    }
}
