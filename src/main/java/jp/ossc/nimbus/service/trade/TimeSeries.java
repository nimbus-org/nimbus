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

public abstract class TimeSeries<E extends TimeSeries.Element> extends ArrayList<TimeSeries.Element> implements Cloneable, java.io.Serializable{

    public TimeSeries(){
    }

    public TimeSeries(List<E> list){
        addAll(list);
    }

    public E getElement(Date time){
        int index = Collections.binarySearch(this, new Element(time, 0d));
        if(index < 0){
            return null;
        }
        return (E)get(index);
    }

    public TimeSeries<E> filter(Date from, Date to){
        int fromIndex = from == null ? 0 : Collections.binarySearch(this, new Element(from, 0d));
        if(fromIndex < 0){
            fromIndex = -fromIndex - 1;
        }
        int toIndex = to == null ? size() : Collections.binarySearch(this, new Element(to, 0d));
        if(toIndex < 0){
            toIndex = -fromIndex - 1;
        }
        TimeSeries<E> clone = (TimeSeries<E>)clone();
        clone.addAll(subList(fromIndex, toIndex));
        return clone;
    }

    public static class Element implements Comparable<Element>, java.io.Serializable{
        protected Date time;
        protected double value;

        public Element(){
        }

        public Element(Date time, double value){
            this.time = time;
            this.value = value;
        }

        public double getTradeStartValue(){
            return getValue();
        }

        public double getTradeEndValue(){
            return getValue();
        }

        public Date getTime(){
            return time;
        }
        public void setTime(Date time){
            this.time = time;
        }

        public double getValue(){
            return value;
        }
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