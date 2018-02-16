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
package jp.ossc.nimbus.util;

import java.util.Comparator;
import java.lang.Math;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.Serializable;

/**
 * 比較可能数値。<p>
 * 
 * @author M.Takata
 */
public class ComparableNumber extends Number implements Comparable, Serializable{
    
    private static final long serialVersionUID = 6924172285753313273L;
    protected final Number number;
    protected final NumberComparator comparator;
    
    public static final NumberComparator ASC = new OrderNumberComparator(true);
    public static final NumberComparator DESC = new OrderNumberComparator(false);
    
    public ComparableNumber(Number number){
        this(number, ASC);
    }
    public ComparableNumber(Number number, NumberComparator comparator){
        this.number = number == null ? new Integer(0) : number;
        this.comparator = comparator;
    }
    
    public Number getNumber(){
        return number;
    }
    
    public NumberComparator getNumberComparator(){
        return comparator;
    }
    
    public byte byteValue(){
        return number.byteValue();
    }
    
    public short shortValue(){
        return number.shortValue();
    }
    
    public int intValue(){
        return number.intValue();
    }
    
    public long longValue(){
        return number.longValue();
    }
    
    public float floatValue(){
        return number.floatValue();
    }
    
    public double doubleValue(){
        return number.doubleValue();
    }
    
    public int compareTo(Object obj){
        if(obj instanceof ComparableNumber){
            return comparator.compare(number, ((ComparableNumber)obj).number);
        }else{
            return comparator.compare(number, (Number)obj);
        }
    }
    
    public boolean equals(Object obj){
        return number.equals(obj);
    }
    
    public int hashCode(){
        return number.hashCode();
    }
    
    public String toString(){
        return number.toString();
    }
    
    public static abstract class NumberComparator implements Comparator, Serializable{
        
        private static final long serialVersionUID = 688003969304020267L;
        
        public int compare(Object obj1, Object obj2){
            return compare((Number)obj1, (Number)obj2);
        }
        
        public abstract int compare(Number num1, Number num2);
    }
    
    public static class OrderNumberComparator extends NumberComparator{
        
        private static final long serialVersionUID = 8713363854005411204L;
        protected transient int more;
        protected transient int less;
        
        public OrderNumberComparator(){
            this(true);
        }
        
        public OrderNumberComparator(boolean isAsc){
            more = isAsc ? 1 : -1;
            less = isAsc ? -1 : 1;
        }
        
        protected int compareByte(Number num1, Number num2){
            final int val = num1.byteValue() - num2.byteValue();
            if(val == 0){
                return 0;
            }else{
                return val > 0 ? more : less;
            }
        }
        protected int compareShort(Number num1, Number num2){
            final int val = num1.shortValue() - num2.shortValue();
            if(val == 0){
                return 0;
            }else{
                return val > 0 ? more : less;
            }
        }
        protected int compareInt(Number num1, Number num2){
            final int val = num1.intValue() - num2.intValue();
            if(val == 0){
                return 0;
            }else{
                return val > 0 ? more : less;
            }
        }
        protected int compareLong(Number num1, Number num2){
            final long val = num1.longValue() - num2.longValue();
            if(val == 0){
                return 0;
            }else{
                return val > 0 ? more : less;
            }
        }
        protected int compareFloat(Number num1, Number num2){
            final int result = Float.compare(num1.floatValue(), num2.floatValue());
            if(result == 0){
                return 0;
            }else{
                return more > 0 ? result : -result;
            }
        }
        protected int compareDouble(Number num1, Number num2){
            final int result = Double.compare(num1.doubleValue(), num2.doubleValue());
            if(result == 0){
                return 0;
            }else{
                return more > 0 ? result : -result;
            }
        }
        protected int compareBigInteger(Number num1, Number num2){
            BigInteger bigInt = null;
            if(num1 instanceof Byte
                    || num1 instanceof Short
                    || num1 instanceof Integer
                    || num1 instanceof Long
            ){
                bigInt = BigInteger.valueOf(num1.longValue());
            }else if(num1 instanceof Float
                    || num1 instanceof Double
            ){
                bigInt = BigInteger.valueOf(Math.round(num1.doubleValue()));
            }else{
                bigInt = (BigInteger)num1;
            }
            final int result = bigInt.compareTo((BigInteger)num2);
            if(result == 0){
                return 0;
            }else{
                return more > 0 ? result : -result;
            }
        }
        protected int compareBigDecimal(Number num1, Number num2){
            BigDecimal bigDec1 = toBigDecimal(num1);
            BigDecimal bigDec2 = toBigDecimal(num2);
            final int result = bigDec1.compareTo(bigDec2);
            if(result == 0){
                return 0;
            }else{
                return more > 0 ? result : -result;
            }
        }
        protected BigDecimal toBigDecimal(Number num){
            if(num instanceof Byte
                    || num instanceof Short
                    || num instanceof Integer
                    || num instanceof Long
            ){
                return BigDecimal.valueOf(num.longValue());
            }else if(num instanceof Float
                    || num instanceof Double
            ){
                return new BigDecimal(num.doubleValue());
            }else if(num instanceof BigInteger){
                return new BigDecimal((BigInteger)num);
            }else if(num instanceof BigDecimal){
                return (BigDecimal)num;
            }else{
                try{
                    return new BigDecimal(num.toString());
                }catch(NumberFormatException e){
                    return new BigDecimal(num.doubleValue());
                }
            }
        }
        
