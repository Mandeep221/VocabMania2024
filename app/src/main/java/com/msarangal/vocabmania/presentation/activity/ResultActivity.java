package com.msarangal.vocabmania.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.msarangal.vocabmania.MySQLiteAdapter;
import com.msarangal.vocabmania.R;
import com.msarangal.vocabmania.TypefaceSpan;

//import tourguide.tourguide.Overlay;
//import tourguide.tourguide.Pointer;
//import tourguide.tourguide.ToolTip;
//import tourguide.tourguide.TourGuide;

public class ResultActivity extends AppCompatActivity implements OnClickListener {

    private Toolbar toolbar;
    private LinearLayout appbar_container;
    private TextView tvTitle;
    private View v;
    private Bundle b;
    private TextView marks, pectangeMarks, Q1, Q2, Q3, Q4, Q5, A1, A2, A3, A4, A5;
    private ImageView fav1, fav2, fav3, fav4, fav5;
    private String[] questionsArray;
    private String[] answersArray;
    private String[] words;
    private String[] selectedAnswerArray;
    private int[] answersNumbers;
    private int[] selectedAnswersNumbers;
    private int testmarks;
    private int percentage;
    private boolean fav1Status = false;
    private boolean fav2Status = false;
    private boolean fav3Status = false;
    private boolean fav4Status = false;
    private boolean fav5Status = false;
    private Animation animScale, fadeIn;
    //    private FitChart fitChart;
    private LinearLayout analyze;
    MySQLiteAdapter mySQLiteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_result);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadin);

