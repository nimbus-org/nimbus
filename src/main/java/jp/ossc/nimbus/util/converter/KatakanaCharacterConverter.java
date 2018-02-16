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
 * カタカナコンバータ。<p>
 * <table border=5>
 *     <tr><th>半角カナ</th><th>全角カナ</th></tr>
 *     <tr><td>｡</td><td>。</td></tr>
 *     <tr><td>｢</td><td>「</td></tr>
 *     <tr><td>｣</td><td>」</td></tr>
 *     <tr><td>､</td><td>、</td></tr>
 *     <tr><td>･</td><td>・</td></tr>
 *     <tr><td>ｦ</td><td>ヲ</td></tr>
 *     <tr><td>ｧ</td><td>ァ</td></tr>
 *     <tr><td>ｨ</td><td>ィ</td></tr>
 *     <tr><td>ｩ</td><td>ゥ</td></tr>
 *     <tr><td>ｪ</td><td>ェ</td></tr>
 *     <tr><td>ｫ</td><td>ォ</td></tr>
 *     <tr><td>ｬ</td><td>ャ</td></tr>
 *     <tr><td>ｭ</td><td>ュ</td></tr>
 *     <tr><td>ｮ</td><td>ョ</td></tr>
 *     <tr><td>ｯ</td><td>ッ</td></tr>
 *     <tr><td>ｰ</td><td>ー</td></tr>
 *     <tr><td>ｱ</td><td>ア</td></tr>
 *     <tr><td>ｲ</td><td>イ</td></tr>
 *     <tr><td>ｳ</td><td>ウ</td></tr>
 *     <tr><td>ｴ</td><td>エ</td></tr>
 *     <tr><td>ｵ</td><td>オ</td></tr>
 *     <tr><td>ｶ</td><td>カ</td></tr>
 *     <tr><td>ｷ</td><td>キ</td></tr>
 *     <tr><td>ｸ</td><td>ク</td></tr>
 *     <tr><td>ｹ</td><td>ケ</td></tr>
 *     <tr><td>ｺ</td><td>コ</td></tr>
 *     <tr><td>ｻ</td><td>サ</td></tr>
 *     <tr><td>ｼ</td><td>シ</td></tr>
 *     <tr><td>ｽ</td><td>ス</td></tr>
 *     <tr><td>ｾ</td><td>セ</td></tr>
 *     <tr><td>ｿ</td><td>ソ</td></tr>
 *     <tr><td>ﾀ</td><td>タ</td></tr>
 *     <tr><td>ﾁ</td><td>チ</td></tr>
 *     <tr><td>ﾂ</td><td>ツ</td></tr>
 *     <tr><td>ﾃ</td><td>テ</td></tr>
 *     <tr><td>ﾄ</td><td>ト</td></tr>
 *     <tr><td>ﾅ</td><td>ナ</td></tr>
 *     <tr><td>ﾆ</td><td>ニ</td></tr>
 *     <tr><td>ﾇ</td><td>ヌ</td></tr>
 *     <tr><td>ﾈ</td><td>ネ</td></tr>
 *     <tr><td>ﾉ</td><td>ノ</td></tr>
 *     <tr><td>ﾊ</td><td>ハ</td></tr>
 *     <tr><td>ﾋ</td><td>ヒ</td></tr>
 *     <tr><td>ﾌ</td><td>フ</td></tr>
 *     <tr><td>ﾍ</td><td>ヘ</td></tr>
 *     <tr><td>ﾎ</td><td>ホ</td></tr>
 *     <tr><td>ﾏ</td><td>マ</td></tr>
 *     <tr><td>ﾐ</td><td>ミ</td></tr>
 *     <tr><td>ﾑ</td><td>ム</td></tr>
 *     <tr><td>ﾒ</td><td>メ</td></tr>
 *     <tr><td>ﾓ</td><td>モ</td></tr>
 *     <tr><td>ﾔ</td><td>ヤ</td></tr>
 *     <tr><td>ﾕ</td><td>ユ</td></tr>
 *     <tr><td>ﾖ</td><td>ヨ</td></tr>
 *     <tr><td>ﾗ</td><td>ラ</td></tr>
 *     <tr><td>ﾘ</td><td>リ</td></tr>
 *     <tr><td>ﾙ</td><td>ル</td></tr>
 *     <tr><td>ﾚ</td><td>レ</td></tr>
 *     <tr><td>ﾛ</td><td>ロ</td></tr>
 *     <tr><td>ﾜ</td><td>ワ</td></tr>
 *     <tr><td>ﾝ</td><td>ン</td></tr>
 *     <tr><td>ﾞ</td><td>゛</td></tr>
 *     <tr><td>ﾟ</td><td>゜</td></tr>
 * </table>
 * 
 * @author M.Takata
 */
