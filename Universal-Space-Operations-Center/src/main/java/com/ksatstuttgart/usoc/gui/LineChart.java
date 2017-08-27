/*
 * The MIT License
 *
 * Copyright 2017 KSat e.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ksatstuttgart.usoc.gui;

import com.ksatstuttgart.usoc.controller.Utility;
import com.ksatstuttgart.usoc.data.message.dataPackage.DataType;
import java.awt.Color;
import java.util.HashMap;
import java.util.TreeMap;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * <h1>LineChart</h1>
 * This class creates line charts with the possibility to dynamically add new
 * data points or data series to the existing chart.
 *
 * @author Valentin Starlinger
 * @version 1.0
 */
public class LineChart extends JPanel {

    private final XYSeriesCollection dataset;
    private final JFreeChart chart;

    public LineChart(String chartname, String x, String y) {
        this(chartname, x, y, 400, 200);
    }

    public LineChart(String chartname, String x, String y, int w, int h) {
        dataset = new XYSeriesCollection();
        chart = createChart(chartname, x, y, dataset);

        this.setPreferredSize(new java.awt.Dimension(w, h));
        final ChartPanel chartPanel = new ChartPanel(chart, w, h,
                w, h, w,
                h, true, true,
                true, true, true, true);
        this.add(chartPanel);
    }

    public LineChart(String chartname, String x, String y, String seriesname, TreeMap<Long, Object> data, DataType dt) {
        dataset = new XYSeriesCollection();
        addSeries(seriesname, data, dt);
        chart = createChart(chartname, x, y, dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel);
    }

    public void addSeries(String name, TreeMap<Long, Object> data, DataType dt) {
        XYSeries s;
        try {
            s = dataset.getSeries(name);
            dataset.removeSeries(s);
            s = new XYSeries(name);
        } catch (UnknownKeyException ex) {
            s = new XYSeries(name);
        }

        for (Long time : data.keySet()) {
            switch (dt) {
                case UINT8:
                case UINT16:
                case INT8:
                case INT16:
                case BIT3:
                case BIT10:
                    s.add((long) time / 1000, (int) data.get(time));
                    break;
                case UINT32:
                    s.add((long) time / 1000, (long) data.get(time));
                    break;
                case FLOAT16:
                    s.add((long) time / 1000, (float) data.get(time));
                    break;
                case FLOAT32:
                    s.add((long) time / 1000, (double) data.get(time));
                    break;
            }
        }

        dataset.addSeries(s);
    }

    private int getSeriesNumber(String name) {
        for (Object s : dataset.getSeries()) {
            if (((XYSeries) s).getKey().equals(name)) {
                return dataset.getSeriesIndex(((XYSeries) s).getKey());
            }
        }
        return -1;
    }

    /**
     * Creates a chart.
     *
     * @param dataset the data for the chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart(String charttitle, String x, String y, final XYDataset dataset) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                charttitle, // chart title
                x, // x axis label
                y, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        chart.setAntiAlias(false);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);
        // get a reference to the plot for further customisation...
        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
        //final XYSplineRenderer r = new XYSplineRenderer();
        plot.setRenderer(r);

        // change the auto tick unit selection to integer units only...
//        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;

    }

    public void updateAxis() {
        XYPlot plot = chart.getXYPlot();

        double dw = this.getWidth() == 0 ? 1 : this.getWidth() / 35;
        double dh = this.getHeight() == 0 ? 1 : this.getHeight() / 35;

        if ((dataset.getDomainUpperBound(true) - dataset.getDomainLowerBound(true)) > 0) {
            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setRange(dataset.getDomainLowerBound(true), dataset.getDomainUpperBound(true));
            double d = (dataset.getDomainUpperBound(true) - dataset.getDomainLowerBound(true)) / dw;
            domain.setTickUnit(new NumberTickUnit((int) d));
        }

        if ((dataset.getRangeUpperBound(true) - dataset.getRangeLowerBound(true)) > 0) {
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setRange(dataset.getRangeLowerBound(true), dataset.getRangeUpperBound(true));
            double r = (dataset.getRangeUpperBound(true) - dataset.getRangeLowerBound(true)) / dh;
            range.setTickUnit(new NumberTickUnit(r));
        }

    }

    public void setSeriesShapesVisible(String series, boolean b) {
        ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setSeriesShapesFilled(getSeriesNumber(series), b);
    }

}