//        fitChart = (FitChart) findViewById(R.id.fitChart);
//        fitChart.setMinValue(0f);
//        fitChart.setMaxValue(100f);


        questionsArray = new String[5];
        answersArray = new String[5];
        words = new String[5];
        selectedAnswerArray = new String[5];
        answersNumbers = new int[5];
        selectedAnswersNumbers = new int[5];
        answersNumbers = new int[5];
        selectedAnswersNumbers = new int[5];
        //Initializing the sqlite adapter

        mySQLiteAdapter = new MySQLiteAdapter(this);

        marks = (TextView) findViewById(R.id.tvMarks);
        pectangeMarks = (TextView) findViewById(R.id.tvPercentMarks);
        Q1 = (TextView) findViewById(R.id.tvQ1);
        Q2 = (TextView) findViewById(R.id.tvQ2);
        Q3 = (TextView) findViewById(R.id.tvQ3);
        Q4 = (TextView) findViewById(R.id.tvQ4);
        Q5 = (TextView) findViewById(R.id.tvQ5);

        A1 = (TextView) findViewById(R.id.tvA1);
        A2 = (TextView) findViewById(R.id.tvA2);
        A3 = (TextView) findViewById(R.id.tvA3);
        A4 = (TextView) findViewById(R.id.tvA4);
        A5 = (TextView) findViewById(R.id.tvA5);

        fav1 = (ImageView) findViewById(R.id.fav1);
        fav2 = (ImageView) findViewById(R.id.fav2);
        fav3 = (ImageView) findViewById(R.id.fav3);
        fav4 = (ImageView) findViewById(R.id.fav4);
        fav5 = (ImageView) findViewById(R.id.fav5);

        //takeAnother.setOnClickListener(this);
        //matchAnswers.setOnClickListener(this);
        fav1.setOnClickListener(this);
        fav2.setOnClickListener(this);
        fav3.setOnClickListener(this);
        fav4.setOnClickListener(this);
        fav5.setOnClickListener(this);

        // getting all the data from questions activity

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SpannableString s = new SpannableString("Result");
        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        b = new Bundle();

        getAllDataFromQuestionsActivity();

        AssignValues();

        analyze = (LinearLayout) findViewById(R.id.btnCompare);

        analyze.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ResultActivity.this, MatchActivity.class);
                in.putExtra("bundleForMatch", b);
                startActivity(in);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }
        });

        /* setup enter and exit animation */
        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        if (item.getItemId() == android.R.id.home) {

            Intent i = new Intent(this, TestActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            finish();
            return true;
        }
        return false;
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub

        v.startAnimation(animScale);

        if (v.getId() == R.id.fav1) {
            if (fav1Status == false) {
                fav1Status = true;
                fav1.setImageResource(R.drawable.star_red_filled);
                long l = SaveToDB(words[0], answersArray[0]);
                if (l != -1) {

                    final Toast toast = Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Added to favorites", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                fav1Status = false;
                fav1.setImageResource(R.drawable.star_red);
                int d = delete(words[0]);
                if (d != -1) {

                    final Toast toast = Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v.getId() == R.id.fav2) {
            if (fav2Status == false) {
                fav2Status = true;
                fav2.setImageResource(R.drawable.star_red_filled);
                long l = SaveToDB(words[1], answersArray[1]);
                if (l != -1) {

                    final Toast toast = Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Added to favorites", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                fav2Status = false;
                fav2.setImageResource(R.drawable.star_red);
                int d = delete(words[1]);
                if (d != -1) {

                    final Toast toast = Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v.getId() == R.id.fav3) {
            if (fav3Status == false) {
                fav3Status = true;
                fav3.setImageResource(R.drawable.star_red_filled);
                long l = SaveToDB(words[2], answersArray[2]);
                if (l != -1) {

                    final Toast toast = Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Added to favorites", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                fav3Status = false;
                fav3.setImageResource(R.drawable.star_red);
                int d = delete(words[2]);
                if (d != -1) {

                    final Toast toast = Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }

        } else if (v.getId() == R.id.fav4) {
            if (fav4Status == false) {
                fav4Status = true;
                fav4.setImageResource(R.drawable.star_red_filled);
                long l = SaveToDB(words[3], answersArray[3]);
                if (l != -1) {

                    final Toast toast = Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Added to favorites", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                fav4Status = false;
                fav4.setImageResource(R.drawable.star_red);
                int d = delete(words[3]);
                if (d != -1) {

                    final Toast toast = Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (v.getId() == R.id.fav5) {
            if (fav5Status == false) {
                fav5Status = true;
                fav5.setImageResource(R.drawable.star_red_filled);
                Long l = SaveToDB(words[4], answersArray[4]);
                if (l != -1) {

                    final Toast toast = Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Added to favorites", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                fav5Status = false;
                fav5.setImageResource(R.drawable.star_red);
                int d = delete(words[4]);
                if (d != -1) {

                    final Toast toast = Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT);
                    toast.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                } else {

                    Toast.makeText(this, "Not Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        super.onBackPressed();
        Intent i = new Intent(this, TestActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
        finish();
    }

    public void getAllDataFromQuestionsActivity() {
        Intent i = getIntent();

        b = i.getBundleExtra("bundleContainingTestData");

        questionsArray = b.getStringArray("allQuestionsText");
        answersArray = b.getStringArray("allAnswersText");
        selectedAnswerArray = b.getStringArray("allSelectedOptionsText");
        answersNumbers = b.getIntArray("correctAnswersNumbers");
        selectedAnswersNumbers = b.getIntArray("selectedAnswersNumbers");
        words = b.getStringArray("words");

        testmarks = b.getInt("marks");

        percentage = (testmarks * 100) / 5;

        marks.setText("" + testmarks + "/5");
        pectangeMarks.setText("" + percentage + "%");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Collection<FitChartValue> values = new ArrayList<>();
//                values.add(new FitChartValue(percentage, ContextCompat.getColor(Result.this, R.color.pink_four)));
//                fitChart.setValues(values);
//                pectangeMarks.setVisibility(View.VISIBLE);
//                pectangeMarks.startAnimation(fadeIn);
            }
        }, 1200);

    }

    public void AssignValues() {

        // assigning questions
        Q1.setText(questionsArray[0]);
        Q2.setText(questionsArray[1]);
        Q3.setText(questionsArray[2]);
        Q4.setText(questionsArray[3]);
        Q5.setText(questionsArray[4]);

        // assigning answers

        A1.setText(answersArray[0]);
        A2.setText(answersArray[1]);
        A3.setText(answersArray[2]);
        A4.setText(answersArray[3]);
        A5.setText(answersArray[4]);
    }

    public void showPopup() {
//        View menuItemView = findViewById(R.id.Overflow);
//        popupmenu = new PopupMenu(Result.this, menuItemView);
//        MenuInflater inflate = popupmenu.getMenuInflater();
//        inflate.inflate(R.menu.result_options, popupmenu.getMenu());
//        popupmenu.show();
//        popupmenu.setOnMenuItemClickListener(this);

    }

    public long SaveToDB(String word, String meaning) {
        long r = mySQLiteAdapter.insert(word, meaning);
        return r;
    }

    //Delete a record from SQLite Databse
    public int delete(String word) {
        int res = mySQLiteAdapter.delete(word);
        //  Message.message(this, "" + res);
        return res;
    }


}
