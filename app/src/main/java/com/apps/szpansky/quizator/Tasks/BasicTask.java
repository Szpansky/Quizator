package com.apps.szpansky.quizator.Tasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.DialogsFragments.Loading;


public abstract class BasicTask extends AsyncTask<Void, Void, Boolean> {

    private FragmentManager fragmentManager;
    private String error = "";

    public BasicTask(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    protected FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    protected String getError() {
        return error;
    }

    protected void setError(String error) {
        this.error = error;
    }


    protected void showProgress(final boolean show) {
        if (getFragmentManager() != null) {
            if (show) {
                Loading loading = Loading.newInstance();
                if (getFragmentManager().findFragmentByTag("Loading") == null)
                    getFragmentManager().beginTransaction().add(loading, "Loading").commit();
            } else {
                Loading loading = (Loading) getFragmentManager().findFragmentByTag("Loading");
                if (loading != null && loading.isVisible()) loading.dismiss();
            }
        }
    }

    protected abstract void onSuccessExecute();

    @Override
    protected void onPreExecute() {
        showProgress(true);
    }

    /**
     * That function is run after asynctask is done,
     * It got some errors checker, like check that task was canceled,
     * getFragment is null or destroyed.
     * On success that class dismiss loading dialog and run abstract method onSuccessExecute otherwise
     * it show dialog with information and dismiss loading.
     * Dismiss after unsuccessfully Loading is delayed to prevent double loading
     * @param aBoolean that param show that async task were completed
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(!isCancelled() && getFragmentManager() != null && !getFragmentManager().isDestroyed()){
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

}
