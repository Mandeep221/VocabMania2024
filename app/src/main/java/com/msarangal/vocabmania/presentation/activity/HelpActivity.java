package com.msarangal.vocabmania.presentation.activity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.msarangal.vocabmania.R;
import com.msarangal.vocabmania.TypefaceSpan;

/**
 * Created by Mandeep on 3/7/2015.
 */
public class HelpActivity extends AppCompatActivity {

    Toolbar toolbar;
    int exit = 0;
    TextView tvTitle;
    View v;
    private LinearLayout appbar_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        SpannableString s = new SpannableString("Help");
        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }
}
