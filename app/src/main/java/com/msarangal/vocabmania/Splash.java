package com.msarangal.vocabmania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;

public class Splash extends AppCompatActivity implements AnimationListener {

    private boolean isVocabVisible = true;
    private MySQLiteAdapter mySQLiteAdapter;
    private Boolean isBackPressed;
    private ImageView image;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private int eCap, mCap, tCap;
    private LinearLayout container;
    Animation animScaleOut;
    private int update = 0;
    private String word_of_the_day, meaning, usage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocab_splash);

        image = (ImageView) findViewById(R.id.img_v);
        container = (LinearLayout) findViewById(R.id.container);

        animScaleOut = AnimationUtils.loadAnimation(this, R.anim.scale_out);

        animScaleOut.setAnimationListener(this);

        isBackPressed = false;
        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                SharedPreferences sharedPrefs = getSharedPreferences(
                        "UserName", MODE_PRIVATE);

                String sp = sharedPrefs.getString("uname", "NA");

                if (sp.matches("NA")) {

                    //User using the app forward the first time
                    //create all the session variables

                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("uname", "VM");
                    editor.putInt("eCount", 0);
                    editor.putInt("mCount", 0);
                    editor.putInt("tCount", 0);
                    editor.putLong("eTwelve", 0);
                    editor.putLong("mTwelve", 0);
                    editor.putLong("tTwelve", 0);
                    editor.putBoolean("eTwelveBool", false);
                    editor.putBoolean("mTwelveBool", false);
                    editor.putBoolean("tTwelveBool", false);
                    editor.commit();

                    //Also create all the tables in the database
                    mySQLiteAdapter = new MySQLiteAdapter(Splash.this);
                    Bundle b = mySQLiteAdapter.checkfive_returnArray("E");

                }

                if (isBackPressed == false) {
                    // Message.message(Splash.this, "User is not new");

                    FirebaseRequest();
                    container.setVisibility(View.GONE);
                    Intent i = new Intent(Splash.this, TestActivity.class);
                    i.putExtra("update_flag", update);
                    startActivity(i);
                    finish();

                }
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;

        finish();
    }

    public void FirebaseRequest() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://boiling-torch-469.firebaseio.com");
        Query query = firebaseDatabase.getReference("/vocabmania/randomize");

        if(Utility.CheckConnectivity(Splash.this)) {
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int level = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (level == 0) {
                            eCap = Integer.parseInt(snapshot.child("capvalue").getValue(String.class));
                        } else if (level == 1) {
                            mCap = Integer.parseInt(snapshot.child("capvalue").getValue(String.class));
                        } else if (level == 2) {
                            tCap = Integer.parseInt(snapshot.child("capvalue").getValue(String.class));
                        } else if (level == 3) {
                            update = snapshot.child("update").getValue(Integer.class);
                        } else {
                            word_of_the_day = snapshot.child("word").getValue(String.class);
                            meaning = snapshot.child("meaning").getValue(String.class);
                            usage = snapshot.child("usage").getValue(String.class);
                        }
                        level++;
                    }

                    getSharedPreferences("UserName", MODE_PRIVATE).edit().putInt("eCapValue", eCap).apply();
                    getSharedPreferences("UserName", MODE_PRIVATE).edit().putInt("mCapValue", mCap).apply();
                    getSharedPreferences("UserName", MODE_PRIVATE).edit().putInt("tCapValue", tCap).apply();
                    getSharedPreferences(Constants.SP, MODE_PRIVATE).edit().putString(Constants.WORD, word_of_the_day).apply();
                    getSharedPreferences(Constants.SP, MODE_PRIVATE).edit().putString(Constants.MEANING, meaning).apply();
                    getSharedPreferences(Constants.SP, MODE_PRIVATE).edit().putString(Constants.USAGE, usage).apply();
                    container.startAnimation(animScaleOut);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);

            LayoutInflater inflater = (LayoutInflater) Splash.this.getSystemService(LAYOUT_INFLATER_SERVICE);

            View v = inflater.inflate(R.layout.no_intent_dialog, null, false);

            Button ok = (Button) v.findViewById(R.id.btnOk);

            ok.setOnClickListener(v1 -> finish());

            builder.setView(v);

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        container.setVisibility(View.GONE);
        Intent i = new Intent(Splash.this, TestActivity.class);
        i.putExtra("update_flag", update);
        startActivity(i);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
