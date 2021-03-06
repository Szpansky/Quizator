package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.apps.szpansky.quizator.Constant;
import com.apps.szpansky.quizator.Fragments.RanksFragment;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserDataInRank;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


public class GetTopUsers extends BasicTask {

    private String getTopTenURL;
    private ArrayList<UserDataInRank> topTen = new ArrayList<>();


    public GetTopUsers(FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        getTopTenURL = Constant.siteURL + Constant.siteApiUser  + "get_top_ten/";
    }


    @Override
    protected void onSuccessExecute() {
        RanksFragment ranksFragment;
        ranksFragment = (RanksFragment) getFragmentManager().findFragmentById(getFragmentManager().findFragmentById(R.id.content_main).getId());
        ranksFragment.setList(topTen);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        URL url;
        try {
            url = new URL(getTopTenURL);
            Request request = builder.url(url).build();
            Response respond;
            respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);

                if (object.getString("status").equals("ok")) {

                    for (int i = 0; i < object.getJSONArray("array").length(); i++) {
                        int userPosition = i +1;
                        UserDataInRank userDataInRank = new UserDataInRank();
                        userDataInRank.setUserName(object.getJSONArray("array").getJSONObject(i).get("display_name").toString());
                        userDataInRank.setUserPoints(object.getJSONArray("array").getJSONObject(i).get("meta_value").toString());
                        userDataInRank.setUserPosition(Integer.toString(userPosition));
                        topTen.add(userDataInRank);
                    }
                    return true;
                } else {
                    setError(getContext().getString(R.string.error_when_downloading));
                    return false;
                }
            } catch (JSONException e) {
                setError(getContext().getString(R.string.error_when_downloading));
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            setError(getContext().getString(R.string.connection_error));
            e.printStackTrace();
            return false;
        }
    }
}
