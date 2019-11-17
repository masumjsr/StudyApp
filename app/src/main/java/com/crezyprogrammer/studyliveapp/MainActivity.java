package com.crezyprogrammer.studyliveapp;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.crezyprogrammer.studylive.fragment.ProfileFragment;
import com.crezyprogrammer.studylive.fragment.TestFragment;
import com.crezyprogrammer.studyliveapp.fragment.ContestFragment;
import com.crezyprogrammer.studyliveapp.fragment.HomeFragment;
import com.crezyprogrammer.studyliveapp.fragment.LeaderFragment;
import com.crezyprogrammer.studyliveapp.fragment.LiveFragment;
import com.crezyprogrammer.studyliveapp.fragment.QuizContestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity {
    String currentFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    final Fragment home = new HomeFragment();
    final Fragment live = new LiveFragment();
    final Fragment test = new TestFragment();
    final Fragment contest = new ContestFragment();
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
    boolean comment =true;

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
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        if (user==null) {
            Menu nav_Menu = drawerNav.getMenu();
            nav_Menu.findItem(R.id.logout).setVisible(false);
        }
        else {
            Menu nav_Menu = drawerNav.getMenu();
            nav_Menu.findItem(R.id.signin).setVisible(false);
        }
        drawerSetup();
        bottomSetup();
        navHeaderSetup();
        profileSetup();
        ImageView imageView=toolbar.findViewById(R.id.drawerIcon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        fm.addOnBackStackChangedListener(() -> {
            List<Fragment> f = fm.getFragments();
            Fragment frag = f.get(0);
            currentFragment = frag.getClass().getSimpleName();
        });

    }

    private void profileSetup() {


    }


    private void navHeaderSetup() {
        headerName = drawerNav.getHeaderView(0).findViewById(R.id.name);
        headerEmail = drawerNav.getHeaderView(0).findViewById(R.id.email);
        imageView = drawerNav.getHeaderView(0).findViewById(R.id.imageView);
if(user!=null) {
    headerName.setText(user.getDisplayName());
    headerEmail.setText(user.getEmail());
    Picasso.get().load(user.getPhotoUrl()).into(imageView);
    Log.i("123321", user.getPhotoUrl() + "");
}
    }

    private void drawerSetup() {
    //    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
    //    drawer.addDrawerListener(toggle);

    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setHomeButtonEnabled(true);
    //    toggle.syncState();



       drawerNav.setNavigationItemSelectedListener(menuItem -> {
           int id = menuItem.getItemId();
           switch (id) {

               case R.id.logout:

                   drawer.closeDrawers();
                   Builder builder2= new Builder(this);
                   builder2.setTitle("Are You Sure?");
                   builder2.setMessage("Are You Want To Sign Out?");
                   builder2.setPositiveButton("Yes", (dialog, which) -> {
                       FirebaseAuth.getInstance().signOut();
                       startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                   });
                   builder2.setNegativeButton("cancel", null);
                   builder2.show();
                   break;
               case  R.id.notice:
                   startActivity(new Intent(getApplicationContext(), NoticeActivity.class));
                   drawer.closeDrawers();
                   break;
                   case  R.id.how_to_work:
                   startActivity(new Intent(getApplicationContext(),HowActivity.class));
                   drawer.closeDrawers();
                   break;
                   case  R.id.policy:
                    String url ="https://www.websitepolicies.com/policies/view/ZEQdS8FI";
                    Intent i = new Intent(ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                   drawer.closeDrawers();
                break;
               case R.id.signin:
                   startActivity(new Intent(this,SignInActivity.class));
                   break;
                case R.id.about:
                    Builder builder = new Builder(this);
                    builder.setTitle("About Us");
                    builder.setMessage("developed with love");
                    builder.setPositiveButton("ok", null);
                    builder.show();
                    drawer.closeDrawers();
                    break;
                    case R.id.share:
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String shareMessage= "\nLet me recommend you this application\n\n";
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch(Exception e) {
                            //e.toString();
                        }
                        drawer.closeDrawers();
                        break;
                        case R.id.contact:
                            Builder builder1 = new Builder(this);
                            builder1.setTitle("Contact Us");
                            builder1.setMessage("Email:developer.mrenglish@gmail.com");
                            builder1.setPositiveButton("ok", null);
                            builder1.show();
                            drawer.closeDrawers();

           }
           return false;
       });

    }

    private void bottomSetup() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //fm.beginTransaction().add(R.id.main_container, grammer, "grammer").hide(grammer).commit();
        fm.beginTransaction().add(R.id.main_container, profile, "profile").hide(profile).commit();
        fm.beginTransaction().add(R.id.main_container, home, "home").commit();
        fm.beginTransaction().add(R.id.main_container, live, "live").hide(live).commit();
        fm.beginTransaction().add(R.id.main_container, test, "test").hide(test).commit();
        fm.beginTransaction().add(R.id.main_container, contest, "contest").hide(contest).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {



            switch (item.getItemId()) {
                case R.id.feed:
                    fm.beginTransaction().hide(active).show(home).commit();
                    active = home;
                    return true;

                case R.id.live:
                    if (user!=null) {


                        fm.beginTransaction().hide(active).show(live).commit();
                        active = live;
                        comment = true;
                    }
                    else
                {
                    Builder builder = new Builder(MainActivity.this);
                    builder.setTitle("Login Required");
                    builder.setMessage("You need to login to use the feature");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                    });
                    builder.setNegativeButton("cancel", null);
                    builder.show();

                }
                    return true;

                case R.id.test:
                    if (user != null) {


                    fm.beginTransaction().hide(active).show(test).commit();
                    active = test;}
                         else
                        {
                            Builder builder = new Builder(MainActivity.this);
                            builder.setTitle("Login Required");
                            builder.setMessage("You need to login to use the feature");
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                }
                            });
                            builder.setNegativeButton("cancel", null);
                            builder.show();

                        }
                    return true;

                    case R.id.contest: if (user!=null) {
                        fm.beginTransaction().hide(active).show(contest).commit();
                        active = contest;
                        comment = true;
                    }
                    else {
                        Builder builder = new Builder(this);
                        builder.setTitle("Sign in requied");
                        builder.setMessage("you must sign in to use the feature");
                        builder.setPositiveButton("ok", null);
                        builder.setNegativeButton("cancel", null);
                        builder.show();
                    }
                        return true;

                        case R.id.profile: if (user != null) {
                            comment = true;
                            fm.beginTransaction().hide(active).show(profile).commit();
                            active = profile;
                            return true;
                    }
                    else {
                        Builder builder = new Builder(this);
                        builder.setTitle("Sign in requied");
                        builder.setMessage("you must sign in to use the feature");
                        builder.setPositiveButton("ok", null);
                        builder.setNegativeButton("cancel", null);
                        builder.show();
                    }
                    }

        return false;
    };


    public  void  setActive(Fragment fragment){
        active=fragment;
    }
    public Fragment getActive() {
        return active;
    }

    @Override
    public void onBackPressed() {


        if (active==home) {
            Builder builder = new Builder(this);
            builder.setTitle("Exit");
            builder.setMessage("Are you sure");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                    System.exit(0);

                }
            });
            builder.setNegativeButton("cancel",null );
            builder.show();
        }

       // Toast.makeText(this, ""+Model.getI(), Toast.LENGTH_SHORT).show();
        if (Model.getI()==1) {
           // super.onBackPressed();
        }
        else {
            navigation.setVisibility(View.VISIBLE);
        }

    }
    public  void setComment(Boolean b){
       // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        comment=b;
    }
}