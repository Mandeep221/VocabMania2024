package com.msarangal.vocabmania.presentation.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msarangal.vocabmania.AdapterForRevision;
import com.msarangal.vocabmania.MySQLiteAdapter;
import com.msarangal.vocabmania.R;
import com.msarangal.vocabmania.TypefaceSpan;
import com.msarangal.vocabmania.WordMeaning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mandeep on 4/8/2015.
 */
public class AllRevisionWordsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout appbar_container;
    private RecyclerView recyclerview;
    private EditText search;
    private TextView emptySearch;
    private MySQLiteAdapter mySQLiteAdapter;
    private Intent i;
    private int level, totalNumberOfWords;
    private AdapterForRevision adapter;
    private AlertDialog alertDialog;
    private Context context;
    private List<WordMeaning> filteredList;
    private List<WordMeaning> data;
    private SpannableString s;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.allrevisionwords);

        mySQLiteAdapter = new MySQLiteAdapter(this);

        data = new ArrayList<>();

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);

        emptySearch = (TextView) findViewById(R.id.emptySearch);
        emptySearch.setVisibility(View.GONE);
        context = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        i = getIntent();
        level = i.getIntExtra("level", 0);
        if (level == 0) {
            categoryName = "BEGINNER";
            s = new SpannableString("All Beginner");
        } else if (level == 1) {
            categoryName = "INTERMEDIATE";
            s = new SpannableString("All Intermediate");
        } else if (level == 2) {
            categoryName = "ADVANCED";
            s = new SpannableString("All Advanced");
        }

        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        search = (EditText) findViewById(R.id.etSearchFav);

        data = mySQLiteAdapter.getRevisionForRecyclerView(level);
        totalNumberOfWords = data.size();
        adapter = new AdapterForRevision(context, data, data, AllRevisionWordsActivity.this);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filteredList = adapter.filter(data, s.toString());
                recyclerview.scrollToPosition(0);
                adapter = new AdapterForRevision(context, filteredList, data, AllRevisionWordsActivity.this);
                recyclerview.setAdapter(adapter);
                recyclerview.setLayoutManager(new LinearLayoutManager(context));

                if (filteredList.isEmpty() == true) {
                    ShowMessageOnEmptySearch(0);
                } else if (filteredList.isEmpty() == false) {
                    ShowMessageOnEmptySearch(1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.allwordsrevision, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            return true;
        } else if (item.getItemId() == R.id.total_words) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.all_words_dialog, null, false);

            TextView tv = (TextView) layout.findViewById(R.id.tv);
            tv.setText("There are a total of " + totalNumberOfWords + " " + categoryName + " level words for you to revise...");

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
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public void ShowMessageOnEmptySearch(int i) {

        if (i == 0)   //empty search
        {
            emptySearch.setText("No match found");
            emptySearch.setVisibility(View.VISIBLE);

        } else if (i == 1)// non empty search
        {
            emptySearch.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }
}
