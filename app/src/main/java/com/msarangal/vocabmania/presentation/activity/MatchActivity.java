package com.msarangal.vocabmania.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.msarangal.vocabmania.presentation.fragment.MyMatchFragment;
import com.msarangal.vocabmania.R;
import com.msarangal.vocabmania.TypefaceSpan;

/**
 * Created by Mandeep on 28/6/2015.
 */
public class MatchActivity extends AppCompatActivity implements MyMatchFragment.OnGettingMatchData {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    MyPagerAdapter adapter;
    Bundle bundleReceived;
    Bundle bundleToForward;
    String[] questionsArray = new String[5];
    String[] answersArray = new String[5];
    String[] selectedAnswerArray = new String[5];
    int[] answersNumbers = new int[5];
    int[] selectedAnswersNumbers = new int[5];
    String question, correctAnswer, yourAnswer;
    int RightOrWrong;
    Animation animScale;
    private LinearLayout appbar_container;
    private TextView tvTitle;
    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.match);
        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        SpannableString s = new SpannableString("Match Answers");
        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        getAllDataFromResultActivity();

        bundleToForward = new Bundle();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            // NavUtils.getParentActivityIntent(this);
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        String[] tabs = {"1", "2", "3", "4", "5"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            MyMatchFragment frag = MyMatchFragment.getInstance(position);
            return frag;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }


    public void getAllDataFromResultActivity() {
        Intent i = getIntent();

        bundleReceived = i.getBundleExtra("bundleForMatch");

        questionsArray = bundleReceived.getStringArray("allQuestionsText");
        answersArray = bundleReceived.getStringArray("allAnswersText");
        selectedAnswerArray = bundleReceived
                .getStringArray("allSelectedOptionsText");
        answersNumbers = bundleReceived.getIntArray("correctAnswersNumbers");
        selectedAnswersNumbers = bundleReceived
                .getIntArray("selectedAnswersNumbers");

    }


    @Override
    public Bundle getMatchData(int p) {

        if (p == 0) {

            question = questionsArray[0];
            correctAnswer = answersArray[0];

            if (selectedAnswersNumbers[0] == 0) {
                RightOrWrong = 2;
                yourAnswer = "NOT Answered";
            } else {
                yourAnswer = selectedAnswerArray[0];
                if (answersNumbers[0] == selectedAnswersNumbers[0]) {
                    RightOrWrong = 1;

                } else {
                    RightOrWrong = 0;

                }
            }

            bundleToForward.putString("Q", question);
            bundleToForward.putString("C", correctAnswer);
            bundleToForward.putString("Y", yourAnswer);
            bundleToForward.putInt("RightOrWrong", RightOrWrong);

            return bundleToForward;

        } else if (p == 1) {
            question = questionsArray[1];
            correctAnswer = answersArray[1];

            if (selectedAnswersNumbers[1] == 0) {
                RightOrWrong = 2;
                yourAnswer = "NOT Answered";
            } else {
                yourAnswer = selectedAnswerArray[1];
                if (answersNumbers[1] == selectedAnswersNumbers[1]) {
                    RightOrWrong = 1;

                } else {
                    RightOrWrong = 0;

                }
            }

            bundleToForward.putString("Q", question);
            bundleToForward.putString("C", correctAnswer);
            bundleToForward.putString("Y", yourAnswer);

            bundleToForward.putInt("RightOrWrong", RightOrWrong);
            return bundleToForward;

        } else if (p == 2) {

            question = questionsArray[2];
            correctAnswer = answersArray[2];

            if (selectedAnswersNumbers[2] == 0) {
                RightOrWrong = 2;
                yourAnswer = "NOT Answered";
            } else {
                yourAnswer = selectedAnswerArray[2];
                if (answersNumbers[2] == selectedAnswersNumbers[2]) {
                    RightOrWrong = 1;

                } else {
                    RightOrWrong = 0;

                }
            }
            bundleToForward.putString("Q", question);
            bundleToForward.putString("C", correctAnswer);
            bundleToForward.putString("Y", yourAnswer);

            bundleToForward.putInt("RightOrWrong", RightOrWrong);
            return bundleToForward;


        } else if (p == 3) {
            question = questionsArray[3];
            correctAnswer = answersArray[3];

            if (selectedAnswersNumbers[3] == 0) {
                RightOrWrong = 2;
                yourAnswer = "NOT Answered";
            } else {
                yourAnswer = selectedAnswerArray[3];
                if (answersNumbers[3] == selectedAnswersNumbers[3]) {
                    RightOrWrong = 1;

                } else {
                    RightOrWrong = 0;

                }
            }

            bundleToForward.putString("Q", question);
            bundleToForward.putString("C", correctAnswer);
            bundleToForward.putString("Y", yourAnswer);

            bundleToForward.putInt("RightOrWrong", RightOrWrong);
            return bundleToForward;


        } else if (p == 4) {

            question = questionsArray[4];
            correctAnswer = answersArray[4];

            if (selectedAnswersNumbers[4] == 0) {
                RightOrWrong = 2;
                yourAnswer = "NOT Answered";
            } else {
                yourAnswer = selectedAnswerArray[4];
                if (answersNumbers[4] == selectedAnswersNumbers[4]) {
                    RightOrWrong = 1;

                } else {
                    RightOrWrong = 0;

                }
            }

            bundleToForward.putString("Q", question);
            bundleToForward.putString("C", correctAnswer);
            bundleToForward.putString("Y", yourAnswer);

            bundleToForward.putInt("RightOrWrong", RightOrWrong);

            return bundleToForward;

        }
        return null;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }
}