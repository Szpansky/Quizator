package com.apps.szpansky.quizator.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.szpansky.quizator.MainActivity;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.Tasks.GetQuestion;
import com.apps.szpansky.quizator.Tasks.RenewUserAnswer;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class UserProfileFragment extends Fragment {

    GetQuestion getQuestion = null;
    RenewUserAnswer renewUserAnswer = null;

    UserData userData;
    QuestionData questionData;

    ImageView userAvatar;
    Button skipLockButton, getQuestionButton;
    ProgressBar progressLvlLoading;
    TextView progressLvlText, userCurrentRank, userNextRank, userPreviousRank, rankPointsNext, userUserName;


    public static UserProfileFragment newInstance(UserData userData) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("userData", userData);
        userProfileFragment.setArguments(bundle);

        return userProfileFragment;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getQuestion != null) getQuestion.cancel(true);
        if (renewUserAnswer != null) renewUserAnswer.cancel(true);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = (UserData) getArguments().getSerializable("userData");

        View view = inflater.inflate(R.layout.fragment_user_info_main, container, false);

        userAvatar = view.findViewById(R.id.user_avatar);
        userUserName = view.findViewById(R.id.user_username);
        userCurrentRank = view.findViewById(R.id.user_current_rank);
        userPreviousRank = view.findViewById(R.id.user_previous_rank);
        userNextRank = view.findViewById(R.id.user_next_rank);
        rankPointsNext = view.findViewById(R.id.rank_points_next);
        skipLockButton = view.findViewById(R.id.skip_lock_button);
        getQuestionButton = view.findViewById(R.id.get_question_button);

        progressLvlLoading = view.findViewById(R.id.progress_lvl);
        progressLvlText = view.findViewById(R.id.progress_txt);

        setUserData();
        onButtonClick();

        return view;
    }


    private void startQuestion() {
        questionData = new QuestionData();
        getQuestion = new GetQuestion(getString(R.string.site_address), userData, questionData, getActivity().getSupportFragmentManager(), getActivity().getBaseContext());
        getQuestion.execute((Void) null);
    }


    private void onButtonClick() {
        getQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuestion();
            }
        });

        skipLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renewUserAnswer = new RenewUserAnswer(getString(R.string.site_address), userData.getCookie(), userData.getUserId(), getFragmentManager());
                renewUserAnswer.execute();
            }
        });

    }


    private void setTextSize(String textSize, TextView textField) {
        if (textSize.length() > 5) {
            textField.setTextSize(14);
            if (textSize.length() > 8) {
                textField.setTextSize(12);
            }
        } else {
            textField.setTextSize(18);
        }
    }


    public void setUserData() {
        Glide.with(this).load(userData.getUserAvatar()).into(userAvatar);
        setTextSize(userData.getRankPrev(), userPreviousRank);
        userCurrentRank.setText(userData.getRankName());
        setTextSize(userData.getRankNext(), userNextRank);
        userNextRank.setText(userData.getRankNext());
        userPreviousRank.setText(userData.getRankPrev());
        String nextRankInfo = getString(R.string.need) + "\n" + userData.getUserPointsNext() + " " + getString(R.string.points_shortcut);
        rankPointsNext.setText(nextRankInfo);
        userUserName.setText(userData.getUsername());

        Integer userPointsInt;
        Integer userPointsNextInt;
        Integer userPointsCurrentRankInt;

        try {
            userPointsInt = Integer.parseInt(userData.getUserPoints());
            userPointsNextInt = Integer.parseInt(userData.getUserPointsNext());
            userPointsCurrentRankInt = Integer.parseInt(userData.getPointsCurrentRank());
        } catch (NumberFormatException e) {
            userPointsInt = 1;
            userPointsNextInt = 1;
            userPointsCurrentRankInt = 0;
        }

        Integer userRating = (((userPointsInt - userPointsCurrentRankInt) * 100) / (userPointsNextInt - userPointsCurrentRankInt));

        if (userRating >= 100) userRating = 100;
        if (userRating <= 0) userRating = 0;

        progressLvlLoading.setProgress(userRating);

        String progress = userRating.toString() + "%";
        progressLvlText.setText(progress);
        getActivity().setTitle("Masz: " + userData.getUserPoints() + " pkt");
    }

}
