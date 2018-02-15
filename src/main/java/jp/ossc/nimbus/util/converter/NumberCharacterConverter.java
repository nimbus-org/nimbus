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

/**
 * 数字コンバータ。<p>
 * <table border=5>
 *     <tr><th>半角数字</th><th>全角数字</th></tr>
 *     <tr><td>0</td><td>０</td></tr>
 *     <tr><td>1</td><td>１</td></tr>
 *     <tr><td>2</td><td>２</td></tr>
 *     <tr><td>3</td><td>３</td></tr>
 *     <tr><td>4</td><td>４</td></tr>
 *     <tr><td>5</td><td>５</td></tr>
 *     <tr><td>6</td><td>６</td></tr>
 *     <tr><td>7</td><td>７</td></tr>
 *     <tr><td>8</td><td>８</td></tr>
 *     <tr><td>9</td><td>９</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public class NumberCharacterConverter extends HankakuZenkakuCharacterConverter
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 7420253969574101704L;
    
    /**
     * [半角数字][全角数字] の配列。
     */
    protected final static char CONV_CHARS[][] = {
        {'\u0030','\uFF10'}, // ０
        {'\u0031','\uFF11'}, // １
        {'\u0032','\uFF12'}, // ２
        {'\u0033','\uFF13'}, // ３
        {'\u0034','\uFF14'}, // ４
        {'\u0035','\uFF15'}, // ５
        {'\u0036','\uFF16'}, // ６
        {'\u0037','\uFF17'}, // ７
        {'\u0038','\uFF18'}, // ８
        {'\u0039','\uFF19'}  // ９
    };
    
    /**
     * 半角→全角変換種別の数字コンバータを生成する。<p>
     */
    public NumberCharacterConverter(){
        super(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * 数字コンバーターを生成します。
     *
     * @param type 変換種別
     * @see HankakuZenkakuCharacterConverter#HANKAKU_TO_ZENKAKU
     * @see HankakuZenkakuCharacterConverter#ZENKAKU_TO_HANKAKU
     */
    public NumberCharacterConverter(int type){
        super(type);
    }
    
    /**
     * 半角全角変換キャラクタ配列を取得する。<p>
     *
     * @return {@link #CONV_CHARS}
     */
    protected char[][] getHankakuZenkakuChars(){
        return CONV_CHARS;
    }
}
