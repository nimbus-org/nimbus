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

import java.util.Date;
import java.sql.Time;

/**
 * タイムフォーマットコンバータ。<p>
 * 
 * @author M.Takata
 */
public class TimeFormatConverter extends DateFormatConverter
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -5574663877130540939L;

    /**
     * フォーマット"yyyy/MM/dd HH:mm:ss.SSS"で日付→文字列変換を行うコンバータを生成する。<p>
     */
    public TimeFormatConverter(){
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param format 数値フォーマット
     * @see #DATE_TO_STRING
     * @see #STRING_TO_DATE
     */
    public TimeFormatConverter(int type, String format){
        super(type, format);
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        
        switch(convertType){
        case DATE_TO_STRING:
            return super.convert(obj);
        case STRING_TO_DATE:
            Object ret = super.convert(obj);
            if(ret == null){
                return null;
            }
            return new Time(((Date)ret).getTime());
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
}
