package com.apps.szpansky.quizator.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.SimpleData.UserDataInRank;

import java.util.ArrayList;


public class AdapterUsersInRanks extends ArrayAdapter<UserDataInRank> {

    public AdapterUsersInRanks(@NonNull Context context, @NonNull ArrayList<UserDataInRank> userDataInRanks) {
        super(context, 0, userDataInRanks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @Nullable ViewGroup parent) {
        UserDataInRank userDataInRank = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rank_user_view, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.user_name);
        TextView userPoints = convertView.findViewById(R.id.user_points);
        TextView userPosition = convertView.findViewById(R.id.user_position);

        userName.setText(userDataInRank.getUserName());
        userPoints.setText(userDataInRank.getUserPoints());
        userPosition.setText(userDataInRank.getUserPosition());
        UserData userData = new UserData();
        System.out.println(userData.getDisplayName());
        if (userData.getDisplayName().equals(userDataInRank.getUserName())) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
        }

        return convertView;
    }
}
