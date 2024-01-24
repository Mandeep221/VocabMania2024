package com.msarangal.vocabmania;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Locale;

/**
 * Created by Mandeep on 29/7/2015.
 */
public class Revise extends AppCompatActivity implements View.OnClickListener {

    private Boolean isBreakTimeOver;
    private Toolbar toolbar;
    private Bundle bundle;
    private int arraySize, counter;
    private String words[] = null;
    private String meanings[] = null;
    private String usages[] = null;
    private List<WordMeaning> data;
    private Button Next, Previous;
    private TextView word, meaning, usage;
    private int level = 0;
    private int exit = 0;
    private long timeLeftForBreak;
    private Intent i;
    private TextToSpeech tts;
    private RelativeLayout pronounce, more;
    private Animation animScale;
    private SpannableString s = null;
    private CountDownTimer countDownTimer;
    private int activityRef;
   // private String deviceid;
    private MySQLiteAdapter mySQLiteAdapter;
    private boolean isLastWordReached = false;
    private RelativeLayout rl_no_words;
    private LinearLayout ll_revise, appbar_container;
    private Button takeTest;
    private boolean isTestTaken = false;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.revise);

//        //get device id
//        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(this.TELEPHONY_SERVICE);
//
//        deviceid = tm.getDeviceId();

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        isBreakTimeOver = false;

        takeTest = (Button) findViewById(R.id.btnTakeTest);

        takeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        });

        ll_revise = (LinearLayout) findViewById(R.id.ll_revise);
        rl_no_words = (RelativeLayout) findViewById(R.id.rl_no_words);

        i = getIntent();

        mySQLiteAdapter = new MySQLiteAdapter(this);
        level = i.getIntExtra("level", 0);
        bundle = mySQLiteAdapter.getRevisionQues(level);

        if (bundle == null) {

            rl_no_words.setVisibility(View.VISIBLE);

        } else {
            ll_revise.setVisibility(View.VISIBLE);
            isTestTaken = true;

            arraySize = bundle.getInt("arraySize");
            timeLeftForBreak = i.getLongExtra("RemainingBreakTime", 100000);
            activityRef = i.getIntExtra("activityReference", 1);


            word = (TextView) findViewById(R.id.tvWordRevision);
            meaning = (TextView) findViewById(R.id.tvRevisionAns);
            usage = (TextView) findViewById(R.id.tvUsage);

            words = new String[arraySize];
            meanings = new String[arraySize];
            usages = new String[arraySize];

            words = bundle.getStringArray("wordsR");
            meanings = bundle.getStringArray("meaningsR");
            usages = bundle.getStringArray("usagesR");
            counter = arraySize - 1;

            word.setText(words[arraySize - 1]);
            meaning.setText(meanings[arraySize - 1]);
            usage.setText(usages[arraySize - 1]);
        }

        pronounce = (RelativeLayout) findViewById(R.id.pronounceWord);
        more = (RelativeLayout) findViewById(R.id.more);
        Next = (Button) findViewById(R.id.btNext);
        Previous = (Button) findViewById(R.id.btPrevious);

        Next.setOnClickListener(this);
        Previous.setOnClickListener(this);
        pronounce.setOnClickListener(this);
        more.setOnClickListener(this);

        if (level == 0) {
            s = new SpannableString("Beginner Revision");
        } else if (level == 1) {
            s = new SpannableString("Intermediate Revision");
        } else if (level == 2) {
            s = new SpannableString("Advanced Revision");
        }

        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            public void onInit(int status) {
                // TODO Auto-generated method stub
                tts.setLanguage(Locale.US);

            }
        });

        if (activityRef == 0) {

        } else if (activityRef == 1) {
            startTimer();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);

        icicle.putInt("counterValue", counter);
        icicle.putString("word", word.getText().toString());
        icicle.putString("meaning", meaning.getText().toString());
        icicle.putString("usage", usage.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        counter = savedInstanceState.getInt("counterValue");
        word.setText(savedInstanceState.getString("word"));
        meaning.setText(savedInstanceState.getString("meaning"));
        usage.setText(savedInstanceState.getString("usage"));

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        if (tts.isSpeaking()) {
            tts.stop();
        }


    }

    public void startTimer() {

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            public void run() {
                countDownTimer = new CountDownTimer(timeLeftForBreak, 500) {
                    // 500 means, onTick function will be called at every 500
                    // milliseconds

                    @Override
                    public void onTick(long leftTimeInMilliseconds) {
                        timeLeftForBreak = leftTimeInMilliseconds;
                        // long seconds = leftTimeInMilliseconds / 1000;
                        // int timeElapsed = 60 - ((int) (seconds / 60 * 100) + (int) (seconds % 60));

                    }

                    @Override
                    public void onFinish() {

                        isBreakTimeOver = true;

                    }
                }.start();

            }
        }, 100);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isTestTaken) {
            getMenuInflater().inflate(R.menu.revision_menu, menu);
            return true;
        } else {
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (tts.isSpeaking()) {
                tts.stop();
            }
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            return true;
        } else if (item.getItemId() == R.id.overflow) {

        } else if (item.getItemId() == R.id.find_a_word) {

            if (tts.isSpeaking()) {
                tts.stop();
            }

            //inflate the dialog layout
            LayoutInflater inflater = LayoutInflater.from(this);
            View findAwordLayout = inflater.inflate(R.layout.find_word, null, false);

            final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findAwordLayout.findViewById(R.id.et_find);

            ArrayAdapter<String> adapterForAutoComplete = new ArrayAdapter<String>(this, R.layout.find_dropdown_item, words);
            autoCompleteTextView.setAdapter(adapterForAutoComplete);

            Button cancel = (Button) findAwordLayout.findViewById(R.id.btnCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                }
            });
            Button find = (Button) findAwordLayout.findViewById(R.id.btnFind);
            find.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String typedWord = autoCompleteTextView.getText().toString();
                    int count = arraySize - 1;
                    boolean ifMatched = false;
                    while (count >= 0) {
                        if (typedWord.equalsIgnoreCase(words[count])) {
                            counter = count;
                            word.setText(words[counter]);
                            meaning.setText(meanings[counter]);
                            usage.setText(usages[counter]);

                            if (counter < arraySize - 1) {
                                Previous.setTextColor(Color.parseColor("#222930"));
                                if (counter == 0) {
                                    Next.setTextColor(Color.parseColor("#CCCCCC"));
                                } else {
                                    Next.setTextColor(Color.parseColor("#222930"));
                                }
                            } else if (counter == arraySize - 1) {
                                Previous.setTextColor(Color.parseColor("#CCCCCC"));
                                Next.setTextColor(Color.parseColor("#222930"));

                            }

                            ifMatched = true;
                            break;
                        }
                        count--;
                    }

                    if (ifMatched == false) {
                        Message.message(Revise.this, "No such word found...");
                    }
                    alertDialog.dismiss();
                }
            });

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            //asign the layout to the dialog
            alertDialogBuilder.setView(findAwordLayout);
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            return true;
        } else if (item.getItemId() == R.id.see_all_words) {

            i = new Intent(this, AllRevisionWords.class);
            i.putExtra("level", level);
            startActivity(i);
            overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
            return true;
        } else if (item.getItemId() == R.id.share_this_) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Word : " + words[counter] + "\n" + "Meaning : " + meanings[counter] + "\n" + "Usage : " + usages[counter]);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "select an option"));
            return true;

        } else if (item.getItemId() == R.id.go_to_last) {

            if (counter == 0) {
                Message.message(this, "You are already on the last word");
            } else {

                if (tts.isSpeaking()) {
                    tts.stop();
                }

                counter = 0;
                word.setText(words[counter]);
                meaning.setText(meanings[counter]);
                usage.setText(usages[counter]);
                Previous.setTextColor(Color.parseColor("#222930"));
                Next.setTextColor(Color.parseColor("#CCCCCC"));
            }
            return true;
        } else if (item.getItemId() == R.id.go_to_first) {

            if (counter == arraySize - 1) {
                Message.message(this, "You are already on the first word");
            } else {

                if (tts.isSpeaking()) {
                    tts.stop();
                }

                counter = arraySize - 1;
                word.setText(words[counter]);
                meaning.setText(meanings[counter]);
                usage.setText(usages[counter]);
                Previous.setTextColor(Color.parseColor("#CCCCCC"));
                Next.setTextColor(Color.parseColor("#222930"));
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btNext) {

            if (tts.isSpeaking() && counter > 0) {
                tts.stop();
            }

            ColorStateList csl = Next.getTextColors();
            int colorN = csl.getDefaultColor();

            if (colorN == Color.parseColor("#222930")) {
                v.startAnimation(animScale);
                counter--;

                word.setText(words[counter]);
                meaning.setText(meanings[counter]);
                usage.setText(usages[counter]);

                if (counter == 0) {
                    isLastWordReached = true;
                    Next.setTextColor(Color.parseColor("#CCCCCC"));
                } else {
                    // Message.message(this, "" + counter);
                    Previous.setTextColor(Color.parseColor("#222930"));
                }
            } else {
                if (isLastWordReached) {
                    Toast.makeText(this, "That's all folks!", Toast.LENGTH_SHORT)
                            .show();

                    isLastWordReached = false;
                }

            }

        } else if (v.getId() == R.id.btPrevious) {

            if (tts.isSpeaking() && counter < arraySize - 1) {
                tts.stop();
            }

            ColorStateList csl = Previous.getTextColors();
            int colorP = csl.getDefaultColor();

            if (colorP == Color.parseColor("#222930")) {

                v.startAnimation(animScale);
                counter++;
                word.setText(words[counter]);
                meaning.setText(meanings[counter]);
                usage.setText(usages[counter]);

                if (counter == arraySize - 1) {
                    Previous.setTextColor(Color.parseColor("#CCCCCC"));
                    // Next.setTextColor(Color.parseColor("#222930"));
                } else {
                    // Message.message(this, "" + counter);
                    Previous.setTextColor(Color.parseColor("#222930"));
                    Next.setTextColor(Color.parseColor("#222930"));

                }
            } else {
                // Message.message(this, "This is the first word");
            }

        } else if (v.getId() == R.id.pronounceWord) {
            v.startAnimation(animScale);
            //tts.speak(Question.getText().toString(),TextToSpeech.QUEUE_FLUSH, null,"first");
            tts.speak(word.getText().toString(), TextToSpeech.QUEUE_FLUSH,
                    null);
        } else if (v.getId() == R.id.more) {
            String WORD;
            WORD = word.getText().toString();
            Intent i = new Intent(Revise.this, LearnMoreFav.class);
            i.putExtra("link", "http://thefreedictionary.com/" + WORD);
            startActivity(i);
            overridePendingTransition(R.anim.card_slide_right_in, R.anim.card_slide_left_out);
        }

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;

        if (tts.isSpeaking()) {
            tts.stop();
        }

        if (isBreakTimeOver == false) {
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        tts.shutdown();
    }

}
