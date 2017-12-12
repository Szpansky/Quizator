package com.apps.szpansky.quizator.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.Fragments.UserProfileFragment;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


/**
 * Class for refresh user data. It don't extend BasicTask, because it do not need loading screen dialog.
 * When it extend BasicTask it may occur runtime exception
 */
public class RefreshUserData extends AsyncTask<Void, Void, Boolean> {


    private final String sendLoginURL;
    private UserData userData;
    private UserProfileFragment userProfileFragment;
    private FragmentManager fragmentManager;
    private String error = "";

    public RefreshUserData(String siteAddress, String cookie, String userID, UserData userData, FragmentManager fragmentManager) {
        sendLoginURL = siteAddress + "cyj@n3k/user/validate_auth_cookie/?insecure=cool&cookie=" + cookie + "&user_id=" + userID;
        this.userData = userData;
        this.fragmentManager = fragmentManager;
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
                setError("Problem podczas odświerzania danych");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setError("Brak połączenia");
            return false;
        }

    }


    private FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    private String getError() {
        return error;
    }

    protected void setError(String error) {
        this.error = error;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (!isCancelled() && getFragmentManager() != null && !getFragmentManager().isDestroyed()) {
            if (aBoolean) {
                if (getFragmentManager() != null && !getFragmentManager().isDestroyed()) {
                    userProfileFragment = UserProfileFragment.newInstance(userData);
                    getFragmentManager().beginTransaction().replace(R.id.content_main, userProfileFragment).commit();
                }
            } else {
                Information information = Information.newInstance("Błąd:\n" + getError());
                getFragmentManager().beginTransaction().add(information, "Information").commit();
            }
        }
    }
}