public class KatakanaCharacterConverter
 extends HankakuZenkakuCharacterConverter implements java.io.Serializable{
    
    private static final long serialVersionUID = 4015624769196620285L;
    
    /**
     * [半角カナ][全角カナ] の配列。
     */
    protected final static char CONV_CHARS[][] = {
        {'\uFF61','\u3002'}, //。
        {'\uFF62','\u300c'}, //「
        {'\uFF63','\u300d'}, //」
        {'\uFF64','\u3001'}, //、
        {'\uFF65','\u30FB'}, //・
        {'\uFF66','\u30F2'}, //ヲ
        {'\uFF67','\u30A1'}, //ァ
        {'\uFF68','\u30A3'}, //ィ
        {'\uFF69','\u30A5'}, //ゥ
        {'\uFF6A','\u30A7'}, //ェ
        {'\uFF6B','\u30A9'}, //ォ
        {'\uFF6C','\u30E3'}, //ャ
        {'\uFF6D','\u30E5'}, //ュ
        {'\uFF6E','\u30E7'}, //ョ
        {'\uFF6F','\u30C3'}, //ッ
        {'\uFF70','\u30FC'}, //ー (ハイフンではない)
        {'\uFF71','\u30A2'}, //ア
        {'\uFF72','\u30A4'}, //イ
        {'\uFF73','\u30A6'}, //ウ
        {'\uFF74','\u30A8'}, //エ
        {'\uFF75','\u30AA'}, //オ
        {'\uFF76','\u30AB'}, //カ
        {'\uFF77','\u30AD'}, //キ
        {'\uFF78','\u30AF'}, //ク
        {'\uFF79','\u30B1'}, //ケ
        {'\uFF7A','\u30B3'}, //コ
        {'\uFF7B','\u30B5'}, //サ
        {'\uFF7C','\u30B7'}, //シ
        {'\uFF7D','\u30B9'}, //ス
        {'\uFF7E','\u30BB'}, //セ
        {'\uFF7F','\u30BD'}, //ソ
        {'\uFF80','\u30BF'}, //タ
        {'\uFF81','\u30C1'}, //チ
        {'\uFF82','\u30C4'}, //ツ
        {'\uFF83','\u30C6'}, //テ
        {'\uFF84','\u30C8'}, //ト
        {'\uFF85','\u30CA'}, //ナ
        {'\uFF86','\u30CB'}, //ニ
        {'\uFF87','\u30CC'}, //ヌ
        {'\uFF88','\u30CD'}, //ネ
        {'\uFF89','\u30CE'}, //ノ
        {'\uFF8A','\u30CF'}, //ハ
        {'\uFF8B','\u30D2'}, //ヒ
        {'\uFF8C','\u30D5'}, //フ
        {'\uFF8D','\u30D8'}, //ヘ
        {'\uFF8E','\u30DB'}, //ホ
        {'\uFF8F','\u30DE'}, //マ
        {'\uFF90','\u30DF'}, //ミ
        {'\uFF91','\u30E0'}, //ム
        {'\uFF92','\u30E1'}, //メ
        {'\uFF93','\u30E2'}, //モ
        {'\uFF94','\u30E4'}, //ヤ
        {'\uFF95','\u30E6'}, //ユ
        {'\uFF96','\u30E8'}, //ヨ
        {'\uFF97','\u30E9'}, //ラ
        {'\uFF98','\u30EA'}, //リ
        {'\uFF99','\u30EB'}, //ル
        {'\uFF9A','\u30EC'}, //レ
        {'\uFF9B','\u30ED'}, //ロ
        {'\uFF9C','\u30EF'}, //ワ
        {'\uFF9D','\u30F3'}, //ン
        {'\uFF9E','\u309B'}, //゛（濁点）
        {'\uFF9F','\u309C'}  //゜（半濁点）
    };
    
    /**
     * 半角→全角変換種別のカタカナコンバータを生成する。<p>
     */
    public KatakanaCharacterConverter(){
        super(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * カタカナコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see HankakuZenkakuCharacterConverter#HANKAKU_TO_ZENKAKU
     * @see HankakuZenkakuCharacterConverter#ZENKAKU_TO_HANKAKU
     */
    public KatakanaCharacterConverter(int type){
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
