package com.msarangal.vocabmania.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msarangal.vocabmania.AdapterForWordMeanings;
import com.msarangal.vocabmania.MySQLiteAdapter;
import com.msarangal.vocabmania.R;
import com.msarangal.vocabmania.TypefaceSpan;
import com.msarangal.vocabmania.WordMeaning;

import java.util.ArrayList;
import java.util.List;

;


/**
 * Created by Mandeep on 21/6/2015.
 */
public class FavoritesActivity extends AppCompatActivity implements AdapterForWordMeanings.ClickListener, AdapterForWordMeanings.OnRecyclerViewEmpty {

    private TextView tvTitle;
    private View v;

    private Toolbar toolbar;
    private RecyclerView recyclerview;
    private AdapterForWordMeanings adapter;
    private Context context;
    private List<WordMeaning> data;
    private List<WordMeaning> filteredList;
    private CardView cardView;
    private TextView noFavorites;
    private EditText searchFav;
    private TextView noFav;
    private MySQLiteAdapter mySQLiteAdapter;
    private int exit = 0;
    private LinearLayout appbar_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);

        mySQLiteAdapter = new MySQLiteAdapter(this);

        //favids = new ArrayList<Integer>();
        context = this;
        data = new ArrayList<>();

        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        SpannableString s = new SpannableString("Favorites");
        s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setTitle(s);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        searchFav = (EditText) findViewById(R.id.etSearchFav);
        noFavorites = (TextView) findViewById(R.id.NoFavorites);
        noFav = (TextView) findViewById(R.id.NoFav);


        cardView = (CardView) findViewById(R.id.card_viewSearch);

        searchFav.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filteredList = adapter.filter(data, s.toString());
                recyclerview.scrollToPosition(0);
                adapter = new AdapterForWordMeanings(context, filteredList, FavoritesActivity.this);
                adapter.setClickListener(FavoritesActivity.this);
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


        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(1000);
        recyclerview.setItemAnimator(defaultItemAnimator);

        data = mySQLiteAdapter.getAll();
        if (data.isEmpty()) {
            noFavorites.setVisibility(View.VISIBLE);
            noFav.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        } else {
            //filteredList = adapter.filter(data, searchFav.getText().toString());
            adapter = new AdapterForWordMeanings(context, data, FavoritesActivity.this);
            adapter.setClickListener(FavoritesActivity.this);
            recyclerview.setAdapter(adapter);
            recyclerview.setLayoutManager(new LinearLayoutManager(context));
            cardView.setVisibility(View.VISIBLE);

            adapter.InitializeData();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void itemClicked(int choice, View view) {

        if (choice == 0) {
            String word;
            TextView tv = (TextView) view.findViewById(R.id.word);
            word = tv.getText().toString();
            Intent i = new Intent(FavoritesActivity.this, LearnMoreFavActivity.class);
            i.putExtra("link", "http://thefreedictionary.com/" + word);
            startActivity(i);
        } else if (choice == 1) {

            String word;
            String meaning;
            TextView tvWord = (TextView) view.findViewById(R.id.word);
            TextView tvMeaning = (TextView) view.findViewById(R.id.meaning);
            word = tvWord.getText().toString();
            meaning = tvMeaning.getText().toString();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "word : " + word + "\n" + "meaning : " + meaning);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "choose"));
        }

    }

    @Override
    public void ShowTextHideRecyclerView() {

        noFavorites.setVisibility(View.VISIBLE);
        noFav.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);


    }

    @Override
    public void EmptySearchOnDelete() {

        noFavorites.setText("No match found");
        noFavorites.setVisibility(View.VISIBLE);
    }

    public void ShowMessageOnEmptySearch(int i) {

        if (i == 0)   //empty search
        {
            noFavorites.setText("No match found");
            noFavorites.setVisibility(View.VISIBLE);

        } else if (i == 1)// non empty search
        {
            noFavorites.setVisibility(View.GONE);
            noFavorites.setText("No Favorites to show");
        }

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }


}
