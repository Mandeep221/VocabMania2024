package com.msarangal.vocabmania;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.msarangal.vocabmania.presentation.activity.AllRevisionWordsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mandeep on 4/8/2015.
 */
public class AdapterForRevision extends RecyclerView.Adapter<AdapterForRevision.MyViewHolder> {

    MySQLiteAdapter mySQLiteAdapter;
    Context context;
    private LayoutInflater inflater;
    List<WordMeaning> filteredData = Collections.emptyList();
    List<WordMeaning> unfilteredData = Collections.emptyList();

    //method used to filter the recyclerview
    public List<WordMeaning> filter(List<WordMeaning> models, String query) {
        query = query.toLowerCase();

        final List<WordMeaning> filteredModelList = new ArrayList<>();
        for (WordMeaning model : models) {
            final String text = model.word.toString().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public AdapterForRevision(Context context, List<WordMeaning> filteredData, List<WordMeaning> unfilteredData, AllRevisionWordsActivity allRevisionWordsActivity) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.filteredData = filteredData;
        this.unfilteredData = unfilteredData;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.revision_singlerow, parent, false);

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

            word = (TextView) itemView.findViewById(R.id.word);
            meaning = (TextView) itemView.findViewById(R.id.meaning);
        }

    }

}
