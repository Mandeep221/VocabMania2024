package com.msarangal.vocabmania.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.msarangal.vocabmania.R;

/**
 *
 * Created by Mandeep Sarangal on 4/15/2016.
 */
public class AllWordsOverActivity extends AppCompatActivity {

    private int count;
    private String lev;
    private TextView tv, tvTwo;
    private RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.words_finish);

        back = (RelativeLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            }
        });
        tv = (TextView) findViewById(R.id.tv);
        tvTwo = (TextView) findViewById(R.id.tvTwo);

        Intent i = getIntent();

        if (i != null) {
            count = i.getIntExtra("wordCount", 0);
            lev = i.getStringExtra("level");
        }

        tv.setText("You have successfully learned " + count + " " + lev + " level words.");
        tvTwo.setText("We will be adding more words in " + lev + " level soon");
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }
}
