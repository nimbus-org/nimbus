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
 * 英字コンバータ。<p>
 * <table border=5>
 *     <tr><th>半角英字</th><th>全角英字</th></tr>
 *     <tr><td>a</td><td>ａ</td></tr>
 *     <tr><td>b</td><td>ｂ</td></tr>
 *     <tr><td>c</td><td>ｃ</td></tr>
 *     <tr><td>d</td><td>ｄ</td></tr>
 *     <tr><td>e</td><td>ｅ</td></tr>
 *     <tr><td>f</td><td>ｆ</td></tr>
 *     <tr><td>g</td><td>ｇ</td></tr>
 *     <tr><td>h</td><td>ｈ</td></tr>
 *     <tr><td>i</td><td>ｉ</td></tr>
 *     <tr><td>j</td><td>ｊ</td></tr>
 *     <tr><td>k</td><td>ｋ</td></tr>
 *     <tr><td>l</td><td>ｌ</td></tr>
 *     <tr><td>m</td><td>ｍ</td></tr>
 *     <tr><td>n</td><td>ｎ</td></tr>
 *     <tr><td>o</td><td>ｏ</td></tr>
 *     <tr><td>p</td><td>ｐ</td></tr>
 *     <tr><td>q</td><td>ｑ</td></tr>
 *     <tr><td>r</td><td>ｒ</td></tr>
 *     <tr><td>s</td><td>ｓ</td></tr>
 *     <tr><td>t</td><td>ｔ</td></tr>
 *     <tr><td>u</td><td>ｕ</td></tr>
 *     <tr><td>v</td><td>ｖ</td></tr>
 *     <tr><td>w</td><td>ｗ</td></tr>
 *     <tr><td>x</td><td>ｘ</td></tr>
 *     <tr><td>y</td><td>ｙ</td></tr>
 *     <tr><td>z</td><td>ｚ</td></tr>
 *     <tr><td>A</td><td>Ａ</td></tr>
 *     <tr><td>B</td><td>Ｂ</td></tr>
 *     <tr><td>C</td><td>Ｃ</td></tr>
 *     <tr><td>D</td><td>Ｄ</td></tr>
 *     <tr><td>E</td><td>Ｅ</td></tr>
 *     <tr><td>F</td><td>Ｆ</td></tr>
 *     <tr><td>G</td><td>Ｇ</td></tr>
 *     <tr><td>H</td><td>Ｈ</td></tr>
 *     <tr><td>I</td><td>Ｉ</td></tr>
 *     <tr><td>J</td><td>Ｊ</td></tr>
 *     <tr><td>K</td><td>Ｋ</td></tr>
 *     <tr><td>L</td><td>Ｌ</td></tr>
 *     <tr><td>M</td><td>Ｍ</td></tr>
 *     <tr><td>N</td><td>Ｎ</td></tr>
 *     <tr><td>O</td><td>Ｏ</td></tr>
 *     <tr><td>P</td><td>Ｐ</td></tr>
 *     <tr><td>Q</td><td>Ｑ</td></tr>
 *     <tr><td>R</td><td>Ｒ</td></tr>
 *     <tr><td>S</td><td>Ｓ</td></tr>
 *     <tr><td>T</td><td>Ｔ</td></tr>
 *     <tr><td>U</td><td>Ｕ</td></tr>
 *     <tr><td>V</td><td>Ｖ</td></tr>
 *     <tr><td>W</td><td>Ｗ</td></tr>
 *     <tr><td>X</td><td>Ｘ</td></tr>
 *     <tr><td>Y</td><td>Ｙ</td></tr>
 *     <tr><td>Z</td><td>Ｚ</td></tr>
 * </table>
 * 
 * @author   M.Takata
 */
