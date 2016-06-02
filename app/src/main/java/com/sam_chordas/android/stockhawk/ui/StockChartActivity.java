package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nilanjan on 30/05/2016.
 * Project: StockHawk
 */
public class StockChartActivity extends AppCompatActivity {

    private LineChartView lineChartView;
    private Cursor cursor;
    private LineSet dataSet;
    private double maxRange, minRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        lineChartView = (LineChartView) findViewById(R.id.linechart);
        Log.d("onCreate", "called");
        dataSet = new LineSet();
        Intent intent = getIntent();
        createLineChart(intent.getStringExtra(QuoteColumns.SYMBOL));
    }

    private void createLineChart(String stringExtra) {
        Log.d("Cursor", "Called");
        cursor = getContentResolver().query(
                QuoteProvider.Quotes.CONTENT_URI,
                null,
                QuoteColumns.SYMBOL + " = ?",
                new String[]{stringExtra},
                QuoteColumns.CREATED + " ASC"
        );
        showLineChart();
    }

    private void showLineChart() {
        cursor.moveToFirst();
        ArrayList<Float> bidList = new ArrayList<>();
        int i = 0;
        while (!cursor.isAfterLast()) {
            float bidPrice = Float.parseFloat(cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            bidList.add(bidPrice);
            dataSet.addPoint(i + "",
                    bidPrice
            );
            cursor.moveToNext();
            i++;
        }
        minRange = Collections.min(bidList);
        maxRange = Collections.max(bidList);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.WHITE);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));
        lineChartView.setBorderSpacing(1)
                .setAxisBorderValues((int) Math.floor(minRange), (int) Math.ceil(maxRange), 1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.WHITE)
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.FULL, gridPaint);
        dataSet.setDotsColor(Color.WHITE)
                .setColor(Color.parseColor("teal"))
                .setSmooth(true)
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.GRAY);
        lineChartView.addData(dataSet);
        lineChartView.show();
    }

}
