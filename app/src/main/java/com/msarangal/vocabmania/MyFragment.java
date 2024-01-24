package com.msarangal.vocabmania;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

/**
 * Created by Mandeep on 25/6/2015.
 */
public class MyFragment extends Fragment {

    private TextView textView, performance;
    private GraphView graphView;
    private RelativeLayout NoPerformanceMessage;
    private int arrayLength;
    private OnGettingGraphValues onGettingGraphValues;
    private Button takeTest;


    public interface OnGettingGraphValues {
        Bundle getGraphpoints(int p);
    }


    @Override
    public void onAttach(Context activity) {
        onGettingGraphValues = (OnGettingGraphValues) activity;
        super.onAttach(activity);
    }

    public MyFragment() {

    }

    public static MyFragment getInstance(int position) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int res;
        int[] rowIds;
        int[] marks;
        String[] testDates;
        int arrayLength;
        View layout = inflater.inflate(R.layout.tab_fragment, container, false);
        //   textView = (TextView) layout.findViewById(R.id.tvposition);
        performance = (TextView) layout.findViewById(R.id.tvPerformance);
        NoPerformanceMessage = (RelativeLayout) layout.findViewById(R.id.rl_no_performance);
        takeTest = (Button) layout.findViewById(R.id.btnTakeTest);

        takeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        graphView = (GraphView) layout.findViewById(R.id.graphView);


        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.parseColor("#666666"));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.parseColor("#666666"));
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
//        graphView.getGridLabelRenderer().setVerticalAxisTitle("score");
//        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(ContextCompat.getColor(getActivity(), R.color.pink_five));
//        graphView.getGridLabelRenderer().setHorizontalAxisTitle("last 5 tests");
//        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(ContextCompat.getColor(getActivity(), R.color.pink_five));
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(100);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinX(1);
        graphView.getViewport().setMaxX(5);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().isScalable();


        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency forward y values
                    return super.formatLabel(value, isValueX) + " %";
                }
            }

        });


        Bundle bundleGraphValues = new Bundle();
        Bundle bundle = getArguments();


        if (bundle.getInt("position") == 0) {

            bundleGraphValues = onGettingGraphValues.getGraphpoints(0);
            res = bundleGraphValues.getInt(MySQLiteAdapter.KEY_RESULT);
            arrayLength = bundleGraphValues.getInt(MySQLiteAdapter.KEY_ARRAY_LENGTH);
            if (res == 1) {

                assignPerformance(arrayLength, 0);
                String[] dateLabels = new String[arrayLength];
                graphView.setTitleColor(Color.parseColor("#727272"));
                marks = new int[arrayLength];
                testDates = new String[arrayLength];
                rowIds = new int[arrayLength];

                marks = bundleGraphValues.getIntArray(MySQLiteAdapter.KEY_MARKS);
                testDates = bundleGraphValues.getStringArray(MySQLiteAdapter.KEY_DATES);
                rowIds = bundleGraphValues.getIntArray(MySQLiteAdapter.KEY_ROWID);

                for (int i = 0, j = arrayLength - 1; i < arrayLength && j >= 0; i++, j--) {
                    dateLabels[i] = testDates[j];
                }

                DataPoint[] dp = new DataPoint[arrayLength];


                for (int i = arrayLength - 1, j = 1; i >= 0 && j <= arrayLength; i--, j++) {

                    dp[j - 1] = new DataPoint(j, marks[i] * 20);

                }

                PointsGraphSeries<DataPoint> series1 = new PointsGraphSeries<DataPoint>(dp);

                series1.setCustomShape(new PointsGraphSeries.CustomShape() {
                    @Override
                    public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                        paint.setStrokeWidth(2);

                        canvas.drawCircle(x, y, 12, paint);
                    }
                });
                series1.setColor(ContextCompat.getColor(getActivity(), R.color.pink_five));
                series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {

                        final Toast toast = Toast.makeText(getActivity(), "Scored " + (int) dataPoint.getY() + "%", Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1000);
                    }
                });


                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dp);

                series2.setThickness(3);
                series2.setDrawBackground(true);
                series2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.graph_color));
                series2.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                graphView.addSeries(series1);
                graphView.addSeries(series2);


            } else {
                assignPerformance(arrayLength, -1);
            }


        } else if (bundle.getInt("position") == 1) {

            bundleGraphValues = onGettingGraphValues.getGraphpoints(1);
            res = bundleGraphValues.getInt(MySQLiteAdapter.KEY_RESULT);
            arrayLength = bundleGraphValues.getInt(MySQLiteAdapter.KEY_ARRAY_LENGTH);
            if (res == 1) {

                assignPerformance(arrayLength, 1);
                graphView.setTitleColor(Color.parseColor("#727272"));
                marks = new int[arrayLength];
                testDates = new String[arrayLength];
                rowIds = new int[arrayLength];

                marks = bundleGraphValues.getIntArray(MySQLiteAdapter.KEY_MARKS);
                testDates = bundleGraphValues.getStringArray(MySQLiteAdapter.KEY_DATES);
                rowIds = bundleGraphValues.getIntArray(MySQLiteAdapter.KEY_ROWID);

                DataPoint[] dp = new DataPoint[arrayLength];
                for (int i = arrayLength - 1, j = 1; i >= 0 && j <= arrayLength; i--, j++) {

                    dp[j - 1] = new DataPoint(j, marks[i] * 20);

                }

                PointsGraphSeries<DataPoint> series1 = new PointsGraphSeries<DataPoint>(dp);

                series1.setCustomShape(new PointsGraphSeries.CustomShape() {
                    @Override
                    public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                        paint.setStrokeWidth(2);
                        // canvas.drawLine(x - 5, y - 5, x + 5, y + 5, paint);
                        // canvas.drawLine(x + 5, y - 5, x - 5, y + 5, paint);
                        canvas.drawCircle(x, y, 12, paint);
                    }
                });
                series1.setColor(ContextCompat.getColor(getActivity(), R.color.pink_five));
                series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {

                        final Toast toast = Toast.makeText(getActivity(), "Scored " + (int) dataPoint.getY() + "%", Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1000);
                    }
                });


                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dp);

                series2.setThickness(3);
                //series2.setDrawDataPoints(true);
                //series2.setDataPointsRadius(4.0f);
                series2.setDrawBackground(true);
                series2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.graph_color));
                series2.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                graphView.addSeries(series1);
                graphView.addSeries(series2);

            } else {
                assignPerformance(arrayLength, -1);
            }

        } else if (bundle.getInt("position") == 2) {

            bundleGraphValues = onGettingGraphValues.getGraphpoints(2);
            res = bundleGraphValues.getInt(MySQLiteAdapter.KEY_RESULT);
            arrayLength = bundleGraphValues.getInt(MySQLiteAdapter.KEY_ARRAY_LENGTH);
            if (res == 1) {

                assignPerformance(arrayLength, 2);
                graphView.setTitleColor(Color.parseColor("#727272"));
                marks = new int[arrayLength];
                testDates = new String[arrayLength];
                rowIds = new int[arrayLength];

                marks = bundleGraphValues.getIntArray(MySQLiteAdapter.KEY_MARKS);
                testDates = bundleGraphValues.getStringArray(MySQLiteAdapter.KEY_DATES);
                rowIds = bundleGraphValues.getIntArray(MySQLiteAdapter.KEY_ROWID);

                DataPoint[] dp = new DataPoint[arrayLength];
                for (int i = arrayLength - 1, j = 1; i >= 0 && j <= arrayLength; i--, j++) {

                    dp[j - 1] = new DataPoint(j, marks[i] * 20);

                }

                PointsGraphSeries<DataPoint> series1 = new PointsGraphSeries<DataPoint>(dp);

                series1.setCustomShape(new PointsGraphSeries.CustomShape() {
                    @Override
                    public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                        paint.setStrokeWidth(2);
                        // canvas.drawLine(x - 5, y - 5, x + 5, y + 5, paint);
                        // canvas.drawLine(x + 5, y - 5, x - 5, y + 5, paint);
                        canvas.drawCircle(x, y, 12, paint);
                    }
                });
                series1.setColor(ContextCompat.getColor(getActivity(), R.color.pink_five));
                series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {

                        final Toast toast = Toast.makeText(getActivity(), "Scored " + (int) dataPoint.getY() + "%", Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1000);
                    }
                });


                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dp);

                series2.setThickness(3);
                //series2.setDrawDataPoints(true);
                //series2.setDataPointsRadius(4.0f);
                series2.setDrawBackground(true);
                series2.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.graph_color));
                series2.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                graphView.addSeries(series1);
                graphView.addSeries(series2);

            } else {
                assignPerformance(arrayLength, -1);
            }
        }


        if (bundle != null) {
            //  textView.setText("The tab currently selected is " + bundle.getInt("position"));
        }

        return layout;
    }

    public void assignPerformance(int arrayLength, int level) {

        String lev = "";

        if (level == 0) {
            lev = "BEGINNER";
        } else if (level == 1) {
            lev = "INTERMEDIATE";
        } else if (level == 2) {
            lev = "ADVANCE";
        }

        // Message.message(getActivity(), "" + arrayLength);

        if (arrayLength > 1) {
            performance.setText("Performance in last " + arrayLength + " " + lev + " level tests");
        } else {
            if (arrayLength == 0) {
                NoPerformanceMessage.setVisibility(View.VISIBLE);
            } else {
                performance.setText("Performance in last " + arrayLength + " " + lev + " level test ");
            }
        }
    }
}