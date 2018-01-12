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
 * 小数桁丸めコンバータ。<p>
 * 
 * @author M.Takata
 */
public class DecimalScaleConverter implements Converter{
    
    private int scale = 0;
    private int roundingMode = BigDecimal.ROUND_HALF_UP;
    private Class returnType;
    
    /**
     * スケールを設定する。<p>
     * デフォルトは、0。<br>
     *
     * @param scale スケール
     */
    public void setScale(int scale){
        this.scale = scale;
    }
    
    /**
     * スケールを取得する。<p>
     *
     * @return スケール
     */
    public int getScale(){
        return scale;
    }
    
    /**
     * 丸めモードを設定する。<p>
     * デフォルトは、{@link BigDecimal#ROUND_HALF_UP}。<br>
     *
     * @param roundingMode 丸めモード
     * @see BigDecimal
     */
    public void setRoundingMode(int roundingMode){
        new BigDecimal(0.0).setScale(scale, roundingMode);
        this.roundingMode = roundingMode;
    }
    
    /**
     * 丸めモードを取得する。<p>
     *
     * @return 丸めモード
     */
    public int getRoundingMode(){
        return roundingMode;
    }
    
    /**
     * 変換後の戻り値の型を設定する。<p>
     * デフォルトは、nullで、入力の型に対応した型で返す。<br>
     * サポートする型は、BigDecimal、Double、Float、String。<br>
     * 
     * @param type 戻り値の型
     */
    public void setReturnType(Class type){
        if(type != null
            && !type.equals(BigDecimal.class)
            && !type.equals(Double.class)
            && !type.equals(Float.class)
            && !type.equals(String.class)
        ){
            throw new IllegalArgumentException("Unsupported type." + type.getName());
        }
        returnType = type;
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
     * 指定されたオブジェクトを丸める。<p>
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
        Class retType = returnType;
        BigDecimal retVal = null;
        if(obj instanceof BigDecimal){
            retVal = ((BigDecimal)obj).setScale(scale, roundingMode);
            if(retType == null){
                retType = BigDecimal.class;
            }
        }else if(obj instanceof Number){
            retVal = new BigDecimal(((Number)obj).doubleValue()).setScale(scale, roundingMode);
            if(retType == null){
                if(obj instanceof Float){
                    retType = Float.class;
                }else{
                    retType = Double.class;
                }
            }
        }else if(obj instanceof String){
            try{
                retVal = new BigDecimal((String)obj).setScale(scale, roundingMode);
            }catch(NumberFormatException e){
                throw new ConvertException(e);
            }
            if(retType == null){
                retType = String.class;
            }
        }else{
            return obj;
        }
        if(retType.equals(BigDecimal.class)){
            return retVal;
        }else if(retType.equals(Double.class)){
            return new Double(retVal.doubleValue());
        }else if(retType.equals(Float.class)){
            return new Float(retVal.floatValue());
        }else{
            return retVal.toString();
        }
    }
}
