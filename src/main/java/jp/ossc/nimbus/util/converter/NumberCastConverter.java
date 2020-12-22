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
package jp.ossc.nimbus.util.converter;

import java.math.*;

/**
 * 数値キャストコンバータ。<p>
 * 
 * @author M.Takata
 */
public class NumberCastConverter implements Converter{
    
    private Class returnType = Double.class;
    
    public NumberCastConverter(){
    }
    
    public NumberCastConverter(Class type){
        setReturnType(type);
    }
    
    /**
     * 変換後の戻り値の型を設定する。<p>
     * デフォルトは、Double。<br>
     * サポートする型は、BigDecimal、BigInteger、Double、Float、Long、Integer、Short、Byte及び数値プリミティブ型。<br>
     * 
     * @param type 戻り値の型
     */
    public void setReturnType(Class type){
        if(type != null
            && !type.equals(BigDecimal.class)
            && !type.equals(BigInteger.class)
            && !type.equals(Double.class)
            && !type.equals(Float.class)
            && !type.equals(Long.class)
            && !type.equals(Integer.class)
            && !type.equals(Short.class)
            && !type.equals(Byte.class)
            && !type.isPrimitive()
            && (type.isPrimitive() && (type.equals(Boolean.TYPE) || type.equals(Character.TYPE)))
        ){
            throw new IllegalArgumentException("Unsupported type." + type.getName());
        }
        if(type.isPrimitive()){
            if(type.equals(Byte.TYPE)){
                returnType = Byte.class;
            }else if(type.equals(Short.TYPE)){
                returnType = Short.class;
            }else if(type.equals(Integer.TYPE)){
                returnType = Integer.class;
            }else if(type.equals(Long.TYPE)){
                returnType = Long.class;
            }else if(type.equals(Float.TYPE)){
                returnType = Float.class;
            }else if(type.equals(Double.TYPE)){
                returnType = Double.class;
            }
        }else{
            returnType = type;
        }
    }
    
    /**
     * 変換後の戻り値の型を取得する。<p>
     * 
     * @return 戻り値の型
     */
    public Class getReturnType(){
        return returnType;
    }
    
    /**
     * 指定されたNumber及び数字を設定された型にキャストする。<p>
     * サポートしない型のオブジェクトが渡されると、そのまま返す。<br>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        Class clazz = obj.getClass();
        if(clazz.equals(returnType)){
            return obj;
        }
        Number retVal = null;
        if(obj instanceof Number){
            retVal = (Number)obj;
        }else if(obj instanceof String){
            try{
                retVal = new BigDecimal((String)obj);
            }catch(NumberFormatException e){
                throw new ConvertException(e);
            }
        }else{
            return obj;
        }
        if(returnType.equals(BigDecimal.class)){
            if(clazz.equals(BigInteger.class)){
                return new BigDecimal((BigInteger)retVal);
            }else if(clazz.equals(Double.class)
                || clazz.equals(Float.class)
            ){
                try{
                    return new BigDecimal(retVal.toString());
                }catch(NumberFormatException e){
                    return new BigDecimal(retVal.doubleValue());
                }
            }else{
                return BigDecimal.valueOf(retVal.longValue());
            }
        }else if(returnType.equals(BigInteger.class)){
            if(clazz.equals(BigDecimal.class)){
                return ((BigDecimal)retVal).toBigInteger();
            }else{
                return BigInteger.valueOf(retVal.longValue());
            }
        }else if(returnType.equals(Double.class)){
            return new Double(retVal.doubleValue());
        }else if(returnType.equals(Float.class)){
            return new Float(retVal.floatValue());
        }else if(returnType.equals(Long.class)){
            return new Long(retVal.longValue());
        }else if(returnType.equals(Integer.class)){
            return new Integer(retVal.intValue());
        }else if(returnType.equals(Short.class)){
            return new Short(retVal.shortValue());
        }else{
            return new Byte(retVal.byteValue());
        }
    }
}
