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
package jp.ossc.nimbus.io;

import java.io.*;

/**
 * 拡張子指定のファイルフィルタ。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.ExtentionFileFilter;
 *
 * File dir = new File("sample");
 * File[] files = dir.listFiles(new ExtentionFileFilter("def"));
 * </pre>
 *
 * @author H.Nakano
 */
public class ExtentionFileFilter implements FilenameFilter, Serializable{
    
    private static final long serialVersionUID = 5687776723127309667L;
    
    /**
     * ファイルの拡張子。<p>
     */
    protected String extention;
    
    /**
     * 拡張子の大文字・小文字を区別するかどうかのフラグ。<p>
     * デフォルトは、trueで、大文字・小文字を区別しない。<br>
     */
    protected boolean isIgnoreCase = true;
    
    private String upperExtention;
    
    /**
     * 拡張子を指定しないフィルタのインスタンスを生成する。<p>
     */
    public ExtentionFileFilter(){
        this(null, true);
    }
    
    /**
     * 指定した拡張子のファイルフィルタのインスタンスを生成する。<p>
     *
     * @param ext 拡張子文字列
     */
    public ExtentionFileFilter(String ext){
        this(ext, true);
    }
    
    /**
     * 指定した拡張子のファイルフィルタのインスタンスを生成する。<p>
     *
     * @param ext 拡張子文字列
     * @param isIgnoreCase 大文字・小文字を区別しない場合はtrue
     */
    public ExtentionFileFilter(String ext, boolean isIgnoreCase){
        setExtention(ext);
        setIgnoreCase(isIgnoreCase);
    }
    
    /**
     * ファイルの拡張子を設定する。<p>
     * 指定された拡張子が、"."から始まらない場合は、自動的に付加する。また、nullや空文字を指定した場合は、フィルタリングしない。<br>
     * 
     * @param ext ファイルの拡張子
     */
    public void setExtention(String ext){
        if(ext == null || ext.length() == 0){
            extention = null;
        }else if(ext.charAt(0) == '.'){
            extention = ext;
            upperExtention = extention.toUpperCase();
        }else{
            extention = '.' + ext;
            upperExtention = extention.toUpperCase();
        }
    }
    
    /**
     * ファイルの拡張子を取得する。<p>
     * 
     * @return ファイルの拡張子
     */
    public String getExtention(){
        return extention;
    }
    
    /**
     * 拡張子の大文字・小文字を無視するかどうかを設定する。<p>
     * デフォルトは、true。
     *
     * @param isIgnoreCase 大文字・小文字を区別しない場合はtrue
     */
    public void setIgnoreCase(boolean isIgnoreCase){
        this.isIgnoreCase = isIgnoreCase;
    }
    
    /**
     * 拡張子の大文字・小文字を無視するかどうかを判定する。<p>
     *
     * @return trueの場合、大文字・小文字を区別しない
     */
    public boolean isIgnoreCase(){
        return isIgnoreCase;
    }
    
    /**
     * 指定された拡張子のファイルかどうか判定する。<p>
     * 
     * @param dir ディレクトリ
     * @param fileName ファイル名
     * @return 指定された拡張子のファイルの場合true
     */
    public boolean accept(File dir, String fileName){
        if(extention == null){
            return true;
        }else if(isIgnoreCase){
            String tmp = fileName.toUpperCase();
            return tmp.endsWith(upperExtention);
        }else{
            return fileName.endsWith(extention);
        }
    }
}
