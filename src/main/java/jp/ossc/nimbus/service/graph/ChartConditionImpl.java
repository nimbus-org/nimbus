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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * チャート条件。<p>
 *
 * @author k2-taniguchi
 */
public class ChartConditionImpl
 implements ChartCondition, java.io.Serializable {
    
    private static final long serialVersionUID = 3418497509927014515L;
    
    /** プロット条件リスト */
    private Map plotConditionMap;
    private List plotConditionList;
    private String title;
    private String titleFontName;
    private int titleFontStyle = -1;
    private int titleFontSize = -1;
    private String defaultSubtitleFontName;
    private int defaultSubtitleFontStyle = Integer.MIN_VALUE;
    private int defaultSubtitleFontSize = Integer.MIN_VALUE;
    private Map subtitleFontNameMap;
    private Map subtitleFontStyleMap;
    private Map subtitleFontSizeMap;

    /**
     * コンストラクタ。<p>
     */
    public ChartConditionImpl() {
    }

    // ChartConditionのJavaDoc
    public void addPlotCondition(PlotCondition plotCondition) {
        if(plotConditionMap == null){
            plotConditionMap = new HashMap();
        }
        if(plotConditionList == null){
            plotConditionList = new ArrayList();
        }
        List conds = null;
        if(plotConditionMap.containsKey(plotCondition.getName())){
            conds = (List)plotConditionMap.get(plotCondition.getName());
        }else{
            conds = new ArrayList();
            plotConditionMap.put(plotCondition.getName(), conds);
        }
        conds.add(plotCondition);
        plotConditionList.add(plotCondition);
    }

    // ChartConditionのJavaDoc
    public PlotCondition[] getPlotConditions(String plotName) {
        if(plotConditionMap == null){
            return new PlotCondition[0];
        }
        if(plotConditionMap.containsKey(plotName)){
            List conds = (List)plotConditionMap.get(plotName);
            return (PlotCondition[])conds
                .toArray(new PlotCondition[conds.size()]);
        }
        return new PlotCondition[0];
    }

    // ChartConditionのJavaDoc
    public PlotCondition[] getPlotConditions() {
        if(plotConditionList == null){
            return new PlotCondition[0];
        }
        return (PlotCondition[]) plotConditionList
            .toArray(new PlotCondition[plotConditionList.size()]);
    }

    // ChartConditionのJavaDoc
    public Iterator getPlotNames() {
        return plotConditionMap.keySet().iterator();
    }

    // ChartConditionのJavaDoc
    public void setTitle(String title) {
        this.title = title;
    }

    // ChartConditionのJavaDoc
    public String getTitle() {
        return title;
    }

    // ChartConditionのJavaDoc
    public void setTitleFontName(String name) {
        titleFontName = name;
    }

    // ChartConditionのJavaDoc
    public String getTitleFontName() {
        return titleFontName;
    }

    // ChartConditionのJavaDoc
    public void setTitleFontStyle(int style) {
        titleFontStyle = style;
    }

    // ChartConditionのJavaDoc
    public int getTitleFontStyle() {
        return titleFontStyle;
    }

    // ChartConditionのJavaDoc
    public void setTitleFontSize(int size) {
        titleFontSize = size;
    }

    // ChartConditionのJavaDoc
    public int getTitleFontSize() {
        return titleFontSize;
    }

    // ChartConditionのJavaDoc
    public void setDefaultSubtitleFontName(String name) {
        defaultSubtitleFontName = name;
    }

    // ChartConditionのJavaDoc
    public String getDefaultSubtitleFontName() {
        return defaultSubtitleFontName;
    }

    // ChartConditionのJavaDoc
    public void setDefaultSubtitleFontStyle(int style) {
        defaultSubtitleFontStyle = style;
    }

    // ChartConditionのJavaDoc
    public int getDefaultSubtitleFontStyle() {
        return defaultSubtitleFontStyle;
    }

    // ChartConditionのJavaDoc
    public void setDefaultSubtitleFontSize(int size) {
        defaultSubtitleFontSize = size;
    }

    // ChartConditionのJavaDoc
    public int getDefaultSubtitleFontSize() {
        return defaultSubtitleFontSize;
    }

    // ChartConditionのJavaDoc
    public void setSubtitleFontName(int index, String name) {
        if (subtitleFontNameMap == null) {
            subtitleFontNameMap = new HashMap();
        }
        subtitleFontNameMap.put(new Integer(index), name);
    }

    // ChartConditionのJavaDoc
    public String getSubtitleFontName(int index) {
        if(subtitleFontNameMap == null){
            return null;
        }
        Integer key = new Integer(index);
        if (subtitleFontNameMap.containsKey(key)) {
            return (String) subtitleFontNameMap.get(key);
        }
        return null;
    }

    // ChartConditionのJavaDoc
    public void setSubtitleFontStyle(int index, int style) {
        if (subtitleFontStyleMap == null) {
            subtitleFontStyleMap = new HashMap();
        }
        subtitleFontStyleMap.put(new Integer(index), new Integer(style));
    }

    // ChartConditionのJavaDoc
    public int getSubtitleFontStyle(int index) {
        if(subtitleFontStyleMap == null){
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (subtitleFontStyleMap.containsKey(key)) {
            return ((Integer) subtitleFontStyleMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    // ChartConditionのJavaDoc
    public void setSubtitleFontSize(int index, int size) {
        if (subtitleFontSizeMap == null) {
            subtitleFontSizeMap = new HashMap();
        }
        subtitleFontSizeMap.put(new Integer(index), new Integer(size));
    }

    // ChartConditionのJavaDoc
    public int getSubtitleFontSize(int index) {
        if(subtitleFontSizeMap == null){
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (subtitleFontSizeMap.containsKey(key)) {
            return ((Integer) subtitleFontSizeMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

}
