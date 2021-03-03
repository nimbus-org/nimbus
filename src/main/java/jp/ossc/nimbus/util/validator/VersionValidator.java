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

/**
 * バージョンバリデータ。<p>
 * 
 * @author M.Takata
 */
public class VersionValidator implements Validator, java.io.Serializable{
    
    private static final long serialVersionUID = -4492562526492747541L;
    
    /**
     * 比較種別：より大きい(>)。<p>
     */
    public static final int COMPARE_TYPE_GREATER_THAN  = 2;
    
    /**
     * 比較種別：以上(>=)。<p>
     */
    public static final int COMPARE_TYPE_GREATER_EQUAL = 1;
    
    /**
     * 比較種別：等しい(==)。<p>
     */
    public static final int COMPARE_TYPE_EQUAL = 0;
    
    /**
     * 比較種別：以下(<=)。<p>
     */
    public static final int COMPARE_TYPE_LESS_EQUAL = -1;
    
    /**
     * 比較種別：より小さい(<)。<p>
     */
    public static final int COMPARE_TYPE_LESS_THAN  = -2;
    
    protected String targetVersion;
    protected int[] targetVersions;
    
    protected int compareType = COMPARE_TYPE_GREATER_EQUAL;
    
    /**
     * 基準となるバージョン番号を設定する。<p>
     *
     * @param version 基準となるバージョン番号
     */
    public void setTargetVersion(String version) throws NumberFormatException{
        targetVersion = version;
        if(targetVersion != null){
            String[] versions = targetVersion.split("\\.");
            targetVersions = new int[versions.length];
            for(int i = 0; i < versions.length; i++){
                targetVersions[i] = Integer.parseInt(versions[i]);
            }
        }else{
            targetVersions = null;
        }
    }
    
    /**
     * 基準となるバージョン番号を取得する。<p>
     *
     * @return 基準となるバージョン番号
     */
    public String getTargetVersion(){
        return targetVersion;
    }
    
    /**
     * 比較種別を設定する。<p>
     * デフォルトは、{@link #COMPARE_TYPE_GREATER_EQUAL}。<br>
     *
     * @param type 比較種別
     * @see #COMPARE_TYPE_GREATER_THAN
     * @see #COMPARE_TYPE_GREATER_EQUAL
     * @see #COMPARE_TYPE_EQUAL
     * @see #COMPARE_TYPE_LESS_EQUAL
     * @see #COMPARE_TYPE_LESS_THAN
     */
    public void setCompareType(int type){
        switch(compareType){
        case COMPARE_TYPE_GREATER_THAN:
        case COMPARE_TYPE_GREATER_EQUAL:
        case COMPARE_TYPE_EQUAL:
        case COMPARE_TYPE_LESS_EQUAL:
        case COMPARE_TYPE_LESS_THAN:
            compareType = type;
            break;
        default:
            throw new IllegalArgumentException("Unsupport type. type=" + type);
        }
    }
    
    /**
     * 比較種別を取得する。<p>
     *
     * @return 比較種別
     */
    public int getCompareType(){
        return compareType;
    }
    
    /**
     * 指定されたバージョン番号が適切かどうかを検証する。<p>
     *
     * @param obj バージョン番号
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(Object obj) throws ValidateException{
        if(obj == null || obj.toString().length() == 0){
            return targetVersion == null || targetVersion.length() == 0 ? true : false;
        }else if(targetVersion == null){
            return false;
        }
        final String version = obj.toString();
        final String[] versions = version.split("\\.");
        boolean result = true;
        boolean isBreak = false;
        for(int i = 0, imax = versions.length; i < imax; i++){
            int ver = 0;
            try{
                ver = Integer.parseInt(versions[i]);
            }catch(NumberFormatException e){
                throw new ValidateException("Not a number : version=" + version);
            }
            final int targetVer = targetVersions.length > i ? targetVersions[i] : 0;
            final int compare = ver - targetVer;
            switch(compareType){
            case COMPARE_TYPE_GREATER_THAN:
                if(compare < 0
                    || (compare == 0 && i == imax - 1)){
                    result = false;
                }else if(compare > 0){
                    isBreak = true;
                }
                break;
            case COMPARE_TYPE_GREATER_EQUAL:
                if(compare < 0){
                    result = false;
                }else if(compare > 0){
                    isBreak = true;
                }
                break;
            case COMPARE_TYPE_EQUAL:
                if(compare != 0){
                    result = false;
                }
                break;
            case COMPARE_TYPE_LESS_EQUAL:
                if(compare > 0){
                    result = false;
                }else if(compare < 0){
                    isBreak = true;
                }
                break;
            case COMPARE_TYPE_LESS_THAN:
                if(compare > 0
                    || (compare == 0 && i == targetVersions.length - 1)){
                    result = false;
                }else if(compare < 0){
                    isBreak = true;
                }
                break;
            default:
            }
            if(!result || isBreak){
                break;
            }
        }
        if(!isBreak && result && versions.length < targetVersions.length){
            final int ver = 0;
            for(int i = versions.length, imax = targetVersions.length; i < imax; i++){
                final int targetVer = targetVersions.length > i ? targetVersions[i] : 0;
                final int compare = ver - targetVer;
                switch(compareType){
                case COMPARE_TYPE_GREATER_THAN:
                    if(compare < 0
                        || (compare == 0 && i == imax - 1)){
                        result = false;
                    }else if(compare > 0){
                        isBreak = true;
                    }
                    break;
                case COMPARE_TYPE_GREATER_EQUAL:
                    if(compare < 0){
                        result = false;
                    }else if(compare > 0){
                        isBreak = true;
                    }
                    break;
                case COMPARE_TYPE_EQUAL:
                    if(compare != 0){
                        result = false;
                    }
                    break;
                case COMPARE_TYPE_LESS_EQUAL:
                    if(compare > 0){
                        result = false;
                    }else if(compare < 0){
                        isBreak = true;
                    }
                    break;
                case COMPARE_TYPE_LESS_THAN:
                    if(compare > 0
                        || (compare == 0 && i == imax - 1)){
                        result = false;
                    }else if(compare < 0){
                        isBreak = true;
                    }
                    break;
                default:
                }
                if(!result || isBreak){
                    break;
                }
            }
        }
        return result;
    }
}