public class AlphabetCharacterConverter
 extends HankakuZenkakuCharacterConverter implements java.io.Serializable{
    
    private static final long serialVersionUID = 669952771398308661L;
    
    /**
     * [半角英字][全角英字] の配列。<p>
     */
    protected static final char CONV_CHARS[][] = {
        {'\u0061','\uff41'}, // 'a','ａ'
        {'\u0062','\uff42'}, // 'b','ｂ'
        {'\u0063','\uff43'}, // 'c','ｃ'
        {'\u0064','\uff44'}, // 'd','ｄ'
        {'\u0065','\uff45'}, // 'e','ｅ'
        {'\u0066','\uff46'}, // 'f','ｆ'
        {'\u0067','\uff47'}, // 'g','ｇ'
        {'\u0068','\uff48'}, // 'h','ｈ'
        {'\u0069','\uff49'}, // 'i','ｉ'
        {'\u006a','\uff4a'}, // 'j','ｊ'
        {'\u006b','\uff4b'}, // 'k','ｋ'
        {'\u006c','\uff4c'}, // 'l','ｌ'
        {'\u006d','\uff4d'}, // 'm','ｍ'
        {'\u006e','\uff4e'}, // 'n','ｎ'
        {'\u006f','\uff4f'}, // 'o','ｏ'
        {'\u0070','\uff50'}, // 'p','ｐ'
        {'\u0071','\uff51'}, // 'q','ｑ'
        {'\u0072','\uff52'}, // 'r','ｒ'
        {'\u0073','\uff53'}, // 's','ｓ'
        {'\u0074','\uff54'}, // 't','ｔ'
        {'\u0075','\uff55'}, // 'u','ｕ'
        {'\u0076','\uff56'}, // 'v','ｖ'
        {'\u0077','\uff57'}, // 'w','ｗ'
        {'\u0078','\uff58'}, // 'x','ｘ'
        {'\u0079','\uff59'}, // 'y','ｙ'
        {'\u007a','\uff5a'}, // 'z','ｚ'
        {'\u0041','\uff21'}, // 'A','Ａ'
        {'\u0042','\uff22'}, // 'B','Ｂ'
        {'\u0043','\uff23'}, // 'C','Ｃ'
        {'\u0044','\uff24'}, // 'D','Ｄ'
        {'\u0045','\uff25'}, // 'E','Ｅ'
        {'\u0046','\uff26'}, // 'F','Ｆ'
        {'\u0047','\uff27'}, // 'G','Ｇ'
        {'\u0048','\uff28'}, // 'H','Ｈ'
        {'\u0049','\uff29'}, // 'I','Ｉ'
        {'\u004a','\uff2a'}, // 'J','Ｊ'
        {'\u004b','\uff2b'}, // 'K','Ｋ'
        {'\u004c','\uff2c'}, // 'L','Ｌ'
        {'\u004d','\uff2d'}, // 'M','Ｍ'
        {'\u004e','\uff2e'}, // 'N','Ｎ'
        {'\u004f','\uff2f'}, // 'O','Ｏ'
        {'\u0050','\uff30'}, // 'P','Ｐ'
        {'\u0051','\uff31'}, // 'Q','Ｑ'
        {'\u0052','\uff32'}, // 'R','Ｒ'
        {'\u0053','\uff33'}, // 'S','Ｓ'
        {'\u0054','\uff34'}, // 'T','Ｔ'
        {'\u0055','\uff35'}, // 'U','Ｕ'
        {'\u0056','\uff36'}, // 'V','Ｖ'
        {'\u0057','\uff37'}, // 'W','Ｗ'
        {'\u0058','\uff38'}, // 'X','Ｘ'
        {'\u0059','\uff39'}, // 'Y','Ｙ'
        {'\u005a','\uff3a'}  // 'Z','Ｚ'
    };
    
    /**
     * 半角→全角変換の英字コンバータを生成する。<p>
     */
    public AlphabetCharacterConverter(){
        this(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * 指定された変換種別の英字コンバーターを生成する。<p>
     *
     * @param type 変換種別
     * @see #HANKAKU_TO_ZENKAKU
     * @see #ZENKAKU_TO_HANKAKU
     */
    public AlphabetCharacterConverter(int type){
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
