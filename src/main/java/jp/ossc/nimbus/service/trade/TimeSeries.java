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
package jp.ossc.nimbus.service.trade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 時系列データ。<p>
 * {@link TimeSeries.Element 時系列要素}のリスト。<br>
 *
 * @author M.Takata
 */
public abstract class TimeSeries<E extends TimeSeries.Element> extends ArrayList<TimeSeries.Element> implements Cloneable, java.io.Serializable{
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public TimeSeries(){
    }
    
    /**
     * 指定された時系列要素のリストを保持するインスタンスを生成する。<p>
     *
     * @param list 時系列要素のリスト
     */
    public TimeSeries(List<E> list){
        addAll(list);
    }
    
    /**
     * 指定された日付の時系列要素を取得する。<p>
     *
     * @param time 日付
     * @return 時系列要素。存在しない場合、null
     */
    public E getElement(Date time){
        int index = Collections.binarySearch(this, new Element(time, 0d));
        if(index < 0){
            return null;
        }
        return (E)get(index);
    }
    
    /**
     * 指定された日付の時系列要素のインデックスを取得する。<p>
     *
     * @param time 日付
     * @return 時系列要素のインデックス。存在しない場合、-1
     */
    public int indexOf(Date time){
        final int index = Collections.binarySearch(this, new Element(time, 0d));
        return index < 0 ? -1 : index;
    }
    
    /**
     * 指定された期間にフィルタリングした時系列データを取得する。<p>
     *
     * @param from 開始日付
     * @param to 終了日付
     * @return フィルタリングされた時系列データ
     */
    public TimeSeries<E> filter(Date from, Date to){
        int fromIndex = from == null ? 0 : Collections.binarySearch(this, new Element(from, 0d));
        if(fromIndex < 0){
            fromIndex = -fromIndex - 1;
        }
        int toIndex = to == null ? size() : Collections.binarySearch(this, new Element(to, 0d));
        if(toIndex < 0){
            toIndex = -toIndex - 1;
        }
        TimeSeries<E> result = null;
        try{
            result = getClass().newInstance();
            result.addAll(subList(fromIndex, toIndex));
        }catch(InstantiationException e){
        }catch(IllegalAccessException e){
        }
        return result;
    }
    
    /**
     * 指定された期間にフィルタリングした時系列データを取得する。<p>
     *
     * @param from 開始インデックス
     * @param to 終了インデックス
     * @return フィルタリングされた時系列データ
     */
    public TimeSeries<E> filter(int from, int to){
        TimeSeries<E> result = null;
        try{
            result = getClass().newInstance();
            result.addAll(subList(from, to));
        }catch(InstantiationException e){
        }catch(IllegalAccessException e){
        }
        return result;
    }
    
    /**
     * 時系列要素。<p>
     *
     * @author M.Takata
     */
    public static class Element implements Comparable<Element>, java.io.Serializable{
        protected Date time;
        protected double value;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public Element(){
        }
        
        /**
         * 指定されて日付と値を持つインスタンスを生成する。<p>
         * 
         * @param time 日付
         * @param value 値
         */
        public Element(Date time, double value){
            this.time = time;
            this.value = value;
        }
        
        /**
         * 取引開始サインと取引開始日に間がある場合の、始値を取得する。<p>
         *
         * @return 始値
         */
        public double getTradeStartValue(){
            return getValue();
        }
        
        /**
         * 取引終了サインと取引終了日に間がある場合の、終値を取得する。<p>
         *
         * @return 終値
         */
        public double getTradeEndValue(){
            return getValue();
        }
        
        /**
         * 日付を取得する。<p>
         *
         * @return 日付
         */
        public Date getTime(){
            return time;
        }
        
        /**
         * 日付を設定する。<p>
         *
         * @param time 日付
         */
        public void setTime(Date time){
            this.time = time;
        }
        
        /**
         * 値を取得する。<p>
         *
         * @return 値
         */
        public double getValue(){
            return value;
        }
        
        /**
         * 値を設定する。<p>
         *
         * @param value 値
         */
        public void setValue(double value){
            this.value = value;
        }
        
        public boolean equals(Object obj){
            if(obj == null || !(obj instanceof Element)){
                return false;
            }
            final Element element = (Element)obj;
            if(time == null){
                return element.time == null;
            }else{
                return time.equals(element.time);
            }
        }
        
        public int hashCode(){
            return time == null ? 0 : time.hashCode();
        }
        
        public int compareTo(Element element){
            if(time == null){
                return element.time == null ? 0 : -1;
            }
            return element.time == null ? 1 : time.compareTo(element.time);
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            final String timeStr = time == null ? null : format.format(time);
            buf.append('{');
            buf.append("time=" + timeStr);
            buf.append(", value=" + value);
            buf.append('}');
            return buf.toString();
        }
    }
}