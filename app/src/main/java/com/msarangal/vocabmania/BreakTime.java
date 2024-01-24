package com.msarangal.vocabmania;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.RequestQueue;

/**
 * Created by Mandeep on 18/7/2015.
 */
public class BreakTime extends AppCompatActivity implements View.OnClickListener {

    private TextView timerH, timerM, timerS, categoryName;
    private Button Revise;
    private long totalTime = 0;
    private CountDownTimer countDownTimer;
    private Toolbar toolbar;
    private Intent intent;
    private RequestQueue requestQueue;
    private Bundle bundle;
    private String level = null;
    private MySQLiteAdapter mySQLiteAdapter;
    private int lev = 0;
    private int exit = 0;
    private Animation animScale;
    private Boolean isReviseInForegroound;
    private CardView cardView;
    private LinearLayout appbar_container;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.breaktime);

        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        mySQLiteAdapter = new MySQLiteAdapter(this);

        timerH = (TextView) findViewById(R.id.tvHours);
        timerM = (TextView) findViewById(R.id.tvMin);
        timerS = (TextView) findViewById(R.id.tvSecs);
        categoryName = (TextView) findViewById(R.id.tvCategoryName);
        Revise = (Button) findViewById(R.id.btnRevise);
        Revise.setOnClickListener(this);
        cardView = (CardView) findViewById(R.id.card_v);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setMax(43200);

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        SpannableString s = new SpannableString("Break Time");
        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
        an.setFillAfter(true);

        intent = getIntent();

        bundle = intent.getBundleExtra("twoTimeValues");
        totalTime = bundle.getLong("twelveHoursAheadTime", 0) - bundle.getLong("currentTime", 0);
        level = bundle.getString("revisionLevel");

        if (level.equalsIgnoreCase("E")) {
            categoryName.setText("BEGINNER");
        } else if (level.equalsIgnoreCase("M")) {
            categoryName.setText("INTERMEDIATE");
        } else if (level.equalsIgnoreCase("T")) {
            categoryName.setText("ADVANCED");
        }

        isReviseInForegroound = false;

        startTimer();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        // Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show();
        isReviseInForegroound = true;
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        isReviseInForegroound = false;

        startTimer();

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

    public void startTimer() {

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            public void run() {
                countDownTimer = new CountDownTimer(totalTime, 500) {
                    // 500 means, onTick function will be called at every 500
                    // milliseconds

                    @Override
                    public void onTick(long leftTimeInMilliseconds) {
                        totalTime = leftTimeInMilliseconds;
                        long seconds = leftTimeInMilliseconds / 1000;

                        long timeElapsed = pb.getMax() - seconds;
                        //long barVal = timeElapsed;
                        pb.setProgress((int) timeElapsed);

                        //int barVal = ((timeElapsed * 100) / 60);
                        // pb.setProgress(barVal);
                        timerH.setText(String.format("%02d", seconds / 3600));

                        timerM.setText(String.format("%02d", (seconds % 3600) / 60));

                        timerS.setText(String.format("%02d", seconds % 60));

                        // format the textview to show the easily readable
                        // format

                    }

                    @Override
                    public void onFinish() {
                        if (timerH.getText().equals("00") && timerM.getText().equals("00") && timerS.getText().equals("00")) {

                            if (isReviseInForegroound == false) {
                                Intent intent = new Intent(BreakTime.this, TestActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);

                            }
                        }

                    }
                }.start();

            }
        }, 0);

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;

        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnRevise) {

            cardView.startAnimation(animScale);

            Bundle bundle = new Bundle();

            if (level.equalsIgnoreCase("E")) {
                lev = 0;

            } else if (level.equalsIgnoreCase("M")) {

                lev = 1;
            } else if (level.equalsIgnoreCase("T")) {

                lev = 2;
            }

            if (bundle != null) {

                //Message.message(this, "level is " + lev + "Time remaining : " + totalTime);
                Intent i = new Intent(this, Revise.class);

                i.putExtra("level", lev);
                i.putExtra("toStartTimer", true);
                i.putExtra("RemainingBreakTime", totalTime);
                i.putExtra("activityReference", 1);

                isReviseInForegroound = true;

                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);

            } else {
                Message.message(this, "Revision Data not loaded properly, please try again");
            }

        }

    }
}
