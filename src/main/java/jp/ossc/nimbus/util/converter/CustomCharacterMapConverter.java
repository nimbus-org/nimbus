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
 * カスタムキャラクタマップコンバータ。<p>
 * 
 * @author M.Takata
 */
public class CustomCharacterMapConverter
 implements CharacterConverter, StringConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -1803120054841953689L;
    
    protected boolean[] charMapArray = null;
    protected char toChar = '?';
    protected Character toCharacter = new Character(toChar);
    protected boolean isTarget = true;
    
    /**
     * 空のカスタムキャラクタマップコンバータを生成する。<p>
     */
    public CustomCharacterMapConverter(){
    }
    
    /**
     * カスタムキャラクタマップコンバータを生成する。<p>
     *
     * @param chars 対象文字配列
     * @param toChar 変換後キャラクタ
     * @param isTarget charsを変換対象とする場合true。chars以外を変換対象とする場合false
     */
    public CustomCharacterMapConverter(char[] chars, char toChar, boolean isTarget){
        setCharMap(chars);
        setToChar(toChar);
        this.isTarget = isTarget;
    }
    
    /**
     * 対象マップ文字配列を設定する。<p>
     *
     * @param chars 対象文字配列
     */
    public void setCharMap(char[] chars){
        if(charMapArray == null){
            if(chars != null){
                charMapArray = new boolean[0xFFFF + 1];
            }
        }else{
            for(int i = 0; i < charMapArray.length; i++){
                charMapArray[i] = false;
            }
        }
        if(chars != null){
            for(int i = 0; i < chars.length; i++){
                charMapArray[(int)chars[i]] = true;
            }
        }
    }
    
    /**
     * 対象マップ文字配列を追加する。<p>
     *
     * @param chars 対象文字配列
     */
    public void addCharMap(char[] chars){
        if(chars == null || chars.length == 0){
            return;
        }
        if(charMapArray == null){
            charMapArray = new boolean[0xFFFF + 1];
        }
        for(int i = 0; i < chars.length; i++){
            charMapArray[(int)chars[i]] = true;
        }
    }
    
    /**
     * 対象マップ文字を追加する。<p>
     *
     * @param c 対象文字
     */
    public void addChar(char c){
        if(charMapArray == null){
            charMapArray = new boolean[0xFFFF + 1];
        }
        charMapArray[(int)c] = true;
    }
    
    /**
     * 対象範囲文字を設定する。<p>
     *
     * @param from 対象範囲開始文字
     * @param to 対象範囲終了文字
     */
    public void setCharRange(char from, char to){
        if(from > to){
            throw new IllegalArgumentException("from > to.");
        }
        if(charMapArray == null){
            charMapArray = new boolean[0xFFFF + 1];
        }else{
            for(int i = 0; i < charMapArray.length; i++){
                charMapArray[i] = false;
            }
        }
        for(int i = from; i <= to; i++){
            charMapArray[i] = true;
        }
    }
    
    /**
     * 対象範囲文字を追加する。<p>
     *
     * @param from 対象範囲開始文字
     * @param to 対象範囲終了文字
     */
    public void addCharRange(char from, char to){
        if(from > to){
            throw new IllegalArgumentException("from > to.");
        }
        if(charMapArray == null){
            charMapArray = new boolean[0xFFFF + 1];
        }
        for(int i = from; i <= to; i++){
            charMapArray[i] = true;
        }
    }
    
    /**
     * 変換後文字を設定する。<p>
     * デフォルトは、'?'。
     *
     * @param c 変換後文字
     */
    public void setToChar(char c){
        this.toChar = c;
        toCharacter = new Character(toChar);
    }
    
    /**
     * 変換後文字を取得する。<p>
     *
     * @return 変換後文字
     */
    public char getToChar(){
        return toChar;
    }
    
    /**
     * 対象範囲の文字を変換対象とするか、対象範囲の文字以外を変換対象とするかを設定する。<p>
     * デフォルトは、true。
     *
     * @param isTarget 対象範囲の文字を変換対象とする場合true。対象範囲の文字以外を変換対象とする場合false
     */
    public void setTarget(boolean isTarget){
        this.isTarget = isTarget;
    }
    
    /**
     * 対象範囲の文字を変換対象とするか、対象範囲の文字以外を変換対象とするかを判定する。<p>
     *
     * @return trueの場合、対象範囲の文字を変換対象とする。falseの場合、対象範囲の文字以外を変換対象とする
     */
    public boolean isTarget(){
        return isTarget;
    }
    
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }else if(obj instanceof Character){
            return convert((Character)obj);
        }else if(obj instanceof String){
            return convert((String)obj);
        }else{
            return obj;
        }
    }
    
    public char convert(char c) throws ConvertException{
        if(charMapArray == null){
            return c;
        }
        return charMapArray[(int)c] ? (isTarget ? toChar : c) : (isTarget ? c : toChar);
    }
    
    public Character convert(Character c) throws ConvertException{
        final char from = c.charValue();
        final char to = convert(from);
        return from == to ? c : toCharacter;
    }
    
    public String convert(String str) throws ConvertException{
        final char[] chars = str.toCharArray();
        char[] result = new char[chars.length];
        boolean isConvert = false;
        for(int i = 0; i < chars.length; i++){
            char c = convert(chars[i]);
            if(c != chars[i] && !isConvert){
                result = new char[chars.length];
                System.arraycopy(chars, 0, result, 0, i);
                isConvert = true;
            }
            if(isConvert){
                result[i] = c;
            }
        }
        return isConvert ? new String(result) : str;
    }
}
