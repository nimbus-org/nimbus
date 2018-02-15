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

import java.io.*;

/**
 * コンバータユーティリティ。<p>
 * 
 * @author M.Takata
 */
public class Converters{
    
    private static final long serialVersionUID = -7008480092627389111L;
    
    private static final StringConverter ALPHABET_HANKAKU_TO_ZENKAKU_STRING
         = new StringConverterImpl(
               new AlphabetStringConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final StringConverter ALPHABET_ZENKAKU_TO_HANKAKU_STRING
         = new StringConverterImpl(
               new AlphabetStringConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final StringConverter KATAKANA_HANKAKU_TO_ZENKAKU_STRING
         = new StringConverterImpl(
               new KatakanaStringConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final StringConverter KATAKANA_ZENKAKU_TO_HANKAKU_STRING
         = new StringConverterImpl(
               new KatakanaStringConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final StringConverter NUMBER_HANKAKU_TO_ZENKAKU_STRING
         = new StringConverterImpl(
               new NumberStringConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final StringConverter NUMBER_ZENKAKU_TO_HANKAKU_STRING
         = new StringConverterImpl(
               new NumberStringConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final StringConverter SYMBOL_HANKAKU_TO_ZENKAKU_STRING
         = new StringConverterImpl(
               new SymbolStringConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final StringConverter SYMBOL_ZENKAKU_TO_HANKAKU_STRING
         = new StringConverterImpl(
               new SymbolStringConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final StringConverter KATAKANA_TO_HIRAGANA_STRING
         = new StringConverterImpl(
               new KatakanaHiraganaStringConverter(
                   KatakanaHiraganaStringConverter.KATAKANA_TO_HIRAGANA
               )
           );
    private static final StringConverter HIRAGANA_TO_KATAKANA_STRING
         = new StringConverterImpl(
               new KatakanaHiraganaStringConverter(
                   KatakanaHiraganaStringConverter.HIRAGANA_TO_KATAKANA
               )
           );
    private static final StringConverter HANKAKU_TO_ZENKAKU_STRING
         = new StringConverterImpl(
               new CustomConverter(
                   new Converter[]{
                       new AlphabetStringConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       ),
                       new KatakanaStringConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       ),
                       new NumberStringConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       ),
                       new SymbolStringConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       )
                   }
               )
           );
    private static final StringConverter ZENKAKU_TO_HANKAKU_STRING
         = new StringConverterImpl(
               new CustomConverter(
                   new Converter[]{
                       new AlphabetStringConverter(
                           HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                       ),
                       new KatakanaStringConverter(
                           HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                       ),
                       new NumberStringConverter(
                           HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                       ),
                       new SymbolStringConverter(
                           HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                       )
                   }
               )
           );
    
    private static final CharacterConverter ALPHABET_HANKAKU_TO_ZENKAKU_CHAR
         = new CharacterConverterImpl(
               new AlphabetCharacterConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final CharacterConverter ALPHABET_ZENKAKU_TO_HANKAKU_CHAR
         = new CharacterConverterImpl(
               new AlphabetCharacterConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final CharacterConverter KATAKANA_HANKAKU_TO_ZENKAKU_CHAR
         = new CharacterConverterImpl(
               new KatakanaCharacterConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final CharacterConverter KATAKANA_ZENKAKU_TO_HANKAKU_CHAR
         = new CharacterConverterImpl(
               new KatakanaCharacterConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final CharacterConverter NUMBER_HANKAKU_TO_ZENKAKU_CHAR
         = new CharacterConverterImpl(
               new NumberCharacterConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final CharacterConverter NUMBER_ZENKAKU_TO_HANKAKU_CHAR
         = new CharacterConverterImpl(
               new NumberCharacterConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final CharacterConverter SYMBOL_HANKAKU_TO_ZENKAKU_CHAR
         = new CharacterConverterImpl(
               new SymbolCharacterConverter(
                   HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
               )
           );
    private static final CharacterConverter SYMBOL_ZENKAKU_TO_HANKAKU_CHAR
         = new CharacterConverterImpl(
               new SymbolCharacterConverter(
                   HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
               )
           );
    private static final CharacterConverter KATAKANA_TO_HIRAGANA_CHAR
         = new CharacterConverterImpl(
               new KatakanaHiraganaCharacterConverter(
                   KatakanaHiraganaCharacterConverter.KATAKANA_TO_HIRAGANA
               )
           );
    private static final CharacterConverter HIRAGANA_TO_KATAKANA_CHAR
         = new CharacterConverterImpl(
               new KatakanaHiraganaCharacterConverter(
                   KatakanaHiraganaCharacterConverter.HIRAGANA_TO_KATAKANA
               )
           );
    private static final CharacterConverter HANKAKU_TO_ZENKAKU_CHAR
         = new CharacterConverterImpl(
               new CustomConverter(
                   new Converter[]{
                       new AlphabetCharacterConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       ),
                       new KatakanaCharacterConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       ),
                       new NumberCharacterConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       ),
                       new SymbolCharacterConverter(
                           HankakuZenkakuConverter.HANKAKU_TO_ZENKAKU
                       )
                   }
               )
           );
    private static final CharacterConverter ZENKAKU_TO_HANKAKU_CHAR
         = new CharacterConverterImpl(
              new CustomConverter(
                  new Converter[]{
                      new AlphabetCharacterConverter(
                          HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                      ),
                      new KatakanaCharacterConverter(
                          HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                      ),
                      new NumberCharacterConverter(
                          HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                      ),
                       new SymbolCharacterConverter(
                          HankakuZenkakuConverter.ZENKAKU_TO_HANKAKU
                      )
                  }
              )
         );
    
    private Converters(){
    }
    
    /**
     * 半角英字→全角英字コンバータを取得する。<p>
     *
     * @return 半角英字→全角英字コンバータ
     * @see AlphabetStringConverter
     */
    public static StringConverter getAlphabetHankakuFromZenkakuStringConverter(){
        return ALPHABET_HANKAKU_TO_ZENKAKU_STRING;
    }
    
    /**
     * 全角英字→半角英字コンバータを取得する。<p>
     *
     * @return 全角英字→半角英字コンバータ
     * @see AlphabetStringConverter
     */
    public static StringConverter getAlphabetZenkakuFromHankakuStringConverter(){
        return ALPHABET_ZENKAKU_TO_HANKAKU_STRING;
    }
    
    /**
     * 半角カタカナ→全角カタカナコンバータを取得する。<p>
     *
     * @return 半角カタカナ→全角カタカナコンバータ
     * @see KatakanaStringConverter
     */
    public static StringConverter getKatakanaHankakuFromZenkakuStringConverter(){
        return KATAKANA_HANKAKU_TO_ZENKAKU_STRING;
    }
    
    /**
     * 全角カタカナ→半角カタカナコンバータを取得する。<p>
     *
     * @return 全角カタカナ→半角カタカナコンバータ
     * @see KatakanaStringConverter
     */
    public static StringConverter getKatakanaZenkakuFromHankakuStringConverter(){
        return KATAKANA_ZENKAKU_TO_HANKAKU_STRING;
    }
    
    /**
     * 半角数字→全角数字コンバータを取得する。<p>
     *
     * @return 半角数字→全角数字コンバータ
     * @see NumberStringConverter
     */
    public static StringConverter getNumberHankakuFromZenkakuStringConverter(){
        return NUMBER_HANKAKU_TO_ZENKAKU_STRING;
    }
    
    /**
     * 全角数字→半角数字コンバータを取得する。<p>
     *
     * @return 全角数字→半角数字コンバータ
     * @see NumberStringConverter
     */
    public static StringConverter getNumberZenkakuFromHankakuStringConverter(){
        return NUMBER_ZENKAKU_TO_HANKAKU_STRING;
    }
    
    /**
     * 半角記号→全角記号コンバータを取得する。<p>
     *
     * @return 半角記号→全角記号コンバータ
     * @see SymbolStringConverter
     */
    public static StringConverter getSymbolHankakuFromZenkakuStringConverter(){
        return SYMBOL_HANKAKU_TO_ZENKAKU_STRING;
    }
    
    /**
     * 全角記号→半角記号コンバータを取得する。<p>
     *
     * @return 全角記号→半角記号コンバータ
     * @see SymbolStringConverter
     */
    public static StringConverter getSymbolZenkakuFromHankakuStringConverter(){
        return SYMBOL_ZENKAKU_TO_HANKAKU_STRING;
    }
    
    /**
     * 全角カタカナ→全角ひらがなコンバータを取得する。<p>
     *
     * @return 全角カタカナ→全角ひらがなコンバータ
     * @see KatakanaHiraganaStringConverter
     */
    public static StringConverter getKatakanaFromHiraganaStringConverter(){
        return KATAKANA_TO_HIRAGANA_STRING;
    }
    
    /**
     * 全角ひらがな→全角カタカナコンバータを取得する。<p>
     *
     * @return 全角ひらがな→全角カタカナコンバータ
     * @see KatakanaHiraganaStringConverter
     */
    public static StringConverter getHiraganaFromKatakanaStringConverter(){
        return HIRAGANA_TO_KATAKANA_STRING;
    }
    
    /**
     * 半角→全角コンバータを取得する。<p>
     * 英字、カタカナ、数字、記号の半角→全角変換を行うコンバータを取得する。
     *
     * @return 半角→全角コンバータ
     * @see AlphabetStringConverter
     * @see KatakanaStringConverter
     * @see NumberStringConverter
     * @see SymbolStringConverter
     */
    public static StringConverter getHankakuFromZenkakuStringConverter(){
        return HANKAKU_TO_ZENKAKU_STRING;
    }
    
    /**
     * 全角→半角コンバータを取得する。<p>
     * 英字、カタカナ、数字、記号の全角→半角変換を行うコンバータを取得する。
     *
     * @return 全角→半角コンバータ
     * @see AlphabetStringConverter
     * @see KatakanaStringConverter
     * @see NumberStringConverter
     * @see SymbolStringConverter
     */
    public static StringConverter getZenkakuFromHankakuStringConverter(){
        return ZENKAKU_TO_HANKAKU_STRING;
    }
    
    /**
     * 半角英字→全角英字コンバータを取得する。<p>
     *
     * @return 半角英字→全角英字コンバータ
     * @see AlphabetCharacterConverter
     */
    public static CharacterConverter getAlphabetHankakuFromZenkakuCharacterConverter(){
        return ALPHABET_HANKAKU_TO_ZENKAKU_CHAR;
    }
    /**
     * 全角英字→半角英字コンバータを取得する。<p>
     *
     * @return 全角英字→半角英字コンバータ
     * @see AlphabetCharacterConverter
     */
    public static CharacterConverter getAlphabetZenkakuFromHankakuCharacterConverter(){
        return ALPHABET_ZENKAKU_TO_HANKAKU_CHAR;
    }
    /**
     * 半角カタカナ→全角カタカナコンバータを取得する。<p>
     *
     * @return 半角カタカナ→全角カタカナコンバータ
     * @see KatakanaCharacterConverter
     */
    public static CharacterConverter getKatakanaHankakuFromZenkakuCharacterConverter(){
        return KATAKANA_HANKAKU_TO_ZENKAKU_CHAR;
    }
    /**
     * 全角カタカナ→半角カタカナコンバータを取得する。<p>
     *
     * @return 全角カタカナ→半角カタカナコンバータ
     * @see KatakanaCharacterConverter
     */
    public static CharacterConverter getKatakanaZenkakuFromHankakuCharacterConverter(){
        return KATAKANA_ZENKAKU_TO_HANKAKU_CHAR;
    }
    /**
     * 半角数字→全角数字コンバータを取得する。<p>
     *
     * @return 半角数字→全角数字コンバータ
     * @see NumberCharacterConverter
     */
    public static CharacterConverter getNumberHankakuFromZenkakuCharacterConverter(){
        return NUMBER_HANKAKU_TO_ZENKAKU_CHAR;
    }
    /**
     * 全角数字→半角数字コンバータを取得する。<p>
     *
     * @return 全角数字→半角数字コンバータ
     * @see NumberCharacterConverter
     */
    public static CharacterConverter getNumberZenkakuFromHankakuCharacterConverter(){
        return NUMBER_ZENKAKU_TO_HANKAKU_CHAR;
    }
    /**
     * 半角記号→全角記号コンバータを取得する。<p>
     *
     * @return 半角記号→全角記号コンバータ
     * @see SymbolCharacterConverter
     */
    public static CharacterConverter getSymbolHankakuFromZenkakuCharacterConverter(){
        return SYMBOL_HANKAKU_TO_ZENKAKU_CHAR;
    }
    /**
     * 全角記号→半角記号コンバータを取得する。<p>
     *
     * @return 全角記号→半角記号コンバータ
     * @see SymbolCharacterConverter
     */
    public static CharacterConverter getSymbolZenkakuFromHankakuCharacterConverter(){
        return SYMBOL_ZENKAKU_TO_HANKAKU_CHAR;
    }
    /**
     * 全角カタカナ→全角ひらがなコンバータを取得する。<p>
     *
     * @return 全角カタカナ→全角ひらがなコンバータ
     * @see KatakanaHiraganaCharacterConverter
     */
    public static CharacterConverter getKatakanaFromHiraganaCharacterConverter(){
        return KATAKANA_TO_HIRAGANA_CHAR;
    }
    /**
     * 全角ひらがな→全角カタカナコンバータを取得する。<p>
     *
     * @return 全角ひらがな→全角カタカナコンバータ
     * @see KatakanaHiraganaCharacterConverter
     */
    public static CharacterConverter getHiraganaFromKatakanaCharacterConverter(){
        return HIRAGANA_TO_KATAKANA_CHAR;
    }
    /**
     * 半角→全角コンバータを取得する。<p>
     * 英字、カタカナ、数字、記号の半角→全角変換を行うコンバータを取得する。
     *
     * @return 半角→全角コンバータ
     * @see AlphabetCharacterConverter
     * @see KatakanaCharacterConverter
     * @see NumberCharacterConverter
     * @see SymbolCharacterConverter
     */
    public static CharacterConverter getHankakuFromZenkakuCharacterConverter(){
        return HANKAKU_TO_ZENKAKU_CHAR;
    }
    /**
     * 全角→半角コンバータを取得する。<p>
     * 英字、カタカナ、数字、記号の全角→半角変換を行うコンバータを取得する。
     *
     * @return 全角→半角コンバータ
     * @see AlphabetCharacterConverter
     * @see KatakanaCharacterConverter
     * @see NumberCharacterConverter
     * @see SymbolCharacterConverter
     */
    public static CharacterConverter getZenkakuFromHankakuCharacterConverter(){
        return ZENKAKU_TO_HANKAKU_CHAR;
    }
    
    /**
     * カスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param fromStrs 変換後文字列配列
     * @param toStrs 変換対象文字列配列
     * @return カスタム文字列コンバータ
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     * @see CustomStringConverter
     */
    public static StringConverter newCustomStringConverter(
        int type,
        String[] fromStrs,
        String[] toStrs
    ){
        return new CustomStringConverter(type, fromStrs, toStrs);
    }
    
    /**
     * カスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param fromChars 変換後文字列配列
     * @param toChars 変換対象文字列配列
     * @return カスタム文字列コンバータ
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     * @see CustomStringConverter
     */
    public static StringConverter newCustomStringConverter(
        int type,
        char[] fromChars,
        char[] toChars
    ){
        return new CustomStringConverter(type, fromChars, toChars);
    }
    
    /**
     * カスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param fromStrs 変換後文字列配列
     * @param toStrs 変換対象文字列配列
     * @param fromChars 変換後文字列配列
     * @param toChars 変換対象文字列配列
     * @return カスタム文字列コンバータ
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     * @see CustomStringConverter
     */
    public static StringConverter newCustomStringConverter(
        int type,
        String[] fromStrs,
        String[] toStrs,
        char[] fromChars,
        char[] toChars
    ){
        return new CustomStringConverter(
            type,
            fromStrs,
            toStrs,
            fromChars,
            toChars
        );
    }
    
    /**
     * 空のカスタムコンバータを生成する。<p>
     * 
     * @return 空のカスタムコンバータ
     */
    public static CustomConverter newCustomConverter(){
        return new CustomConverter();
    }
    
    /**
     * 指定したコンバータを連結したカスタムコンバータを生成する。<p>
     * 
     * @param convs コンバータ配列
     * @return カスタムコンバータ
     */
    public static CustomConverter newCustomConverter(Converter[] convs){
        return new CustomConverter(convs);
    }
    
    /**
     * 指定した正規表現のコンバータを生成する。<p>
     *
     * @return 正規表現コンバータ
     */
    public static PatternStringConverter patternStringConverter(){
        return new PatternStringConverter();
    }
    
    /**
     * 指定した正規表現のコンバータを生成する。<p>
     *
     * @param flags マッチングフラグ
     * @return 正規表現コンバータ
     */
    public static PatternStringConverter patternStringConverter(int flags){
        return new PatternStringConverter(flags);
    }
    
    /**
     * 指定した正規表現のコンバータを生成する。<p>
     *
     * @param flags マッチングフラグ
     * @param fromStrs 変換する正規表現文字列配列
     * @param toStrs 変換対象文字列配列
     * @return 正規表現コンバータ
     */
    public static PatternStringConverter patternStringConverter(
        int flags,
        String[] fromStrs,
        String[] toStrs
    ){
        return new PatternStringConverter(flags,fromStrs,toStrs);
    }
    
    private static final class StringConverterImpl
     implements StringConverter, Serializable{
        
        private static final long serialVersionUID = 1416548061709103644L;
        
        private Converter converter;
        public StringConverterImpl(Converter conv){
            converter = conv;
        }
        public StringConverterImpl(StringConverter conv){
            converter = conv;
        }
        public Object convert(Object obj) throws ConvertException{
            return converter.convert(obj);
        }
        public String convert(String obj) throws ConvertException{
            return (String)converter.convert(obj);
        }
    }
    
    private static final class CharacterConverterImpl
     implements CharacterConverter, Serializable{
        
        private static final long serialVersionUID = -3076044124853526944L;
        
        private Converter converter;
        public CharacterConverterImpl(Converter conv){
            converter = conv;
        }
        public CharacterConverterImpl(CharacterConverter conv){
            converter = conv;
        }
        public Object convert(Object obj) throws ConvertException{
            return converter.convert(obj);
        }
        public Character convert(Character c) throws ConvertException{
            return (Character)converter.convert(c);
        }
        public char convert(char c) throws ConvertException{
            if(converter instanceof CharacterConverter){
                return ((CharacterConverter)converter).convert(c);
            }else{
                return c;
            }
        }
    }
}
