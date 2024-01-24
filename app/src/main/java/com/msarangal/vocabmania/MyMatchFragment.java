package com.msarangal.vocabmania;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Mandeep on 28/6/2015.
 */
public class MyMatchFragment extends Fragment {

    TextView title, question, correctAnswer, yourAnswer, right, wrong, unanswered;
    String Que, CorrectAns, YourAns;
    int rightwrong;
    OnGettingMatchData onGettingMatchData;


    public MyMatchFragment() {

    }

    public static MyMatchFragment getInstance(int position) {
        MyMatchFragment fragment = new MyMatchFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.mymatchfragment, container, false);

        question = (TextView) layout.findViewById(R.id.tvQueDialog);
        correctAnswer = (TextView) layout.findViewById(R.id.tvCorrectAns);
        yourAnswer = (TextView) layout.findViewById(R.id.tvYourAns);
        right = (TextView) layout.findViewById(R.id.tvTick);
        wrong = (TextView) layout.findViewById(R.id.tvCross);
        //unanswered = (TextView) layout.findViewById(R.id.tvUnanswerd);

        Bundle bundleFromMatch;
        Bundle bundleArgs = getArguments();

        if (bundleArgs.getInt("position") == 0) {
            bundleFromMatch = onGettingMatchData.getMatchData(0);

            rightwrong = bundleFromMatch.getInt("RightOrWrong");
            if (rightwrong == 1) {
                right.setVisibility(View.VISIBLE);
            } else if (rightwrong == 0) {
                wrong.setVisibility(View.VISIBLE);
            } else {
                //unanswered.setVisibility(View.VISIBLE);
            }
            Que = bundleFromMatch.getString("Q");
            CorrectAns = bundleFromMatch.getString("C");
            YourAns = bundleFromMatch.getString("Y");

            question.setText(Que);
            correctAnswer.setText(CorrectAns);
            yourAnswer.setText(YourAns);
        } else if (bundleArgs.getInt("position") == 1) {
            bundleFromMatch = onGettingMatchData.getMatchData(1);

            rightwrong = bundleFromMatch.getInt("RightOrWrong");
            if (rightwrong == 1) {
                right.setVisibility(View.VISIBLE);
            } else if (rightwrong == 0) {
                wrong.setVisibility(View.VISIBLE);
            } else {
               // unanswered.setVisibility(View.VISIBLE);
            }

            Que = bundleFromMatch.getString("Q");
            CorrectAns = bundleFromMatch.getString("C");
            YourAns = bundleFromMatch.getString("Y");

            question.setText(Que);
            correctAnswer.setText(CorrectAns);
            yourAnswer.setText(YourAns);

        } else if (bundleArgs.getInt("position") == 2) {
            bundleFromMatch = onGettingMatchData.getMatchData(2);

            rightwrong = bundleFromMatch.getInt("RightOrWrong");
            if (rightwrong == 1) {
                right.setVisibility(View.VISIBLE);
            } else if (rightwrong == 0) {
                wrong.setVisibility(View.VISIBLE);
            } else {
               // unanswered.setVisibility(View.VISIBLE);
            }

            Que = bundleFromMatch.getString("Q");
            CorrectAns = bundleFromMatch.getString("C");
            YourAns = bundleFromMatch.getString("Y");

            question.setText(Que);
            correctAnswer.setText(CorrectAns);
            yourAnswer.setText(YourAns);

        } else if (bundleArgs.getInt("position") == 3) {
            bundleFromMatch = onGettingMatchData.getMatchData(3);

            rightwrong = bundleFromMatch.getInt("RightOrWrong");
            if (rightwrong == 1) {
                right.setVisibility(View.VISIBLE);
            } else if (rightwrong == 0) {
                wrong.setVisibility(View.VISIBLE);
            } else {
               // unanswered.setVisibility(View.VISIBLE);
            }

            Que = bundleFromMatch.getString("Q");
            CorrectAns = bundleFromMatch.getString("C");
            YourAns = bundleFromMatch.getString("Y");

            question.setText(Que);
            correctAnswer.setText(CorrectAns);
            yourAnswer.setText(YourAns);

        } else if (bundleArgs.getInt("position") == 4) {
            bundleFromMatch = onGettingMatchData.getMatchData(4);

            rightwrong = bundleFromMatch.getInt("RightOrWrong");
            if (rightwrong == 1) {
                right.setVisibility(View.VISIBLE);
            } else if (rightwrong == 0) {
                wrong.setVisibility(View.VISIBLE);
            } else {
               // unanswered.setVisibility(View.VISIBLE);
            }

            Que = bundleFromMatch.getString("Q");
            CorrectAns = bundleFromMatch.getString("C");
            YourAns = bundleFromMatch.getString("Y");

            question.setText(Que);
            correctAnswer.setText(CorrectAns);
            yourAnswer.setText(YourAns);

        }

        return layout;
    }

    public interface OnGettingMatchData {
        public Bundle getMatchData(int p);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        onGettingMatchData = (OnGettingMatchData) activity;

    }


}
