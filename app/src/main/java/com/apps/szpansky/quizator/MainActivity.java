package com.apps.szpansky.quizator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int RESULT_FROM_QUESTION = 1;

    private String cookie;
    private String userAvatar;
    private String userPoints;
    private String nickname;
    private String registered;
    private String email;
    private String nicename;
    private String username;
    private String userId;
    private String userPointsNext;
    private String rank_name;
    private String rank_next;
    TextView textUserPoints;
    TextView textUserNextPoints;
    TextView textUserLvl;
    TextView textUserNextLvl;
    TextView textUserName;
    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.getHeaderView(0);


        Bundle bundle = getIntent().getExtras();
        TextView textViewNavEmail = navView.findViewById(R.id.textViewNavEmail);
        textUserPoints = findViewById(R.id.userPoints);

        textUserLvl = findViewById(R.id.userLvl);
        textUserNextLvl = findViewById(R.id.userNextLvl);
        textUserName =  findViewById(R.id.userName);
        textUserNextPoints = findViewById(R.id.userNextPoints);

        ImageView thumbNail = navView.findViewById(R.id.thumbNail);
        ImageView imageView = findViewById(R.id.imageView);


        if (bundle != null) {
            cookie = bundle.getString("cookie");
            userId = bundle.getString("userId");
            username = bundle.getString("username");
            nicename = bundle.getString("nicename");
            email = bundle.getString("email");
            registered = bundle.getString("registered");
            nickname = bundle.getString("nickname");
            userPoints = bundle.getString("points");
            userPointsNext = bundle.getString("points_next");
            rank_name = bundle.getString("rank_name");
            rank_next = bundle.getString("rank_next");
            userAvatar = bundle.getString("userAvatar");
        }


        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestion();
            }
        });

        Glide.with(this).load(userAvatar).into(imageView);
        Glide.with(this).load(userAvatar).into(thumbNail);

        textViewNavEmail.setText(email);


        setUserLvl();

        setAds();

    }


    private void setAds(){
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private void setUserLvl() {

        textUserLvl.setText(rank_name);
        textUserNextLvl.setText(rank_next);
        textUserPoints.setText(userPoints);
        textUserName.setText(username);
        textUserNextPoints.setText(userPointsNext);

        Integer userPointsInt;
        Integer userPointsNextInt;

        try {
            userPointsInt = Integer.parseInt(userPoints);
            userPointsNextInt = Integer.parseInt(userPointsNext);
        } catch (NumberFormatException e) {
            userPointsInt = 1;
            userPointsNextInt = 1;
        }

        if (userPointsNextInt == 0) userPointsNextInt = 1;


        Integer userRating = ((userPointsInt * 100) / userPointsNextInt);

        if (userRating >= 100) userRating = 100;
        if (userRating <= 0) userRating = 0;

        ProgressBar progressBar = findViewById(R.id.progressBar3);
        progressBar.setProgress(userRating);
        progressBar.setScaleY(9f);

        TextView completeLvl = findViewById(R.id.progressText);
        String progress = userRating.toString() + "%";
        completeLvl.setText(progress);


    }


    private void startQuestion() {
        Intent startQuestion = new Intent(getBaseContext(), GetQuestion.class);
        startQuestion.putExtra("cookie", cookie);
        startQuestion.putExtra("userPoints", userPoints);
        startQuestion.putExtra("userId", userId);
        startActivityForResult(startQuestion, RESULT_FROM_QUESTION);
    }


    @Override
    protected void onStop() {
        super.onStop();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_FROM_QUESTION:
                if (resultCode == RESULT_OK) {
                    Bundle result = data.getExtras();
                    if (result != null) {
                        userPoints = result.getString("userPoints");
                    }
                    textUserPoints.setText(userPoints);
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Wtajemniczenie.pl");
            dialog.setMessage("Aplikacja stworzona z myślą by ułatwić zdobywanie punktów w serwisie wtajemniczenie.pl");
            dialog.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alert = dialog.create();
            alert.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_logout: {
                finish();
            }
            break;
            case R.id.nav_quest: {
                startQuestion();
            }
            break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
