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
 * カタカナひらがなコンバータ。<p>
 * <table border=5 align=center>
 *     <tr><th>カタカナ</th><th>ひらがな</th></tr>
 *     <tr><td>ア</td><td>あ</td></tr>
 *     <tr><td>ァ</td><td>ぁ</td></tr>
 *     <tr><td>イ</td><td>い</td></tr>
 *     <tr><td>ィ</td><td>ぃ</td></tr>
 *     <tr><td>ウ</td><td>う</td></tr>
 *     <tr><td>ゥ</td><td>ぅ</td></tr>
 *     <tr><td>エ</td><td>え</td></tr>
 *     <tr><td>ェ</td><td>ぇ</td></tr>
 *     <tr><td>オ</td><td>お</td></tr>
 *     <tr><td>ォ</td><td>ぉ</td></tr>
 *     <tr><td>カ</td><td>か</td></tr>
 *     <tr><td>ガ</td><td>が</td></tr>
 *     <tr><td>キ</td><td>き</td></tr>
 *     <tr><td>ギ</td><td>ぎ</td></tr>
 *     <tr><td>ク</td><td>く</td></tr>
 *     <tr><td>グ</td><td>ぐ</td></tr>
 *     <tr><td>ケ</td><td>け</td></tr>
 *     <tr><td>ゲ</td><td>げ</td></tr>
 *     <tr><td>コ</td><td>こ</td></tr>
 *     <tr><td>ゴ</td><td>ご</td></tr>
 *     <tr><td>サ</td><td>さ</td></tr>
 *     <tr><td>ザ</td><td>ざ</td></tr>
 *     <tr><td>シ</td><td>し</td></tr>
 *     <tr><td>ジ</td><td>じ</td></tr>
 *     <tr><td>ス</td><td>す</td></tr>
 *     <tr><td>ズ</td><td>ず</td></tr>
 *     <tr><td>セ</td><td>せ</td></tr>
 *     <tr><td>ゼ</td><td>ぜ</td></tr>
 *     <tr><td>ソ</td><td>そ</td></tr>
 *     <tr><td>ゾ</td><td>ぞ</td></tr>
 *     <tr><td>タ</td><td>た</td></tr>
 *     <tr><td>ダ</td><td>だ</td></tr>
 *     <tr><td>チ</td><td>ち</td></tr>
 *     <tr><td>ヂ</td><td>ぢ</td></tr>
 *     <tr><td>ッ</td><td>っ</td></tr>
 *     <tr><td>ツ</td><td>つ</td></tr>
 *     <tr><td>ヅ</td><td>づ</td></tr>
 *     <tr><td>テ</td><td>て</td></tr>
 *     <tr><td>デ</td><td>で</td></tr>
 *     <tr><td>ト</td><td>と</td></tr>
 *     <tr><td>ド</td><td>ど</td></tr>
 *     <tr><td>ナ</td><td>な</td></tr>
 *     <tr><td>ニ</td><td>に</td></tr>
 *     <tr><td>ヌ</td><td>ぬ</td></tr>
 *     <tr><td>ネ</td><td>ね</td></tr>
 *     <tr><td>ノ</td><td>の</td></tr>
 *     <tr><td>ハ</td><td>は</td></tr>
 *     <tr><td>バ</td><td>ば</td></tr>
 *     <tr><td>パ</td><td>ぱ</td></tr>
 *     <tr><td>ヒ</td><td>ひ</td></tr>
 *     <tr><td>ビ</td><td>び</td></tr>
 *     <tr><td>ピ</td><td>ぴ</td></tr>
 *     <tr><td>フ</td><td>ふ</td></tr>
 *     <tr><td>ブ</td><td>ぶ</td></tr>
 *     <tr><td>プ</td><td>ぷ</td></tr>
 *     <tr><td>ヘ</td><td>へ</td></tr>
 *     <tr><td>ベ</td><td>べ</td></tr>
 *     <tr><td>ペ</td><td>ぺ</td></tr>
 *     <tr><td>ホ</td><td>ほ</td></tr>
 *     <tr><td>ボ</td><td>ぼ</td></tr>
 *     <tr><td>ポ</td><td>ぽ</td></tr>
 *     <tr><td>マ</td><td>ま</td></tr>
 *     <tr><td>ミ</td><td>み</td></tr>
 *     <tr><td>ム</td><td>む</td></tr>
 *     <tr><td>メ</td><td>め</td></tr>
 *     <tr><td>モ</td><td>も</td></tr>
 *     <tr><td>ヤ</td><td>や</td></tr>
 *     <tr><td>ャ</td><td>ゃ</td></tr>
 *     <tr><td>ユ</td><td>ゆ</td></tr>
 *     <tr><td>ュ</td><td>ゅ</td></tr>
 *     <tr><td>ヨ</td><td>よ</td></tr>
 *     <tr><td>ョ</td><td>ょ</td></tr>
 *     <tr><td>ラ</td><td>ら</td></tr>
 *     <tr><td>リ</td><td>り</td></tr>
 *     <tr><td>ル</td><td>る</td></tr>
 *     <tr><td>レ</td><td>れ</td></tr>
 *     <tr><td>ロ</td><td>ろ</td></tr>
 *     <tr><td>ヮ</td><td>ゎ</td></tr>
 *     <tr><td>ワ</td><td>わ</td></tr>
 *     <tr><td>ヰ</td><td>ゐ</td></tr>
 *     <tr><td>ヱ</td><td>ゑ</td></tr>
 *     <tr><td>ヲ</td><td>を</td></tr>
 *     <tr><td>ン</td><td>ん</td></tr>
 *     <tr><td>ヽ</td><td>ゝ</td></tr>
 *     <tr><td>ヾ</td><td>ゞ</td></tr>
 * </table>
 * 
 * @author M.Takata
 */
public class KatakanaHiraganaStringConverter extends AbstractStringConverter
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 2095171984891385726L;
    
    /**
     * カタカナ→ひらがな変換を表す変換種別定数。<p>
     */
    public static final int KATAKANA_TO_HIRAGANA = POSITIVE_CONVERT;
    
    /**
     * ひらがな→カタカナ変換を表す変換種別定数。<p>
     */
    public static final int HIRAGANA_TO_KATAKANA = REVERSE_CONVERT;
    
    /**
     * カタカナ→ひらがな変換種別のカタカナひらがなコンバータを生成する。<p>
     */
    public KatakanaHiraganaStringConverter(){
        super(KATAKANA_TO_HIRAGANA);
    }
    
    /**
     * カタカナひらがなコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #KATAKANA_TO_HIRAGANA
     * @see #HIRAGANA_TO_KATAKANA
     */
    public KatakanaHiraganaStringConverter(int type){
        super(type);
    }
    
    /**
     * 変換キャラクタ配列を取得する。<p>
     *
     * @return {@link KatakanaHiraganaCharacterConverter#CONV_CHARS}
     */
    protected char[][] getConvertChars(){
        return KatakanaHiraganaCharacterConverter.CONV_CHARS;
    }
    
    /**
     * 変換文字列配列を取得する。<p>
     *
     * @return null
     */
    protected String[][] getConvertStrings(){
        return null;
    }
}
