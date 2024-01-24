package com.msarangal.vocabmania;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mandeep on 21/6/2015.
 */
public class AdapterForWordMeanings extends RecyclerView.Adapter<AdapterForWordMeanings.MyViewHolder> {

    private OnRecyclerViewEmpty onRecyclerViewEmpty;
    MySQLiteAdapter mySQLiteAdapter;
    PopupMenu popupMenu;
    List<WordMeaning> dataObjects, unFilteredData;

    public void InitializeData() {

        dataObjects = new ArrayList<>();
        unFilteredData = new ArrayList<>();
    }


    //method used to filter the recyclerview
    public List<WordMeaning> filter(List<WordMeaning> models, String query) {
        query = query.toLowerCase();
        mySQLiteAdapter = new MySQLiteAdapter(context);
        dataObjects = mySQLiteAdapter.getAll();

        final List<WordMeaning> filteredModelList = new ArrayList<>();
        for (WordMeaning model : dataObjects) {
            final String text = model.word.toString().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }

        return filteredModelList;
    }


    public interface OnRecyclerViewEmpty {
        public void ShowTextHideRecyclerView();

        public void EmptySearchOnDelete();
    }

    private LayoutInflater inflater;
    Animation animScale;
    Context context;
    ClickListener clickListener;
    List<WordMeaning> filteredData = Collections.emptyList();


    public AdapterForWordMeanings(Context context, List<WordMeaning> data, Favorites f) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.filteredData = data;
        animScale = AnimationUtils.loadAnimation(context, R.anim.scale_anim);
        onRecyclerViewEmpty = f;
        clickListener = f;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.favorite_singlerow, parent, false);

        Log.d("Mandy", "oncCreateHolder called");

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        WordMeaning current = filteredData.get(position);
        holder.word.setText(current.word);
        holder.meaning.setText(current.meaning);

    }

    public void delete(int position) {

        //fetching word to delete
        String word;
        WordMeaning wm = filteredData.get(position);
        word = wm.word;


        //deleting the word
        mySQLiteAdapter = new MySQLiteAdapter(context);
        mySQLiteAdapter.delete(word);
        filteredData.remove(position);
        notifyItemRemoved(position);

        //getting the original list from the database
        unFilteredData = mySQLiteAdapter.getAll();

        if (filteredData.isEmpty() && unFilteredData.isEmpty()) {
            onRecyclerViewEmpty.ShowTextHideRecyclerView();
        } else if (filteredData.isEmpty()) {
            onRecyclerViewEmpty.EmptySearchOnDelete();
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;

    }

    @Override
    public int getItemCount() {

        return filteredData.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView word;
        TextView meaning;
        Button delete;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //This is where the Pop up of item in fav is shown
                    showPopup(v);

                }
            });
            word = (TextView) itemView.findViewById(R.id.word);
            meaning = (TextView) itemView.findViewById(R.id.meaning);
            delete = (Button) itemView.findViewById(R.id.btnDelete);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animScale);
                    delete(getAdapterPosition());
                    v.setEnabled(false);
                }
            });
        }

    }

    public interface ClickListener {
        public void itemClicked(int i, View view);
    }


    public void showPopup(final View v) {

        popupMenu = new PopupMenu(context, v);
        final MenuInflater inflate = popupMenu.getMenuInflater();
        inflate.inflate(R.menu.favorites_menu, popupMenu.getMenu());

        //forward showing the icons in pop up

        try {
            Class<?> classPopupMenu = Class.forName(popupMenu
                    .getClass().getName());
            Field mPopup = classPopupMenu.getDeclaredField("mPopup");
            mPopup.setAccessible(true);
            Object menuPopupHelper = mPopup.get(popupMenu);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                    .getClass().getName());
            Method setForceIcons = classPopupHelper.getMethod(
                    "setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_learnmore) {
                    clickListener.itemClicked(0, v);

                } else if (item.getItemId() == R.id.action_share) {

                    clickListener.itemClicked(1, v);
                }
                return true;
            }
        });

    }

}