        public int compare(Number num1, Number num2){
            if(num1 == null && num2 == null){
                return 0;
            }else if(num1 == null && num2 != null){
                return less;
            }else if(num1 != null && num2 == null){
                return more;
            }
            if(num1 instanceof Byte){
                if(num2 instanceof Byte){
                    return compareByte(num1, num2);
                }else if(num2 instanceof Short){
                    return compareShort(num1, num2);
                }else if(num2 instanceof Integer){
                    return compareInt(num1, num2);
                }else if(num2 instanceof Long){
                    return compareLong(num1, num2);
                }else if(num2 instanceof Float){
                    return compareFloat(num1, num2);
                }else if(num2 instanceof Double){
                    return compareDouble(num1, num2);
                }else if(num2 instanceof BigInteger){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else if(num1 instanceof Short){
                if(num2 instanceof Byte
                        || num2 instanceof Short
                ){
                    return compareShort(num1, num2);
                }else if(num2 instanceof Integer){
                    return compareInt(num1, num2);
                }else if(num2 instanceof Long){
                    return compareLong(num1, num2);
                }else if(num2 instanceof Float){
                    return compareFloat(num1, num2);
                }else if(num2 instanceof Double){
                    return compareDouble(num1, num2);
                }else if(num2 instanceof BigInteger){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else if(num1 instanceof Integer){
                if(num2 instanceof Byte
                        || num2 instanceof Short
                        || num2 instanceof Integer
                ){
                    return compareInt(num1, num2);
                }else if(num2 instanceof Long){
                    return compareLong(num1, num2);
                }else if(num2 instanceof Float){
                    return compareFloat(num1, num2);
                }else if(num2 instanceof Double){
                    return compareDouble(num1, num2);
                }else if(num2 instanceof BigInteger){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else if(num1 instanceof Long){
                if(num2 instanceof Byte
                        || num2 instanceof Short
                        || num2 instanceof Integer
                        || num2 instanceof Long
                ){
                    return compareLong(num1, num2);
                }else if(num2 instanceof Float){
                    return compareFloat(num1, num2);
                }else if(num2 instanceof Double){
                    return compareDouble(num1, num2);
                }else if(num2 instanceof BigInteger){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else if(num1 instanceof Float){
                if(num2 instanceof Byte
                        || num2 instanceof Short
                        || num2 instanceof Integer
                        || num2 instanceof Long
                        || num2 instanceof Float
                ){
                    return compareFloat(num1, num2);
                }else if(num2 instanceof Double){
                    return compareDouble(num1, num2);
                }else if(num2 instanceof BigInteger){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else if(num1 instanceof Double){
                if(num2 instanceof Byte
                        || num2 instanceof Short
                        || num2 instanceof Integer
                        || num2 instanceof Long
                        || num2 instanceof Float
                        || num2 instanceof Double
                ){
                    return compareDouble(num1, num2);
                }else if(num2 instanceof BigInteger){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else if(num1 instanceof BigInteger){
                if(num2 instanceof Byte
                        || num2 instanceof Short
                        || num2 instanceof Integer
                        || num2 instanceof Long
                        || num2 instanceof Float
                        || num2 instanceof Double
                        || num2 instanceof BigInteger
                ){
                    return compareBigInteger(num1, num2);
                }else{
                    return compareBigDecimal(num1, num2);
                }
            }else{
                return compareBigDecimal(num1, num2);
            }
        }
        
        private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException{
            out.defaultWriteObject();
            out.writeBoolean(more > 0 ? true : false);
        }
        
        private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
            in.defaultReadObject();
            final boolean isAsc = in.readBoolean();
            more = isAsc ? 1 : -1;
            less = isAsc ? -1 : 1;
        }
    }
}