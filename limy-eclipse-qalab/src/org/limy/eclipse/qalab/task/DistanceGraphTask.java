/*
 * Created 2007/02/07
 * Copyright (C) 2003-2009  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * Limy Eclipse Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Limy Eclipse Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Limy Eclipse Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.limy.eclipse.qalab.task;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;
import org.limy.eclipse.qalab.common.LimyGraphUtils;
import org.limy.xml.XmlUtils;
import org.limy.xml.XmlXpathUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * パッケージ毎のメインシーケンスからの距離をグラフ化するAntタスクです。
 * @author Naoki Iwami
 */
public class DistanceGraphTask extends Task {

    // ------------------------ Fields

    /** JDepend-xmlファイル */
    private File in;

    /** 出力pngファイル */
    private File out;

//    public static void main(String[] args) {
//        DistanceGraphTask task = new DistanceGraphTask();
//        task.setXmlFile(new File("C:\\var\\home\\prog\\v6\\dest\\jdepend_report.xml"));
////        task.setXmlFile(new File("C:\\var\\home\\prog\\plugin_dev\\limy-eclipse-qalab\\dest\\jdepend_report.xml"));
//        task.execute();
//    }
    
    // ------------------------ Override Methods

    @Override
    public void execute() {
        XYDataset dataset = createDataset();
        try {
            drawGraph(dataset);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    // ------------------------ Getter/Setter Methods

    /**
     * JDepend-xmlファイルを設定します。
     * @param in JDepend-xmlファイル
     */
    public void setIn(File in) {
        this.in = in;
    }

    /**
     * 出力pngファイルを設定します。
     * @param out 出力pngファイル
     */
    public void setOut(File out) {
        this.out = out;
    }

    /**
     * @return
     */
    private XYDataset createDataset() {
        
        try {
            Element root = XmlUtils.parse(in);
            return createDataset(root);
        } catch (TransformerException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * @param root
     * @return 
     * @throws TransformerException 
     */
    private DefaultXYDataset createDataset(Element root) throws TransformerException {
        
        DefaultXYDataset dataset = new DefaultXYDataset();
        List<Point2D> points = new ArrayList<Point2D>();

        Iterable<Node> nodes = XmlXpathUtils.getNodeList(root, "/JDepend/Packages/Package");
        for (Node node : nodes) {
            if (XPathAPI.selectSingleNode(node, "Stats") == null) {
                continue;
            }
            double i = Double.parseDouble(XPathAPI.selectSingleNode(node, "Stats/I")
                    .getFirstChild().getNodeValue());
            double a = Double.parseDouble(XPathAPI.selectSingleNode(node, "Stats/A")
                    .getFirstChild().getNodeValue());

            log("I = " + i + ", A = " + a);
            points.add(new Point2D.Double(i, a));
        }

        double[][] values = new double[2][points.size()];
        for (int i = 0; i < points.size(); i++) {
            values[0][i] = points.get(i).getX();
            values[1][i] = points.get(i).getY();
        }
        dataset.addSeries("key", values);
        
        return dataset;
    }

    /**
     * @param dataset 
     * @throws IOException 
     * 
     */
    private void drawGraph(XYDataset dataset) throws IOException {

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Distance from the Main Sequence", "Instability", "Abstractness",
                dataset, PlotOrientation.VERTICAL,
                false, false, false);

        XYPlot plot = chart.getXYPlot();

        plot.getRenderer().addAnnotation(
                new XYLineAnnotation(-0.1, 1.1, 1.1, -0.1,
                        new BasicStroke(2), new Color(50, 220, 50)), Layer.BACKGROUND);
        
        plot.getRenderer().setShape(new Ellipse2D.Double(-4, -4, 8, 8));
        plot.getRenderer().setPaint(new Color(0xec, 0x76, 0x37));
        
        plot.getDomainAxis().setRangeWithMargins(0, 1);
        plot.getRangeAxis().setRangeWithMargins(0, 1);

        chart.getTitle().setPaint(Color.BLUE);
        
        plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairPaint(Color.GRAY);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.GRAY);
        
        LimyGraphUtils.writeImagePng(chart, out, 400, 380);

    }
    
}
