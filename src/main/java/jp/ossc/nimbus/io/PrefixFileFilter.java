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
 * プレフィクスファイルフィルタ。<p>
 * 指定されたプレフィクスのファイルのみを抽出するフィルタ。
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.PrefixFileFilter;
 *
 * File dir = new File("sample");
 * File[] files = dir.listFiles(new PrefixFileFilter("test"));
 * </pre>
 *
 * @author H.Nakano
 */
public class PrefixFileFilter implements FilenameFilter, Serializable{
    
    private static final long serialVersionUID = 3579204076741445814L;
    
    /**
     * ファイルのプレフィクス。<p>
     */
    protected String prefix;
    
    private String upperPrefix;
    
    /**
     * プレフィクスの大文字・小文字を区別するかどうかのフラグ。<p>
     * デフォルトは、falseで、大文字・小文字を区別する。<br>
     */
    protected boolean isIgnoreCase;
    
    /**
     * プレフィクスを指定しないフィルタのインスタンスを生成する。<p>
     */
    public PrefixFileFilter(){
        this(null, false);
    }
    
    /**
     * 指定したプレフィクスのファイルのみを抽出するフィルタのインスタンスを生成する。<p>
     *
     * @param prefix プレフィクス
     */
    public PrefixFileFilter(String prefix){
        this(prefix, false);
    }
    
    /**
     * 指定したプレフィクスのファイルのみを抽出するフィルタのインスタンスを生成する。<p>
     *
     * @param prefix プレフィクス
     * @param isIgnoreCase プレフィクスの大文字・小文字を区別しない場合はtrue
     */
    public PrefixFileFilter(
        String prefix,
        boolean isIgnoreCase
    ){
        setPrefix(prefix);
        setIgnoreCase(isIgnoreCase);
    }
    
    /**
     * ファイルのプレフィクスを設定する。<p>
     * nullや空文字を指定した場合は、フィルタリングしない。<br>
     * 
     * @param prefix ファイルのプレフィクス
     */
    public void setPrefix(String prefix){
        if(prefix == null || prefix.length() == 0){
            this.prefix = null;
        }else{
            this.prefix = prefix;
            upperPrefix = prefix.toUpperCase();
        }
    }
    
    /**
     * ファイルのプレフィクスを取得する。<p>
     * 
     * @return ファイルのプレフィクス
     */
    public String getPrefix(){
        return prefix;
    }
    
    /**
     * プレフィクスの大文字・小文字を無視するかどうかを設定する。<p>
     * デフォルトは、true。
     *
     * @param isIgnoreCase 大文字・小文字を区別しない場合はtrue
     */
    public void setIgnoreCase(boolean isIgnoreCase){
        this.isIgnoreCase = isIgnoreCase;
    }
    
    /**
     * プレフィクスの大文字・小文字を無視するかどうかを判定する。<p>
     *
     * @return trueの場合、大文字・小文字を区別しない
     */
    public boolean isIgnoreCase(){
        return isIgnoreCase;
    }
    
    /**
     * 指定されたプレフィクスのファイルかどうか判定する。<p>
     * 
     * @param dir ディレクトリ
     * @param fileName ファイル名
     * @return 指定されたプレフィクスのファイルの場合true
     */
    public boolean accept(File dir, String fileName) {
        if(isIgnoreCase){
            final String tmp = fileName.toUpperCase();
            return tmp.startsWith(upperPrefix);
        }else{
            return fileName.startsWith(prefix);
        }
    }
}
