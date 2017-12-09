package com.apps.szpansky.quizator.Tasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;


public abstract class BasicTask extends AsyncTask<Void, Void, Boolean> {

    private FragmentManager fragmentManager;
    private String error = "";


    protected FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    protected void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    protected String getError() {
        return error;
    }

    protected void setError(String error) {
        this.error = error;
    }


    protected void showProgress(final boolean show) {
        if (show) {
            Loading loading = Loading.newInstance();
            if (getFragmentManager().findFragmentByTag("Loading") == null)
                getFragmentManager().beginTransaction().add(loading, "Loading").commit();
        } else {
            Loading loading = (Loading) getFragmentManager().findFragmentByTag("Loading");
            if (loading != null && loading.isVisible()) loading.dismiss();
        }

    }

    protected abstract void onSuccessExecute();


    @Override
    protected void onPreExecute() {
        showProgress(true);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            showProgress(false);
            onSuccessExecute();
        } else {
            Information information = Information.newInstance("Błąd:\n" + getError());
            getFragmentManager().beginTransaction().add(information, "Information").commit();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                }
            }, 300);

        }
    }

}
