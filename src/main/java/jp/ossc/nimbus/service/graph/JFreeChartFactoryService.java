/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 *
 * Copyright 2003 The Nimbus Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.service.graph;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * JFreeChartファクトリサービス。<p>
 *
 * @author k2-taniguchi
 */
public class JFreeChartFactoryService extends ServiceBase
    implements JFreeChartFactory, JFreeChartFactoryServiceMBean {
    
    private static final long serialVersionUID = -7164526648533773901L;
    
    /** テンプレート用JFreeChart */
    private JFreeChart tmpJFreeChart;
    /** プロットファクトリサービス名 */
    private ServiceName plotFactoryServiceName;
    /** プロットファクトリ */
    private PlotFactory plotFactory;
    private List subtitles;
    /** レジェンドを生成するか(デフォルトtrue) */
    private boolean createLegend = true;

    // ServiceBaseのJavaDoc
    public void createService() throws Exception {
        tmpJFreeChart = new JFreeChart(new XYPlot());
        subtitles = new ArrayList();
    }

    // ServiceBaseのJavaDoc
    public void startService() throws Exception {
        if (plotFactoryServiceName != null) {
            plotFactory = (PlotFactory) ServiceManagerFactory.getServiceObject(plotFactoryServiceName);
        }
        if(plotFactory == null){
            throw new IllegalArgumentException(
                "PlotFactory must be specified."
            );
        }

    }

    // ServiceBaseのJavaDoc
    public void stopService() throws Exception {
        subtitles.clear();
    }

    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception {
        tmpJFreeChart = null;
        subtitles = null;
    }

    // JFreeChartFactoryのJavaDoc
    public JFreeChart getJFreeChart() {
        return tmpJFreeChart;
    }

    // JFreeChartFactoryのJavaDoc
    public JFreeChart createChart(ChartCondition chartCondition)
        throws JFreeChartCreateException {
        Plot plot = null;
        try {
            plot = plotFactory.createPlot(chartCondition.getPlotConditions());
        } catch (PlotCreateException e) {
            // プロット生成失敗
            throw new JFreeChartCreateException(e);
        }

        JFreeChart chart = copyJFreeChart(plot);
        // タイトルの設定
        if (chartCondition.getTitle() != null
            && (chartCondition.getTitleFontName() != null
                || chartCondition.getTitleFontStyle() != Integer.MIN_VALUE
                || chartCondition.getTitleFontSize() != Integer.MIN_VALUE)
        ) {
            Font newFont = null;
            TextTitle orgTitle = chart.getTitle();
            if (orgTitle != null) {
                newFont = mergeFont(
                    orgTitle.getFont(),
                    chartCondition.getTitleFontName(),
                    chartCondition.getTitleFontStyle(),
                    chartCondition.getTitleFontSize()
                );
            }

            if (newFont != null) {
                chart.setTitle(
                    new TextTitle(
                        chartCondition.getTitle(),
                        newFont
                    )
                );
            }else{
                chart.setTitle(chartCondition.getTitle());
            }
        } else if (chartCondition.getTitle() != null) {
            // タイトル文字列のみ設定された
            chart.setTitle(chartCondition.getTitle());
        }

        if (chart.getSubtitleCount() > 0) {
            List subList = chart.getSubtitles();
            String defaultFontName = chartCondition.getDefaultSubtitleFontName();
            int defaultFontStyle = chartCondition.getDefaultSubtitleFontStyle();
            int defaultFontSize = chartCondition.getDefaultSubtitleFontSize();
            if (defaultFontName != null
                || defaultFontStyle != Integer.MIN_VALUE
                || defaultFontSize != Integer.MIN_VALUE
            ) {
                // サブタイトルすべてにデフォルトのフォントを設定する。
                for (int i = 0; i < subList.size(); i++) {
                    Object subtitle = subList.get(i);
                    if (subtitle instanceof LegendTitle) {
                        LegendTitle legendTitle = (LegendTitle) subtitle;
                        legendTitle.setItemFont(
                            mergeFont(
                                legendTitle.getItemFont(),
                                defaultFontName,
                                defaultFontStyle,
                                defaultFontSize
                            )
                        );
                    } else if (subtitle instanceof TextTitle) {
                        TextTitle textTitle = (TextTitle) subtitle;
                        textTitle.setFont(
                            mergeFont(
                                textTitle.getFont(),
                                defaultFontName,
                                defaultFontStyle,
                                defaultFontSize
                            )
                        );
                    }
                }
            } else {
                // 個々のサブタイトルにフォントを設定
                for (int i = 0; i < subList.size(); i++) {
                    String subFontName = chartCondition.getSubtitleFontName(i);
                    int subFontStyle = chartCondition.getSubtitleFontStyle(i);
                    int subFontSize = chartCondition.getSubtitleFontSize(i);
                    if (subFontName == null
                        && subFontStyle == Integer.MIN_VALUE
                        && subFontSize == Integer.MIN_VALUE
                    ) {
                        continue;
                    }

                    Title subtitle = chart.getSubtitle(i);
                    if (subtitle instanceof LegendTitle) {
                        LegendTitle legendTitle = (LegendTitle) subtitle;
                        legendTitle.setItemFont(
                            mergeFont(
                                legendTitle.getItemFont(),
                                subFontName,
                                subFontStyle,
                                subFontSize
                            )
                        );
                    } else if (subtitle instanceof TextTitle) {
                        TextTitle textTitle = (TextTitle) subtitle;
                        textTitle.setFont(
                            mergeFont(
                                textTitle.getFont(),
                                subFontName,
                                subFontStyle,
                                subFontSize
                            )
                        );
                    }
                }
            }
        }

        return chart;
    }

    /**
     * 元のフォントと指定されたフォント名、フォントスタイル、フォントサイズをマージする。<p>
     * フォント名、フォントスタイル、フォントサイズが指定されていなければ、
     * 元のフォントの値を引き継ぎます。
     *
     * @param orgFont 元のフォント
     * @param fontName フォント名
     * @param fontStyle フォントスタイル
     * @param fontSize フォントサイズ
     * @return マージしたフォント
     */
    private Font mergeFont(
        Font orgFont,
        String fontName,
        int fontStyle,
        int fontSize
    ) {
        if (orgFont == null) {
            return new Font(fontName, fontStyle, fontSize);
        }

        String newName = orgFont.getName();
        int newStyle = orgFont.getStyle();
        int newSize = orgFont.getSize();
        if (fontName != null) {
            newName = fontName;
        }
        if (fontStyle != Integer.MIN_VALUE) {
            newStyle = fontStyle;
        }
        if (fontSize != Integer.MIN_VALUE) {
            newSize = fontSize;
        }
        return new Font(newName, newStyle, newSize);
    }

    /**
     * テンプレート用JFreeChartから値をコピーしたJFreeChartを生成する。<p>
     *
     * @param plot プロット
     * @return JFreeChart
     */
    private JFreeChart copyJFreeChart(Plot plot) {
        JFreeChart chart = new JFreeChart(null, null, plot, createLegend);
        chart.setAntiAlias(tmpJFreeChart.getAntiAlias());
        chart.setBackgroundImage(tmpJFreeChart.getBackgroundImage());
        chart.setBackgroundImageAlignment(tmpJFreeChart.getBackgroundImageAlignment());
        chart.setBackgroundImageAlpha(tmpJFreeChart.getBackgroundImageAlpha());
        chart.setBackgroundPaint(tmpJFreeChart.getBackgroundPaint());
        chart.setBorderPaint(tmpJFreeChart.getBorderPaint());
        chart.setBorderStroke(tmpJFreeChart.getBorderStroke());
        chart.setBorderVisible(tmpJFreeChart.isBorderVisible());
        chart.setNotify(tmpJFreeChart.isNotify());
        chart.setPadding(tmpJFreeChart.getPadding());
        chart.setRenderingHints(tmpJFreeChart.getRenderingHints());
        if (subtitles.size() > 0) {
            chart.setSubtitles(subtitles);
        }
        chart.setTitle(tmpJFreeChart.getTitle());
        if(chart.getLegend() != null){
            chart.getLegend().setItemFont(tmpJFreeChart.getLegend().getItemFont());
        }
        return chart;
    }

    // JFreeChartFactoryServiceMBeanのJavaDoc
    public ServiceName getPlotFactoryServiceName() {
        return plotFactoryServiceName;
    }

    // JFreeChartFactoryServiceMBeanのJavaDoc
    public void setPlotFactoryServiceName(ServiceName serviceName) {
        plotFactoryServiceName = serviceName;
    }

    // JFreeChartFactoryServiceMBeanのJavaDoc
    public void addSubtitle(Title title) {
        subtitles.add(title);
    }

    // JFreeChartFactoryServiceMBeanのJavaDoc
    public void setCreateLegend(boolean createLegend) {
        this.createLegend = createLegend;
    }

    public void setPlotFactory(PlotFactory factory){
        plotFactory = factory;
    }

}
