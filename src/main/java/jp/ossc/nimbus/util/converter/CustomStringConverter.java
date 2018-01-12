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
 * カスタム文字列コンバータ。<p>
 * 
 * @author M.Takata
 */
public class CustomStringConverter extends AbstractStringConverter{
    
    private static final long serialVersionUID = 2598748283194585459L;
    
    protected char[][] convertChars;
    protected String[][] convertStrings;
    
    /**
     * 順方向変換する空のカスタム文字列コンバータを生成する。<p>
     */
    public CustomStringConverter(){
        this(POSITIVE_CONVERT);
    }
    
    /**
     * 空のカスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     */
    public CustomStringConverter(int type){
        super(type);
    }
    
    /**
     * カスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param fromStrs 変換後文字列配列
     * @param toStrs 変換対象文字列配列
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     */
    public CustomStringConverter(int type, String[] fromStrs, String[] toStrs){
        super(type);
        setConvertStrings(fromStrs, toStrs);
    }
    
    /**
     * カスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param fromChars 変換後文字列配列
     * @param toChars 変換対象文字列配列
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     */
    public CustomStringConverter(int type, char[] fromChars, char[] toChars){
        super(type);
        setConvertChars(fromChars, toChars);
    }
    
    /**
     * カスタム文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param fromStrs 変換後文字列配列
     * @param toStrs 変換対象文字列配列
     * @param fromChars 変換後文字列配列
     * @param toChars 変換対象文字列配列
     * @see ReversibleConverter#POSITIVE_CONVERT
     * @see ReversibleConverter#REVERSE_CONVERT
     */
    public CustomStringConverter(
        int type,
        String[] fromStrs,
        String[] toStrs,
        char[] fromChars,
        char[] toChars
    ){
        super(type);
        setConvertStrings(fromStrs, toStrs);
        setConvertChars(fromChars, toChars);
    }
    
    /**
     * 変換文字列配列を設定する。<p>
     *
     * @param fromStrs 変換対象文字列配列
     * @param toStrs 変換後文字列配列
     */
    public void setConvertStrings(String[] fromStrs, String[] toStrs){
        if(toStrs == null && fromStrs == null){
            convertStrings = null;
        }else if((toStrs == null || fromStrs == null)
            || toStrs.length != fromStrs.length){
            throw new IllegalArgumentException("Invalid ConvertStrings.");
        }else{
            String[][] convStrs = new String[toStrs.length][];
            for(int i = 0; i < toStrs.length; i++){
                if(fromStrs[i] == null || toStrs[i] == null){
                    throw new IllegalArgumentException("Invalid ConvertStrings.");
                }
                convStrs[i] = new String[]{fromStrs[i], toStrs[i]};
            }
            convertStrings = convStrs;
        }
    }
    
    /**
     * 変換キャラクタ配列を設定する。<p>
     *
     * @param fromChars 変換対象文字配列
     * @param toChars 変換後文字配列
     */
    public void setConvertChars(char[] fromChars, char[] toChars){
        if(toChars == null && fromChars == null){
            convertChars = null;
        }else if((toChars == null || fromChars == null)
            || toChars.length != fromChars.length){
            throw new IllegalArgumentException("Invalid ConvertChars.");
        }else{
            char[][] convChars = new char[toChars.length][];
            for(int i = 0; i < toChars.length; i++){
                convChars[i] = new char[]{fromChars[i], toChars[i]};
            }
            convertChars = convChars;
        }
    }
    
    /**
     * 変換キャラクタ配列を取得する。<p>
     *
     * @return 変換キャラクタ配列
     */
    protected char[][] getConvertChars(){
        return convertChars;
    }
    
    /**
     * 変換文字列配列を取得する。<p>
     *
     * @return 変換文字列配列
     */
    protected String[][] getConvertStrings(){
        return convertStrings;
    }
}
