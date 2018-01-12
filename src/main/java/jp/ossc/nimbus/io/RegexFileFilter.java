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
import java.util.regex.*;

/**
 * 正規表現ファイルフィルタ。<p>
 * 指定された正規表現に合致するファイルのみを抽出するフィルタ。
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.RegexFileFilter;
 *
 * File dir = new File("sample");
 * File[] files = dir.listFiles(new RegexFileFilter("test_[0-9]\\.log"));
 * </pre>
 *
 * @author M.Takata
 */
public class RegexFileFilter implements FilenameFilter, Serializable{
    
    private static final long serialVersionUID = -7076739508599572780L;
    
    private Pattern pattern;
    
    /**
     * 正規表現を指定しないフィルタのインスタンスを生成する。<p>
     */
    public RegexFileFilter(){
        this(null);
    }
    
    /**
     * 指定した正規表現のファイルのみを抽出するフィルタのインスタンスを生成する。<p>
     *
     * @param regex 正規表現
     */
    public RegexFileFilter(String regex){
        this(regex, 0);
    }
    
    /**
     * 指定した正規表現のファイルのみを抽出するフィルタのインスタンスを生成する。<p>
     *
     * @param regex 正規表現
     * @param flags マッチフラグ
     */
    public RegexFileFilter(String regex, int flags){
        setPattern(regex, flags);
    }
    
    /**
     * 指定した正規表現のファイルのみを抽出するように設定する。<p>
     *
     * @param regex 正規表現
     */
    public void setPattern(String regex){
        if(regex == null){
            pattern = null;
        }else{
            pattern = Pattern.compile(regex);
        }
    }
    
    /**
     * 指定した正規表現のファイルのみを抽出するように設定する。<p>
     *
     * @param regex 正規表現
     * @param flags マッチフラグ
     */
    public void setPattern(String regex, int flags){
        if(regex == null){
            pattern = null;
        }else{
            pattern = Pattern.compile(regex, flags);
        }
    }
    
    /**
     * 指定された正規表現のファイルかどうか判定する。<p>
     * 
     * @param dir ディレクトリ
     * @param fileName ファイル名
     * @return 指定されたプレフィクスのファイルの場合true
     */
    public boolean accept(File dir, String fileName) {
        if(pattern == null){
            return false;
        }
        final Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
}