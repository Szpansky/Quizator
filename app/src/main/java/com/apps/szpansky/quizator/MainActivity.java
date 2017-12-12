package com.apps.szpansky.quizator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.apps.szpansky.quizator.DialogsFragments.Information;
import com.apps.szpansky.quizator.Fragments.LoadAdFragment;
import com.apps.szpansky.quizator.Fragments.AppBarFragment;
import com.apps.szpansky.quizator.Fragments.UserProfileFragment;
import com.apps.szpansky.quizator.SimpleData.QuestionData;
import com.apps.szpansky.quizator.SimpleData.UserData;
import com.apps.szpansky.quizator.Tasks.GetQuestion;
import com.apps.szpansky.quizator.Tasks.RefreshUserData;
import com.apps.szpansky.quizator.Tools.MySharedPreferences;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GetQuestion getQuestion = null;
    RefreshUserData refreshUserData;    //do not initialize that asynctask with null, to prevent runtime exception

    UserData userData;
    QuestionData questionData;
    Toolbar toolbar;
    DrawerLayout drawer;

    UserProfileFragment userProfileFragment;
    AppBarFragment appBarFragment;
    LoadAdFragment loadAdFragment;

    CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onPause() {
        super.onPause();
        if (refreshUserData != null) refreshUserData.cancel(true);
        if (getQuestion != null) getQuestion.cancel(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBundle();
        setViews();
        setToolbar();

        userProfileFragment = UserProfileFragment.newInstance(userData);
        appBarFragment = AppBarFragment.newInstance(userData);
        loadAdFragment = LoadAdFragment.newInstance();

        getSupportFragmentManager().beginTransaction().add(R.id.content_main, userProfileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content_app_bar, appBarFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.ad_frame, loadAdFragment).commit();


        if (!MySharedPreferences.getMainTutorialWasShown(this)) {
            MySharedPreferences.setMainTutorialWasShown(this, true);
            showTutorial();
        }
    }


    private void showTutorial() {
        Information information = Information.newInstance(getString(R.string.about_app));
        getSupportFragmentManager().beginTransaction().add(information, "Information").commit();
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
    }


    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    }


    private void startQuestion() {
        questionData = new QuestionData();
        getQuestion = new GetQuestion(getString(R.string.site_address), userData, questionData, getSupportFragmentManager(), getBaseContext());
        getQuestion.execute((Void) null);
    }


    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));
        }
    }

    /**
     * Method override for simply change title of activity
     *
     * @param title the new title
     */
    @Override
    public void setTitle(CharSequence title) {
        collapsingToolbarLayout.setTitle(title);
    }


    /**
     * That method is for refresh user data
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        refreshUserData = new RefreshUserData(getString(R.string.site_address), userData.getCookie(), userData.getUserId(), userData, getSupportFragmentManager());
        refreshUserData.execute();
    }


    /**
     * Method first close drawer, when drawer is closed it run super method
     */
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

            Information information = Information.newInstance(getString(R.string.about_app));
            getSupportFragmentManager().beginTransaction().add(information, "Information").commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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
            case R.id.nav_rate_app: {
                rateApp();
            }
            break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
