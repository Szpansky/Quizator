package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentManager;

import com.apps.szpansky.quizator.Constant;
import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.Fragments.UserProfileFragment;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


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
    private final WeakReference<Context> context;
    OkHttpClient client;
    Request.Builder builder;

    public RefreshUserData(UserData userData, FragmentManager fragmentManager, Context context) {
        sendLoginURL = Constant.siteURL + Constant.siteApiUser  + "validate_auth_cookie/?cookie=" + userData.getCookie() + "&user_id=" + userData.getUserId();
        this.userData = userData;
        this.fragmentManager = fragmentManager;
        this.context = new WeakReference<>(context);
        client = new OkHttpClient();
        builder = new Request.Builder();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(sendLoginURL);
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
                setError(context.get().getString(R.string.error_when_downloading));
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setError(context.get().getString(R.string.connection_error));
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
                Information information = Information.newInstance(context.get().getString(R.string.error) + "\n" + getError());
                getFragmentManager().beginTransaction().add(information, "Information").commit();
            }
        }
    }
}
