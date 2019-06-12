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
 * 文字列編集コンバータ。<p>
 * 
 * @author M.Takata
 */
public class StringEditConverter
 implements StringConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -3531421350654717601L;
    private boolean isTrim;
    private boolean isToLowerCase;
    private boolean isToUpperCase;
    private boolean isToCapitalize;
    private int startIndex = -1;
    private int endIndex = -1;
    private boolean isIgnoreArrayIndexOutOfBounds;
    private String splitRegex;
    private int splitIndex;
<<<<<<< HEAD
    private int splitLimit;
=======
>>>>>>> branch 'master' of ssh://git@github.com/nimbus-org/nimbus.git
    
    /**
     * トリムするかどうかを設定する。<p>
     *
     * @param trim トリムする場合true
     */
    public void setTrim(boolean trim){
        isTrim = trim;
    }
    
    /**
     * トリムするかどうかを判定する。<p>
     *
     * @return trueの場合、トリムする
     */
    public boolean isTrim(){
        return isTrim;
    }
    
    /**
     * 小文字に変換するかどうかを設定する。<p>
     *
     * @param lower 小文字に変換する場合true
     */
    public void setToLowerCase(boolean lower){
        isToLowerCase = lower;
        if(isToLowerCase && isToUpperCase){
            isToUpperCase = false;
        }
    }
    
    /**
     * 小文字に変換するかどうかを判定する。<p>
     *
     * @return trueの場合、小文字に変換する
     */
    public boolean isToLowerCase(){
        return isToLowerCase;
    }
    
    /**
     * 大文字に変換するかどうかを設定する。<p>
     *
     * @param upper 大文字に変換する場合true
     */
    public void setToUpperCase(boolean upper){
        isToUpperCase = upper;
        if(isToLowerCase && isToUpperCase){
            isToLowerCase = false;
        }
    }
    
    /**
     * 大文字に変換するかどうかを判定する。<p>
     *
     * @return trueの場合、大文字に変換する
     */
    public boolean isToUpperCase(){
        return isToUpperCase;
    }
    
    /**
     * 頭文字を大文字に変換するかどうかを設定する。<p>
     *
     * @param capitalize 頭文字を大文字に変換する場合true
     */
    public void setToCapitalize(boolean capitalize){
        isToCapitalize = capitalize;
    }
    
    /**
     * 頭文字を大文字に変換するかどうかを判定する。<p>
     *
     * @return trueの場合、頭文字を大文字に変換する
     */
    public boolean isToCapitalize(){
        return isToCapitalize;
    }
    
    /**
     * 部分文字列にするための開始位置を設定する。<p>
     *
     * @param index 部分文字列にするための開始位置
     */
    public void setStartIndex(int index){
        startIndex = index;
    }
    
    /**
     * 部分文字列にするための開始位置を取得する。<p>
     *
     * @return 部分文字列にするための開始位置
     */
    public int getStartIndex(){
        return startIndex;
    }
    
    /**
     * 部分文字列にするための終了位置を設定する。<p>
     *
     * @param index 部分文字列にするための終了位置
     */
    public void setEndIndex(int index){
        endIndex = index;
    }
    
    /**
     * 部分文字列にするための終了位置を取得する。<p>
     *
     * @return 部分文字列にするための終了位置
     */
    public int getEndIndex(){
        return endIndex;
    }
    
    /**
     * 指定された部分文字列に満たない場合に、例外を発生させないようにするかどうかを設定する。<p>
     *
     * @param isIgnore 例外を発生させないようにする場合true
     */
    public void setIgnoreArrayIndexOutOfBounds(boolean isIgnore){
        isIgnoreArrayIndexOutOfBounds = isIgnore;
    }
    
    /**
     * 指定された部分文字列に満たない場合に、例外を発生させないようにするかどうかを判定する。<p>
     *
     * @return 例外を発生させないようにする場合true
     */
    public boolean isIgnoreArrayIndexOutOfBounds(){
        return isIgnoreArrayIndexOutOfBounds;
    }
    
    /**
     * スプリットする正規表現文字列を設定する。<p>
     *
     * @param regex スプリットする正規表現文字列
     */
    public void setSplitRegex(String regex){
        splitRegex = regex;
    }
    
    /**
     * スプリットする正規表現文字列を取得する。<p>
     *
     * @return スプリットする正規表現文字列
     */
    public String getSplitRegex(){
        return splitRegex;
    }
    
    /**
     * スプリットした文字列配列の何番目を取り出すか設定する。<p>
     *
     * @param index スプリットした文字列配列上のインデックス
     */
    public void setSplitIndex(int index){
        splitIndex = index;
    }
    
    /**
     * スプリットした文字列配列の何番目を取り出すか取得する。<p>
     *
     * @return スプリットした文字列配列上のインデックス
     */
    public int getSplitIndex(){
        return splitIndex;
    }
    
<<<<<<< HEAD
    /**
     * スプリットする正規表現を何回適用するかを設定する。<p>
     *
     * @param limit スプリットする正規表現の適用回数
     */
    public void setSplitLimit(int limit){
        splitLimit = limit;
    }
    
    /**
     * スプリットする正規表現を何回適用するかを取得する。<p>
     *
     * @return スプリットする正規表現の適用回数
     */
    public int getSplitLimit(){
        return splitLimit;
    }
    
=======
>>>>>>> branch 'master' of ssh://git@github.com/nimbus-org/nimbus.git
    public Object convert(Object obj) throws ConvertException{
        return convert(obj == null ? null : obj.toString());
    }
    
    public String convert(String str) throws ConvertException{
        if(str == null || str.length() == 0){
            return str;
        }
        String result = str;
        if(isTrim){
            result = result.trim();
        }
        if(splitRegex != null){
<<<<<<< HEAD
            String[] results = splitLimit > 0 ? result.split(splitRegex, splitLimit) : result.split(splitRegex);
=======
            String[] results = result.split(splitRegex);
>>>>>>> branch 'master' of ssh://git@github.com/nimbus-org/nimbus.git
            result = results[splitIndex];
        }
        int sIndex = startIndex;
        int eIndex = endIndex;
        if(isIgnoreArrayIndexOutOfBounds){
            if(sIndex > 0 && sIndex >= result.length()){
                sIndex = result.length();
            }
            if(eIndex > 1 && eIndex > result.length()){
                eIndex = result.length();
            }
        }
        if(sIndex == 0){
            sIndex = -1;
        }
        if(eIndex == result.length()){
            eIndex = -1;
        }
        if(sIndex >= 0 && eIndex >= 0){
            try{
                result = result.substring(sIndex, eIndex);
            }catch(ArrayIndexOutOfBoundsException e){
                throw new ConvertException(e);
            }
        }else if(sIndex >= 0){
            try{
                result = result.substring(sIndex);
            }catch(ArrayIndexOutOfBoundsException e){
                throw new ConvertException(e);
            }
        }else if(eIndex >= 0){
            try{
                result = result.substring(0, eIndex);
            }catch(ArrayIndexOutOfBoundsException e){
                throw new ConvertException(e);
            }
        }
        if(isToLowerCase){
            result = result.toLowerCase();
        }else if(isToUpperCase){
            result = result.toUpperCase();
        }
        if(isToCapitalize){
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result;
    }
    
}