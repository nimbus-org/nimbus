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
 * JSONエスケープコンバータ。<p>
 *
 * @author M.Takata
 */
public class JSONEscapeConverter implements StringConverter, ReversibleConverter, java.io.Serializable{
    
    private static final long serialVersionUID = 4357679227789614028l;
    
    /**
     * JSONエスケープを表す変換種別定数。<p>
     */
    public static final int ESCAPE = POSITIVE_CONVERT;
    
    /**
     * JSONエスケープ解除を表す変換種別定数。<p>
     */
    public static final int UNESCAPE = REVERSE_CONVERT;
    
    private static final char ESCAPE_CHAR = '\\';
    
    private static final char QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char SLASH = '/';
    private static final char BACK_SPACE = '\b';
    private static final char BACK_SPACE_CHAR = 'b';
    private static final char CHANGE_PAGE = '\f';
    private static final char CHANGE_PAGE_CHAR = 'f';
    private static final char LF = '\n';
    private static final char LF_CHAR = 'n';
    private static final char CR = '\r';
    private static final char CR_CHAR = 'r';
    private static final char TAB = '\t';
    private static final char TAB_CHAR = 't';
    
    private static final String ESCAPE_QUOTE = "\\\"";
    private static final String ESCAPE_BACK_SLASH = "\\\\";
    private static final String ESCAPE_SLASH = "\\/";
    private static final String ESCAPE_BACK_SPACE = "\\b";
    private static final String ESCAPE_CHANGE_PAGE = "\\f";
    private static final String ESCAPE_LF = "\\n";
    private static final String ESCAPE_CR = "\\r";
    private static final String ESCAPE_TAB = "\\t";
    
    /**
     * 変換種別。<p>
     */
    protected int convertType = ESCAPE;
    
    /**
     * JSONエスケープ時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかのフラグ。<p>
     * デフォルトは、trueでユニコードエスケープする。<br>
     */
    protected boolean isUnicodeEscape = true;
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #ESCAPE
     * @see #UNESCAPE
     */
    public void setConvertType(int type){
        switch(convertType){
        case ESCAPE:
        case UNESCAPE:
            convertType = type;
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
    
    /**
     * JSONエスケープ時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかを判定する。<p>
     *
     * @return エスケープする場合true
     */
    public boolean isUnicodeEscape(){
        return isUnicodeEscape;
    }
    
    /**
     * JSONエスケープ時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかを設定する。<p>
     * デフォルトは、trueでユニコードエスケープする。<br>
     *
     * @param isEscape エスケープする場合true
     */
    public void setUnicodeEscape(boolean isEscape){
        isUnicodeEscape = isEscape;
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
     *
     * @param str 変換対象の文字列 
     * @return 変換後の文字列
     * @exception ConvertException 変換に失敗した場合
     */
    public String convert(String str) throws ConvertException{
        switch(convertType){
        case ESCAPE:
            return escape(str);
        case UNESCAPE:
            return unescape(str);
        default:
            return str;
        }
    }
    
    private String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        boolean isEscape = false;
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, imax = str.length(); i < imax; i++){
            final char c = str.charAt(i);
            
            switch(c){
            case QUOTE:
                buf.append(ESCAPE_QUOTE);
                isEscape = true;
                break;
            case BACK_SLASH:
                buf.append(ESCAPE_BACK_SLASH);
                isEscape = true;
                break;
            case SLASH:
                buf.append(ESCAPE_SLASH);
                isEscape = true;
                break;
            case BACK_SPACE:
                buf.append(ESCAPE_BACK_SPACE);
                isEscape = true;
                break;
            case CHANGE_PAGE:
                buf.append(ESCAPE_CHANGE_PAGE);
                isEscape = true;
                break;
            case LF:
                buf.append(ESCAPE_LF);
                isEscape = true;
                break;
            case CR:
                buf.append(ESCAPE_CR);
                isEscape = true;
                break;
            case TAB:
                buf.append(ESCAPE_TAB);
                isEscape = true;
                break;
            default:
                if(isUnicodeEscape
                    && !(c == 0x20
                     || c == 0x21
                     || (0x23 <= c && c <= 0x5B)
                     || (0x5D <= c && c <= 0x7E))
                ){
                    isEscape = true;
                    toUnicode(c, buf);
                }else{
                    buf.append(c);
                }
            }
        }
        return isEscape ? buf.toString() : str;
    }
    
    private StringBuilder toUnicode(char c, StringBuilder buf){
        buf.append(ESCAPE_CHAR);
        buf.append('u');
        int mask = 0xf000;
        for(int i = 0; i < 4; i++){
            mask = 0xf000 >> (i * 4);
            int val = c & mask;
            val = val << (i * 4);
            switch(val){
            case 0x0000:
                buf.append('0');
                break;
            case 0x1000:
                buf.append('1');
                break;
            case 0x2000:
                buf.append('2');
                break;
            case 0x3000:
                buf.append('3');
                break;
            case 0x4000:
                buf.append('4');
                break;
            case 0x5000:
                buf.append('5');
                break;
            case 0x6000:
                buf.append('6');
                break;
            case 0x7000:
                buf.append('7');
                break;
            case 0x8000:
                buf.append('8');
                break;
            case 0x9000:
                buf.append('9');
                break;
            case 0xa000:
                buf.append('a');
                break;
            case 0xb000:
                buf.append('b');
                break;
            case 0xc000:
                buf.append('c');
                break;
            case 0xd000:
                buf.append('d');
                break;
            case 0xe000:
                buf.append('e');
                break;
            case 0xf000:
                buf.append('f');
                break;
            default:
            }
        }
        return buf;
    }
    
    private String unescape(String str){
        if(str != null){
            final int length = str.length();
            final StringBuilder buf = new StringBuilder(length);
            boolean isUnescape = false;
            for(int i = 0; i < length;){
                //文字列を切り取る
                char c = str.charAt(i++);
                //エスケープなら
                if(c == '\\' && length > i){
                    isUnescape = true;
                    c = str.charAt(i++);
                    switch(c){
                    case BACK_SPACE_CHAR:
                        c = BACK_SPACE;
                        break;
                    case CHANGE_PAGE_CHAR:
                        c = CHANGE_PAGE;
                        break;
                    case LF_CHAR:
                        c = LF;
                        break;
                    case CR_CHAR:
                        c = CR;
                        break;
                    case TAB_CHAR:
                        c = TAB;
                        break;
                    case QUOTE:
                    case BACK_SLASH:
                    case SLASH:
                    default:
                    }
                }
                buf.append(c);
            }
            if(isUnescape){
                str = buf.toString();
            }
        }
        return str;
    }
    
}
