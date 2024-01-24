package com.msarangal.vocabmania;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity implements MyFragment.OnGettingGraphValues {

    private androidx.appcompat.widget.Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPagerAdapter adapter;
    private MySQLiteAdapter mySQLiteAdapter;
    private TextView tvTitle;
    private LinearLayout appbar_container;
    private View v;
    int exit = 0;
    Bundle bundleGraphValuesE;
    Bundle bundleGraphValuesM;
    Bundle bundleGraphValuesT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySQLiteAdapter = new MySQLiteAdapter(this);

        //three bundles forward EASY, MEDIUM and TOUGH graph points
        bundleGraphValuesE = new Bundle();
        bundleGraphValuesM = new Bundle();
        bundleGraphValuesT = new Bundle();
        // long l= mySQLiteAdapter.insertIntoValuesForGraph(1,"E");
        bundleGraphValuesE = mySQLiteAdapter.checkfive_returnArray("E");
        bundleGraphValuesM = mySQLiteAdapter.checkfive_returnArray("M");
        bundleGraphValuesT = mySQLiteAdapter.checkfive_returnArray("T");

        //Typeface typeface= Typeface.createFromAsset(getAssets(),"fonts/Nunito-Regular.ttf");

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (androidx.appcompat.widget.Toolbar) appbar_container.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        SpannableString s = new SpannableString("Progress");
        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Bundle getGraphpoints  (int p) {

        if (p == 0) {
            return bundleGraphValuesE;

        } else if (p == 1) {
            return bundleGraphValuesM;
        } else if (p == 2) {
            return bundleGraphValuesT;
        }
        return null;
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        String[] tabs = {"Beginner", "Intermediate", "Advance"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            MyFragment frag = MyFragment.getInstance(position);
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }

}
