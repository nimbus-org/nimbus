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
 * 改行コードを削除して複数行の文字列を1行の文字列に変換する{@link WritableElement}実装クラス。<p>
 * 
 * @author M.Takata
 */
public class LineSimpleElement extends SimpleElement {
    
    private static final long serialVersionUID = -2483151443183569493L;
    
    private boolean isTrim;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public LineSimpleElement(){
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param value 変換対象のオブジェクト
     */
    public LineSimpleElement(Object value){
        super(value);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key キー
     * @param value 変換対象のオブジェクト
     */
    public LineSimpleElement(Object key, Object value){
        super(key, value);
    }
    
    /**
     * 改行直後の空白文字をトリムするかどうかを設定する。<p>
     * デフォルトは、falseでトリムしない。<br>
     * 
     * @param trim トリムする場合は、true
     */
    public void setTrim(boolean trim){
        isTrim = trim;
    }
    
    /**
     * 改行直後の空白文字をトリムするかどうかを判定する。<p>
     * 
     * @return trueの場合はトリムする
     */
    public boolean isTrim(){
        return isTrim;
    }
    
    /**
     * 改行コードを削除して複数行の文字列を1行の文字列に変換する。<p>
     * 
     * @return 変換後文字列
     */
    public String toString(){
        final String str = super.toString();
        if(str == null
             || str.length() == 0
             || (str.indexOf('\n') == -1
                    && str.indexOf('\r') == -1)
        ){
            return str;
        }
        final StringBuilder buf = new StringBuilder();
        boolean isLineSeparator = false;
        for(int i = 0, max = str.length(); i < max; i++){
            char c = str.charAt(i);
            if(c == '\n' || c == '\r'){
                isLineSeparator = true;
            }else{
                if(!isTrim || !isLineSeparator || !Character.isWhitespace(c)){
                    buf.append(c);
                    isLineSeparator = false;
                }
            }
        }
        return buf.toString();
    }
}
