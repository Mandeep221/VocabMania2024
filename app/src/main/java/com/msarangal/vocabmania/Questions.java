package com.msarangal.vocabmania;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.android.volley.RequestQueue;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Questions extends AppCompatActivity implements OnClickListener,
        OnMenuItemClickListener, TextToSpeech.OnInitListener {
    private androidx.appcompat.widget.Toolbar toolbar;
    private View v;
    private TextView Time, Question, QuestionNo;
    private LinearLayout rl;
    private Animation ain, aout;
    private View ve;
    private RequestQueue requestQueue;
    int qp;
    Calendar cal1;
    private TextToSpeech tts;
    private long totalTime = 0;
    private long twelveHoursAhead;
    private String level = null;
    private String[] questionsArray = new String[5];
    private String[] op1Array = new String[5];
    private String[] op2Array = new String[5];
    private String[] op3Array = new String[5];
    private String[] words = new String[5];
    private int[] answerArray = new int[5];
    private String[] answertextArray = new String[5];
    private String[] selectedAnswerTextArray = new String[5];
    private int marks = 0;
    private SharedPreferences sharedPreferences;
    private int eCount, mCount, tCount, testNumber;
    private MySQLiteAdapter mySQLiteAdapter;
    private String uName = null;
    private Boolean isTestPaused = false;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private int exit = 0;
    private int counter = 0; // to keep track of which question is being vied by the
    // user currently

    private int[] SelectedOptionsarray = {0, 0, 0, 0, 0}; // array to store the
    // selected option by the
    // user;

    private Button option1_button, option2_button, option3_button, NextQuestion,
            PreviousQuestion, submit;
    private ImageView pronounce;
    private ProgressBar pb;
    private CountDownTimer countDownTimer;
    private Animation animScale;
    private Boolean isDBnotified, isError = false;
    long resultvalue = -2;
    private SpannableString s;
    private int capValue;
    private boolean isFirebaseActive = false;
    private boolean isResponseReceived = false;
    private CircularProgressView circularProgressView;
    private LinearLayout mainLayout, ErrorMessageLayout, appbar_container;
    private TextView ErrorMessage;
    private Button tryAgain;
    private RelativeLayout rl_op1, rl_op2, rl_op3;
    private boolean isLastQuesReached = false;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray("selectedOptions", SelectedOptionsarray);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SelectedOptionsarray = savedInstanceState.getIntArray("selectedOptions");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);

        requestQueue = VolleySingleton.getInstance().getRequestQueue();

        isDBnotified = false;

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (androidx.appcompat.widget.Toolbar) appbar_container.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(
                "UserName", MODE_PRIVATE);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);

        Intent i = getIntent();
        uName = i.getStringExtra("uName");
        level = i.getStringExtra("testCategory");
        testNumber = i.getIntExtra("testNumber", 100);

        ErrorMessageLayout = (LinearLayout) findViewById(R.id.ll_connectionMsg);
        ErrorMessage = (TextView) findViewById(R.id.tvNoConnection);
        tryAgain = (Button) findViewById(R.id.btnTryAgain);

        tryAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.CheckConnectivity(Questions.this)) {
                    // UseVolley(uName, level, testNumber);
                    FirebaseRequest(level + "_" + testNumber);
                    ErrorMessageLayout.setVisibility(View.GONE);
                    circularProgressView.setVisibility(View.VISIBLE);
                    isError = false;
                } else {
                    ErrorMessage.setText("No Internet Connection");
                    ErrorMessageLayout.setVisibility(View.VISIBLE);
                    isError = true;
                }
            }
        });

        QuestionNo = (TextView) findViewById(R.id.tvQuestionNumber);
        Question = (TextView) findViewById(R.id.tvQuestion);
        pronounce = (ImageView) findViewById(R.id.pronounceWord);
        option1_button = (Button) findViewById(R.id.btOption1);
        option2_button = (Button) findViewById(R.id.btOption2);
        option3_button = (Button) findViewById(R.id.btOption3);

        rl_op1 = (RelativeLayout) findViewById(R.id.rl_op1);
        rl_op2 = (RelativeLayout) findViewById(R.id.rl_op2);
        rl_op3 = (RelativeLayout) findViewById(R.id.rl_op3);

        NextQuestion = (Button) findViewById(R.id.btNextQuestion);
        PreviousQuestion = (Button) findViewById(R.id.btPreviousQuestion);
        submit = (Button) findViewById(R.id.submit);
        rl = (LinearLayout) findViewById(R.id.submitlayout);
        ve = findViewById(R.id.ver);

        circularProgressView = (CircularProgressView) findViewById(R.id.progress_view);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        Time = (TextView) findViewById(R.id.tvTime);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setMax(180);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular_timer);

        pb.setProgressDrawable(drawable);

        ain = AnimationUtils.loadAnimation(this, R.anim.fadin);
        aout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        rl.setVisibility(View.GONE);

        Question.setMovementMethod(new ScrollingMovementMethod());


        tts = new TextToSpeech(this, this);

        pronounce.setOnClickListener(this);
        option1_button.setOnClickListener(this);
        option2_button.setOnClickListener(this);
        option3_button.setOnClickListener(this);
        NextQuestion.setOnClickListener(this);
        PreviousQuestion.setOnClickListener(this);
        submit.setOnClickListener(this);

        if (level.equalsIgnoreCase("E")) {
            s = new SpannableString("Beginner");

        } else if (level.equalsIgnoreCase("M")) {
            s = new SpannableString("Intermediate");

        } else {
            s = new SpannableString("Advance");
        }

        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);


        //set total time
        totalTime = 180 * 1000;


        if (Utility.CheckConnectivity(Questions.this)) {
            //  UseVolley(uName, level, testNumber);
            FirebaseRequest(level + "_" + testNumber);
        } else {
            ErrorMessage.setText("No Internet Connection");
            ErrorMessageLayout.setVisibility(View.VISIBLE);
            circularProgressView.setVisibility(View.GONE);
            isError = true;
        }
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (tts.isSpeaking()) {
            tts.stop();
        }


        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isResponseReceived) {
            getMenuInflater().inflate(R.menu.questions_menu, menu);

            MenuItem pause = menu.findItem(R.id.pausetest);

            MenuItemCompat.setActionView(pause, R.layout.pause_layout);

            LinearLayout pLayout = (LinearLayout) MenuItemCompat.getActionView(pause);

            pLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    countDownTimer.cancel();

                    if (tts.isSpeaking()) {
                        tts.stop();
                    }

                    alertDialogBuilder = new AlertDialog.Builder(
                            Questions.this);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.pause_dialog, null, false);
                    Button resume = (Button) layout.findViewById(R.id.btnOk);
                    resume.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startTimer();
                            alertDialog.dismiss();
                        }
                    });


                    alertDialogBuilder.setView(layout);
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                }
            });

            final MenuItem discard = menu.findItem(R.id.discardtest);

            MenuItemCompat.setActionView(discard, R.layout.discard_layout);

            LinearLayout dLayout = (LinearLayout) MenuItemCompat.getActionView(discard);

            dLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    countDownTimer.cancel();

                    if (tts.isSpeaking()) {
                        tts.stop();
                    }

                    discardDialog();
                }
            });

            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (alertDialog == null) {
            startTimer();
        } else if (alertDialog != null && alertDialog.isShowing() == false) {
            startTimer();
        }

    }


    public void startTimer() {

        if (isFirebaseActive || isError) {
            //doNothing;
        } else {
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
                            ///long timeElapsed = 300 - ((int) (seconds / 60 * 100) + (int) (seconds % 60));
                            long timeElapsed = pb.getMax() - seconds;

                            //long barVal = timeElapsed;
                            pb.setProgress((int) timeElapsed);

                            Time.setText(String.format("%02d", seconds / 60) + ":"
                                    + String.format("%02d", seconds % 60));
                            // format the textview to show the easily readable
                            // format

                        }

                        @Override
                        public void onFinish() {
                            if (Time.getText().equals("00:00")) {

                                if (!isDBnotified) {
                                    submit.setEnabled(false);
                                    SubmitAnswers();
                                } else {
                                    Toast.makeText(Questions.this, "DB already notified", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }.start();

                }
            }, 20);
        }
    }

    public void SetFirstQuestion() {
        Question.setText(questionsArray[0]);
        option1_button.setText(op1Array[0]);
        option2_button.setText(op2Array[0]);
        option3_button.setText(op3Array[0]);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.btOption1) {
            SelectedOptionsarray[counter] = 1;
            option1_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
            option2_button.setTextColor(Color.parseColor("#666666"));
            option3_button.setTextColor(Color.parseColor("#666666"));
            rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
            rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
            rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
        } else if (id == R.id.btOption2) {
            SelectedOptionsarray[counter] = 2;
            option1_button.setTextColor(Color.parseColor("#666666"));
            option2_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
            option3_button.setTextColor(Color.parseColor("#666666"));
            rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
            rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
            rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
        } else if (id == R.id.btOption3) {
            SelectedOptionsarray[counter] = 3;
            option1_button.setTextColor(Color.parseColor("#666666"));
            option2_button.setTextColor(Color.parseColor("#666666"));
            option3_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
            rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
            rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
            rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
        } else if (id == R.id.btNextQuestion) {
            if (tts.isSpeaking() && counter < 4) {
                tts.stop();
            }
            ColorStateList cslist = NextQuestion.getTextColors();
            int colorN = cslist.getDefaultColor();

            if (colorN == Color.parseColor("#222930")) {
                v.startAnimation(animScale);
                counter++;

                qp = counter + 1;
                QuestionNo.setText("" + qp);

                if (counter > 0) {
                    PreviousQuestion.setTextColor(Color.parseColor("#222930"));
                }
                if (counter >= 4) {
                    NextQuestion.setTextColor(Color.parseColor("#CCCCCC"));
                    isLastQuesReached = true;

                    rl.setVisibility(View.VISIBLE);
                    rl.setAnimation(ain);
                    rl.startAnimation(ain);
                    ve.setVisibility(View.GONE);
                    submit.setEnabled(true);

                }

                Question.setText(questionsArray[counter]);
                option1_button.setText(op1Array[counter]);
                option2_button.setText(op2Array[counter]);
                option3_button.setText(op3Array[counter]);

                int j = SelectedOptionsarray[counter];
                if (j == 1) {
                    option1_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
                    option2_button.setTextColor(Color.parseColor("#666666"));
                    option3_button.setTextColor(Color.parseColor("#666666"));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                } else if (j == 2) {
                    option1_button.setTextColor(Color.parseColor("#666666"));
                    option2_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
                    option3_button.setTextColor(Color.parseColor("#666666"));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                } else if (j == 3) {
                    option1_button.setTextColor(Color.parseColor("#666666"));
                    option2_button.setTextColor(Color.parseColor("#666666"));
                    option3_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
                } else {
                    option1_button.setTextColor(Color.parseColor("#666666"));
                    option2_button.setTextColor(Color.parseColor("#666666"));
                    option3_button.setTextColor(Color.parseColor("#666666"));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                }

            } else {
                if (isLastQuesReached == true) {
                    Toast.makeText(this, "That's all folks!", Toast.LENGTH_SHORT)
                            .show();

                    isLastQuesReached = false;
                }

            }
        } else if (id == R.id.btPreviousQuestion) {
            if (tts.isSpeaking() && counter > 0) {
                tts.stop();
            }
            ColorStateList csl = PreviousQuestion.getTextColors();
            int colorP = csl.getDefaultColor();
            if (colorP == Color.parseColor("#222930")) {
                v.startAnimation(animScale);
                counter--;
                qp = counter + 1;
                QuestionNo.setText("" + qp);

                if (counter < 1) {
                    PreviousQuestion.setTextColor(Color.parseColor("#CCCCCC"));
                } else {
                    PreviousQuestion.setTextColor(Color.parseColor("#222930"));
                }
                if (counter < 4) {
                    NextQuestion.setTextColor(Color.parseColor("#222930"));

                    if (counter == 3) {
                        rl.setVisibility(View.GONE);
                        rl.setAnimation(aout);
                        rl.startAnimation(aout);
                        ve.setVisibility(View.VISIBLE);
                        submit.setEnabled(false);
                    }
                }

                Question.setText(questionsArray[counter]);
                option1_button.setText(op1Array[counter]);
                option2_button.setText(op2Array[counter]);
                option3_button.setText(op3Array[counter]);

                int i = SelectedOptionsarray[counter];
                if (i == 1) {
                    option1_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
                    option2_button.setTextColor(Color.parseColor("#666666"));
                    option3_button.setTextColor(Color.parseColor("#666666"));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                } else if (i == 2) {
                    option1_button.setTextColor(Color.parseColor("#666666"));
                    option2_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
                    option3_button.setTextColor(Color.parseColor("#666666"));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                } else if (i == 3) {
                    option1_button.setTextColor(Color.parseColor("#666666"));
                    option2_button.setTextColor(Color.parseColor("#666666"));
                    option3_button.setTextColor(ContextCompat.getColor(Questions.this, R.color.pink_five));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background_dark));
                } else {
                    option1_button.setTextColor(Color.parseColor("#666666"));
                    option2_button.setTextColor(Color.parseColor("#666666"));
                    option3_button.setTextColor(Color.parseColor("#666666"));
                    rl_op1.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op2.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                    rl_op3.setBackground(ContextCompat.getDrawable(Questions.this, R.drawable.layout_background));
                }

            } else {

            }
        } else if (id == R.id.submit) {
            if (isDBnotified == false) {

                SubmitAnswers();

            }
        } else if (id == R.id.pronounceWord) {
            v.startAnimation(animScale);
            //tts.speak(Question.getText().toString(),TextToSpeech.QUEUE_FLUSH, null,"first");
            tts.speak(Question.getText().toString(), TextToSpeech.QUEUE_FLUSH,
                    null);
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (isError || isFirebaseActive) {
            exit = 1;
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
        } else {
            if (countDownTimer != null)
                countDownTimer.cancel();

            if (tts.isSpeaking()) {
                tts.stop();
            }

            discardDialog();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        tts.shutdown();
    }

    public boolean onMenuItemClick(MenuItem item) {
        // TODO Auto-generated method stub

        return true;
    }

    public long AddRevisionQues(int rLev) {
        long dbRes = 100;
        for (int i = 0; i < answertextArray.length && i < words.length; i++) {
            dbRes = mySQLiteAdapter.insertRevisionQues(rLev, words[i], answertextArray[i], questionsArray[i]);
        }

        return dbRes;
    }

    public void SubmitAnswers() {

        for (int g = 0; g < 5; g++) {
            if (SelectedOptionsarray[g] == 1) {
                selectedAnswerTextArray[g] = op1Array[g];
            } else if (SelectedOptionsarray[g] == 2) {
                selectedAnswerTextArray[g] = op2Array[g];
            } else if (SelectedOptionsarray[g] == 3) {
                selectedAnswerTextArray[g] = op3Array[g];
            } else {
                selectedAnswerTextArray[g] = "NOT Answered";
            }
        }


        // how many answers are right
        for (int k = 0; k < 5; k++) {
            if (answerArray[k] == SelectedOptionsarray[k])
                marks++;
        }

        //Adding the marks in the sqlite DB
        mySQLiteAdapter = new MySQLiteAdapter(this);
        mySQLiteAdapter.insertIntoValuesForGraph(marks, level);

        //Adding the test number in SQLite DB

        if (level.equalsIgnoreCase("E")) {
            //insert the test number in SQLite DB
            mySQLiteAdapter.insertE(testNumber);

            //get the easy count to check forward multiple of 3
            eCount = sharedPreferences.getInt("eCount", 1);
            eCount = eCount + 1;

            if (eCount % 3 == 0) {
                cal1 = Calendar.getInstance(); // creates calendar
                cal1.setTime(new Date()); // sets calendar time/date
                cal1.add(Calendar.HOUR_OF_DAY, 12); // adds 12 hour
                cal1.getTime(); // returns new date object, 12 hours in the future

                twelveHoursAhead = cal1.getTimeInMillis();

                sharedPreferences.edit().putLong("eTwelve", twelveHoursAhead).apply();
            }

            sharedPreferences.edit().putInt("eCount", eCount).apply();

            //add easy revision questions in sqlite DB
            resultvalue = AddRevisionQues(0);

        } else if (level.equalsIgnoreCase("M")) {

            //insert the test number in SQLite DB
            mySQLiteAdapter.insertM(testNumber);

            //get the med count to check forward multiple of 3
            mCount = sharedPreferences.getInt("mCount", 1);
            mCount = mCount + 1;

            if (mCount % 3 == 0) {
                cal1 = Calendar.getInstance(); // creates calendar
                cal1.setTime(new Date()); // sets calendar time/date
                cal1.add(Calendar.HOUR_OF_DAY, 12); // adds 12 hour
                cal1.getTime(); // returns new date object, 12 hours in the future

                twelveHoursAhead = cal1.getTimeInMillis();

                sharedPreferences.edit().putLong("mTwelve", twelveHoursAhead).apply();
            }

            sharedPreferences.edit().putInt("mCount", mCount).apply();

            //add medium revision ques in sqlite DB
            resultvalue = AddRevisionQues(1);


        } else if (level.equalsIgnoreCase("T")) {
            //insert the test number in SQLite DB
            mySQLiteAdapter.insertT(testNumber);

            //get the tough count to check forward multiple of 3
            tCount = sharedPreferences.getInt("tCount", 1);
            tCount = tCount + 1;

            if (tCount % 3 == 0) {

                cal1 = Calendar.getInstance(); // creates calendar
                cal1.setTime(new Date()); // sets calendar time/date
                cal1.add(Calendar.HOUR_OF_DAY, 12); // adds 12 hour
                cal1.getTime(); // returns new date object, 12 hours in the future

                twelveHoursAhead = cal1.getTimeInMillis();

                sharedPreferences.edit().putLong("tTwelve", twelveHoursAhead).apply();
            }

            sharedPreferences.edit().putInt("tCount", tCount).apply();

            //add tough revision ques in sqlite DB
            resultvalue = AddRevisionQues(2);
        }

        if (resultvalue != -1) {
            isDBnotified = true;

            Intent intent = new Intent(Questions.this, Result.class);

            Bundle bundleForResultClass = new Bundle();

            bundleForResultClass.putInt("marks", marks);
            bundleForResultClass.putIntArray("correctAnswersNumbers",
                    answerArray);
            bundleForResultClass.putIntArray("selectedAnswersNumbers",
                    SelectedOptionsarray);
            bundleForResultClass.putStringArray("allQuestionsText",
                    questionsArray);
            bundleForResultClass.putStringArray("allAnswersText",
                    answertextArray);
            bundleForResultClass.putStringArray("allSelectedOptionsText",
                    selectedAnswerTextArray);
            bundleForResultClass.putStringArray("words", words);

            intent.putExtra("bundleContainingTestData", bundleForResultClass);

            countDownTimer.cancel();

            startActivity(intent);
            overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);


            // Message.message(this, "Database notified successfully");

        } else {
            // Message.message(this, "some DB error occurred!");
        }

    }

    public void FirebaseRequest(String level_attempt) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://boiling-torch-469.firebaseio.com");
        Query query = firebaseDatabase.getReference("/vocabmania/questions").orderByChild("level_attempt").equalTo(level_attempt);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                isFirebaseActive = false;

                int i = 0;
                String QUESTION_KEY = "question";
                String QUESTION_OP1_KEY = "op1";
                String QUESTION_OP2_KEY = "op2";
                String QUESTION_OP3_KEY = "op3";
                String ANSWER_KEY = "answer";
                String WORD_KEY = "word";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    questionsArray[i] = snapshot.child(QUESTION_KEY).getValue(String.class);
                    op1Array[i] = snapshot.child(QUESTION_OP1_KEY).getValue(String.class);
                    op2Array[i] = snapshot.child(QUESTION_OP2_KEY).getValue(String.class);
                    op3Array[i] = snapshot.child(QUESTION_OP3_KEY).getValue(String.class);
                    String ithAnswer = snapshot.child(ANSWER_KEY).getValue(String.class);
                    answerArray[i] = Integer.parseInt(ithAnswer);
                    words[i] = snapshot.child(WORD_KEY).getValue(String.class);

                    i++;
                }

                Questions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        circularProgressView.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                        isResponseReceived = true;
                        invalidateOptionsMenu();

                        for (int g = 0; g < 5; g++) {
                            if (answerArray[g] == 1) {
                                answertextArray[g] = op1Array[g];
                            } else if (answerArray[g] == 2) {
                                answertextArray[g] = op2Array[g];
                            } else if (answerArray[g] == 3) {
                                answertextArray[g] = op3Array[g];
                            }

                        }

                        //set 1st question
                        SetFirstQuestion();

                        if (exit == 0) {
                            startTimer();
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isFirebaseActive = false;

                ErrorMessage.setText(error.getMessage());
                ErrorMessageLayout.setVisibility(View.VISIBLE);
                circularProgressView.setVisibility(View.GONE);
            }
        });
    }

    public void discardDialog() {
        alertDialogBuilder = new AlertDialog.Builder(
                Questions.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.discard_dialog, null, false);
        Button resume = (Button) layout.findViewById(R.id.btnResume);
        resume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                alertDialog.dismiss();
            }
        });

        Button discard = (Button) layout.findViewById(R.id.btnDiscard);
        discard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                finish();
                overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setView(layout);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        alertDialog.show();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                Toast.makeText(getApplicationContext(), "Language support has been installed", Toast.LENGTH_SHORT).show();

            }

        }
    }
}
