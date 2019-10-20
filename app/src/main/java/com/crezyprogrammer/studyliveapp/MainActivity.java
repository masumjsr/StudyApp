package com.crezyprogrammer.studyliveapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.crezyprogrammer.studyliveapp.fragment.HomeFragment;
import com.crezyprogrammer.studyliveapp.fragment.LiveFragment;
import com.crezyprogrammer.studylive.fragment.ProfileFragment;
import com.crezyprogrammer.studylive.fragment.TestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    final Fragment home = new HomeFragment();
    final Fragment live = new LiveFragment();
    final Fragment test = new TestFragment();
    final Fragment profile = new ProfileFragment();
    FragmentManager fm = getSupportFragmentManager();
    Fragment active = home;
    FirebaseUser user;
    TextView headerName, headerEmail;
    @BindView(R.id.main_container)
    FrameLayout mainContainer;
    @BindView(R.id.drawer_nav)
    NavigationView drawerNav;
    ImageView imageView;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        drawerSetup();
        bottomSetup();
        navHeaderSetup();



    }



    private void navHeaderSetup() {
        headerName=drawerNav.getHeaderView(0).findViewById(R.id.name);
        headerEmail=drawerNav.getHeaderView(0).findViewById(R.id.email);
        imageView=drawerNav.getHeaderView(0).findViewById(R.id.imageView);
    headerName.setText(user.getDisplayName());
    headerEmail.setText(user.getEmail());
        Picasso.get().load(user.getPhotoUrl()).into(imageView);
        Log.i("123321",user.getPhotoUrl()+"");
    }

    private void drawerSetup() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();

    }

    private void bottomSetup() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm.beginTransaction().add(R.id.main_container, profile, "profile").hide(profile).commit();
        fm.beginTransaction().add(R.id.main_container, home, "home").commit();
        fm.beginTransaction().add(R.id.main_container, live, "live").hide(live).commit();
        fm.beginTransaction().add(R.id.main_container, test, "test").hide(test).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.feed:
                fm.beginTransaction().hide(active).show(home).commit();
                active = home;
                return true;

            case R.id.live:
                fm.beginTransaction().hide(active).show(live).commit();
                active = live;
                return true;

            case R.id.test:
                fm.beginTransaction().hide(active).show(test).commit();
                active = test;
                return true;
            case R.id.profile:
                fm.beginTransaction().hide(active).show(profile).commit();
                active = profile;
                return true;
        }
        return false;
    };


public Fragment getActive(){
    return  active;
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigation.setVisibility(View.VISIBLE);
    }

}