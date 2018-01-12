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

import java.util.HashMap;
import java.util.Map;

public class XYPlotConditionImpl extends PlotConditionImpl
    implements XYPlotCondition {
    
    private static final long serialVersionUID = 8417050199185753740L;
    
    /** ècé≤ÇÃâ¬éãèÛë‘ */
    private Map rangeAxisVisibleMap = null;
    private String defaultDomainAxisTickLabelFontName;
    private int defaultDomainAxisTickLabelFontStyle = Integer.MIN_VALUE;
    private int defaultDomainAxisTickLabelFontSize = Integer.MIN_VALUE;
    private String defaultRangeAxisTickLabelFontName;
    private int defaultRangeAxisTickLabelFontStyle = Integer.MIN_VALUE;
    private int defaultRangeAxisTickLabelFontSize = Integer.MIN_VALUE;
    private Map domainAxisTickLabelFontNameMap;
    private Map domainAxisTickLabelFontStyleMap;
    private Map domainAxisTickLabelFontSizeMap;
    private Map rangeAxisTickLabelFontNameMap;
    private Map rangeAxisTickLabelFontStyleMap;
    private Map rangeAxisTickLabelFontSizeMap;
    private String defaultDomainAxisLabelFontName;
    private int defaultDomainAxisLabelFontStyle = Integer.MIN_VALUE;
    private int defaultDomainAxisLabelFontSize = Integer.MIN_VALUE;
    private String defaultRangeAxisLabelFontName;
    private int defaultRangeAxisLabelFontStyle = Integer.MIN_VALUE;
    private int defaultRangeAxisLabelFontSize = Integer.MIN_VALUE;
    private Map domainAxisLabelFontNameMap;
    private Map domainAxisLabelFontStyleMap;
    private Map domainAxisLabelFontSizeMap;
    private Map rangeAxisLabelFontNameMap;
    private Map rangeAxisLabelFontStyleMap;
    private Map rangeAxisLabelFontSizeMap;

    // XYPlotConditionÇÃJavaDoc
    public void setRangeAxisVisible(int index, Boolean visible) {
        if(rangeAxisVisibleMap == null){
            rangeAxisVisibleMap = new HashMap();
        }
        rangeAxisVisibleMap.put(new Integer(index), visible);
    }

    // XYPlotConditionÇÃJavaDoc
    public Boolean isRangeAxisVisible(int index) {
        if(rangeAxisVisibleMap == null){
            return null;
        }
        Integer key = new Integer(index);
        if (rangeAxisVisibleMap.containsKey(key)) {
            return (Boolean) rangeAxisVisibleMap.get(key);
        }
        return null;
    }

    public Map getRangeAxisVisibleMap() {
        return rangeAxisVisibleMap;
    }

    public void addRangeAxisVisibleMap(Map map) {
        if (rangeAxisVisibleMap == null) {
            rangeAxisVisibleMap = new HashMap();
        }
        rangeAxisVisibleMap.putAll(map);
    }

//    public Map getDomainAxisTickLabelFontMap() {
//        return domainAxisTickLabelFontMap;
//    }
//
//    public void addDomainAxisTickLabelFontMap(Map map) {
//        if(domainAxisTickLabelFontMap == null){
//            domainAxisTickLabelFontMap = new HashMap();
//        }
//        domainAxisTickLabelFontMap.putAll(map);
//    }

    public void setDefaultDomainAxisTickLabelFontName(String name) {
        defaultDomainAxisTickLabelFontName = name;
    }

    public String getDefaultDomainAxisTickLabelFontName() {
        return defaultDomainAxisTickLabelFontName;
    }

    public void setDefaultDomainAxisTickLabelFontStyle(int style) {
        defaultDomainAxisTickLabelFontStyle = style;
    }

    public int getDefaultDomainAxisTickLabelFontStyle() {
        return defaultDomainAxisTickLabelFontStyle;
    }

    public void setDefaultDomainAxisTickLabelFontSize(int size) {
        defaultDomainAxisTickLabelFontSize = size;
    }

    public int getDefaultDomainAxisTickLabelFontSize() {
        return defaultDomainAxisTickLabelFontSize;
    }

    public void setDefaultRangeAxisTickLabelFontName(String name) {
        defaultRangeAxisTickLabelFontName = name;
    }

    public String getDefaultRangeAxisTickLabelFontName() {
        return defaultRangeAxisTickLabelFontName;
    }

    public void setDefaultRangeAxisTickLabelFontStyle(int style) {
        defaultRangeAxisTickLabelFontStyle = style;
    }

    public int getDefaultRangeAxisTickLabelFontStyle() {
        return defaultRangeAxisTickLabelFontStyle;
    }

    public void setDefaultRangeAxisTickLabelFontSize(int size) {
        defaultRangeAxisTickLabelFontSize = size;
    }

    public int getDefaultRangeAxisTickLabelFontSize() {
        return defaultRangeAxisTickLabelFontSize;
    }

    public void setDomainAxisTickLabelFontName(int index, String name) {
        if (domainAxisTickLabelFontNameMap == null) {
            domainAxisTickLabelFontNameMap = new HashMap();
        }
        domainAxisTickLabelFontNameMap.put(new Integer(index), name);
    }

    public String getDomainAxisTickLabelFontName(int index) {
        if (domainAxisTickLabelFontNameMap == null) {
            return null;
        }
        Integer key = new Integer(index);
        if(domainAxisTickLabelFontNameMap.containsKey(key)){
            return (String) domainAxisTickLabelFontNameMap.get(key);
        }
        return null;
    }

    public Map getDomainAxisTickLabelFontNameMap() {
        return domainAxisTickLabelFontNameMap;
    }

    public void addDomainAxisTickLabelFontNameMap(Map map) {
        if (domainAxisTickLabelFontNameMap == null) {
            domainAxisTickLabelFontNameMap = new HashMap();
        }
        domainAxisTickLabelFontNameMap.putAll(map);
    }

    public void setDomainAxisTickLabelFontStyle(int index, int style) {
        if (domainAxisTickLabelFontStyleMap == null) {
            domainAxisTickLabelFontStyleMap = new HashMap();
        }
        domainAxisTickLabelFontStyleMap.put(new Integer(index), new Integer(style));
    }

    public int getDomainAxisTickLabelFontStyle(int index) {
        if (domainAxisTickLabelFontStyleMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (domainAxisTickLabelFontStyleMap.containsKey(key)) {
            return ((Integer)domainAxisTickLabelFontStyleMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getDomainAxisTickLabelFontStyleMap() {
        return domainAxisTickLabelFontStyleMap;
    }

    public void addDomainAxisTickLabelFontStyleMap(Map map) {
        if (domainAxisTickLabelFontStyleMap == null) {
            domainAxisTickLabelFontStyleMap = new HashMap();
        }
        domainAxisTickLabelFontStyleMap.putAll(map);
    }

    public void setDomainAxisTickLabelFontSize(int index, int size) {
        if (domainAxisTickLabelFontSizeMap == null) {
            domainAxisTickLabelFontSizeMap = new HashMap();
        }
        domainAxisTickLabelFontSizeMap.put(new Integer(index), new Integer(size));
    }

    public int getDomainAxisTickLabelFontSize(int index) {
        if (domainAxisTickLabelFontSizeMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (domainAxisTickLabelFontSizeMap.containsKey(key)) {
            return ((Integer)domainAxisTickLabelFontSizeMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getDomainAxisTickLabelFontSizeMap() {
        return domainAxisTickLabelFontSizeMap;
    }

    public void addDomainAxisTickLabelFontSizeMap(Map map) {
        if (domainAxisTickLabelFontSizeMap == null) {
            domainAxisTickLabelFontSizeMap = new HashMap();
        }
        domainAxisTickLabelFontSizeMap.putAll(map);
    }

    public void setRangeAxisTickLabelFontName(int index, String name) {
        if (rangeAxisTickLabelFontNameMap == null) {
            rangeAxisTickLabelFontNameMap = new HashMap();
        }
        rangeAxisTickLabelFontNameMap.put(new Integer(index), name);
    }

    public String getRangeAxisTickLabelFontName(int index) {
        if (rangeAxisTickLabelFontNameMap == null) {
            return null;
        }
        Integer key = new Integer(index);
        if(rangeAxisTickLabelFontNameMap.containsKey(key)){
            return (String) rangeAxisTickLabelFontNameMap.get(key);
        }
        return null;
    }

    public Map getRangeAxisTickLabelFontNameMap() {
        return rangeAxisTickLabelFontNameMap;
    }

    public void addRangeAxisTickLabelFontNameMap(Map map) {
        if (rangeAxisTickLabelFontNameMap == null) {
            rangeAxisTickLabelFontNameMap = new HashMap();
        }
        rangeAxisTickLabelFontNameMap.putAll(map);
    }

    public void setRangeAxisTickLabelFontStyle(int index, int style) {
        if (rangeAxisTickLabelFontStyleMap == null) {
            rangeAxisTickLabelFontStyleMap = new HashMap();
        }
        rangeAxisTickLabelFontStyleMap.put(new Integer(index), new Integer(style));
    }

    public int getRangeAxisTickLabelFontStyle(int index) {
        if (rangeAxisTickLabelFontStyleMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (rangeAxisTickLabelFontStyleMap.containsKey(key)) {
            return ((Integer)rangeAxisTickLabelFontStyleMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getRangeAxisTickLabelFontStyleMap() {
        return rangeAxisTickLabelFontStyleMap;
    }

    public void addRangeAxisTickLabelFontStyleMap(Map map) {
        if (rangeAxisTickLabelFontStyleMap == null) {
            rangeAxisTickLabelFontStyleMap = new HashMap();
        }
        rangeAxisTickLabelFontStyleMap.putAll(map);
    }

    public void setRangeAxisTickLabelFontSize(int index, int size) {
        if (rangeAxisTickLabelFontSizeMap == null) {
            rangeAxisTickLabelFontSizeMap = new HashMap();
        }
        rangeAxisTickLabelFontSizeMap.put(new Integer(index), new Integer(size));
    }

    public int getRangeAxisTickLabelFontSize(int index) {
        if (rangeAxisTickLabelFontSizeMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (rangeAxisTickLabelFontSizeMap.containsKey(key)) {
            return ((Integer)rangeAxisTickLabelFontSizeMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getRangeAxisTickLabelFontSizeMap() {
        return rangeAxisTickLabelFontSizeMap;
    }

    public void addRangeAxisTickLabelFontSizeMap(Map map) {
        if (rangeAxisTickLabelFontSizeMap == null) {
            rangeAxisTickLabelFontSizeMap = new HashMap();
        }
        rangeAxisTickLabelFontSizeMap.putAll(map);
    }

    public void setDefaultDomainAxisLabelFontName(String name) {
        defaultDomainAxisLabelFontName = name;
    }

    public String getDefaultDomainAxisLabelFontName() {
        return defaultDomainAxisLabelFontName;
    }

    public void setDefaultDomainAxisLabelFontStyle(int style) {
        defaultDomainAxisLabelFontStyle = style;
    }

    public int getDefaultDomainAxisLabelFontStyle() {
        return defaultDomainAxisLabelFontStyle;
    }

    public void setDefaultDomainAxisLabelFontSize(int size) {
        defaultDomainAxisLabelFontSize = size;
    }
    public int getDefaultDomainAxisLabelFontSize() {
        return defaultDomainAxisLabelFontSize;
    }

    public void setDefaultRangeAxisLabelFontName(String name) {
        defaultRangeAxisLabelFontName = name;
    }

    public String getDefaultRangeAxisLabelFontName() {
        return defaultRangeAxisLabelFontName;
    }

    public void setDefaultRangeAxisLabelFontStyle(int style) {
        defaultRangeAxisLabelFontStyle = style;
    }

    public int getDefaultRangeAxisLabelFontStyle() {
        return defaultRangeAxisLabelFontStyle;
    }

    public void setDefaultRangeAxisLabelFontSize(int size) {
        defaultRangeAxisLabelFontSize = size;
    }

    public int getDefaultRangeAxisLabelFontSize() {
        return defaultRangeAxisLabelFontSize;
    }

    public void setDomainAxisLabelFontName(int index, String name) {
        if (domainAxisLabelFontNameMap == null) {
            domainAxisLabelFontNameMap = new HashMap();
        }
        domainAxisLabelFontNameMap.put(new Integer(index), name);
    }
    public String getDomainAxisLabelFontName(int index) {
        if (domainAxisLabelFontNameMap == null) {
            return null;
        }
        Integer key = new Integer(index);
        if(domainAxisLabelFontNameMap.containsKey(key)){
            return (String) domainAxisLabelFontNameMap.get(key);
        }
        return null;
    }

    public Map getDomainAxisLabelFontNameMap() {
        return domainAxisLabelFontNameMap;
    }

    public void addDomainAxisLabelFontNameMap(Map map) {
        if (domainAxisLabelFontNameMap == null) {
            domainAxisLabelFontNameMap = new HashMap();
        }
        domainAxisLabelFontNameMap.putAll(map);
    }

    public void setDomainAxisLabelFontStyle(int index, int style) {
        if (domainAxisLabelFontStyleMap == null) {
            domainAxisLabelFontStyleMap = new HashMap();
        }
        domainAxisLabelFontStyleMap.put(new Integer(index), new Integer(style));
    }
    public int getDomainAxisLabelFontStyle(int index) {
        if (domainAxisLabelFontStyleMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (domainAxisLabelFontStyleMap.containsKey(key)) {
            return ((Integer)domainAxisLabelFontStyleMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getDomainAxisLabelFontStyleMap() {
        return domainAxisLabelFontStyleMap;
    }

    public void addDomainAxisLabelFontStyleMap(Map map) {
        if (domainAxisLabelFontStyleMap == null) {
            domainAxisLabelFontStyleMap = new HashMap();
        }
        domainAxisLabelFontStyleMap.putAll(map);
    }

    public void setDomainAxisLabelFontSize(int index, int size) {
        if (domainAxisLabelFontSizeMap == null) {
            domainAxisLabelFontSizeMap = new HashMap();
        }
        domainAxisLabelFontSizeMap.put(new Integer(index), new Integer(size));
    }

    public int getDomainAxisLabelFontSize(int index) {
        if (domainAxisLabelFontSizeMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (domainAxisLabelFontSizeMap.containsKey(key)) {
            return ((Integer)domainAxisLabelFontSizeMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getDomainAxisLabelFontSizeMap() {
        return domainAxisLabelFontSizeMap;
    }

    public void addDomainAxisLabelFontSizeMap(Map map) {
        if (domainAxisLabelFontSizeMap == null) {
            domainAxisLabelFontSizeMap = new HashMap();
        }
        domainAxisLabelFontSizeMap.putAll(map);
    }

    public void setRangeAxisLabelFontName(int index, String name) {
        if (rangeAxisLabelFontNameMap == null) {
            rangeAxisLabelFontNameMap = new HashMap();
        }
        rangeAxisLabelFontNameMap.put(new Integer(index), name);
    }

    public String getRangeAxisLabelFontName(int index) {
        if (rangeAxisLabelFontNameMap == null) {
            return null;
        }
        Integer key = new Integer(index);
        if(rangeAxisLabelFontNameMap.containsKey(key)){
            return (String) rangeAxisLabelFontNameMap.get(key);
        }
        return null;
    }

    public Map getRangeAxisLabelFontNameMap() {
        return rangeAxisLabelFontNameMap;
    }

    public void addRangeAxisLabelFontNameMap(Map map) {
        if (rangeAxisLabelFontNameMap == null) {
            rangeAxisLabelFontNameMap = new HashMap();
        }
        rangeAxisLabelFontNameMap.putAll(map);
    }

    public void setRangeAxisLabelFontStyle(int index, int style) {
        if (rangeAxisLabelFontStyleMap == null) {
            rangeAxisLabelFontStyleMap = new HashMap();
        }
        rangeAxisLabelFontStyleMap.put(new Integer(index), new Integer(style));
    }

    public int getRangeAxisLabelFontStyle(int index) {
        if (rangeAxisLabelFontStyleMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (rangeAxisLabelFontStyleMap.containsKey(key)) {
            return ((Integer)rangeAxisLabelFontStyleMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getRangeAxisLabelFontStyleMap() {
        return rangeAxisLabelFontStyleMap;
    }

    public void addRangeAxisLabelFontStyleMap(Map map) {
        if (rangeAxisLabelFontStyleMap == null) {
            rangeAxisLabelFontStyleMap = new HashMap();
        }
        rangeAxisLabelFontStyleMap.putAll(map);
    }

    public void setRangeAxisLabelFontSize(int index, int size) {
        if (rangeAxisLabelFontSizeMap == null) {
            rangeAxisLabelFontSizeMap = new HashMap();
        }
        rangeAxisLabelFontSizeMap.put(new Integer(index), new Integer(size));
    }

    public int getRangeAxisLabelFontSize(int index) {
        if (rangeAxisLabelFontSizeMap == null) {
            return Integer.MIN_VALUE;
        }
        Integer key = new Integer(index);
        if (rangeAxisLabelFontSizeMap.containsKey(key)) {
            return ((Integer)rangeAxisLabelFontSizeMap.get(key)).intValue();
        }
        return Integer.MIN_VALUE;
    }

    public Map getRangeAxisLabelFontSizeMap() {
        return rangeAxisLabelFontSizeMap;
    }

    public void addRangeAxisLabelFontSizeMap(Map map) {
        if (rangeAxisLabelFontSizeMap == null) {
            rangeAxisLabelFontSizeMap = new HashMap();
        }
        rangeAxisLabelFontSizeMap.putAll(map);
    }

}
