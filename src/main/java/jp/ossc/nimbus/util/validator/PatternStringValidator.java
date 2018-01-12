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
package jp.ossc.nimbus.util.validator;

import java.util.regex.*;

/**
 * 正規表現文字列バリデータ。<p>
 * 
 * @author M.Takata
 */
public class PatternStringValidator extends AbstractStringValidator
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 3525013552708443644L;
    
    /**
     * 文字列を検証する正規表現。<p>
     */
    protected Pattern pattern;
    
    /**
     * 検証に使用する正規表現パターンを設定する。<p>
     *
     * @param pattern 正規表現パターン
     */
    public void setPattern(Pattern pattern){
        this.pattern = pattern;
    }
    
    /**
     * 検証に使用する正規表現パターンを取得する。<p>
     *
     * @return 正規表現パターン
     */
    public Pattern getPattern(){
        return pattern;
    }
    
    /**
     * 検証に使用する正規表現パターン文字列を設定する。<p>
     *
     * @param pattern 正規表現パターン文字列
     */
    public void setPatternString(String pattern){
        this.pattern = Pattern.compile(pattern);
    }
    
    /**
     * 検証に使用する正規表現パターン文字列を設定する。<p>
     *
     * @param pattern 正規表現パターン文字列
     * @param flags マッチフラグ
     */
    public void setPatternString(String pattern, int flags){
        this.pattern = Pattern.compile(pattern, flags);
    }
    
    /**
     * 検証に使用する正規表現パターン文字列を取得する。<p>
     *
     * @return 正規表現パターン文字列
     */
    public String getPatternString(){
        return pattern == null ? null : pattern.pattern();
    }
    
    /**
     * 指定された文字列が正規表現にマッチするかどうかを検証する。<p>
     *
     * @param str 検証対象の文字列
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateString(String str) throws ValidateException{
        if(pattern != null){
            if(!pattern.matcher(str).matches()){
                return false;
            }
        }
        return true;
    }
}