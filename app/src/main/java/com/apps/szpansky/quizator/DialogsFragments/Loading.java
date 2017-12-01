package com.apps.szpansky.quizator.DialogsFragments;

import android.support.v4.app.DialogFragment;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.szpansky.quizator.R;


public class Loading extends DialogFragment {

    Activity activity;
    FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // method for sdk 16 block rotation while loading

        activity = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();
    }


    public static Loading newInstance() {
        Loading loading = new Loading();

        loading.setStyle(STYLE_NO_TITLE, R.style.LoadingDialog);
        loading.setCancelable(false);

        return loading;
    }

    @Override
    public void onDestroy() {
        onDismiss(getDialog());
        super.onDestroy();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Loading loading = (Loading) fragmentManager.findFragmentByTag("Loading");

            if (loading != null) {
                if (!loading.isVisible()) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);     //activity is fild in that class for prevent null point exception
                }
            }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, container, false);


        return view;
    }
}

