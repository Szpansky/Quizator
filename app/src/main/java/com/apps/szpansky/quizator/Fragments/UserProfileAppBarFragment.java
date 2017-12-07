package com.apps.szpansky.quizator.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserData;


public class UserProfileAppBarFragment extends Fragment {

    UserData userData;

    Button addQuestionButton;

    public static UserProfileAppBarFragment newInstance(UserData userData) {
        UserProfileAppBarFragment userProfileAppBarFragment = new UserProfileAppBarFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("userData", userData);
        userProfileAppBarFragment.setArguments(bundle);

        return userProfileAppBarFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = (UserData) getArguments().getSerializable("userData");

        View view = inflater.inflate(R.layout.user_info_app_bar, container, false);

        addQuestionButton = view.findViewById(R.id.add_question_button);

        onButtonClick();

        return view;
    }


private void onButtonClick(){
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(),"Dostępne w krótce",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
}


}
