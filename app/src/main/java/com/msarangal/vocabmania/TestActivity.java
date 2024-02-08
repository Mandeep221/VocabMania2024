package com.msarangal.vocabmania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Prokure User on 4/6/2016.
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button get;
    private TextView tv;
    private CardView take_test, revise, progress, favorites, share, rate;
//    private Firebase mRef;
    private SharedPreferences sharedPreferences;
    private int eCount, mCount, tCount;
    private int easyCapValue, medCapValue, toughCapValue, eTestNo, mTestNo, tTestNo, total_entries;
    private Long eTwelve, mTwelve, tTwelve;
    private MySQLiteAdapter mySQLiteAdapter;
    private Calendar cal1, cal2;
    private String uName = null, word, meaning, usage;
    private Long currentTime, twelveHoursAheadTime;
    private AlertDialog dialog_test, dialog_revise, dialog_update, dialog_wordOfTheDay;
    private RelativeLayout policy, help, wordOfTheDay;
    private Bundle bundle;
    private android.app.AlertDialog alertDialog;
    private int updateFlag = 0;
//    public TourGuide mTutorialHandler;
    private int counter = 0;
    private int exit = 0;
    private Animation fadein, fadeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_design);

        fadein = AnimationUtils.loadAnimation(this, R.anim.fadin);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        Intent i = getIntent();

        if (i != null) {

            updateFlag = i.getIntExtra("update_flag", 0);
        }

        word = getSharedPreferences(Constants.SP, MODE_PRIVATE).getString(Constants.WORD, "");
        meaning = getSharedPreferences(Constants.SP, MODE_PRIVATE).getString(Constants.MEANING, "");
        usage = getSharedPreferences(Constants.SP, MODE_PRIVATE).getString(Constants.USAGE, "");

        mySQLiteAdapter = new MySQLiteAdapter(this);

        help = (RelativeLayout) findViewById(R.id.rl_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestActivity.this, Help.class);
                TestActivity.this.startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }
        });

        wordOfTheDay = (RelativeLayout) findViewById(R.id.rl_word);
        wordOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordOfTheDay();
            }
        });

        policy = findViewById(R.id.rl_privacy);
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestActivity.this, LearnMoreFav.class);
                i.putExtra("link", "https://docs.google.com/document/d/1b9uta-BUGGQvvi_5rekN6t5_g-MhgFXT5OQ2VOTwlQw");
                i.putExtra("isPrivacy", true);
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }
        });

        take_test = (CardView) findViewById(R.id.card_take_test);

        take_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test_options();
            }
        });

        revise = (CardView) findViewById(R.id.card_revise);
        revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revise();
            }
        });

        progress = (CardView) findViewById(R.id.card_progress);
        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(TestActivity.this, MainActivity.class);
                TestActivity.this.startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }
        });

        favorites = (CardView) findViewById(R.id.card_favorite);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestActivity.this, Favorites.class);
                TestActivity.this.startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }
        });

        share = (CardView) findViewById(R.id.card_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri playStoreWebsite = Uri.parse("http://play.google.com/store/apps/details?id=" + TestActivity.this.getPackageName());
                // Uri playStoreAndroid = Uri.parse("market://details?id=" + context.getPackageName());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Take your ENGLISH VOCABULARY to the next level, Add new words to your arsenal every day forward FREE! \n" + "APP LINK : " + playStoreWebsite);
                sendIntent.setType("text/plain");
                TestActivity.this.startActivity(Intent.createChooser(sendIntent, "select an option"));

            }
        });

        rate = (CardView) findViewById(R.id.card_rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TestActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + TestActivity.this.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    TestActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + TestActivity.this.getPackageName())));
                }
            }
        });

        if (updateFlag != 0) {
            DisplayUpdateDialog(updateFlag);
        }

   /* setup enter and exit animation */
        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);
    }


    public void test_options() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.test_options_dialog, null, false);

        RelativeLayout test_beginner = (RelativeLayout) v.findViewById(R.id.test_beginner);
        RelativeLayout test_intermediate = (RelativeLayout) v.findViewById(R.id.test_intermediate);
        RelativeLayout test_advanced = (RelativeLayout) v.findViewById(R.id.test_advanced);

        test_beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchBeginnerTest();

                dialog_test.dismiss();

            }
        });

        test_intermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchIntermediateTest();
                dialog_test.dismiss();
            }
        });

        test_advanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchAdvancedTest();
                dialog_test.dismiss();
            }
        });

        builder.setView(v);
        dialog_test = builder.create();
        dialog_test.setCanceledOnTouchOutside(true);

        dialog_test.show();
    }

    public void revise() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.revise_dialog, null, false);

        RelativeLayout revise_beginner = (RelativeLayout) v.findViewById(R.id.revise_beginner);
        RelativeLayout revise_intermediate = (RelativeLayout) v.findViewById(R.id.revise_intermediate);
        RelativeLayout revise_advanced = (RelativeLayout) v.findViewById(R.id.revise_advanced);

        bundle = new Bundle();

        revise_beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_revise.dismiss();
                bundle = mySQLiteAdapter.getRevisionQues(0);

                launchRevision(bundle, 0);

            }
        });

        revise_intermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_revise.dismiss();
                bundle = mySQLiteAdapter.getRevisionQues(1);

                launchRevision(bundle, 1);
            }
        });

        revise_advanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_revise.dismiss();
                bundle = mySQLiteAdapter.getRevisionQues(2);

                launchRevision(bundle, 2);
            }
        });

        builder.setView(v);
        dialog_revise = builder.create();
        dialog_revise.setCanceledOnTouchOutside(true);

        dialog_revise.show();
    }

    public void launchBeginnerTest() {
        sharedPreferences = getSharedPreferences("UserName", MODE_PRIVATE);
        eCount = sharedPreferences.getInt("eCount", 1);
        uName = sharedPreferences.getString("uname", "manu_21");

        if (eCount == 0 || eCount % 3 != 0) {

            //get EASY cap value from shared prefs
            easyCapValue = sharedPreferences.getInt("eCapValue", 20);

            total_entries = (int) mySQLiteAdapter.checkTotalEntries(0);

            if (total_entries == easyCapValue) {

                Intent i = new Intent(TestActivity.this, AllWordsOver.class);
                i.putExtra("level", "BEGINNER");
                i.putExtra("wordCount", (easyCapValue * 5));
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);

            } else {

                //loop to get a unique random number from RANDOMIZE_EASY less than or equal to cap value that has not been used yet
                while (true) {
                    eTestNo = new Random().nextInt(easyCapValue) + 1;
                    int res = mySQLiteAdapter.checkEasyRandom(String.valueOf(eTestNo));

                    if (res == 0) {
                        break;
                    } else {
                        continue;
                    }
                }

                Intent i = new Intent(TestActivity.this, Questions.class);

                i.putExtra("uName", uName);
                i.putExtra("testCategory", "E");
                i.putExtra("testNumber", eTestNo);
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }

        } else if (eCount % 3 == 0) {

            eTwelve = sharedPreferences.getLong("eTwelve", 0);

            if (eTwelve == 0) {
                Message.message(this, "Some Data error occurred, Please try again");
            } else {


                cal2 = Calendar.getInstance();
                cal2.setTime(new Date());

                // dateCompare = cal1.compareTo(cal2);

                currentTime = cal2.getTimeInMillis();
                twelveHoursAheadTime = eTwelve;
            }


            if (twelveHoursAheadTime - currentTime > 0) {
                Intent i = new Intent(TestActivity.this, BreakTime.class);

                Bundle bundle = new Bundle();
                bundle.putLong("twelveHoursAheadTime", twelveHoursAheadTime);
                bundle.putLong("currentTime", currentTime);
                bundle.putString("revisionLevel", "E");

                i.putExtra("twoTimeValues", bundle);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            } else {

                //get EASY cap value from shared prefs
                easyCapValue = sharedPreferences.getInt("eCapValue", 20);
                //loop to get a unique random number from RANDOMIZE_EASY less than or equal to cap value that has not been used yet
                total_entries = (int) mySQLiteAdapter.checkTotalEntries(0);

                if (total_entries == easyCapValue) {
                    Intent i = new Intent(TestActivity.this, AllWordsOver.class);
                    i.putExtra("level", "BEGINNER");
                    i.putExtra("wordCount", (easyCapValue * 5));
                    startActivity(i);
                    overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
                } else {

                    while (true) {
                        eTestNo = new Random().nextInt(easyCapValue) + 1;
                        int res = mySQLiteAdapter.checkEasyRandom(String.valueOf(eTestNo));

                        if (res == 0) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    Intent i = new Intent(TestActivity.this, Questions.class);

                    i.putExtra("uName", uName);
                    i.putExtra("testCategory", "E");
                    i.putExtra("testNumber", eTestNo);
                    startActivity(i);
                    overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
                }
            }

        }


    }

    public void launchIntermediateTest() {

        sharedPreferences = getSharedPreferences("UserName", MODE_PRIVATE);
        mCount = sharedPreferences.getInt("mCount", 1);

        uName = sharedPreferences.getString("uname", "manu_21");


        if (mCount == 0 || mCount % 3 != 0) {

            //get MEDIUM cap value from shared prefs
            medCapValue = sharedPreferences.getInt("mCapValue", 20);

            total_entries = (int) mySQLiteAdapter.checkTotalEntries(1);

            if (total_entries == medCapValue) {
                Intent i = new Intent(TestActivity.this, AllWordsOver.class);
                i.putExtra("level", "INTERMEDIATE");
                i.putExtra("wordCount", (medCapValue * 5));
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            } else {
                //loop to get a unique random number from RANDOMIZE_MED less than or equal to cap value that has not been used yet
                while (true) {
                    mTestNo = new Random().nextInt(medCapValue) + 1;

                    int res = mySQLiteAdapter.checkMedRandom(String.valueOf(mTestNo));

                    if (res == 0) {
                        break;
                    } else {
                        continue;
                    }
                }

                Intent i = new Intent(TestActivity.this, Questions.class);

                i.putExtra("uName", uName);
                i.putExtra("testCategory", "M");
                i.putExtra("testNumber", mTestNo);
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            }
        } else if (mCount % 3 == 0) {

            mTwelve = sharedPreferences.getLong("mTwelve", 0);

            if (mTwelve == 0) {
                Message.message(this, "Some Data error occurred, Please try again");
            } else {

                cal2 = Calendar.getInstance();
                cal2.setTime(new Date());

                //dateCompare = cal1.compareTo(cal2);

                currentTime = cal2.getTimeInMillis();
                twelveHoursAheadTime = mTwelve;

            }

            if (twelveHoursAheadTime - currentTime > 0) {
                Intent i = new Intent(TestActivity.this, BreakTime.class);

                Bundle bundle = new Bundle();
                bundle.putLong("twelveHoursAheadTime", twelveHoursAheadTime);
                bundle.putLong("currentTime", currentTime);
                bundle.putString("revisionLevel", "M");

                i.putExtra("twoTimeValues", bundle);
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            } else {

                //get MEDIUM cap value from shared prefs
                medCapValue = sharedPreferences.getInt("mCapValue", 20);

                total_entries = (int) mySQLiteAdapter.checkTotalEntries(1);

                if (total_entries == medCapValue) {
                    Intent i = new Intent(TestActivity.this, AllWordsOver.class);
                    i.putExtra("level", "INTERMEDIATE");
                    i.putExtra("wordCount", (medCapValue * 5));
                    startActivity(i);
                    overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
                } else {
                    //loop to get a unique random number from RANDOMIZE_EASY less than or equal to cap value that has not been used yet
                    while (true) {
                        mTestNo = new Random().nextInt(medCapValue) + 1;
                        int res = mySQLiteAdapter.checkMedRandom(String.valueOf(mTestNo));

                        if (res == 0) {
                            break;
                        } else {
                            continue;
                        }
                    }

                    Intent i = new Intent(TestActivity.this, Questions.class);

                    i.putExtra("uName", uName);
                    i.putExtra("testCategory", "M");
                    i.putExtra("testNumber", mTestNo);
                    startActivity(i);
                    overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
                }
            }
        }


    }

    public void launchAdvancedTest() {

        sharedPreferences = getSharedPreferences("UserName", MODE_PRIVATE);
        tCount = sharedPreferences.getInt("tCount", 1);

        uName = sharedPreferences.getString("uname", "manu_21");


        if (tCount == 0 || tCount % 3 != 0) {

            //get TOUGH cap value from shared prefs
            toughCapValue = sharedPreferences.getInt("tCapValue", 20);

            total_entries = (int) mySQLiteAdapter.checkTotalEntries(2);

            if (total_entries == toughCapValue) {
                Intent i = new Intent(TestActivity.this, AllWordsOver.class);
                i.putExtra("level", "ADVANCE");
                i.putExtra("wordCount", (toughCapValue * 5));
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            } else {
                //loop to get a unique random number from RANDOMIZE_MED less than or equal to cap value that has not been used yet
                while (true) {
                    tTestNo = new Random().nextInt(toughCapValue) + 1;
                    int res = mySQLiteAdapter.checkToughRandom(String.valueOf(tTestNo));

                    if (res == 0) {
                        break;
                    } else {
                        continue;
                    }
                }

                Intent i = new Intent(TestActivity.this, Questions.class);

                i.putExtra("uName", uName);
                i.putExtra("testCategory", "T");
                i.putExtra("testNumber", tTestNo);
                startActivity(i);
                overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);

            }
        } else if (tCount % 3 == 0) {

            tTwelve = sharedPreferences.getLong("tTwelve", 0);

            if (tTwelve == 0) {
                Message.message(this, "Some Data error occurred, Please try again");
            } else {

                cal2 = Calendar.getInstance();
                cal2.setTime(new Date());

                //dateCompare = cal1.compareTo(cal2);

                currentTime = cal2.getTimeInMillis();
                twelveHoursAheadTime = tTwelve;
            }


            if (twelveHoursAheadTime - currentTime > 0) {
                Intent i = new Intent(TestActivity.this, BreakTime.class);

                Bundle bundle = new Bundle();
                bundle.putLong("twelveHoursAheadTime", twelveHoursAheadTime);
                bundle.putLong("currentTime", currentTime);
                bundle.putString("revisionLevel", "T");

                i.putExtra("twoTimeValues", bundle);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            } else {

                //get TOUGH cap value from shared prefs
                toughCapValue = sharedPreferences.getInt("tCapValue", 20);
                if (total_entries == toughCapValue) {
                    Intent i = new Intent(TestActivity.this, AllWordsOver.class);
                    i.putExtra("level", "ADVANCE");
                    i.putExtra("wordCount", (toughCapValue * 5));
                    startActivity(i);
                    overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
                } else {
                    //loop to get a unique random number from RANDOMIZE_EASY less than or equal to cap value that has not been used yet
                    while (true) {
                        tTestNo = new Random().nextInt(toughCapValue) + 1;
                        int res = mySQLiteAdapter.checkToughRandom(String.valueOf(tTestNo));

                        if (res == 0) {
                            break;
                        } else {
                            continue;
                        }
                    }


                    Intent i = new Intent(TestActivity.this, Questions.class);

                    i.putExtra("uName", uName);
                    i.putExtra("testCategory", "T");
                    i.putExtra("testNumber", tTestNo);
                    startActivity(i);
                    overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
                }
            }

        }

    }


    public void launchRevision(Bundle bundle, int level) {
        if (bundle != null) {

            Intent i = new Intent(TestActivity.this, Revise.class);

            i.putExtra("revisionBundle", bundle);
            i.putExtra("level", level);
            i.putExtra("activityReference", 0);

            startActivity(i);
            overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);

            if (level == 0) {
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            } else if (level == 2) {
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }

        } else {
            final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                    TestActivity.this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.no_revision_dialog, null, false);
            TextView tv = (TextView) layout.findViewById(R.id.tv);
            String lev = "";
            if (level == 0) {
                lev = "BEGINNER";
            } else if (level == 1) {
                lev = "INTERMEDIATE";
            } else {
                lev = "ADVANCE";
            }

            tv.setText("No test has been taken in " + lev + " category yet, Please take a test first.");

            Button ok = (Button) layout.findViewById(R.id.btnOk);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialogBuilder.setView(layout);

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

    }

    public void DisplayUpdateDialog(int updateFlag) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TestActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.update_dialog, null, false);

        Button noThanks = (Button) view.findViewById(R.id.btnNoThanks);
        noThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_update.dismiss();
            }
        });

        Button update = (Button) view.findViewById(R.id.btnUpdate);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TestActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + TestActivity.this.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    TestActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + TestActivity.this.getPackageName())));
                }
            }
        });

        if (updateFlag == 2) {
            noThanks.setVisibility(View.GONE);
        }

        alertDialogBuilder.setView(view);
        dialog_update = alertDialogBuilder.create();
        dialog_update.setCancelable(false);
        dialog_update.setCanceledOnTouchOutside(false);
        dialog_update.show();
    }

    public void wordOfTheDay() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TestActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.word_of_the_day_dialog, null, false);

        TextView mWord = (TextView) view.findViewById(R.id.tvWord);
        mWord.setText(word);
        TextView mMeaning = (TextView) view.findViewById(R.id.tvMeaning);
        mMeaning.setText(meaning);
        TextView mUsage = (TextView) view.findViewById(R.id.tvUsage);
        mUsage.setText(usage);

        RelativeLayout share = (RelativeLayout) view.findViewById(R.id.rl_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "word : " + word + "\n" + "meaning : " + meaning + "\n" + "usage : " + usage);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "choose"));

            }
        });

        Button gotIt = (Button) view.findViewById(R.id.btnGotIt);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_wordOfTheDay.dismiss();
            }
        });

        alertDialogBuilder.setView(view);
        dialog_wordOfTheDay = alertDialogBuilder.create();
        dialog_wordOfTheDay.show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        exit += 1;

        if (exit == 2) {
            finish();
        }

        if (exit == 1) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            startTimer();
        }
    }

    public void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                exit = 0;
            }
        }, 2000);
    }
}
