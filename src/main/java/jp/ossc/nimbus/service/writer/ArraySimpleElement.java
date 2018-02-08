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
package jp.ossc.nimbus.service.writer;

/**
 * 配列オブジェクトを任意のセパレータ区切りの文字列に変換する{@link WritableElement}実装クラス。<p>
 *
 * @author M.Takata
 */
public class ArraySimpleElement extends SimpleElement {
    
    private static final long serialVersionUID = -569975982814446827L;
    
    /**
     * デフォルトのセパレータ。<p>
     */
    public static final String DEFAULT_SEPARATOR = ",";
    
    private String separator = DEFAULT_SEPARATOR;
    
    /**
     * セパレータを設定する。<p>
     * 
     * @param sep セパレータ
     */
    public void setSeparator(String sep){
        separator = sep;
    }
    
    /**
     * セパレータを取得する。<p>
     * 
     * @return セパレータ
     */
    public String getSeparator(){
        return separator;
    }
    
    /**
     * この要素(配列)の値を任意のセパレータ区切りで羅列したものにして取得する。<p>
     * 
     * @return この要素(配列)の値を任意のセパレータ区切りで羅列したもの
     */
    public String toString(){
        if(mValue == null || !mValue.getClass().isArray()){
            return super.toString();
        }
        final Object[] array = (Object[])mValue;
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = array.length; i < max; i++){
            buf.append(array[i]);
            if(i != max - 1){
                buf.append(separator);
            }
        }
        
        return buf.toString();
    }
    
    /**
     * この要素(配列)の値を任意のセパレータ区切りで羅列したものにして取得する。<p>
     * {@link #toString()}と同じ値を返す。<br>
     * 
     * @return この要素(配列)の値を任意のセパレータ区切りで羅列したもの
     */
    public Object toObject(){
        return toString();
    }
}
