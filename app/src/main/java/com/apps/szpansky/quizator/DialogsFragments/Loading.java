package com.apps.szpansky.quizator.DialogsFragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.szpansky.quizator.R;


public class Loading extends DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static Loading newInstance() {
        Loading loading = new Loading();

        loading.setStyle(STYLE_NO_TITLE, R.style.LoadingDialog);
        loading.setCancelable(false);

        return loading;
    }


    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, container, false);


        return view;
    }
}

