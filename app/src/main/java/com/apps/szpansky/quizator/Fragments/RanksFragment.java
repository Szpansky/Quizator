package com.apps.szpansky.quizator.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.apps.szpansky.quizator.R;
import com.apps.szpansky.quizator.SimpleData.UserDataInRank;
import com.apps.szpansky.quizator.Tasks.GetTopUsers;
import com.apps.szpansky.quizator.Tools.AdapterUsersInRanks;

import java.util.ArrayList;


public class RanksFragment extends Fragment {

    GetTopUsers getTopUsers = null;
    ListView rankList;
    AdapterUsersInRanks adapter;


    public static RanksFragment newInstance() {
        RanksFragment ranksFragment = new RanksFragment();

        return ranksFragment;
    }


    public void setList(ArrayList<UserDataInRank> topTen){
        adapter = new AdapterUsersInRanks(getContext(),topTen);
        rankList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(rankList);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i <listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getTopUsers != null) {
            getTopUsers.cancel(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ranks, container, false);
        rankList = view.findViewById(R.id.rank_list);


        getTopUsers = new GetTopUsers(getFragmentManager(), getContext());
        getTopUsers.execute((Void) null);

        return view;
    }

}
