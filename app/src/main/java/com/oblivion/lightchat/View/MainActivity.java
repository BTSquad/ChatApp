package com.oblivion.lightchat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oblivion.lightchat.Adapter.PagerAdapter;
import com.oblivion.lightchat.Fragment.KontakFragment;
import com.oblivion.lightchat.Fragment.PesanFragment;
import com.oblivion.lightchat.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static Context contextOfApplication;

    private Fragment pesanFm, kontakFm;

    private PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private int extrasPosition;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        contextOfApplication = getApplicationContext();

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(R.color.primaryDarkColor, PorterDuff.Mode.SRC_IN);

        Intent intent = getIntent();

        if(intent == null) {
            extrasPosition = 0;
        } else {
            extrasPosition = intent.getIntExtra("position", 0);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    setFragment(pesanFm);
                }else {
                    setFragment(kontakFm);
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(R.color.primaryDarkColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(extrasPosition);

    }

    private void init(){

        pesanFm = new PesanFragment();
        kontakFm = new KontakFragment();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tabLayout = findViewById(R.id.tab_view);
        viewPager = findViewById(R.id.viewPager_slide);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

//        ImageView userPhoto = findViewById(R.id.user_photo);
//
//        Picasso.get().load(mUser.getPhotoUrl()).fit().centerCrop().into(userPhoto);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setelan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.setelan_menu:

                return true;

            case R.id.logout_item:

                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, MasukActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, "Home");
        fragmentTransaction.commit();
    }




}
