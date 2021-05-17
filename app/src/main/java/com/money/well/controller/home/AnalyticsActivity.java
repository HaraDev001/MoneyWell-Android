package com.money.well.controller.home;

import androidx.appcompat.app.AlertDialog;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kal.rackmonthpicker.MonthType;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;
import com.money.well.R;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;

import java.util.ArrayList;
import java.util.Random;


public class AnalyticsActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private ImageView imgTopRight;
    private TextView txtTopTitle;
    public static ArrayList<Integer> monthlySavingList;
    private LineChart chtMonthlySaving;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_analytics);
        initBasicUI();

    }

    private void initBasicUI(){

        final RackMonthPicker rackMonthPicker = new RackMonthPicker(this)
                .setMonthType(MonthType.NUMBER)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        System.out.println(month);
                        System.out.println(startDate);
                        System.out.println(endDate);
                        System.out.println(year);
                        System.out.println(monthLabel);
                        Toast.makeText(getApplicationContext(), "month: " + month + ",year " + year, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });
        imgTopRight = findViewById(R.id.img_top_right);
        imgTopRight.setImageResource(R.drawable.calendar_icon);
        imgTopRight.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                rackMonthPicker.show();
            }
        });
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_analytics);
        initMonthlySavingChart();
        initMonthlyContributionChart();
    }

    public void initMonthlySavingChart(){

        monthlySavingList = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            Random rand = new Random();
            Integer initialHeart = rand.nextInt(55) + 60;
            monthlySavingList.add(initialHeart);
        }
        chtMonthlySaving = (LineChart) findViewById(R.id.cht_monthly_saving);
//        chtMonthlySaving.setOnChartValueSelectedListener(this);
        chtMonthlySaving.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.i("Entry selected", e.toString());
            }

            @Override
            public void onNothingSelected() {
                Log.i("Nothing selected", "Nothing selected.");
            }
        });


        // enable description text
        chtMonthlySaving.getDescription().setEnabled(false);

        // enable touch gestures
        chtMonthlySaving.setTouchEnabled(true);

        // enable scaling and dragging
        chtMonthlySaving.setDragEnabled(true);
        chtMonthlySaving.setScaleEnabled(true);
        chtMonthlySaving.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chtMonthlySaving.setPinchZoom(false);
        chtMonthlySaving.setAutoScaleMinMaxEnabled(false);

        // set an alternative background color
        chtMonthlySaving.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chtMonthlySaving.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chtMonthlySaving.getLegend();

        // modify the legend ...
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextColor(getResources().getColor(R.color.clr_main));

//        l.setXOffset(5f);

        XAxis xl = chtMonthlySaving.getXAxis();

        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
//        xl.setDrawLabels(false);
        xl.setAxisLineColor(getResources().getColor(R.color.clr_main));
        xl.setTextColor(getResources().getColor(R.color.clr_main));
        xl.setEnabled(true);

        YAxis leftAxis = chtMonthlySaving.getAxisLeft();
        leftAxis.setAxisMaximum(120f);
        leftAxis.setAxisMinimum(60f);
        leftAxis.setLabelCount(4, true);
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.clr_main));
        leftAxis.setTextColor(getResources().getColor(R.color.clr_main));
        leftAxis.setInverted(false);
        leftAxis.setSpaceTop(3f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chtMonthlySaving.getAxisRight();
        rightAxis.setEnabled(false);

        // init chart values
        int arraySize = monthlySavingList.size();
        int zeroCount = 0;
        /*if (lineChartXLimit > arraySize)
            zeroCount = lineChartXLimit - arraySize;

        for (int i = 0; i < zeroCount; i++) {
            addBrainAttentionEntry(0);
            addBrainMeditationEntry(0);
        }*/

        for (int i = zeroCount; i < arraySize; i++) {
            addMonthlySavingChangeEntry(monthlySavingList.get(i));
        }
        lastMonthlySavingIndex = arraySize;
    }


    public void initMonthlyContributionChart(){

        monthlySavingList = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            Random rand = new Random();
            Integer initialHeart = rand.nextInt(55) + 60;
            monthlySavingList.add(initialHeart);
        }
        chtMonthlySaving = (LineChart) findViewById(R.id.cht_monthly_contribution);
//        chtMonthlySaving.setOnChartValueSelectedListener(this);
        chtMonthlySaving.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.i("Entry selected", e.toString());
            }

            @Override
            public void onNothingSelected() {
                Log.i("Nothing selected", "Nothing selected.");
            }
        });


        // enable description text
        chtMonthlySaving.getDescription().setEnabled(false);

        // enable touch gestures
        chtMonthlySaving.setTouchEnabled(true);

        // enable scaling and dragging
        chtMonthlySaving.setDragEnabled(true);
        chtMonthlySaving.setScaleEnabled(true);
        chtMonthlySaving.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chtMonthlySaving.setPinchZoom(false);
        chtMonthlySaving.setAutoScaleMinMaxEnabled(false);

        // set an alternative background color
        chtMonthlySaving.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chtMonthlySaving.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chtMonthlySaving.getLegend();

        // modify the legend ...
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextColor(getResources().getColor(R.color.clr_main));

//        l.setXOffset(5f);

        XAxis xl = chtMonthlySaving.getXAxis();

        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
//        xl.setDrawLabels(false);
        xl.setAxisLineColor(getResources().getColor(R.color.clr_main));
        xl.setTextColor(getResources().getColor(R.color.clr_main));
        xl.setEnabled(true);

        YAxis leftAxis = chtMonthlySaving.getAxisLeft();
        leftAxis.setAxisMaximum(120f);
        leftAxis.setAxisMinimum(60f);
        leftAxis.setLabelCount(4, true);
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.clr_main));
        leftAxis.setTextColor(getResources().getColor(R.color.clr_main));
        leftAxis.setInverted(false);
        leftAxis.setSpaceTop(3f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chtMonthlySaving.getAxisRight();
        rightAxis.setEnabled(false);

        // init chart values
        int arraySize = monthlySavingList.size();
        int zeroCount = 0;
        /*if (lineChartXLimit > arraySize)
            zeroCount = lineChartXLimit - arraySize;

        for (int i = 0; i < zeroCount; i++) {
            addBrainAttentionEntry(0);
            addBrainMeditationEntry(0);
        }*/

        for (int i = zeroCount; i < arraySize; i++) {
            addMonthlySavingChangeEntry(monthlySavingList.get(i));
        }
        lastMonthlySavingIndex = arraySize;
    }

    int lastMonthlySavingIndex = 0;
    private void addMonthlySavingChangeEntry(int heartChagnedValuer) {
        LineData data = chtMonthlySaving.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet(getResources().getColor(R.color.clr_main), "");
                data.addDataSet(set);
            }

            float asa = (float) heartChagnedValuer;
            data.addEntry(new Entry(set.getEntryCount(),asa ), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chtMonthlySaving.notifyDataSetChanged();

            // limit the number of visible entries
            chtMonthlySaving.setVisibleXRangeMaximum(20f);

            // move to the latest entry
            chtMonthlySaving.moveViewToX(data.getEntryCount());
        }
    }

    // --------------------
    // ---- Chart Data ----
    // --------------------

    public LineDataSet createSet(int colorValue, String title) {

        LineDataSet set = new LineDataSet(null, title);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colorValue);
        set.setLineWidth(2f);
        set.setFillAlpha(65);
        // set the small circle on graph
        set.setDrawCircles(false);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(colorValue);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

}
