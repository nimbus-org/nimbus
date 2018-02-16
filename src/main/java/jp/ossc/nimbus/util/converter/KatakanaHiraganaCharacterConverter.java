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
public class KatakanaHiraganaCharacterConverter
 extends AbstractCharacterConverter implements java.io.Serializable{
    
    private static final long serialVersionUID = 2219660761265490304L;
    
    /**
     * カタカナ→ひらがな変換を表す変換種別定数。<p>
     */
    public static final int KATAKANA_TO_HIRAGANA = POSITIVE_CONVERT;
    
    /**
     * ひらがな→カタカナ変換を表す変換種別定数。<p>
     */
    public static final int HIRAGANA_TO_KATAKANA = REVERSE_CONVERT;
    
    /**
     * [全角カナ][全角かな] の配列。
     */
    protected final static char CONV_CHARS[][] = {
        {'\u30a2','\u3042'}, //{'ア','あ'}
        {'\u30a1','\u3041'}, //{'ァ','ぁ'}
        {'\u30a4','\u3044'}, //{'イ','い'}
        {'\u30a3','\u3043'}, //{'ィ','ぃ'}
        {'\u30a6','\u3046'}, //{'ウ','う'}
        {'\u30a5','\u3045'}, //{'ゥ','ぅ'}
        {'\u30a8','\u3048'}, //{'エ','え'}
        {'\u30a7','\u3047'}, //{'ェ','ぇ'}
        {'\u30aa','\u304a'}, //{'オ','お'}
        {'\u30a9','\u3049'}, //{'ォ','ぉ'}
        {'\u30ab','\u304b'}, //{'カ','か'}
        {'\u30ac','\u304c'}, //{'ガ','が'}
        {'\u30ad','\u304d'}, //{'キ','き'}
        {'\u30ae','\u304e'}, //{'ギ','ぎ'}
        {'\u30af','\u304f'}, //{'ク','く'}
        {'\u30b0','\u3050'}, //{'グ','ぐ'}
        {'\u30b1','\u3051'}, //{'ケ','け'}
        {'\u30b2','\u3052'}, //{'ゲ','げ'}
        {'\u30b3','\u3053'}, //{'コ','こ'}
        {'\u30b4','\u3054'}, //{'ゴ','ご'}
        {'\u30b5','\u3055'}, //{'サ','さ'}
        {'\u30b6','\u3056'}, //{'ザ','ざ'}
        {'\u30b7','\u3057'}, //{'シ','し'}
        {'\u30b8','\u3058'}, //{'ジ','じ'}
        {'\u30b9','\u3059'}, //{'ス','す'}
        {'\u30ba','\u305a'}, //{'ズ','ず'}
        {'\u30bb','\u305b'}, //{'セ','せ'}
        {'\u30bc','\u305c'}, //{'ゼ','ぜ'}
        {'\u30bd','\u305d'}, //{'ソ','そ'}
        {'\u30be','\u305e'}, //{'ゾ','ぞ'}
        {'\u30bf','\u305f'}, //{'タ','た'}
        {'\u30c0','\u3060'}, //{'ダ','だ'}
        {'\u30c1','\u3061'}, //{'チ','ち'}
        {'\u30c2','\u3062'}, //{'ヂ','ぢ'}
        {'\u30c3','\u3063'}, //{'ッ','っ'}
        {'\u30c4','\u3064'}, //{'ツ','つ'}
        {'\u30c5','\u3065'}, //{'ヅ','づ'}
        {'\u30c6','\u3066'}, //{'テ','て'}
        {'\u30c7','\u3067'}, //{'デ','で'}
        {'\u30c8','\u3068'}, //{'ト','と'}
        {'\u30c9','\u3069'}, //{'ド','ど'}
        {'\u30ca','\u306a'}, //{'ナ','な'}
        {'\u30cb','\u306b'}, //{'ニ','に'}
        {'\u30cc','\u306c'}, //{'ヌ','ぬ'}
        {'\u30cd','\u306d'}, //{'ネ','ね'}
        {'\u30ce','\u306e'}, //{'ノ','の'}
        {'\u30cf','\u306f'}, //{'ハ','は'}
        {'\u30d0','\u3070'}, //{'バ','ば'}
        {'\u30d1','\u3071'}, //{'パ','ぱ'}
        {'\u30d2','\u3072'}, //{'ヒ','ひ'}
        {'\u30d3','\u3073'}, //{'ビ','び'}
        {'\u30d4','\u3074'}, //{'ピ','ぴ'}
        {'\u30d5','\u3075'}, //{'フ','ふ'}
        {'\u30d6','\u3076'}, //{'ブ','ぶ'}
        {'\u30d7','\u3077'}, //{'プ','ぷ'}
        {'\u30d8','\u3078'}, //{'ヘ','へ'}
        {'\u30d9','\u3079'}, //{'ベ','べ'}
        {'\u30da','\u307a'}, //{'ペ','ぺ'}
        {'\u30db','\u307b'}, //{'ホ','ほ'}
        {'\u30dc','\u307c'}, //{'ボ','ぼ'}
        {'\u30dd','\u307d'}, //{'ポ','ぽ'}
        {'\u30de','\u307e'}, //{'マ','ま'}
        {'\u30df','\u307f'}, //{'ミ','み'}
        {'\u30e0','\u3080'}, //{'ム','む'}
        {'\u30e1','\u3081'}, //{'メ','め'}
        {'\u30e2','\u3082'}, //{'モ','も'}
        {'\u30e4','\u3084'}, //{'ヤ','や'}
        {'\u30e3','\u3083'}, //{'ャ','ゃ'}
        {'\u30e6','\u3086'}, //{'ユ','ゆ'}
        {'\u30e5','\u3085'}, //{'ュ','ゅ'}
        {'\u30e8','\u3088'}, //{'ヨ','よ'}
        {'\u30e7','\u3087'}, //{'ョ','ょ'}
        {'\u30e9','\u3089'}, //{'ラ','ら'}
        {'\u30ea','\u308a'}, //{'リ','り'}
        {'\u30eb','\u308b'}, //{'ル','る'}
        {'\u30ec','\u308c'}, //{'レ','れ'}
        {'\u30ed','\u308d'}, //{'ロ','ろ'}
        {'\u30ee','\u308e'}, //{'ヮ','ゎ'}
        {'\u30ef','\u308f'}, //{'ワ','わ'}
        {'\u30f0','\u3090'}, //{'ヰ','ゐ'}
        {'\u30f1','\u3091'}, //{'ヱ','ゑ'}
        {'\u30f2','\u3092'}, //{'ヲ','を'}
        {'\u30f3','\u3093'}, //{'ン','ん'}
        {'\u30fd','\u309d'}, //{'ヽ','ゝ'}
        {'\u30fe','\u309e'}  //{'ヾ','ゞ'}
    };
    
    /**
     * カタカナ→ひらがな変換種別のカタカナひらがなコンバータを生成する。<p>
     */
    public KatakanaHiraganaCharacterConverter(){
        super(KATAKANA_TO_HIRAGANA);
    }
    
    /**
     * カタカナひらがなコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #KATAKANA_TO_HIRAGANA
     * @see #HIRAGANA_TO_KATAKANA
     */
    public KatakanaHiraganaCharacterConverter(int type){
        super(type);
    }
    
    /**
     * 変換キャラクタ配列を取得する。<p>
     *
     * @return {@link #CONV_CHARS}
     */
    protected char[][] getConvertChars(){
        return CONV_CHARS;
    }
}
