package com.apps.szpansky.quizator.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.szpansky.quizator.DialogsFragments.Loading;
import com.apps.szpansky.quizator.ShowQuestionActivity;
import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.Tasks.GetQuestion;
import com.apps.szpansky.quizator.Tasks.RenewUserAnswer;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class UserProfileFragment extends Fragment implements RewardedVideoAdListener {

    private static boolean REWARDED = false;

    private final int RESULT_FROM_QUESTION = 1;

    UserData userData;
    QuestionData questionData;

    ImageView userAvatar;
    Button skipLockButton, getQuestionButton;
    ProgressBar progressLvlLoading;
    TextView progressLvlText, userCurrentRank, userNextRank, userPreviousRank, rankPointsNext, userUserName;
    private RewardedVideoAd mAd;
    RenewUserAnswer renewUserAnswer;
    GetQuestion getQuestion;


    public static UserProfileFragment newInstance(UserData userData) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("userData", userData);
        userProfileFragment.setArguments(bundle);

        return userProfileFragment;
    }


    private void setRewardedVideo() {
        mAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mAd.setRewardedVideoAdListener(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = (UserData) getArguments().getSerializable("userData");

        View view = inflater.inflate(R.layout.user_info_main, container, false);


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
        //  adView = view.findViewById(R.id.adView);


        setUserData();
        setAds();
        setRewardedVideo();
        onButtonClick();


        return view;
    }


    private void showProgress(final boolean show) {
        if (show) {
            Loading loading = Loading.newInstance();
            if (getActivity().getSupportFragmentManager().findFragmentByTag("Loading") == null)
                getActivity().getSupportFragmentManager().beginTransaction().add(loading, "Loading").commit();
        } else {
            Loading loading = (Loading) getActivity().getSupportFragmentManager().findFragmentByTag("Loading");
            if (loading != null && loading.isVisible()) loading.dismiss();
            loading = null;
        }

    }


    @SuppressLint("StaticFieldLeak")
    private void startQuestion() {
        questionData = new QuestionData();

        getQuestion = new GetQuestion(getString(R.string.site_address), userData, questionData, getActivity().getSupportFragmentManager()) {
            @Override
            public void onSuccessExecute() {
                System.out.println(questionData.getText());
                Intent startQuestion = new Intent(getActivity().getBaseContext(), ShowQuestionActivity.class);
                startQuestion.putExtra("questionData", questionData);
                startQuestion.putExtra("userData", userData);
                startActivity(startQuestion);
            }
        };
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
            public void onClick(View view) {
                showProgress(true);
                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAd.loadAd(getResources().getString(R.string.ads_reward_main_id), new AdRequest.Builder().build());
                    }
                }, 300);
                System.out.println("test");
            }
        });
    }


    private void setAds() {
        //   AdRequest adRequest = new AdRequest.Builder().build();
        //  adView.loadAd(adRequest);
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
    }


    @Override
    public void onRewardedVideoAdLoaded() {
        REWARDED = false;
        mAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        showProgress(false);
    }

    @Override
    public void onRewardedVideoStarted() {
        showProgress(false);
    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (REWARDED) {
            renewUserAnswer = new RenewUserAnswer(getString(R.string.site_address), userData.getCookie(), userData.getUserId(), getActivity().getSupportFragmentManager());
            renewUserAnswer.execute();
        } else {
            showProgress(false);
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        REWARDED = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        showProgress(false);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        showProgress(false);
        Toast.makeText(getActivity(), R.string.ad_failed_to_load, Toast.LENGTH_SHORT).show();
    }
}
