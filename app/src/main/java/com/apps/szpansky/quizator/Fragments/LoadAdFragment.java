package com.apps.szpansky.quizator.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.szpansky.quizator.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class LoadAdFragment extends Fragment {

    AdView adView;

    public static LoadAdFragment newInstance() {

        return new LoadAdFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adView = new AdView(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad, container, false);

        adView = view.findViewById(R.id.adView);
        setAds();

        return view;
    }


    private void setAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
