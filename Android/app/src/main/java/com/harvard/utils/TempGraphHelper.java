package com.harvard.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.harvard.R;

import java.util.List;

/**
 * Created by Naveen Raj on 05/09/2017.
 */

public class TempGraphHelper {
    public static LineChart updateLineChart(LineChart chart, int max, List<Entry> entries, List<String> xValues, String barColor) {
        Resources res = chart.getContext().getResources();

        chart.setDrawBorders(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setYOffset(32f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(14);
        xAxis.setTextColor(R.color.black_shade);
        xAxis.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(true);
        yAxis.setDrawZeroLine(true);
        yAxis.setShowOnlyMinMax(false);
        yAxis.setTextSize(14);
        yAxis.setTextColor(R.color.black_shade);
        yAxis.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDescription("");

        LineDataSet set = new LineDataSet(entries, "");
        set.setCircleColor(Color.parseColor(barColor));
        set.setCircleRadius(4f);
        set.setDrawCircleHole(false);
        set.setColor(Color.parseColor(barColor));
        set.setLineWidth(2f);
        set.setDrawValues(true);
        set.setDrawFilled(true);
        set.setFillColor(Color.parseColor(barColor));
        set.setFillAlpha(50);

        LineData data = new LineData(xValues, set);
        if (entries.size() > 0) {
            chart.setData(data);
        }
        chart.fitScreen();
        chart.setNoDataText("No Data");
        chart.invalidate();

        return chart;
    }
}
