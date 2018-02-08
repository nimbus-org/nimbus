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
package jp.ossc.nimbus.beans;

import java.lang.reflect.*;

import jp.ossc.nimbus.core.NimbusClassLoader;

/**
 * Beansパッケージユーティリティ。<p>
 *
 * @author M.Takata
 */
class Utility{
    
    /**
     * システムプロパティ参照開始文字列。<p>
     */
    public static final String SYSTEM_PROPERTY_START = "${";
    
    /**
     * システムプロパティ参照終了文字列。<p>
     */
    public static final String SYSTEM_PROPERTY_END = "}";
    
    /**
     * XMLコメントアウト開始文字列。<p>
     */
    public static final String COMENT_START = "<!--";
    
    /**
     * XMLコメントアウト終了文字列。<p>
     */
    public static final String COMENT_END = "-->";
    
    private static final String ARRAY_CLASS_SUFFIX = "[]";
    
    private static final String ESCAPE_CR_KEY = "\\r";
    private static final String ESCAPE_LF_KEY = "\\n";
    private static final String ESCAPE_TAB_KEY = "\\t";
    private static final String ESCAPE_PRINT_KEY = "\\f";
    
    private static final String ESCAPE_CR = "\r";
    private static final String ESCAPE_LF = "\n";
    private static final String ESCAPE_TAB = "\t";
    private static final String ESCAPE_PRINT = "\f";
    
    /**
     * 指定された文字列をトリムする。<p>
     * トリムは、指定された文字列の前後の空白文字（{@link Character#isWhitespace(char)}がtrueとなる文字）を削除する。
     * 
     * @param str 文字列
     * @return トリムされた文字列
     */
    public static String trim(String str){
        String result = str;
        for(int i = 0, max = result.length(); i < max; i++){
            final char c = result.charAt(i);
            if(!Character.isWhitespace(c)){
                result = result.substring(i);
                break;
            }
        }
        for(int i = result.length(); --i >= 0;){
            final char c = result.charAt(i);
            if(!Character.isWhitespace(c)){
                result = result.substring(0, i + 1);
                break;
            }
        }
        return result;
    }
    
    /**
     * 指定された文字列内のXMLコメントアウト部分を除去する。<p>
     *
     * @param str 文字列
     * @return XMLコメントアウト部分を除去した文字列
     */
    public static String xmlComentOut(String str){
        String result = str;
        final int startIndex = result.indexOf(COMENT_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(COMENT_END);
        if(endIndex == -1 || startIndex > endIndex){
            return result;
        }
        result = result.substring(0, startIndex)
             + result.substring(endIndex + COMENT_END.length());
        if(result.indexOf(COMENT_START) != -1){
            return xmlComentOut(result);
        }
        return result;
    }
    
    /**
     * 指定された文字列内のプロパティ参照文字列をシステムプロパティの値に置換する。<p>
     * 但し、"${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をシステムプロパティの値に置換した文字列
     */
    public static String replaceSystemProperty(String str){
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(SYSTEM_PROPERTY_END);
        if(endIndex == -1 || startIndex > endIndex){
            return result;
        }
        final String propStr = result.substring(
            startIndex + SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            if(propStr.equals(ESCAPE_CR_KEY)){
                prop = ESCAPE_CR;
            }else if(propStr.equals(ESCAPE_LF_KEY)){
                prop = ESCAPE_LF;
            }else if(propStr.equals(ESCAPE_TAB_KEY)){
                prop = ESCAPE_TAB;
            }else if(propStr.equals(ESCAPE_PRINT_KEY)){
                prop = ESCAPE_PRINT;
            }else{
                prop = System.getProperty(propStr);
            }
        }
        if(prop == null){
            return result.substring(0, endIndex + SYSTEM_PROPERTY_END.length())
             + replaceSystemProperty(
                result.substring(endIndex + SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(SYSTEM_PROPERTY_START) != -1){
            return replaceSystemProperty(result);
        }
        return result;
    }
    
    public static String unicodeConvert(String str){
        char c;
        int len = str.length();
        StringBuffer buf = new StringBuffer(len);
        
        for(int i = 0; i < len; ){
            c = str.charAt(i++);
            if(c == '\\' && i < len){
                c = str.charAt(i++);
                if(c == 'u'){
                    int startIndex = i;
                    int value = 0;
                    boolean isUnicode = true;
                    for(int j = 0; j < 4; j++){
                        if(i >= len){
                            isUnicode = false;
                            break;
                        }
                        c = str.charAt(i++);
                        switch(c){
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            isUnicode = false;
                            break;
                        }
                    }
                    if(isUnicode){
                        buf.append((char)value);
                    }else{
                        buf.append('\\').append('u');
                        i = startIndex;
                    }
                }else{
                    buf.append('\\').append(c);
                }
            }else{
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static Class convertStringToClass(String typeStr)
     throws ClassNotFoundException{
        Class type = null;
        if(typeStr != null){
            if(Byte.TYPE.getName().equals(typeStr)){
                type = Byte.TYPE;
            }else if(Character.TYPE.getName().equals(typeStr)){
                type = Character.TYPE;
            }else if(Short.TYPE.getName().equals(typeStr)){
                type = Short.TYPE;
            }else if(Integer.TYPE.getName().equals(typeStr)){
                type = Integer.TYPE;
            }else if(Long.TYPE.getName().equals(typeStr)){
                type = Long.TYPE;
            }else if(Float.TYPE.getName().equals(typeStr)){
                type = Float.TYPE;
            }else if(Double.TYPE.getName().equals(typeStr)){
                type = Double.TYPE;
            }else if(Boolean.TYPE.getName().equals(typeStr)){
                type = Boolean.TYPE;
            }else{
                if(typeStr.endsWith(ARRAY_CLASS_SUFFIX)
                    && typeStr.length() > 2){
                    final Class elementType = convertStringToClass(
                        typeStr.substring(0, typeStr.length() - 2)
                    );
                    type = Array.newInstance(elementType, 0).getClass();
                }else{
                    type = Class.forName(
                        typeStr,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                }
            }
        }
        return type;
    }
}
