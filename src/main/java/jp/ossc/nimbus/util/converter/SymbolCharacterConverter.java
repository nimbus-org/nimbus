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
 * 記号コンバータ。<p>
 * <table border=5>
 *     <tr><th>半角記号</th><th>全角記号</th></tr>
 *     <tr><td>&nbsp;</td><td>　</td></tr>
 *     <tr><td>!</td><td>！</td></tr>
 *     <tr><td>"</td><td>”</td></tr>
 *     <tr><td>#</td><td>＃</td></tr>
 *     <tr><td>$</td><td>＄</td></tr>
 *     <tr><td>%</td><td>％</td></tr>
 *     <tr><td>&amp</td><td>＆</td></tr>
 *     <tr><td>'</td><td>’</td></tr>
 *     <tr><td>(</td><td>（</td></tr>
 *     <tr><td>)</td><td>）</td></tr>
 *     <tr><td>*</td><td>＊</td></tr>
 *     <tr><td>+</td><td>＋</td></tr>
 *     <tr><td>,</td><td>，</td></tr>
 *     <tr><td>-</td><td>－(MS932)</td></tr>
 *     <tr><td>.</td><td>．</td></tr>
 *     <tr><td>/</td><td>／</td></tr>
 *     <tr><td>:</td><td>：</td></tr>
 *     <tr><td>;</td><td>；</td></tr>
 *     <tr><td>&lt</td><td>＜</td></tr>
 *     <tr><td>=</td><td>＝</td></tr>
 *     <tr><td>&gt</td><td>＞</td></tr>
 *     <tr><td>?</td><td>？</td></tr>
 *     <tr><td>@</td><td>＠</td></tr>
 *     <tr><td>[</td><td>［</td></tr>
 *     <tr><td>\</td><td>￥(MS932)</td></tr>
 *     <tr><td>]</td><td>］</td></tr>
 *     <tr><td>^</td><td>＾</td></tr>
 *     <tr><td>_</td><td>＿</td></tr>
 *     <tr><td>`</td><td>‘</td></tr>
 *     <tr><td>{</td><td>｛</td></tr>
 *     <tr><td>|</td><td>｜</td></tr>
 *     <tr><td>}</td><td>｝</td></tr>
 *     <tr><td>~</td><td>～</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public class SymbolCharacterConverter extends HankakuZenkakuCharacterConverter
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 7864849670204503111L;
    
    /**
     * [半角記号][全角記号] の配列
     */
    protected final static char CONV_CHARS[][] = {
        /* 0x0020 - 0x002F */
        {'\u0020','\u3000'}, // 　
        {'\u0021','\uff01'}, // ！
        {'\u0022','\u201d'}, // ”
        {'\u0023','\uff03'}, // ＃
        {'\u0024','\uff04'}, // ＄
        {'\u0025','\uff05'}, // ％
        {'\u0026','\uff06'}, // ＆
        {'\u005C\u0027','\u2019'}, // ’
        {'\u0028','\uff08'}, // （
        {'\u0029','\uff09'}, // ）
        {'\u002A','\uff0a'}, // ＊
        {'\u002B','\uff0b'}, // ＋
        {'\u002C','\uff0c'}, // ，
        {'\u002D','\uff0d'}, // －  // MS932 でのハイフン対応
        {'\u002E','\uff0e'}, // ．
        {'\u002F','\uff0f'}, // ／
        /* 0x003A - 0x0040 */
        {'\u003A','\uff1a'}, // ：
        {'\u003B','\uff1b'}, // ；
        {'\u003C','\uff1c'}, // ＜
        {'\u003D','\uff1d'}, // ＝
        {'\u003E','\uff1e'}, // ＞
        {'\u003F','\uff1f'}, // ？
        {'\u0040','\uff20'}, // ＠
        /* 0x005B - 0x0060 */
        {'\u005B','\uff3b'}, // ［
        {'\u005C\u005C','\uffe5'}, // ￥ MS932 
        {'\u005D','\uff3d'}, // ］
        {'\u005E','\uff3e'}, // ＾
        {'\u005F','\uff3f'}, // ＿
        {'\u0060','\u2018'}, // ‘
        /* 0x007B - 0x007E */
        {'\u007B','\uff5b'}, // ｛
        {'\u007C','\uff5c'}, // ｜
        {'\u007D','\uff5d'}, // ｝
        {'\u007E','\uff5e'} // ～
    };
    
    /**
     * 半角→全角変換種別の記号コンバータを生成する。<p>
     */
    public SymbolCharacterConverter(){
        super(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * 記号コンバーターを生成する。<p>
     *
     * @param type 変換種別
     * @see HankakuZenkakuCharacterConverter#HANKAKU_TO_ZENKAKU
     * @see HankakuZenkakuCharacterConverter#ZENKAKU_TO_HANKAKU
     */
    public SymbolCharacterConverter(int type){
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
