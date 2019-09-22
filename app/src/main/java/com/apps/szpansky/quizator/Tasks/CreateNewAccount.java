package com.apps.szpansky.quizator.Tasks;

import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.apps.szpansky.quizator.Constant;
import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.Tools.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


public class CreateNewAccount extends BasicTask {

    public boolean WAS_CREATED = false;

    private String mEmail,
            mPassword,
            mUsername,
            mDisplayName,
            siteAddress,
            nonceURL;

    public CreateNewAccount(String email, String password, String username, String displayName, FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        mEmail = email;
        mPassword = password;
        mUsername = username;

        if (displayName.isEmpty())
            mDisplayName = username;
        else mDisplayName = displayName;


        this.nonceURL = Constant.siteURL + Constant.siteApiUser  + "get_nonce/?controller=user&method=register";
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL(nonceURL);
            Request request = builder.url(url).build();
            Response respond = client.newCall(request).execute();
            String json = respond.body().string();
            try {
                JSONObject object = new JSONObject(json);

                if (object.getString("status").equals("ok")) {
                    String nonceValue = object.getString("nonce");
                    String registerURL = Constant.siteURL + Constant.siteApiUser  +  "register/?notify=no&username=" + mUsername + "&email=" + mEmail + "&nonce=" + nonceValue + "&display_name=" + mDisplayName + "&user_pass=" + mPassword;

                    url = new URL(registerURL);
                  //  client = new OkHttpClient();
                   // builder = new Request.Builder();
                    request = builder.url(url).build();
                    respond = client.newCall(request).execute();
                    String json2 = respond.body().string();

                    JSONObject object2 = new JSONObject(json2);
                    if (object2.getString("status").equals("error")) {
                        setError(object2.getString("error"));
                        WAS_CREATED = false;
                        return false;
                    }
                    WAS_CREATED = true;
                    return true;
                } else {
                    setError(object.getString("error"));
                    WAS_CREATED = false;
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setError(getContext().getString(R.string.error_when_downloading));
                WAS_CREATED = false;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setError(getContext().getString(R.string.connection_error));
            WAS_CREATED = false;
            return false;
        }
    }


    @Override
    protected void onSuccessExecute() {
        Information information = Information.newInstance(getContext().getString(R.string.account_created_info));
        getFragmentManager().beginTransaction().add(information, "Information").commit();
        if (getContext() != null) {
            MySharedPreferences.setLogin(getContext(), mUsername);
            MySharedPreferences.setPassword(getContext(), mPassword);
        }

    }
}
