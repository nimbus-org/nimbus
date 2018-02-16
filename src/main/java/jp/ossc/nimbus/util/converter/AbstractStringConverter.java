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
 * 文字列コンバータの抽象クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class AbstractStringConverter
 implements StringConverter, ReversibleConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -4121468203318618862L;
    
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
    public AbstractStringConverter(){
        this(POSITIVE_CONVERT);
    }
    
    /**
     * 指定された変換種別の文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #POSITIVE_CONVERT
     * @see #REVERSE_CONVERT
     */
    public AbstractStringConverter(int type){
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
        }else{
            return convert(
                (String)(obj instanceof String ? obj : String.valueOf(obj))
            );
        }
    }
    
    /**
     * 文字列を変換する。<p>
     * 変換文字列配列と変換キャラクタ配列を使って変換する。<br>
     *
     * @param str 変換対象の文字列 
     * @return 変換後の文字列
     * @exception ConvertException 変換に失敗した場合
     */
    public String convert(String str) throws ConvertException{
        String result = str;
        
        result = convertStrings(result);
        result = convertChars(result);
        
        return result;
    }
    
    /**
     * 指定された文字列を、変換キャラクタ配列を使って変換する。<p>
     *
     * @param str 変換対象の文字列 
     * @return 変換後の文字列
     * @exception ConvertException 変換に失敗した場合
     * @see #getConvertChars()
     */
    protected String convertChars(String str) throws ConvertException{
        String result = str;
        
        final char[][] convertChars = getConvertChars();
        if(convertChars != null){
            char[] chars = null;
            for(int i = 0, max = result.length(); i < max; i++){
                char c = result.charAt(i);
                for(int j = 0; j < convertChars.length; j++){
                    if(c == convertChars[j][from]){
                        if(chars == null){
                            chars = str.toCharArray();
                        }
                        chars[i] = convertChars[j][to];
                    }
                }
            }
            if(chars != null){
                result = new String(chars);
            }
        }
        
        return result;
    }
    
    /**
     * 指定された文字列を、変換文字列配列を使って変換する。<p>
     *
     * @param str 変換対象の文字列 
     * @return 変換後の文字列
     * @exception ConvertException 変換に失敗した場合
     * @see #getConvertStrings()
     */
    protected String convertStrings(String str) throws ConvertException{
        String result = str;
        final String[][] convertStrings = getConvertStrings();
        if(convertStrings != null){
            final StringBuilder buf = new StringBuilder(result);
            boolean isReplace = false;
            for(int i = 0; i < convertStrings.length; i++){
                int length = convertStrings[i][from].length();
                int index = buf.lastIndexOf(convertStrings[i][from]);
                if(index != -1){
                    isReplace = true;
                    do{
                        buf.replace(
                            index,
                            index + length,
                            convertStrings[i][to]
                        );
                        if(index < length){
                            break;
                        }
                    }while((index = buf.lastIndexOf(
                        convertStrings[i][from], index - 1)) != -1);
                }
            }
            if(isReplace){
                result = buf.toString();
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
    
    /**
     * 変換文字列配列を取得する。<p>
     *
     * @return 変換文字列配列
     */
    protected abstract String[][] getConvertStrings();
}
