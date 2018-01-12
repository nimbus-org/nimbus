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
 *     <tr><td>ｳﾞ</td><td>ヴ</td></tr>
 *     <tr><td>ﾞ</td><td>゛</td></tr>
 *     <tr><td>ﾟ</td><td>゜</td></tr>
 *     <tr><td>ｶﾞ</td><td>ガ</td></tr>
 *     <tr><td>ｷﾞ</td><td>ギ</td></tr>
 *     <tr><td>ｸﾞ</td><td>グ</td></tr>
 *     <tr><td>ｹﾞ</td><td>ゲ</td></tr>
 *     <tr><td>ｺﾞ</td><td>ゴ</td></tr>
 *     <tr><td>ｻﾞ</td><td>ザ</td></tr>
 *     <tr><td>ｼﾞ</td><td>ジ</td></tr>
 *     <tr><td>ｽﾞ</td><td>ズ</td></tr>
 *     <tr><td>ｾﾞ</td><td>ゼ</td></tr>
 *     <tr><td>ｿﾞ</td><td>ゾ</td></tr>
 *     <tr><td>ﾀﾞ</td><td>ダ</td></tr>
 *     <tr><td>ﾁﾞ</td><td>ヂ</td></tr>
 *     <tr><td>ﾂﾞ</td><td>ヅ</td></tr>
 *     <tr><td>ﾃﾞ</td><td>デ</td></tr>
 *     <tr><td>ﾄﾞ</td><td>ド</td></tr>
 *     <tr><td>ﾊﾞ</td><td>バ</td></tr>
 *     <tr><td>ﾋﾞ</td><td>ビ</td></tr>
 *     <tr><td>ﾌﾞ</td><td>ブ</td></tr>
 *     <tr><td>ﾍﾞ</td><td>ベ</td></tr>
 *     <tr><td>ﾎﾞ</td><td>ボ</td></tr>
 *     <tr><td>ﾊﾞ</td><td>パ</td></tr>
 *     <tr><td>ﾋﾞ</td><td>ピ</td></tr>
 *     <tr><td>ﾌﾞ</td><td>プ</td></tr>
 *     <tr><td>ﾍﾞ</td><td>ペ</td></tr>
 *     <tr><td>ﾎﾞ</td><td>ポ</td></tr>
 * </table>
 * 
 * @author M.Takata
 */
public class KatakanaStringConverter extends HankakuZenkakuStringConverter
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -5689893283542592612L;
    
    /**
     * [濁点(半濁点)付き半角カナ][全角カナ] の配列。
     */
    protected final static String CONV_STRS[][] = {
        {"\uFF73\uFF9E","\u30F4"}, //ヴ
        {"\uFF76\uFF9E","\u30AC"}, //ガ
        {"\uFF77\uFF9E","\u30AE"}, //ギ
        {"\uFF78\uFF9E","\u30B0"}, //グ
        {"\uFF79\uFF9E","\u30B2"}, //ゲ
        {"\uFF7A\uFF9E","\u30B4"}, //ゴ
        {"\uFF7B\uFF9E","\u30B6"}, //ザ
        {"\uFF7C\uFF9E","\u30B8"}, //ジ
        {"\uFF7D\uFF9E","\u30BA"}, //ズ
        {"\uFF7E\uFF9E","\u30BC"}, //ゼ
        {"\uFF7F\uFF9E","\u30BE"}, //ゾ
        {"\uFF80\uFF9E","\u30C0"}, //ダ
        {"\uFF81\uFF9E","\u30C2"}, //ヂ
        {"\uFF82\uFF9E","\u30C5"}, //ヅ
        {"\uFF83\uFF9E","\u30C7"}, //デ
        {"\uFF84\uFF9E","\u30C9"}, //ド
        {"\uFF8A\uFF9E","\u30D0"}, //バ
        {"\uFF8B\uFF9E","\u30D3"}, //ビ
        {"\uFF8C\uFF9E","\u30D6"}, //ブ
        {"\uFF8D\uFF9E","\u30D9"}, //ベ
        {"\uFF8E\uFF9E","\u30DC"}, //ボ
        {"\uFF8A\uFF9F","\u30D1"}, //パ
        {"\uFF8B\uFF9F","\u30D4"}, //ピ
        {"\uFF8C\uFF9F","\u30D7"}, //プ
        {"\uFF8D\uFF9F","\u30DA"}, //ペ
        {"\uFF8E\uFF9F","\u30DD"}  //ポ
    };
    
    /**
     * 半角→全角変換種別のカタカナコンバータを生成する。<p>
     */
    public KatakanaStringConverter(){
        super(HANKAKU_TO_ZENKAKU);
    }
    
    /**
     * カタカナコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see HankakuZenkakuStringConverter#HANKAKU_TO_ZENKAKU
     * @see HankakuZenkakuStringConverter#ZENKAKU_TO_HANKAKU
     */
    public KatakanaStringConverter(int type){
        super(type);
    }
    
    /**
     * 半角全角変換キャラクタ配列を取得する。<p>
     *
     * @return {@link KatakanaCharacterConverter#CONV_CHARS}
     */
    protected char[][] getHankakuZenkakuChars(){
        return KatakanaCharacterConverter.CONV_CHARS;
    }
    
    /**
     * 半角全角変換文字列配列を取得する。<p>
     *
     * @return {@link #CONV_STRS}
     */
    protected String[][] getHankakuZenkakuStrings(){
        return CONV_STRS;
    }
}
