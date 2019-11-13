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

import java.util.*;

/**
 * マッピングコンバータ。<p>
 * 変換対象オブジェクトと変換後オブジェクトをマッピングして、変換を行う。<br>
 * 
 * @author M.Takata
 */
public class MappingConverter implements Converter{
    
    private Map mapping = new HashMap();
    private Object defaultValue;
    
    /**
     * 変換対象オブジェクトと変換後オブジェクトのマッピングを設定する。<p>
     *
     * @param input 変換対象オブジェクト
     * @param output 変換後オブジェクト
     */
    public void setMapping(Object input, Object output){
        mapping.put(input, output);
    }
    
    /**
     * マッピングが存在しない場合に返す値を設定する。<p>
     *
     * @param value マッピングが存在しない場合に返す値
     */
    public void setDefaultValue(Object value){
        defaultValue = value;
    }
    
    /**
     * マッピングが存在しない場合に返す値を取得する。<p>
     *
     * @return マッピングが存在しない場合に返す値
     */
    public Object getDefaultValue(){
        return defaultValue;
    }
    
    public Object convert(Object obj) throws ConvertException{
        return mapping.containsKey(obj) ? mapping.get(obj) : defaultValue;
    }
}
