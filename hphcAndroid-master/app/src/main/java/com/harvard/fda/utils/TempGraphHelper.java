/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.harvard.fda.R;

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
