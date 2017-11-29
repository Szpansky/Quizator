package com.apps.szpansky.quizator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.Fragments.UserProfileFragment;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int RESULT_FROM_QUESTION = 1;

    UserData userData;
    TextView textUserPoints;
    Toolbar toolbar;
    ImageView thumbNail;
    DrawerLayout drawer;
    TextView textViewNavEmail;

    UserProfileFragment userProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        setToolbar();
        getBundle();
        setUserData();

        userProfileFragment = UserProfileFragment.newInstance(userData);

        getSupportFragmentManager().beginTransaction().add(R.id.content_main,  userProfileFragment).commit();
    }


    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userData = (UserData) bundle.getSerializable("userData");
        } else {
            finish();
        }
    }


    private void setViews() {
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.getHeaderView(0);
        thumbNail = navView.findViewById(R.id.thumbNail);
        textViewNavEmail = navView.findViewById(R.id.textViewNavEmail);
    }


    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    private void setUserData() {
        Glide.with(this).load(userData.getUserAvatar()).into(thumbNail);
        textViewNavEmail.setText(userData.getEmail());
    }


    private void startQuestion() {
        Intent startQuestion = new Intent(getBaseContext(), GetQuestion.class);
        startQuestion.putExtra("userData", userData);
        startActivityForResult(startQuestion, RESULT_FROM_QUESTION);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
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
                        userData.setUserPoints(result.getString("userPoints"));
                        userProfileFragment.userData.setUserPoints(result.getString("userPoints"));
                    }
                    userProfileFragment.setUserData();
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

            Information information = Information.newInstance("Informacje o aplikacji");
            getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

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
