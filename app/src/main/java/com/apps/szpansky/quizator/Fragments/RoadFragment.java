package com.apps.szpansky.quizator.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserData;


public class RoadFragment extends Fragment {

    UserData userData;
    ImageView loading1, loading2, loading3, loading4, loading5, loading6, loading7, loading8, loading9;

    public static RoadFragment newInstance(UserData userData) {
        RoadFragment roadFragment = new RoadFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("userData", userData);
        roadFragment.setArguments(bundle);

        return roadFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = (UserData) getArguments().getSerializable("userData");

        View view = inflater.inflate(R.layout.framgent_road, container, false);

        loading1 = view.findViewById(R.id.loading_1);
        loading2 = view.findViewById(R.id.loading_2);
        loading3 = view.findViewById(R.id.loading_3);
        loading4 = view.findViewById(R.id.loading_4);
        loading5 = view.findViewById(R.id.loading_5);
        loading6 = view.findViewById(R.id.loading_6);
        loading7 = view.findViewById(R.id.loading_7);
        loading8 = view.findViewById(R.id.loading_8);
        loading9 = view.findViewById(R.id.loading_9);

        setUserProgressRoad(userData.getRankName());
        return view;
    }


    private void setUserProgressRoad(String userRank) {

        loading1.setVisibility(View.INVISIBLE);
        loading2.setVisibility(View.INVISIBLE);
        loading3.setVisibility(View.INVISIBLE);
        loading4.setVisibility(View.INVISIBLE);
        loading5.setVisibility(View.INVISIBLE);
        loading6.setVisibility(View.INVISIBLE);
        loading7.setVisibility(View.INVISIBLE);
        loading8.setVisibility(View.INVISIBLE);
        loading9.setVisibility(View.INVISIBLE);

        switch (userRank.toLowerCase()) {
            case "kret": {
                loading1.setVisibility(View.VISIBLE);
                break;
            }
            case "żmija": {
                loading2.setVisibility(View.VISIBLE);
                break;
            }
            case "busiarz": {
                loading3.setVisibility(View.VISIBLE);
                break;
            }
            case "buldożer": {
                loading4.setVisibility(View.VISIBLE);
                break;
            }
            case "betoniarka": {
                loading5.setVisibility(View.VISIBLE);
                break;
            }
            case "dzwig": {
                loading6.setVisibility(View.VISIBLE);
                break;
            }
            case "programista": {
                loading7.setVisibility(View.VISIBLE);
                break;
            }
            case "traktorzysta": {
                loading8.setVisibility(View.VISIBLE);
                break;
            }
            case "ksiądz": {
                loading9.setVisibility(View.VISIBLE);
                break;
            }
        }
    }
}