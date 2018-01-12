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
 * キャラクタコンバータの抽象クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class AbstractCharacterConverter
 implements CharacterConverter, StringConverter, ReversibleConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -4403134705436532180L;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * 変換元の配列インデックス。<p>
     */
    protected int from;
    
    /**
     * 変換後の配列インデックス。<p>
     */
    protected int to;
    
    /**
     * 順方向変換のキャラクタコンバータを生成する。<p>
     */
    public AbstractCharacterConverter(){
        this(POSITIVE_CONVERT);
    }
    
    /**
     * 指定された変換種別のキャラクタコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #POSITIVE_CONVERT
     * @see #REVERSE_CONVERT
     */
    public AbstractCharacterConverter(int type){
        setConvertType(type);
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #POSITIVE_CONVERT
     * @see #REVERSE_CONVERT
     */
    public void setConvertType(int type){
        convertType = type;
        switch(convertType){
        case POSITIVE_CONVERT:
            from = 0;
            to = 1;
            break;
        case REVERSE_CONVERT:
            from = 1;
            to = 0;
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid convert type : " + type
            );
        }
    }
    
    /**
     * 変換種別を取得する。<p>
     *
     * @return 変換種別
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
    }
    
    // ConverterのJavaDoc
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
    
    public String convert(String str) throws ConvertException{
        final char[] chars = str.toCharArray();
        char[] result = null;
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
    
    /**
     * キャラクタを変換する。<p>
     * 変換キャラクタ配列を使って変換する。<br>
     *
     * @param c 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     */
    public Character convert(Character c) throws ConvertException{
        Character result = c;
        
        result = convertChars(result);
        
        return result;
    }
    
    /**
     * キャラクタを変換する。<p>
     * 変換キャラクタ配列を使って変換する。<br>
     *
     * @param c 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     */
    public char convert(char c) throws ConvertException{
        char result = c;
        
        result = convertChars(result);
        
        return result;
    }
    
    /**
     * 指定されたキャラクタを、変換キャラクタ配列を使って変換する。<p>
     *
     * @param c 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     * @see #getConvertChars()
     */
    protected Character convertChars(Character c) throws ConvertException{
        char result = convertChars(c.charValue());
        if(result == c.charValue()){
            return c;
        }else{
            return new Character(result);
        }
    }
    
    /**
     * 指定されたキャラクタを、変換キャラクタ配列を使って変換する。<p>
     *
     * @param c 変換対象のキャラクタ
     * @return 変換後のキャラクタ
     * @exception ConvertException 変換に失敗した場合
     * @see #getConvertChars()
     */
    protected char convertChars(char c) throws ConvertException{
        char result = c;
        
        final char[][] convertChars = getConvertChars();
        if(convertChars != null){
            for(int i = 0; i < convertChars.length; i++){
                if(c == convertChars[i][from]){
                    result = convertChars[i][to];
                }
            }
        }
        
        return result;
    }
    
    /**
     * 変換キャラクタ配列を取得する。<p>
     *
     * @return 変換キャラクタ配列
     */
    protected abstract char[][] getConvertChars();
}
